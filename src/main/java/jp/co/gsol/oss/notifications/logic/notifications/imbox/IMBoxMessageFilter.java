package jp.co.gsol.oss.notifications.logic.notifications.imbox;

import java.util.Date;
import java.util.TimeZone;

import jp.co.intra_mart.foundation.i18n.datetime.DateTime;
import jp.co.intra_mart.imbox.model.Message;

/**
 * IMBox message's filter.
 * @author Global solutions company limited
 *
 */
public final class IMBoxMessageFilter {

    /**
     * constructor.
     */
    private IMBoxMessageFilter() { };

    /**
     * judge whether message should be pushed.
     * @param m message
     * @param t time zone
     * @return whether message should be pushed
     */
    public static boolean shouldPush(final Message m, final TimeZone t) {
        if (m == null || t == null)
            throw new IllegalArgumentException("message and time zone must not be null.");
        final Date today = DateTime.now(t).withTime(0, 0, 0).getDate();
        return m.getPostDate().after(today);
    }
}