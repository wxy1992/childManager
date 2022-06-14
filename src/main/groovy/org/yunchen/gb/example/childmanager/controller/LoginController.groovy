package org.yunchen.gb.example.childmanager.controller

import org.yunchen.gb.core.GbSpringUtils
import org.yunchen.gb.plugin.springsecurity.GbSpringSecurityService
import org.yunchen.gb.plugin.springsecurity.GbSpringSecurityUtils
import org.yunchen.gb.plugin.springsecurity.captcha.CaptchaVerificationFailedException
import org.yunchen.gb.core.annotation.GbController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.session.SessionAuthenticationException
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/**
 * 系统登录控制器
 */

@GbController
class LoginController {
    @Autowired
    MessageSource messageSource
    @Autowired
    GbSpringSecurityService gbSpringSecurityService

    public void concurrentSession(HttpServletRequest request, HttpServletResponse response, Model model) {
        String msg = GbSpringUtils.getI18nMessage("springSecurity.errors.login.concurrent.session");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        if (GbSpringSecurityUtils.isAjax(request)) {
            response.sendError(605, msg);
        } else {
            response.writer.println(msg);
        }
    }

    public void alreadyLogin(HttpServletRequest request, HttpServletResponse response) {
        String url = request.contextPath + GbSpringUtils.getConfiginfo("gb.springsecurity.successHandler.defaultTargetUrl");
        String msg = GbSpringUtils.getI18nMessage("springSecurity.errors.login.already", [url]);
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        if (GbSpringSecurityUtils.isAjax(request)) {
            response.sendError(606, msg);
        } else {
            response.writer.println(msg);
        }
    }

    public String auth(HttpServletRequest request, HttpServletResponse response, Model model) {
        if (gbSpringSecurityService.isLoggedIn()) {
            return "redirect:${GbSpringUtils.getConfiginfo('gb.springsecurity.successHandler.defaultTargetUrl')}";
        } else {
            if (gbSpringSecurityService.isAjax(request)) {
                response.writer.println("{result:false,message:'need login'}")
                return null
            }
            nocache(response)
            String postUrl = "${request.contextPath}${GbSpringUtils.getConfiginfo('gb.springsecurity.apf.filterProcessesUrl')}"
            model.addAttribute("postUrl", postUrl);
            return "/login/auth";
        }
    }


    @ResponseBody
    public Map ajaxSuccess() {
        Map map = [:];
        map.result = true;
        map.url = GbSpringUtils.getConfiginfo("gb.springsecurity.successHandler.defaultTargetUrl");
        return map;
    }

    @ResponseBody
    public Map authajaxfail(javax.servlet.http.HttpServletRequest request, HttpServletResponse response, Model model) {
        Map map = [:];
        map.result = false;
        map.message = getExceptionMessage(request.session);
        return map;
    }

    public String authfail(javax.servlet.http.HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("message", getExceptionMessage(request.session));
        String postUrl = "${request.contextPath}${GbSpringUtils.getConfiginfo('gb.springsecurity.apf.filterProcessesUrl')}"
        model.addAttribute("postUrl", postUrl);
        return "/login/auth";
    }

    public String denied(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {
        response.setStatus(403);
        return "/login/denied";
    }

    @ResponseBody
    public Map ajaxDenied(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {
        response.setStatus(403);
        Map map = [:];
        map.result = false;
        map.message = GbSpringUtils.getI18nMessage("springSecurity.denied.message");
        return map;
    }

    private String getExceptionMessage(HttpSession httpSession) {
        String msg = '';
        def exception = httpSession.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)
        if (exception) {
            if (exception instanceof AccountExpiredException) {
                msg = messageSource.getMessage("springSecurity.errors.login.expired", [].toArray(), LocaleContextHolder.getLocale())
            } else if (exception instanceof CredentialsExpiredException) {
                msg = messageSource.getMessage("springSecurity.errors.login.passwordExpired", [].toArray(), LocaleContextHolder.getLocale())
            } else if (exception instanceof DisabledException) {
                msg = messageSource.getMessage("springSecurity.errors.login.disabled", [].toArray(), LocaleContextHolder.getLocale())
            } else if (exception instanceof LockedException) {
                msg = messageSource.getMessage("springSecurity.errors.login.locked", [].toArray(), LocaleContextHolder.getLocale())
            } else if (exception instanceof SessionAuthenticationException) {
                msg = messageSource.getMessage("springSecurity.errors.login.maximum", [].toArray(), LocaleContextHolder.getLocale())
            } else if (exception instanceof CaptchaVerificationFailedException) {
                msg = messageSource.getMessage("springSecurity.errors.captcha.invalid", [].toArray(), LocaleContextHolder.getLocale())
            } else {
                //org.springframework.security.authentication.BadCredentialsException: Bad credentials
                //用户名或密码不正确
                msg = messageSource.getMessage("springSecurity.errors.login.fail", [].toArray(), LocaleContextHolder.getLocale())
            }
        }
        return msg;
    }
    /** cache controls */
    private void nocache(javax.servlet.http.HttpServletResponse response) {
        response.setHeader('Cache-Control', 'no-cache') // HTTP 1.1
        response.addDateHeader('Expires', 0)
        response.setDateHeader('max-age', 0)
        response.setIntHeader('Expires', -1) //prevents caching at the proxy server
        response.addHeader('cache-Control', 'private') //IE5.x only
    }
}
