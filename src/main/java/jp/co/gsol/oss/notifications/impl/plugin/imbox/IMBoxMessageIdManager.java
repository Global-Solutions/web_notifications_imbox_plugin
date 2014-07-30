package jp.co.gsol.oss.notifications.impl.plugin.imbox;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import com.google.common.base.Optional;

public class IMBoxMessageIdManager {
    private static final Map<String, String> messageIds = new ConcurrentHashMap<>();

    static public Optional<String> messageId(final String key) {
        return messageId(key, Optional.<String>absent());
    }

    static public Optional<String> messageId(final String key, final Optional<String> messageId) {
        if (!messageId.isPresent())
            return Optional.fromNullable(messageIds.get(key));
        messageIds.put(key, messageId.get());
        return Optional.of(messageId.get());
    }
}