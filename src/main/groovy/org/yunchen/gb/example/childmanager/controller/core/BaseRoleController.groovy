package org.yunchen.gb.example.childmanager.controller.core

import org.yunchen.gb.example.childmanager.domain.core.BaseRole
import org.yunchen.gb.example.childmanager.domain.core.BaseUserBaseRole
import org.yunchen.gb.example.childmanager.service.core.UtilService
import org.yunchen.gb.example.childmanager.service.core.BaseRoleService
import org.yunchen.gb.core.GbSpringUtils
import org.yunchen.gb.core.PageParams
import org.yunchen.gb.plugin.poi.ExcelWriteBuilder
import org.yunchen.gb.core.annotation.GbController
import org.springframework.ui.Model
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ResponseBody
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired


/**
 * 角色管理控制器
 */
@Slf4j
@GbController
class BaseRoleController {
    @Autowired
    private BaseRoleService baseRoleService
    @Autowired
    private UtilService utilService

    public void index() {

    }

    @ResponseBody
    public Map json(PageParams pageParams, String search) {
        Map map = new HashMap();
        Closure searchLogic = {
            if (search) {
                or {
                    like('authority', "%${search}%")
                    like('description', "%${search}%")
                }
            }
        }

        long count = baseRoleService.count(searchLogic)
        List list = baseRoleService.list(pageParams, searchLogic)

        map.total = count;
        map.rows = list;
        return map;
    }

    public void show(String id, Model model) {
        BaseRole baseRoleInstance = baseRoleService.read(id)
        model.addAttribute("baseRoleInstance", baseRoleInstance);
    }

    public void create(Model model) {
        BaseRole baseRoleInstance = baseRoleService.create()
        model.addAttribute("baseRoleInstance", baseRoleInstance);
    }

    @ResponseBody
    public Map save(BaseRole baseRoleInstance, Model model) {
        baseRoleInstance.id = baseRoleInstance.authority;
        Map map = new HashMap();
        if (baseRoleInstance == null) {
            notFound(map, "");
            return map;
        }

        if (baseRoleInstance.hasErrors()) {
            foundErrors(baseRoleInstance, map);
            return map;
        }

        if (!baseRoleService.save(baseRoleInstance)) {
            foundErrors(baseRoleInstance, map);
        } else {
            map.result = true;
            map.id = baseRoleInstance.id;
            String domainName = GbSpringUtils.getI18nMessage("baseRole.label");
            map.message = GbSpringUtils.getI18nMessage("default.created.message", [domainName, baseRoleInstance.id]);
        }
        return map;
    }

    public void edit(String id, Model model) {
        BaseRole baseRoleInstance = baseRoleService.read(id)
        model.addAttribute("baseRoleInstance", baseRoleInstance);
    }

    @ResponseBody
    public Map update(BaseRole baseRoleInstance, Long version) {
        Map map = new HashMap();
        if (baseRoleInstance == null) {
            notFound(map, "${baseRoleInstance.id}")
            return map
        }
        if (version != null) {
            if (baseRoleInstance.version > version) {
                map.result = false
                map.message = GbSpringUtils.getI18nMessage("default.optimistic.locking.failure", [baseRoleInstance.id]);
                return map;
            }
        }
        if (baseRoleInstance.hasErrors()) {
            foundErrors(baseRoleInstance, map);
            return map
        }
        boolean result = baseRoleService.save(baseRoleInstance)
        if (!result) {
            foundErrors(baseRoleInstance, map);
        } else {
            map.result = true;
            String domainName = GbSpringUtils.getI18nMessage("baseRole.label");
            map.message = GbSpringUtils.getI18nMessage("default.updated.message", [domainName, baseRoleInstance.id]);
        }
        return map;
    }

    @ResponseBody
    public Map delete(String id) {
        Map map = new HashMap();
        BaseRole baseRoleInstance = baseRoleService.get(id)
        if (baseRoleInstance == null) {
            notFound(map, "${id}");
            return map;
        }
        String domainId = baseRoleInstance.id;
        boolean result = baseRoleService.delete(baseRoleInstance)
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
    public Map deletes(String ids) {
        Map map = new HashMap();
        boolean result = baseRoleService.deletes(ids)
        map.result = result;
        String domainName = GbSpringUtils.getI18nMessage("baseRole.label");
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
                row(["id", "version", "info"]);
                baseRoleService.list(new PageParams(max: 100), {}).each { BaseRole baseRoleInstance ->
                    row([baseRoleInstance.id, baseRoleInstance.version, baseRoleInstance.toString()])
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
        String domainName = GbSpringUtils.getI18nMessage("baseRole.label");
        map.result = false;
        map.message = GbSpringUtils.getI18nMessage("default.not.found.message", [domainName, id]);
        return map;
    }

    protected Map foundErrors(BaseRole baseRoleInstance, Map map) {
        map.result = false;
        map.errors = utilService.collectionErrorMap(BaseRole, baseRoleInstance);
        return map;
    }
}
