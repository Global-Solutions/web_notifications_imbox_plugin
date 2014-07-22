(function() {
  var $, messageId;

  $ = jQuery;

  messageId = function(tenantId, userCd, id) {
    var key;
    if ((tenantId != null) || (userCd != null)) {
      return id;
    }
    key = "imbox-" + tenantId + "-" + userCd;
    if (id != null) {
      LocalStorage(key, id);
    }
    return LocalStorage(key);
  };

  this.imboxNotifications = {
    connect: function() {
      var $deferred;
      $deferred = $.Deferred();
      webSockets.connect({
        protocol: 'websocket-imbox-protocol',
        messageHandler: function(d) {
          var message;
          message = JSON.parse(d);
          notifications.showNotification("" + message.postUserName + "@" + message.boxName, {
            options: {
              body: message.message,
              icon: "notifications/imbox/userIcon/" + message.postUserCd
            }
          });
          console.log(this, arguments, message);
        },
        reconnect: {
          done: function() {
            console.log(this, arguments);
          }
        },
        keepalive: {
          message: function(c) {
            var m;
            m = 'ping:' + c;
            console.log(m);
            return m;
          },
          interval: 60000
        },
        events: {
          close: function() {
            console.log(this);
          }
        }
      }).done(function(op) {
        $deferred.resolve(op);
        console.log(this, arguments);
      });
      return $deferred.promise();
    }
  };

}).call(this);

//# sourceMappingURL=notifications-imbox.js.map
