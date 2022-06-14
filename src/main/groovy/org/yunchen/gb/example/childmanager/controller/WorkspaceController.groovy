package org.yunchen.gb.example.childmanager.controller;

import org.yunchen.gb.example.childmanager.domain.core.*;
import org.yunchen.gb.example.childmanager.domain.core.menu.*;
import org.yunchen.gb.plugin.springsecurity.GbSpringSecurityService
import org.yunchen.gb.plugin.springsecurity.GbSpringSecurityUtils;
import org.yunchen.gb.core.annotation.GbController
import org.yunchen.gb.plugin.poi.ExcelReadBuilder
import org.yunchen.gb.core.GbSpringUtils
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.session.SessionRegistry
import javax.servlet.http.HttpServletRequest
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Transactional
@GbController
public class WorkspaceController {
    @Autowired
    SessionRegistry sessionRegistry
    @Autowired
    GbSpringSecurityService gbSpringSecurityService

    public void index(HttpServletRequest request, Model model) {
        //在线人数统计
/*        log.info( sessionRegistry.allPrincipals*.username);
        log.info(GbSpringSecurityUtils.getPrincipalAuthorities());
        log.info(GbSpringSecurityUtils.ifAnyGranted("ROLE_USER,ROLE_ADMIN"));
        log.info(GbSpringSecurityUtils.ifAllGranted("ROLE_USER,ROLE_ADMIN"));
        log.info(GbSpringSecurityUtils.ifNotGranted("ROLE_USER,ROLE_ADMIN"));
        */
        List list = new ArrayList<Map>();
        if (GbSpringUtils.getConfiginfo("spring.profiles.active").equals("development")) {
            Map map = new HashMap<String, String>();
            map.put("数据库管理工具", request.getContextPath() + GbSpringUtils.getConfiginfo("spring.h2.console.path"));
            list.add(map);
            Map map1 = new HashMap<String, String>();
            map1.put("代码生成工具", request.getContextPath() + "/webconsole/index");
            list.add(map1);
        }
        model.addAttribute("tools", list);
    }

    public void profile(Model model) {
        model.addAttribute("currentUser", BaseUser.read(gbSpringSecurityService.principal.id));
    }


    @ResponseBody
    public Map roleMenu(HttpServletRequest request, Model model) {
        Map map = [:];
        List currentRoleMenuIds = GbSpringSecurityUtils.getPrincipalAuthorities().collect {
            (BaseRole.read(it.authority).menuItems.collect { it.id }).join(',')
        }.join(',').tokenize(',').collect { it.toLong() };
        String str = "";
        List allMenu = MenuItem.list(['sort': 'sequenceNum', 'order': 'asc'])
        str = assembleRoleMenu(request, str, null, allMenu, currentRoleMenuIds);
        map.menu = str;
        return map;
    }

    private String assembleRoleMenu(HttpServletRequest request, String menuStr, MenuItem nodeMenu, List allMenu, List currentRoleMenuIds) {
        if (!nodeMenu) {
            allMenu.findAll { it.parent == null }.each { MenuItem oneMenu ->
                menuStr = processOneMenuString(request, menuStr, oneMenu, allMenu, currentRoleMenuIds)
            }
        } else {
            List childrenMenu = allMenu.findAll { it.parentId == nodeMenu.id }
            if (childrenMenu.size() > 0) {
                childrenMenu.each { MenuItem oneChildMenu ->
                    menuStr = processOneMenuString(request, menuStr, oneChildMenu, allMenu, currentRoleMenuIds)
                }
            }
        }
        return menuStr;
    }

    private String processOneMenuString(HttpServletRequest request, String menuStr, MenuItem oneChildMenu, List allMenu, List currentRoleMenuIds) {
        if (currentRoleMenuIds.contains(oneChildMenu.id)) {
            if (allMenu.findAll { it.parentId == oneChildMenu.id }.size() > 0) {
                //menuStr += "<a class=\"collapse-item\" href=\"${oneChildMenu.url ? (request.contextPath + oneChildMenu.url) : '#'}\">${oneChildMenu.name}</a>";
                menuStr = assembleRoleMenu(request, menuStr, oneChildMenu, allMenu, currentRoleMenuIds);
            } else {
                if (oneChildMenu.url) {
                    menuStr += "<a class='collapse-item' href='${request.contextPath + oneChildMenu.url}'>${oneChildMenu.name}</a>";
                }
                menuStr = assembleRoleMenu(request, menuStr, oneChildMenu, allMenu, currentRoleMenuIds);
            }
        }
        return menuStr
    }

    //演示ajax中excel文件上传
    @ResponseBody
    public Map upload(javax.servlet.http.HttpServletRequest request, Model model) {
        Map map = new HashMap();
        MultipartFile file = request.getFile('excelImport');
        if (file && !file?.empty && file.originalFilename.toLowerCase().endsWith(".xls")) {
            try {
                new ExcelReadBuilder(2003, file.bytes).eachLine([sheet: 'sheet1', labels: true]) {
                    if (it.rowNum > 2) {
                        println "${it.rowNum},${cell(0)},${cell(1)},${cell(2)},${cell(3)}......"
                    }
                }
                map.result = true;
            } catch (e) {
                map.result = false;
                map.message = e.message;
            }
        } else {
            map.result = false;
            map.message = "file is empty!";
        }
        return map;
    }
}
