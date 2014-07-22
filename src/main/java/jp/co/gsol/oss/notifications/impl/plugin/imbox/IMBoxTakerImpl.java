package jp.co.gsol.oss.notifications.impl.plugin.imbox;

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
    }
}