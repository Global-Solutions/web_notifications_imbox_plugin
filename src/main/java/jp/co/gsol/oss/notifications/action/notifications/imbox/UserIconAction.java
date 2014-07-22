package jp.co.gsol.oss.notifications.action.notifications.imbox;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import jp.co.gsol.oss.notifications.form.notifications.imbox.UserIconForm;
import jp.co.intra_mart.common.platform.log.Logger;
import jp.co.intra_mart.foundation.service.client.file.PublicStorage;
import jp.co.intra_mart.imbox.exception.IMBoxException;
import jp.co.intra_mart.imbox.model.User;
import jp.co.intra_mart.imbox.service.Services;
import jp.co.intra_mart.imbox.service.UserOperations;

import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.ResponseUtil;

public class UserIconAction {

    @Resource
    @ActionForm
    UserIconForm userIconForm;

    @Resource
    HttpServletResponse response;

    @Execute(validator = false, urlPattern = "{userCd}")
    public String index() throws IOException {
        if (!StringUtil.isEmpty(userIconForm.userCd)) {
            final UserOperations uo = Services.get(UserOperations.class);
            try {
                final User user = uo.getUser(userIconForm.userCd);
                if (user == null) return null;
                final String path = user.getAttachPath();
                final PublicStorage storage = new PublicStorage(path);
                try (final InputStream is = storage.open()) {
                    ResponseUtil.download(user.getAttachId(), is);
                }
            } catch (final IMBoxException | IOException e) {
                Logger.getLogger()
                .debug("Error occured while fetching icon. userCd: " + userIconForm.userCd, e);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        return null;
    }
}
