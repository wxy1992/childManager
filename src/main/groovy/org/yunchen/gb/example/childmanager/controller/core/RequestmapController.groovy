package org.yunchen.gb.example.childmanager.controller.core

import org.yunchen.gb.example.childmanager.domain.core.BaseRole
import org.yunchen.gb.example.childmanager.domain.core.Requestmap
import org.yunchen.gb.example.childmanager.service.core.UtilService
import org.yunchen.gb.example.childmanager.service.core.RequestmapService
import org.yunchen.gb.core.GbSpringUtils
import org.yunchen.gb.core.PageParams
import org.yunchen.gb.plugin.poi.ExcelWriteBuilder
import org.yunchen.gb.plugin.springsecurity.GbSpringSecurityService
import org.yunchen.gb.core.annotation.GbController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ResponseBody
import groovy.util.logging.Slf4j
import org.springframework.transaction.annotation.Transactional

/**
 * 访问控制控制器
 */
@Slf4j
@Transactional
@GbController
class RequestmapController {
    @Autowired
    private GbSpringSecurityService gbSpringSecurityService
    @Autowired
    private RequestmapService requestmapService
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
                    like('url', "%${search}%")
                    like('configAttribute', "%${search}%")
                }
            }
        }
        long count = requestmapService.count(searchLogic)
        List list = requestmapService.list(pageParams, searchLogic)
        map.total = count;
        map.rows = list;
        return map;
    }

    public void show(long id, Model model) {
        Requestmap requestmapInstance = requestmapService.read(id)
        model.addAttribute("requestmapInstance", requestmapInstance);
    }

    public void create(Model model) {
        Requestmap requestmapInstance = requestmapService.create()
        model.addAttribute("requestmapInstance", requestmapInstance);
        model.addAttribute("roles", BaseRole.list(['sort': 'id', 'order': 'asc']));
        model.addAttribute("authorityRoles", []);
        List authorityTypeSource = [];
        authorityTypeSource << [id: 'permitAll', label: '允许全部访问', disabled: ''];
        authorityTypeSource << [id: 'isAnonymous()', label: '只允许匿名访问', disabled: ''];
        authorityTypeSource << [id: 'isFullyAuthenticated()', label: '允许登录后访问', disabled: ''];
        authorityTypeSource << [id: 'hasAnyRole', label: '允许角色访问', disabled: ''];
        authorityTypeSource << [id: 'denyAll', label: '不允许访问', disabled: 'disabled'];
        model.addAttribute("authorityTypeSource", authorityTypeSource);
    }

    @ResponseBody
    public Map save(Requestmap requestmapInstance) {
        Map map = new HashMap();
        if (requestmapInstance == null) {
            notFound(map);
            return map;
        }

        if (requestmapInstance.hasErrors()) {
            foundErrors(requestmapInstance, map);
            return map;
        }
        if (requestmapInstance.authorityType == 'hasAnyRole') {
            if (requestmapInstance.authorityRole) {
                requestmapInstance.configAttribute = "hasAnyRole('${requestmapInstance.authorityRole}')";
            }
        } else {
            if (requestmapInstance.authorityType) {
                requestmapInstance.configAttribute = requestmapInstance.authorityType;
            }
        }
        if (!requestmapService.save(requestmapInstance)) {
            foundErrors(requestmapInstance, map);
        } else {
            //清除ACL访问控制缓存
            gbSpringSecurityService.clearCachedRequestmaps();
            map.result = true;
            map.id = requestmapInstance.id;
            map.message = "保存成功";
        }
        return map;
    }

    public void edit(long id, Model model) {
        Requestmap requestmapInstance = requestmapService.read(id)
        model.addAttribute("requestmapInstance", requestmapInstance);
        model.addAttribute("roles", BaseRole.list(['sort': 'id', 'order': 'asc']));
        model.addAttribute("authorityRoles", requestmapInstance.authorityRole?.tokenize(",") ?: []);
        List authorityTypeSource = [];
        authorityTypeSource << [id: 'permitAll', label: '允许全部访问', disabled: ''];
        authorityTypeSource << [id: 'isAnonymous()', label: '只允许匿名访问', disabled: ''];
        authorityTypeSource << [id: 'isFullyAuthenticated()', label: '允许登录后访问', disabled: ''];
        authorityTypeSource << [id: 'hasAnyRole', label: '允许角色访问', disabled: ''];
        authorityTypeSource << [id: 'denyAll', label: '不允许访问', disabled: 'disabled'];
        model.addAttribute("authorityTypeSource", authorityTypeSource);
    }

    @ResponseBody
    public Map update(Requestmap requestmapInstance, Long version) {
        Map map = new HashMap();
        if (requestmapInstance == null) {
            notFound(map)
            return map
        }
        if (version != null) {
            if (requestmapInstance.version > version) {
                map.result = false
                map.message = "数据已被其他人修改,请重新加载查看";
                return map
            }
        }
        if (requestmapInstance.hasErrors()) {
            foundErrors(requestmapInstance, map);
            return map
        }
        if (requestmapInstance.authorityType == 'hasAnyRole') {
            if (requestmapInstance.authorityRole) {
                requestmapInstance.configAttribute = "hasAnyRole('${requestmapInstance.authorityRole}')";
            }
        } else {
            if (requestmapInstance.authorityType) {
                requestmapInstance.configAttribute = requestmapInstance.authorityType;
            }
        }
        boolean result = requestmapService.save(requestmapInstance)
        if (!result) {
            foundErrors(requestmapInstance, map);
        } else {
            //清除ACL访问控制缓存
            gbSpringSecurityService.clearCachedRequestmaps();
            map.result = true;
            map.message = "修改成功";
        }
        return map;
    }

    @ResponseBody
    public Map delete(long id) {
        Map map = new HashMap();
        Requestmap requestmapInstance = Requestmap.get(id);
        if (requestmapInstance == null) {
            notFound(map);
            return map;
        }
        boolean result = requestmapService.delete(requestmapInstance)
        if (result) {
            //清除ACL访问控制缓存
            gbSpringSecurityService.clearCachedRequestmaps();
            map.result = true;
            map.message = "删除成功";
        } else {
            map.result = false;
            map.message = "删除失败";
        }
        return map;
    }

    @ResponseBody
    public Map deletes(String ids) {
        Map map = new HashMap();
        boolean result = requestmapService.deletes(ids)
        if (result) {
            //清除ACL访问控制缓存
            gbSpringSecurityService.clearCachedRequestmaps();
            map.result = true;
            map.message = "全部删除成功";
        } else {
            map.result = false;
            map.message = "全部删除失败";
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
                requestmapService.list(new PageParams(max: 100), {}).each { requestmapInstance ->
                    row([requestmapInstance.id, requestmapInstance.version, requestmapInstance.toString()])
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


    protected Map notFound(Map map) {
        map.result = false;
        map.message = "表中未发现此记录";
        return map;
    }

    protected Map foundErrors(Requestmap requestmapInstance, Map map) {
        map.result = false;
        map.errors = utilService.collectionErrorMap(Requestmap, requestmapInstance);
        return map;
    }
}
