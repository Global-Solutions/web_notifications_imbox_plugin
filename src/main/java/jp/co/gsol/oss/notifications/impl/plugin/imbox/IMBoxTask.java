package jp.co.gsol.oss.notifications.impl.plugin.imbox;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import jp.co.gsol.oss.notifications.impl.AbstractWebSocketTask;
import jp.co.intra_mart.foundation.context.Contexts;
import jp.co.intra_mart.foundation.context.model.AccountContext;
import jp.co.intra_mart.foundation.i18n.datetime.DateTime;
import jp.co.intra_mart.imbox.exception.IMBoxException;
import jp.co.intra_mart.imbox.model.Message;
import jp.co.intra_mart.imbox.model.Thread;
import jp.co.intra_mart.imbox.service.MyBoxService;
import jp.co.intra_mart.imbox.service.Services;

public class IMBoxTask extends AbstractWebSocketTask {

    private String messageId = null;
    private String lastMessageId = null;

    @Override
    protected List<String> processedMessage(final String key,
            final Map<String, String> param) {
        // TODO 自動生成されたメソッド・スタブ
        //if (param != null && Long.valueOf(((String) param.get("lastTime"))) + 10000 > System.currentTimeMillis())
        //    return Optional.absent();
        final AccountContext ac = Contexts.get(AccountContext.class);
        final MyBoxService mbs = Services.get(MyBoxService.class);
        final String lastMid = param.get("lastMessageId");
        final Date today = DateTime.now(ac.getTimeZone()).withTime(0, 0, 0).getDate();
        System.out.println("uc:" + ac.getUserCd() + " mid:" + lastMid);
        final List<String> messages = new ArrayList<>();
        String mid = null;
        try {
            for (Thread t : mbs.getLatestThreads(lastMid))
                for (Message m : t.getMessages()) {
                    mid = m.getMessageId();
                    if (lastMid == null && m.getPostDate().before(today))
                        continue;
                    final Map<String, String> message = new HashMap<>();
                    message.put("boxName", m.getBoxName());
                    message.put("messageId", m.getMessageId());
                    message.put("postUserName", m.getPostUserName());
                    message.put("postUserCd", m.getPostUserCd());
                    message.put("message", m.getMessageText());
                    messages.add(JSON.encode(message));
                }
        } catch (IMBoxException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        }
        messageId = mid != null ? mid : lastMid;
        lastMessageId = lastMid != null ? lastMid : mid;
        return messages;
    }
    @Override
    protected Map<String, String> done(final String key, final boolean sent) {
        // TODO 自動生成されたメソッド・スタブ
        Map<String, String> param = new HashMap<>();
        if (messageId != null && sent)
            param.put("lastMessageId", messageId);
        else if (lastMessageId != null)
            param.put("lastMessageId", lastMessageId);
        return param;
    }
    @Override
    protected Map<String, String> initialParam(final String key) {
        final Map<String, String> param = new HashMap<>();
        return param;
    }
}
