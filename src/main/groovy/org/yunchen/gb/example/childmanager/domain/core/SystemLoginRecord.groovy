package org.yunchen.gb.example.childmanager.domain.core;

import org.yunchen.gb.core.GbDomainSimpleJsonSerializer
import org.yunchen.gb.core.annotation.Title
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import grails.gorm.annotation.Entity
import org.springframework.format.annotation.DateTimeFormat

import java.sql.Timestamp

/**
 * 系统登录记录表
 */
@Entity
@Title(zh_CN = "系统登录记录")
@JsonIgnoreProperties(["errors", "metaClass", "dirty", "attached", "dirtyPropertyNames", "handler", "target", "session", "entityPersisters", "hibernateLazyInitializer", "initialized", "proxyKey", "children"])
class SystemLoginRecord {
    @Title(zh_CN = "登录用户")
    @JsonSerialize(using = GbDomainSimpleJsonSerializer.class)
    BaseUser baseUser
    @Title(zh_CN = "访问ip地址")
    String remoteaddr
    @Title(zh_CN = "sessionId")
    String sessionId
    @Title(zh_CN = "登入时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    //with spring mvc
    Date loginTime
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    //with spring mvc
    @Title(zh_CN = "登出时间")
    //若没有点击logout按钮，则此处为null
    Date logoutTime
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    //with spring mvc
    Timestamp dateCreated
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    //with spring mvc
    Timestamp lastUpdated
    static constraints = {
        baseUser(nullable: false)
        remoteaddr(maxSize: 50, blank: true, nullable: true)
        sessionId(maxSize: 200, blank: true, nullable: true) //unique: true
        loginTime(nullable: false)
        logoutTime(nullable: true)
    }

    static mapping = {
        //id generator:'native', params:[sequence:'SystemLoginRecord_id_seq']
        comment "系统登录记录表"
    }

    String toString() {
        return "${baseUser} from ${remoteaddr} login system at ${dateCreated.format('yyyy-MM-dd hh:mm ss')}";
    }
}
