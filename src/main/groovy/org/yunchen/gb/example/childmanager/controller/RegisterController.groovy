package org.yunchen.gb.example.childmanager.controller

import org.yunchen.gb.example.childmanager.domain.core.BaseRole
import org.yunchen.gb.example.childmanager.domain.core.BaseUser
import org.yunchen.gb.example.childmanager.domain.core.BaseUserBaseRole
import org.yunchen.gb.example.childmanager.service.RegisterService
import org.yunchen.gb.plugin.springsecurity.GbSpringSecurityService
import org.yunchen.gb.core.annotation.GbController
import org.yunchen.gb.core.GbSpringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

/**
 * 系统注册控制器
 */


@GbController
class RegisterController {
    @Autowired
    private RegisterService registerService
    @Autowired
    private GbSpringSecurityService gbSpringSecurityService

    public String index(HttpServletRequest request, Model model) {
        if (gbSpringSecurityService.isLoggedIn()) {
            return "redirect:${GbSpringUtils.getConfiginfo('gb.springsecurity.successHandler.defaultTargetUrl')}";
        } else {
            return "/register/index"
        }
    }

    @ResponseBody
    public Map onlineReg(BaseUser baseUser, HttpServletRequest request) {
        Map map = new HashMap();
        if (gbSpringSecurityService.isLoggedIn()) {
            map.result = false;
            map.message = "用户已登录系统，无法注册新帐号";
        } else {
            if (request.getParameter("password") != request.getParameter("repassword")) {
                map.result = false;
                map.message = "确认密码与密码输入不相同";
            } else {
                baseUser = registerService.register(baseUser)
                if (baseUser.id && !baseUser.hasErrors()) {
                    map.result = true;
                    map.message = "注册成功";
                } else {
                    map.result = false;
                    Map errors = new HashMap();
                    baseUser.errors.allErrors.each { FieldError error ->
                        String message = GbSpringUtils.getI18nMessage("register.${error.field}.${error.code}", error.arguments.toList(), error.defaultMessage);
                        errors.put(error.field, message);
                    }
                    map.errors = errors;
                    map.message = "注册失败";
                }
            }
        }
        return map;
    }
}

