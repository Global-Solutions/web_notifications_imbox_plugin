# notifications-imbox.coffee は、intra-mart Accel PlatformのMyBox通知をWebNotificationで行う機能です。
# notifications.coffeeにWebNotification表示とWebSocket通信の機能を依存しています。
#
# ### 設定の仕方
# <pre>
# var tenantId = ???;
# var userCd = ???;
# jQuery(function() {
#     jQuery.when(
#         notifications.onLoadHandler(),
#         webSockets.onLoadHandler(),
#         imboxNotifications.onLoadHandler(tenantId, userCd)
#         )
#     .done(function() {
#         imboxNotifications.connect().done(function(op) {
#             wsop = op;
#         });
#     });
# });
# </pre>
#
# Copyright 2014 Global Solutions company limited
#
# notifications.coffee may be freely distributed under the GPL-3.0 license.
# - - -


# local jQuery object
$ = jQuery
# messageId取得関数
messageId = -> return

# localStorageへのインターフェイスのファクトリ
storageFactory = (prefix, tenantId, userCd) ->
    key = "#{prefix}.#{tenantId}.#{userCd}"
    (value) ->
        if not value?
            return localStorage[key]
        localStorage[key] = "#{value}"
        
# ## IMBox Notifications implements
# - @onLoadHandler: 起動時の処理
# - @connect: 接続処理
@imboxNotifications =
    # ### onLoadHandler(tenantId, userCd)
    # 既読メッセージ保存に使用
    # #### arguments
    # - tenantId: ログインユーザのテナントID
    # - userCd: ログインユーザのユーザコード
    onLoadHandler: (tenantId, userCd) ->
        messageId = storageFactory 'notifications.imbox', tenantId, userCd
        return

    # ### connect() -> $.prototype.promise
    # デフォルトサーバのIMBox通知サービスに接続
    # 接続完了すると、promiseがresolveされる。
    # promiseハンドラには、WebSocket操作オブジェクトが渡される。
    connect: ->
        $deferred = $.Deferred()
        op = {}
        webSockets.connect(
            protocol: 'websocket-imbox-protocol'
            # メッセージを受け取ったら、WebNotificationを表示する  
            # 併せて、既読処理を行う
            messageHandler: (d) ->
                message = JSON.parse d
                notifications.showNotification(
                    "#{message.postUserName}@#{message.boxName}",
                    options:
                        body: message.message
                        icon: "notifications/imbox/userIcon/#{message.postUserCd}?#{message.iconId}" \
                              if message.iconId
                ) if not message.pong?
                messageId message.messageId
                console.log @, arguments, message
                return
            reconnect:
                done: ->
                    # 再接続完了時に、既読状態を送信する
                    op.send? JSON.stringify messageId: messageId() or ''
                    console.log @, arguments
                    return
            keepalive:
                message: (c) ->
                    ping = ping: c
                    ping.messageId = '' if not messageId()
                    JSON.stringify ping
                interval: 60000
        ).done (o) ->
            op = o
            # 既読状態が空の時は、メッセージIDをリクエストする
            op.send JSON.stringify messageId: messageId() or ''
            $deferred.resolve op
            console.log @, arguments
            return
        $deferred.promise()
