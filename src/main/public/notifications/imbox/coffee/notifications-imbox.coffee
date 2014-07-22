$ = jQuery
messageId = (tenantId, userCd, id) ->
    return id if tenantId? or userCd?
    key = "imbox-#{tenantId}-#{userCd}"
    LocalStorage key, id if id?
    LocalStorage key

@imboxNotifications =
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
                        icon: "notifications/imbox/userIcon/#{message.postUserCd}"
                )
                console.log @, arguments, message
                return
            reconnect:
                done: ->
                    console.log @, arguments
                    return
            keepalive:
                message: (c) ->
                    m = 'ping:' + c
                    console.log m
                    m
                interval: 60000
            events:
                close: ->
                    console.log @
                    return
        ).done (op) ->
            #op.send JSON.stringify messageId: messageId
            $deferred.resolve op
            console.log @, arguments
            return
        $deferred.promise()
