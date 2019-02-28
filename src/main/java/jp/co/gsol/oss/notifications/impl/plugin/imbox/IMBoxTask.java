package jp.co.gsol.oss.notifications.impl.plugin.imbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.util.lang.StringUtil;


import com.google.common.base.Optional;

import net.arnx.jsonic.JSON;

import jp.co.gsol.oss.notifications.impl.AbstractWebSocketTask;
import jp.co.gsol.oss.notifications.logic.notifications.imbox.IMBoxMessageFilter;
import jp.co.intra_mart.common.platform.log.Logger;
import jp.co.intra_mart.foundation.context.Contexts;
import jp.co.intra_mart.foundation.context.model.AccountContext;
import jp.co.intra_mart.imbox.exception.IMBoxException;
import jp.co.intra_mart.imbox.model.Message;
import jp.co.intra_mart.imbox.model.Thread;
import jp.co.intra_mart.imbox.model.User;
import jp.co.intra_mart.imbox.service.MyBoxService;
import jp.co.intra_mart.imbox.service.Services;
import jp.co.intra_mart.imbox.service.UserOperations;

/**
 * IMBox protocol's push event handler.
 * @author Global solutions company limited
 */
public class IMBoxTask extends AbstractWebSocketTask {
    /** max times for waiting for last messageId.*/
    private static final int MAX_INIT_WAIT_COUNT = 5;
    /** current latest messageId.*/
    private String messageId = null;
    /** last latest messageID.*/
    private String lastMessageId = null;
    /** counter for wait for last messageId.*/
    private int initWaitingCount = 0;

    @Override
    protected List<String> processedMessage(final String key,
            final Map<String, String> param) {
        final List<String> messages = new ArrayList<>();
        final String count = param.get("waitingCount");
        final String lastMid = param.get("lastMessageId");
        if (!StringUtil.isEmpty(count))
            initWaitingCount = Integer.valueOf(count);
        if (lastMid == null && initWaitingCount < MAX_INIT_WAIT_COUNT)
            return messages;
        final AccountContext ac = Contexts.get(AccountContext.class);
        final MyBoxService mbs = Services.get(MyBoxService.class);
        final UserOperations uo = Services.get(UserOperations.class);
        String mid = null;
        try {
            for (Thread t : mbs.getLatestThreads(lastMid))
                for (Message m : t.getMessages()) {
                    mid = m.getMessageId();
                    if (!IMBoxMessageFilter.shouldPush(m, ac.getTimeZone()))
                        continue;
                    final Map<String, String> message = new HashMap<>();
                    message.put("boxName", m.getBoxName());
                    message.put("messageId", m.getMessageId());
                    message.put("postUserName", m.getPostUserName());
                    message.put("postUserCd", m.getPostUserCd());
                    message.put("message", m.getMessageText());
                    if (null != m.getPostUserDeleteFlag() && !m.getPostUserDeleteFlag().booleanValue()) {
                        final User user = uo.getUser(m.getPostUserCd());
                        if (user != null)
                            message.put("iconId", user.getAttachId());
                    }
                    messages.add(JSON.encode(message));
                }
        } catch (final IMBoxException e) {
            Logger.getLogger().error("event loop abort", e);
        }
        messageId = !StringUtil.isEmpty(mid) ? mid : lastMid;
        lastMessageId = !StringUtil.isEmpty(lastMid) ? lastMid : mid;
        return messages;
    }
    @Override
    protected Map<String, String> done(final String key, final boolean sent) {
        final String storeMessageId = !StringUtil.isEmpty(messageId) && sent
                ? messageId : !StringUtil.isEmpty(lastMessageId) ? lastMessageId : null;
            final Map<String, String> param = new HashMap<>();
        if (!StringUtil.isEmpty(storeMessageId)) {
            param.put("lastMessageId", storeMessageId);
            IMBoxMessageIdManager.messageId(key, Optional.of(storeMessageId));
        } else
            param.put("waitingCount", String.valueOf(initWaitingCount + 1));
        return param;
    }
    @Override
    protected Map<String, String> initialParam(final String key) {
        final Map<String, String> param = new HashMap<>();
        final Optional<String> mid = IMBoxMessageIdManager.messageId(key);
        if (mid.isPresent()) {
            param.put("lastMessageId", mid.get());
        }
        return param;
    }
}
