package org.yunchen.gb.example.childmanager.domain.core;

import org.yunchen.gb.core.GbSpringUtils
import org.yunchen.gb.core.annotation.Title
import org.yunchen.gb.core.annotation.GbVersionJsonIgnoreFix
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import grails.gorm.annotation.Entity
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.format.annotation.DateTimeFormat

/**
 * 系统基本用户类
 */
@EqualsAndHashCode(includes = 'username')
@ToString(includes = 'username', includeNames = true, includePackage = false)
@Entity
@GbVersionJsonIgnoreFix
@Title(zh_CN = "基本用户")
@JsonIgnoreProperties(["errors", "metaClass", "dirty", "attached", "dirtyPropertyNames", "handler", "target", "session", "entityPersisters", "hibernateLazyInitializer", "initialized", "proxyKey", "children"])
class BaseUser implements Serializable {
    private static final long serialVersionUID = 1
/*	@Title(zh_CN="主键序列")
	String id*/
    @Title(zh_CN = "用户名")
    String username
    @Title(zh_CN = "真实姓名")
    String realname
    @Title(zh_CN = "密码")
    String password
    @Title(zh_CN = "启用")
    boolean enabled = true
    @Title(zh_CN = "账户过期")
    boolean accountExpired
    @Title(zh_CN = "账户锁定")
    boolean accountLocked
    @Title(zh_CN = "密码过期")
    boolean passwordExpired
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    //with spring mvc
    @Title(zh_CN = '创建日期')
    Date dateCreated
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    //with spring mvc
    @Title(zh_CN = '修改日期')
    Date lastUpdated


    boolean isEnabled() {
        return this.enabled
    }

    Set<BaseRole> getAuthorities() {
        BaseUserBaseRole.findAllByBaseUser(this)*.baseRole
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = GbSpringUtils.getBean("passwordEncoder").encode(password)
    }

    static constraints = {
        username(blank: false, unique: true, minSize: 3, maxSize: 200);
        realname(blank: false, nullable: false, maxSize: 200);
        password(blank: false, nullable: false, maxSize: 200);
    }

    static mapping = {
        //id generator: 'uuid'
        //id generator:'native', params:[sequence:'baseuser_id_seq']
        comment "基本用户"
        password column: '`password`'
    }

    String toString() {
        return this.realname;
    }
}
