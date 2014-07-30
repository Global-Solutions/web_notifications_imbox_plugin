(function() {
  var $, messageId, storageFactory;

  $ = jQuery;

  messageId = function() {};

  storageFactory = function(prefix, tenantId, userCd) {
    var key;
    key = "" + prefix + "." + tenantId + "." + userCd;
    return function(value) {
      if (value == null) {
        return localStorage[key];
      }
      return localStorage[key] = "" + value;
    };
  };

  this.imboxNotifications = {
    onLoadHandler: function(tenantId, userCd) {
      messageId = storageFactory('notifications.imbox', tenantId, userCd);
    },
    connect: function() {
      var $deferred, op;
      $deferred = $.Deferred();
      op = {};
      webSockets.connect({
        protocol: 'websocket-imbox-protocol',
        messageHandler: function(d) {
          var message;
          message = JSON.parse(d);
          if (!message.pong) {
            notifications.showNotification("" + message.postUserName + "@" + message.boxName, {
              options: {
                body: message.message,
                icon: message.iconId ? "notifications/imbox/userIcon/" + message.postUserCd + "?" + message.iconId : void 0
              }
            });
          }
          messageId(message.messageId);
          console.log(this, arguments, message);
        },
        reconnect: {
          done: function() {
            if (typeof op.send === "function") {
              op.send(JSON.stringify({
                messageId: messageId() || ''
              }));
            }
            console.log(this, arguments);
          }
        },
        keepalive: {
          message: function(c) {
            var ping;
            ping = {
              ping: c
            };
            if (!messageId()) {
              ping.messageId = '';
            }
            return JSON.stringify(ping);
          },
          interval: 60000
        }
      }).done(function(o) {
        op = o;
        op.send(JSON.stringify({
          messageId: messageId() || ''
        }));
        $deferred.resolve(op);
        console.log(this, arguments);
      });
      return $deferred.promise();
    }
  };

}).call(this);

//# sourceMappingURL=notifications-imbox.js.map
