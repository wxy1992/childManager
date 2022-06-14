/**
 * 系统登出控制器
 */
package org.yunchen.gb.example.childmanager.controller

import org.yunchen.gb.example.childmanager.domain.core.SystemLoginRecord
import org.yunchen.gb.core.annotation.GbController
import org.yunchen.gb.core.GbSpringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.session.SessionRegistry
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 系统登出控制器
 */
@Transactional
@GbController
class LogoutController {
    @Autowired
    SessionRegistry sessionRegistry
    /**
     * Index action. Redirects to the Spring security logout uri.
     */
    @RequestMapping(value = ["", "index"])
    def index(HttpServletRequest request, HttpServletResponse response) {

        //取消register session
        sessionRegistry.removeSessionInformation(request.session.id);
        SystemLoginRecord systemLoginRecord = SystemLoginRecord.findBySessionId(request.session.id);
        if (systemLoginRecord) {
            systemLoginRecord.logoutTime = new Date();
            systemLoginRecord.save(flush: true);
        }
        response.sendRedirect(request.contextPath + GbSpringUtils.getConfiginfo("gb.springsecurity.logout.filterProcessesUrl"))
        // '/logoff'
        response.flushBuffer()
    }
}
