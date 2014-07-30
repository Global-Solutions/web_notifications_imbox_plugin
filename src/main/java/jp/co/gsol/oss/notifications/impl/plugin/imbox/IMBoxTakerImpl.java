package jp.co.gsol.oss.notifications.impl.plugin.imbox;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

import com.caucho.websocket.WebSocketContext;
import com.google.common.base.Optional;

import jp.co.gsol.oss.notifications.impl.AbstractTakerImpl;

public class IMBoxTakerImpl extends AbstractTakerImpl {
    @Override
    public Optional<String> processClass() {
        return Optional.of(IMBoxTask.class.getCanonicalName());
    }
    @Override
    public void onReadText(final WebSocketContext context,
            final String key, final String message) throws IOException {
        final Map<String, Object> m = JSON.decode(message);
        final Object ping = m.get("ping");
        final Object messageId = m.get("messageId");
        if (messageId != null) {
            if (ping != null) {
                final Optional<String> latestId = IMBoxMessageIdManager.messageId(key);
                if (latestId.isPresent()) {
                    final Map<String, Object> id = new HashMap<>();
                    id.put("messageId", latestId.get());
                    id.put("pong", ping);
                    final String pong = JSON.encode(id);
                    synchronized (context) {
                        final PrintWriter out = context.startTextMessage();
                        out.print(pong);
                        out.close();
                    }
                }
            } else {
                final String mid = messageId instanceof String ? (String) messageId : "";
                IMBoxMessageIdManager.messageId(key, Optional.of(mid));
            }
        }
    }
}