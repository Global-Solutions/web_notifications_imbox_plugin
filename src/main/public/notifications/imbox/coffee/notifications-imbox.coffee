$ = jQuery
messageId = -> return

storageFactory = (prefix, tenantId, userCd) ->
    key = "#{prefix}.#{tenantId}.#{userCd}"
    (value) ->
        if not value?
            return localStorage[key]
        localStorage[key] = "#{value}"
        
@imboxNotifications =
    onLoadHandler: (tenantId, userCd) ->
        messageId = storageFactory 'notifications.imbox', tenantId, userCd
        return

    connect: ->
        $deferred = $.Deferred()
        webSockets.connect(
            protocol: 'websocket-imbox-protocol'
            messageHandler: (d) ->
                message = JSON.parse d
                notifications.showNotification(
                    "#{message.postUserName}@#{message.boxName}",
                    options:
                        body: message.message
                        icon: "notifications/imbox/userIcon/#{message.postUserCd}?#{message.iconId}" \
                              if message.iconId
                )
                messageId message.messageId
                console.log @, arguments, message
                return
            reconnect:
                done: ->
                    console.log @, arguments
                    return
            keepalive:
                message: (c) ->
                    "ping: #{c}"
                interval: 60000
        ).done (op) ->
            op.send JSON.stringify messageId: messageId() or ""
            $deferred.resolve op
            console.log @, arguments
            return
        $deferred.promise()
