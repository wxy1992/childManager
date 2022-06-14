package org.yunchen.gb.example.childmanager.controller.core


import org.yunchen.gb.example.childmanager.domain.core.BaseUser
import org.yunchen.gb.example.childmanager.domain.core.SystemLoginRecord
import org.yunchen.gb.example.childmanager.service.core.SystemLoginRecordService
import org.yunchen.gb.core.annotation.GbController
import org.yunchen.gb.core.PageParams
import org.yunchen.gb.plugin.poi.ExcelWriteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ResponseBody
import groovy.util.logging.Slf4j
import org.springframework.transaction.annotation.Transactional

/**
 * 系统登录日志控制器
 */
@Slf4j
@Transactional
@GbController
class SystemLoginRecordController {
    @Autowired
    private SystemLoginRecordService systemLoginRecordService

    public void index() {

    }

    @ResponseBody
    public Map json(PageParams pageParams, String search) {
        Map map = new HashMap()
        Closure searchLogic = {
            if (search) {
                like('name', "%${search}%")
            }
        }
        long count = systemLoginRecordService.count(searchLogic)
        List list = systemLoginRecordService.list(pageParams, searchLogic)
        map.total = count;
        map.rows = list;
        return map;
    }

    public void show(long id, Model model) {
        SystemLoginRecord systemLoginRecordInstance = systemLoginRecordService.read(id)
        model.addAttribute("systemLoginRecordInstance", systemLoginRecordInstance);
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
        ExcelWriteBuilder excelWriteBuilder = new ExcelWriteBuilder(new File(tempFile));
        excelWriteBuilder.workbook {
            sheet("sheet1") {
                row(["id", "version", "info"]);
                systemLoginRecordService.list(new PageParams(max: 100), {}).each { SystemLoginRecord systemLoginRecordInstance ->
                    row([systemLoginRecordInstance.id, systemLoginRecordInstance.version, systemLoginRecordInstance.toString()])
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

}

