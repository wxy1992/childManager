package org.yunchen.gb.example.childmanager.controller.core

import org.yunchen.gb.example.childmanager.domain.core.BaseRole;
import org.yunchen.gb.example.childmanager.domain.core.BaseUser;
import org.yunchen.gb.example.childmanager.domain.core.BaseUserBaseRole;
import org.yunchen.gb.example.childmanager.domain.core.SystemLoginRecord;
import org.yunchen.gb.example.childmanager.service.core.UtilService
import org.yunchen.gb.example.childmanager.service.core.BaseUserBaseRoleService
import org.yunchen.gb.example.childmanager.service.core.BaseUserService
import org.yunchen.gb.core.GbSpringUtils
import org.yunchen.gb.core.PageParams
import org.yunchen.gb.plugin.poi.ExcelWriteBuilder
import org.yunchen.gb.core.annotation.GbController
import org.springframework.ui.Model;
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import groovy.util.logging.Slf4j
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired

/**
 * 用户管理控制器
 */
@Slf4j
@GbController
class BaseUserController {
    @Autowired
    private BaseUserService baseUserService
    @Autowired
    private BaseUserBaseRoleService baseUserBaseRoleService
    @Autowired
    private UtilService utilService

    public void index(Model model) {
        model.addAttribute("roles", BaseRole.list(['sort': 'id', 'order': 'asc']));
    }

    @ResponseBody
    public Map json(PageParams pageParams, String search, String role) {
        Map map = new HashMap();
        if (!role) {
            Closure searchLogic = {
                if (search) {
                    or {
                        like('username', "%${search}%")
                        like('realname', "%${search}%")
                    }
                }
            }
            long count = baseUserService.count(searchLogic)
            List list = baseUserService.list(pageParams, searchLogic)
            map.total = count;
            map.rows = list;
        } else {
            Closure searchLogic = {
                createAlias("baseRole", "roleDomain")
                eq("roleDomain.authority", role);
                if (search) {
                    createAlias("baseUser", "userDomain")
                    or {
                        like('userDomain.username', "%${search}%")
                        like('userDomain.realname', "%${search}%")
                    }
                }
            }
            long count = baseUserBaseRoleService.count(searchLogic)
            List list = baseUserBaseRoleService.list(pageParams, searchLogic)
            map.total = count;
            map.rows = list.baseUser;
        }

        return map;
    }

    public void show(long id, Model model) {
        BaseUser baseUserInstance = baseUserService.read(id)
        model.addAttribute("baseUserInstance", baseUserInstance);
    }

    public void create(Model model) {
        BaseUser baseUserInstance = baseUserService.create()
        model.addAttribute("baseUserInstance", baseUserInstance);
        model.addAttribute("baseUserRoles", []);
        model.addAttribute("booleanVals", [true, false]);
        model.addAttribute("roles", BaseRole.list(['sort': 'id', 'order': 'asc']));
    }

    @ResponseBody
    public Map save(HttpServletRequest request, BaseUser baseUserInstance) {
        Map map = new HashMap();
        if (baseUserInstance == null) {
            notFound(map, "");
            return map;
        }

        if (baseUserInstance.hasErrors()) {
            foundErrors(baseUserInstance, map);
            return map;
        }

        if (!baseUserService.save(baseUserInstance)) {
            foundErrors(baseUserInstance, map);
        } else {
            if (request.getParameterValues("authorities") && request.getParameterValues("authorities").size() > 0) {
                baseUserBaseRoleService.authRoleToUser(baseUserInstance, request.getParameterValues("authorities").toList())
            }
            map.result = true;
            map.id = baseUserInstance.id;
            map.password = baseUserInstance.password;
            String domainName = GbSpringUtils.getI18nMessage("baseUser.label");
            map.message = GbSpringUtils.getI18nMessage("default.created.message", [domainName, baseUserInstance.id]);
        }
        return map;
    }

    public void edit(long id, Model model) {
        BaseUser baseUserInstance = baseUserService.read(id)
        model.addAttribute("baseUserInstance", baseUserInstance);
        model.addAttribute("baseUserRoles", baseUserInstance.authorities*.id);
        model.addAttribute("booleanVals", [true, false]);
        model.addAttribute("roles", BaseRole.list(['sort': 'id', 'order': 'asc']));
    }

    @ResponseBody
    public Map update(HttpServletRequest request, BaseUser baseUserInstance, Long version) {
        Map map = new HashMap();
        if (baseUserInstance == null) {
            notFound(map, "${baseUserInstance.id}")
            return map
        }
        if (version != null) {
            if (baseUserInstance.version > version) {
                map.result = false
                map.message = GbSpringUtils.getI18nMessage("default.optimistic.locking.failure", [baseUserInstance.id]);
                return map;
            }
        }
        if (baseUserInstance.hasErrors()) {
            foundErrors(baseUserInstance, map);
            return map
        }
        boolean result = baseUserService.save(baseUserInstance)
        if (!result) {
            foundErrors(baseUserInstance, map);
        } else {
            if (request.getParameterValues("authorities") && request.getParameterValues("authorities").size() > 0) {
                baseUserBaseRoleService.authRoleToUser(baseUserInstance, request.getParameterValues("authorities").toList())
            }
            map.result = true;
            map.password = baseUserInstance.password;
            String domainName = GbSpringUtils.getI18nMessage("baseUser.label");
            map.message = GbSpringUtils.getI18nMessage("default.updated.message", [domainName, baseUserInstance.id]);
        }
        return map;
    }

    @ResponseBody
    public Map delete(long id) {
        Map map = new HashMap();
        BaseUser baseUserInstance = BaseUser.get(id);
        if (baseUserInstance == null) {
            notFound(map, "${id}");
            return map;
        }
        String domainId = baseUserInstance.id;
        boolean result = baseUserService.delete(baseUserInstance)
        map.result = result;
        String domainName = GbSpringUtils.getI18nMessage("baseRole.label");
        if (result) {
            map.message = GbSpringUtils.getI18nMessage("default.deleted.message", [domainName, domainId]);
        } else {
            map.message = GbSpringUtils.getI18nMessage("default.not.deleted.message", [domainName, domainId]);
        }
        return map;
    }

    @ResponseBody
    public Map deletes(String ids, Model model) {
        Map map = new HashMap();
        String domainName = GbSpringUtils.getI18nMessage("baseUser.label");
        boolean result = baseUserService.deletes(ids)
        map.result = result;
        if (result) {
            map.message = GbSpringUtils.getI18nMessage("default.deleted.message", [domainName, ids]);
        } else {
            map.message = GbSpringUtils.getI18nMessage("default.not.deleted.message", [domainName, ids]);
        }
        return map;
    }

    public void download(javax.servlet.http.HttpServletResponse response) {
        File tempFile = File.createTempFile("_tmp", ".xls")
        //开发模式获取模板文件
        URL templateFileResource = ClassLoader.getResource("/templates/tools/empty.xls")
        if (templateFileResource) {
            tempFile.bytes = new File(templateFileResource.toURI()).bytes
        } else {
            //jar包模式下获取模板文件
            tempFile.bytes = this.class.classLoader.getResourceAsStream("/templates/tools/empty.xls")?.bytes
        }
        ExcelWriteBuilder excelWriteBuilder = new ExcelWriteBuilder(tempFile);
        excelWriteBuilder.workbook {
            sheet("sheet1") {
                row(["id", "用户名", "真实姓名", "是否启用"]);
                baseUserService.list(new PageParams(max: 100), {}).each { BaseUser baseUserInstance ->
                    row([baseUserInstance.id, baseUserInstance.username, baseUserInstance.realname, baseUserInstance.enabled])
                }
            }
        }
        byte[] excelBytes = excelWriteBuilder.download();
        try {
            response.setContentType("application/octet-stream;charset=GBK")
            response.addHeader("Content-Disposition", 'attachment; filename="' + new String("文件导出Excel.xls".getBytes("GBK"), "ISO8859-1") + '"');
            OutputStream out = response.outputStream;
            out.write(excelBytes);
            out.close();
        } catch (e) {
            log.error(e.message);
            response.writer.print(e.message);
        } finally {
            tempFile.delete()
        }
    }

    protected Map notFound(Map map, String id) {
        String domainName = GbSpringUtils.getI18nMessage("baseUser.label");
        map.result = false;
        map.message = GbSpringUtils.getI18nMessage("default.not.found.message", [domainName, id]);
        return map;
    }

    protected Map foundErrors(BaseUser baseUserInstance, Map map) {
        map.result = false;
        map.errors = utilService.collectionErrorMap(BaseUser, baseUserInstance);
        return map;
    }
}

