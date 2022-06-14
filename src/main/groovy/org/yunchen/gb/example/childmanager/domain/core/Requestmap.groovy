package org.yunchen.gb.example.childmanager.domain.core;

import org.yunchen.gb.core.annotation.Title
import org.yunchen.gb.core.annotation.GbVersionJsonIgnoreFix
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import grails.gorm.annotation.Entity
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.http.HttpMethod

/**
 * 系统访问控制表
 */
@EqualsAndHashCode(includes = ['configAttribute', 'httpMethod', 'url'])
@ToString(includes = ['configAttribute', 'httpMethod', 'url'], cache = true, includeNames = true, includePackage = false)
@Entity
@GbVersionJsonIgnoreFix
@Title(zh_CN = "访问控制列表")
@JsonIgnoreProperties(["errors", "metaClass", "dirty", "attached", "dirtyPropertyNames", "handler", "target", "session", "entityPersisters", "hibernateLazyInitializer", "initialized", "proxyKey", "children"])
class Requestmap implements Serializable {
    private static final long serialVersionUID = 1
    @Title(zh_CN = "名称")
    String name
    @Title(zh_CN = "访问地址")
    String url
    @Title(zh_CN = "访问方法")
    HttpMethod httpMethod
    @Title(zh_CN = "配置属性")
    String configAttribute
    @Title(zh_CN = "配置属性-授权模式")
    String authorityType
    @Title(zh_CN = "配置属性-授权角色")
    String authorityRole
    @Title(zh_CN = "是否生成菜单")
    boolean isMenu;

    void beforeInsert() {
        if (!authorityType) {
            if (configAttribute?.contains("hasAnyRole")) {
                authorityType = "hasAnyRole";
                authorityRole = (configAttribute.toString() - "hasAnyRole(" - ")").replaceAll("'", "").replaceAll('"', '');
            }
            if (configAttribute?.contains("permitAll") || configAttribute?.contains("isAnonymous") || configAttribute?.contains("isFullyAuthenticated") || configAttribute?.contains("denyAll")) {
                authorityType = configAttribute
            }
        }
    }
    static constraints = {
        name(blank: false, maxSize: 200);
        configAttribute(blank: false, maxSize: 200);
        httpMethod(nullable: true);
        url(blank: false, unique: 'httpMethod', maxSize: 200);
        authorityType(nullable: true, maxSize: 200, inList: ['hasAnyRole', 'permitAll', 'isAnonymous()', 'isFullyAuthenticated()', 'denyAll']);
        authorityRole(nullable: true, maxSize: 200);
        isMenu();
    }
    static mapping = {
        //id generator:'native', params:[sequence:'requestmap_id_seq']
        comment "访问控制列表"
        //cache true
    }
}
