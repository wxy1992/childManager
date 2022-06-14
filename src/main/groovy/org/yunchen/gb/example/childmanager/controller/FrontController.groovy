package org.yunchen.gb.example.childmanager.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

/**
 * 系统前台控制器
 */
@Controller
class FrontController {
    @RequestMapping(value = "/")
    public String index(HttpServletRequest request, Model model) {
        return "/front/index"
    }
}
