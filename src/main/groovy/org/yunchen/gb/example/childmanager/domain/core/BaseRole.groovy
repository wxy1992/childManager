package org.yunchen.gb.example.childmanager.domain.core;

import org.yunchen.gb.example.childmanager.domain.core.menu.MenuItem
import org.yunchen.gb.core.annotation.Title
import org.yunchen.gb.core.annotation.GbVersionJsonIgnoreFix
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import grails.gorm.annotation.Entity
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * 系统基本角色类
 */

@EqualsAndHashCode(includes = 'authority')
@ToString(includes = 'authority', includeNames = true, includePackage = false)
@Entity
@GbVersionJsonIgnoreFix
@Title(zh_CN = "基本角色")
@JsonIgnoreProperties(["errors", "metaClass", "dirty", "attached", "dirtyPropertyNames", "handler", "target", "session", "entityPersisters", "hibernateLazyInitializer", "initialized", "proxyKey", "children", "menuItems"])
class BaseRole implements Serializable {
    private static final long serialVersionUID = 1
    @Title(zh_CN = "主键序列")
    String id
    @Title(zh_CN = "角色名称")
    String authority
    @Title(zh_CN = "描述")
    String description
    @Title(zh_CN = "菜单")
    static hasMany = [menuItems: MenuItem]
    static constraints = {
        authority(blank: false, unique: true, maxSize: 200);
        description(blank: false, maxSize: 200);
    }
    static mapping = {
        comment "基本角色"
        id generator: 'assigned'
        //cache true
    }

    String toString() {
        return authority;
    }
}

