package jp.co.gsol.oss.notifications.impl.plugin.imbox;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Optional;

/**
 * last latest messageId collector.
 * @author Global solutions company limited
 */
public final class IMBoxMessageIdManager {
    /** .*/
    private IMBoxMessageIdManager() { }
    /** last latest messageIds.*/
    private static final Map<String, String> MESSAGE_IDS = new ConcurrentHashMap<>();

    /**
     * key's last latest messageId.
     * @param key identification for the session
     * @return last latest messageId
     */
    public static Optional<String> messageId(final String key) {
        return messageId(key, Optional.<String>absent());
    }

    /**
     * set or get key's last latest messageId.
     * @param key identification for the session
     * @param messageId latest messageId
     * @return latest messageId
     */
    public static Optional<String> messageId(final String key, final Optional<String> messageId) {
        if (!messageId.isPresent())
            return Optional.fromNullable(MESSAGE_IDS.get(key));
        MESSAGE_IDS.put(key, messageId.get());
        return Optional.of(messageId.get());
    }
}