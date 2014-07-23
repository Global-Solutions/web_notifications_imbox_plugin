package jp.co.gsol.oss.notifications.impl.plugin.imbox;

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
            final String key, final String message) {
        final Map<String, String> m = JSON.decode(message);
        final String messageId = m.get("messageId");
        IMBoxMessageIdManager.messageId(key, Optional.of(messageId != null ? messageId : ""));
    }
}