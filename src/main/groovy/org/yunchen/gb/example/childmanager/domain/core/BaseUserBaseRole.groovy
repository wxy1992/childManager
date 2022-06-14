package org.yunchen.gb.example.childmanager.domain.core;

import org.yunchen.gb.core.annotation.Title
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import grails.gorm.DetachedCriteria
import grails.gorm.annotation.Entity
import groovy.transform.ToString
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * 系统用户角色映射类
 */

@ToString(cache = true, includeNames = true, includePackage = false)
@Entity
@Title(zh_CN = "用户角色映射表")
@JsonIgnoreProperties(["errors", "metaClass", "dirty", "attached", "dirtyPropertyNames", "handler", "target", "session", "entityPersisters", "hibernateLazyInitializer", "initialized", "proxyKey", "children"])
class BaseUserBaseRole implements Serializable {

    private static final long serialVersionUID = 1
    @Title(zh_CN = "用户")
    BaseUser baseUser
    @Title(zh_CN = "角色")
    BaseRole baseRole


    @Override
    boolean equals(other) {
        if (!(other instanceof BaseUserBaseRole)) {
            return false
        }

        other.baseUser?.id == baseUser?.id && other.baseRole?.id == baseRole?.id
    }

    @Override
    int hashCode() {
        def builder = new HashCodeBuilder()
        if (baseUser) builder.append(baseUser.id)
        if (baseRole) builder.append(baseRole.id)
        builder.toHashCode()
    }

    static BaseUserBaseRole get(def baseUserId, def baseRoleId) {
        criteriaFor(baseUserId, baseRoleId).get()
    }

    static boolean exists(def baseUserId, def baseRoleId) {
        criteriaFor(baseUserId, baseRoleId).count()
    }

    private static DetachedCriteria criteriaFor(def baseUserId, def baseRoleId) {
        BaseUserBaseRole.where {
            baseUser == BaseUser.load(baseUserId) &&
                    baseRole == BaseRole.load(baseRoleId)
        }
    }

    static BaseUserBaseRole create(BaseUser baseUser, BaseRole baseRole, boolean flush = false) {
        def instance = new BaseUserBaseRole(baseUser: baseUser, baseRole: baseRole);
        instance.save(flush: flush, insert: true)
        instance
    }

    static boolean remove(BaseUser u, BaseRole r, boolean flush = false) {
        if (u == null || r == null) return false

        int rowCount = BaseUserBaseRole.where { baseUser == u && baseRole == r }.deleteAll()

        if (flush) {
            BaseUserBaseRole.withSession { it.flush() }
        }

        rowCount
    }

    static void removeAll(BaseUser u, boolean flush = false) {
        if (u == null) return

        BaseUserBaseRole.where { baseUser == u }.deleteAll()

        if (flush) {
            BaseUserBaseRole.withSession { it.flush() }
        }
    }

    static void removeAll(BaseRole r, boolean flush = false) {
        if (r == null) return

        BaseUserBaseRole.where { baseRole == r }.deleteAll()

        if (flush) {
            BaseUserBaseRole.withSession { it.flush() }
        }
    }

    static constraints = {
    }

    static mapping = {
        id composite: ['baseUser', 'baseRole']
        version false
        comment "用户角色映射表"
    }
}
