package org.yunchen.gb.example.childmanager.controller.core.menu

import org.yunchen.gb.example.childmanager.domain.core.BaseRole
import org.yunchen.gb.example.childmanager.domain.core.Requestmap
import org.yunchen.gb.example.childmanager.domain.core.menu.MenuItem
import org.yunchen.gb.example.childmanager.service.core.UtilService
import org.yunchen.gb.example.childmanager.service.core.menu.MenuItemService
import org.yunchen.gb.core.GbSpringUtils
import org.yunchen.gb.core.PageParams
import org.yunchen.gb.plugin.poi.ExcelWriteBuilder
import org.yunchen.gb.core.annotation.GbController
import org.springframework.ui.Model
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ResponseBody
import groovy.util.logging.Slf4j
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired

/**
 * 菜单项管理控制器
 */
@Slf4j
@Transactional
@GbController
class MenuItemController {
    @Autowired
    private MenuItemService menuItemService
    @Autowired
    private UtilService utilService

    public void index() {
    }

    @ResponseBody
    public Map json(PageParams pageParams, String search) {
        Map map = new HashMap();
        Closure searchLogic = {
            if (search) {
                like('name', "%${search}%")
            }
        }
        long count = menuItemService.count(searchLogic)
        List list = menuItemService.list(pageParams, searchLogic)
        map.total = count;
        map.rows = list;
        return map;
    }

    public void show(long id, Model model) {
        MenuItem menuItemInstance = menuItemService.read(id)
        model.addAttribute("menuItemInstance", menuItemInstance);
    }

    public void create(Model model) {
        MenuItem menuItemInstance = menuItemService.create()
        model.addAttribute("menuItemInstance", menuItemInstance);
        model.addAttribute("requestmaps", Requestmap.list());
        model.addAttribute("parents", MenuItem.list());
    }

    @ResponseBody
    public Map save(MenuItem menuItemInstance) {
        Map map = new HashMap();
        if (menuItemInstance == null) {
            notFound(map, "");
            return map;
        }
        if (menuItemInstance.hasErrors()) {
            foundErrors(menuItemInstance, map);
            return map;
        }

        if (!menuItemService.save(menuItemInstance)) {
            foundErrors(menuItemInstance, map);
        } else {
            map.result = true;
            String domainName = GbSpringUtils.getI18nMessage("menuItem.label");
            map.message = GbSpringUtils.getI18nMessage("default.created.message", [domainName, menuItemInstance.id]);
        }
        return map;
    }

    public void edit(long id, Model model) {
        MenuItem menuItemInstance = menuItemService.read(id)
        model.addAttribute("menuItemInstance", menuItemInstance);
        model.addAttribute("requestmaps", Requestmap.list());
        model.addAttribute("parents", MenuItem.list());

    }

    @ResponseBody
    public Map update(MenuItem menuItemInstance, Long version) {
        Map map = new HashMap();
        if (menuItemInstance == null) {
            notFound(map, "${menuItemInstance.id}")
            return map
        }
        if (version != null) {
            if (menuItemInstance.version > version) {
                map.result = false
                map.message = GbSpringUtils.getI18nMessage("default.optimistic.locking.failure", [menuItemInstance.id]);
                return map;
            }
        }
        if (menuItemInstance.hasErrors()) {
            foundErrors(menuItemInstance, map);
            return map
        }
        boolean result = menuItemService.save(menuItemInstance)
        if (!result) {
            foundErrors(menuItemInstance, map);
        } else {
            map.result = true;
            String domainName = GbSpringUtils.getI18nMessage("menuItem.label");
            map.message = GbSpringUtils.getI18nMessage("default.updated.message", [domainName, menuItemInstance.id]);
        }
        return map;
    }

    @ResponseBody
    public Map delete(long id) {
        Map map = new HashMap();
        MenuItem menuItemInstance = menuItemService.get(id)
        if (menuItemInstance == null) {
            notFound(map, "${id}");
            return map;
        }
        String domainId = menuItemInstance.id;
        boolean result = menuItemService.delete(menuItemInstance)
        map.result = result;
        String domainName = GbSpringUtils.getI18nMessage("menuItem.label");
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
        boolean result = menuItemService.deletes(ids)
        map.result = result;
        String domainName = GbSpringUtils.getI18nMessage("menuItem.label");
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
                menuItemService.list(new PageParams(max: 100), {}).each { MenuItem menuItemInstance ->
                    row([menuItemInstance.id, menuItemInstance.version, menuItemInstance.toString()])
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
        String domainName = GbSpringUtils.getI18nMessage("menuItem.label");
        map.result = false;
        map.message = GbSpringUtils.getI18nMessage("default.not.found.message", [domainName, id]);
        return map;
    }

    protected Map foundErrors(MenuItem menuItemInstance, Map map) {
        map.result = false;
        map.errors = utilService.collectionErrorMap(MenuItem, menuItemInstance);
        return map;
    }


    //菜单树操作

    public void tree(Model model) {

    }

    @ResponseBody
    public List treeJson(Model model) {
        List list = [];
        List menuItems = MenuItem.list(['sort': 'sequenceNum', 'order': 'asc']);
        List menuItems2 = MenuItem.findAllByParentIsNull(['sort': 'sequenceNum', 'order': 'asc']);
        return arrangeTree(list, menuItems, menuItems2);
    }

    private List arrangeTree(List list, List item1, List item2) {
        if (item2 && item2.size() > 0) {
            item2.each { i2 ->
                List item22 = item1.findAll { it.parent?.id == i2.id };
                Map map = [:];
                map.id = i2.id;
                map.pid = (i2.parent?.id) ?: 0;
                map.name = i2.name;
                if (item22 && item22.size() > 0) {
                    map.open = true;
                    map.children = arrangeTree([], item1, item22);
                }
                list << map;

            }
        }
        return list;
    }

    @ResponseBody
    public Map treeMoveAction(Long dragId, Long dropId, String type) {
        Map map = [:];
        map.result = true;
        MenuItem menuItem = MenuItem.get(dragId);
        switch (type) {
            case "isRoot":
                menuItem.sequenceNum = 0;
                menuItem.save(flush: true);
                break;
            case "inner":
                MenuItem menuItem2 = MenuItem.get(dropId);
                if (menuItem2) {
                    menuItem.parent = menuItem2;
                    menuItem.save(flush: true);
                }
                break;
            case "prev":
                MenuItem menuItem2 = MenuItem.get(dropId);
                if (menuItem2) {
                    menuItem.sequenceNum = menuItem2.sequenceNum - 1;
                    menuItem.parent = menuItem2.parent;
                    menuItem.save(flush: true);
                }
                break;
            case "next":
                MenuItem menuItem2 = MenuItem.get(dropId);
                if (menuItem2) {
                    menuItem.sequenceNum = menuItem2.sequenceNum + 1;
                    menuItem.parent = menuItem2.parent;
                    menuItem.save(flush: true);
                }
                break;
        }
        return map;
    }
    //角色菜单树操作
    public void roleTree(String roleId, Model model) {
        model.addAttribute("roleId", roleId);
    }

    @ResponseBody
    public List roleTreeJson(String roleId) {
        BaseRole baseRole = BaseRole.read(roleId);
        List list = [];
        List menuItems = MenuItem.list(['sort': 'sequenceNum', 'order': 'asc']);
        List menuItems2 = MenuItem.findAllByParentIsNull(['sort': 'sequenceNum', 'order': 'asc']);
        return arrangeRoleTree(list, menuItems, menuItems2, baseRole?.menuItems*.id);
    }

    private List arrangeRoleTree(List list, List items1, List items2, List roleMenuItemIds) {
        if (items2 && items2.size() > 0) {
            items2.each { MenuItem i2 ->
                List items22 = (items1.findAll { it.parent?.id == i2?.id }) ?: [];
                Map map = [:];
                map.id = i2.id;
                map.pid = (i2.parent?.id) ?: 0;
                map.name = i2.name;
                if (roleMenuItemIds.contains(i2.id)) {
                    map.checked = true;
                }
                if (items22 && items22.size() > 0) {
                    map.open = true;
                    List currentList = [];
                    map.children = arrangeRoleTree(currentList, items1, items22, roleMenuItemIds);
                }
                list << map;

            }
        }
        return list;
    }


    @ResponseBody
    public Map treeCheckAction(String roleId, String treeIds) {
        Map map = [:];
        map.result = true;
        BaseRole baseRole = BaseRole.get(roleId);

        baseRole.menuItems = [];
        baseRole.save(flush: true);
        treeIds.tokenize(",").each {
            baseRole.addToMenuItems(MenuItem.read(it.toLong()));
        }
        baseRole.save(flush: true);
        return map;
    }
}
