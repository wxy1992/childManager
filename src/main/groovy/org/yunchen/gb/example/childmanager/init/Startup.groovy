package org.yunchen.gb.example.childmanager.init;


import org.springframework.transaction.annotation.Transactional;
import org.yunchen.gb.core.annotation.GbBootstrap
import org.yunchen.gb.example.childmanager.domain.core.*;
import org.yunchen.gb.example.childmanager.domain.core.menu.MenuItem;
import javax.annotation.PreDestroy
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException;

@Transactional
@GbBootstrap
class Startup {
    public void init() {
        //初始化安全信息
        createDefaultRoles();
        createDefaultUsers();
        createRequestMap();
        initMenu();
        //@todo初始化系统数据
    }

    @PreDestroy
    public void destroy() {
        println "system shutdown"
    }

    private def createDefaultRoles() {
        if (!BaseRole.findByAuthority('ROLE_ADMIN')) {
            def role = new BaseRole(id: 'ROLE_ADMIN', authority: 'ROLE_ADMIN', description: "超级管理员");
            role.id = role.authority;
            role.save(flush: true);
        }

        if (!BaseRole.findByAuthority('ROLE_USER')) {
            def role = new BaseRole(id: 'ROLE_USER', authority: 'ROLE_USER', description: "注册用户");
            role.id = role.authority;
            role.save(flush: true);
        }
    }

    private def createDefaultUsers() {

        def admin = BaseUser.findByUsername('admin');
        if (!admin) {
            admin = new BaseUser(username: 'admin', realname: 'admin', password: 'admin', enabled: true);
            admin.save(flush: true)
            BaseUserBaseRole.create(admin, BaseRole.findByAuthority('ROLE_ADMIN'), true);
        }

        def user = BaseUser.findByUsername('user');
        if (!user) {
            user = new BaseUser(username: 'user', realname: 'user', password: 'user', enabled: true);
            user.save(flush: true);
            BaseUserBaseRole.create(user, BaseRole.findByAuthority('ROLE_USER'), true);
        }

    }

    private def createRequestMap() {
        if (Requestmap.count() == 0) {
            //static resource
            new Requestmap(name: 'webjars resource', url: '/webjars/**', configAttribute: 'permitAll').save(flush: true);
            new Requestmap(name: 'static resource', url: '/static/**', configAttribute: 'permitAll').save(flush: true);
            new Requestmap(name: 'js resource', url: '/js/**', configAttribute: "permitAll").save(flush: true);
            new Requestmap(name: 'images resource', url: '/images/**', configAttribute: 'permitAll').save(flush: true);
            new Requestmap(name: 'css resource', url: '/css/**', configAttribute: 'permitAll').save(flush: true);
            new Requestmap(name: 'favicon resource', url: '/images/favicon.ico', configAttribute: 'permitAll').save(flush: true);
            new Requestmap(name: 'favicon.ico resource', url: '/favicon.ico', configAttribute: 'permitAll').save(flush: true);


            new Requestmap(name: '自定义错误', url: '/error/**', configAttribute: 'permitAll').save(flush: true);
            new Requestmap(name: '登录控制', url: '/login/**', configAttribute: 'permitAll').save(flush: true);
            new Requestmap(name: '登出控制', url: '/logout/**', configAttribute: 'permitAll').save(flush: true);
            new Requestmap(name: '注册管理', url: '/register/**', configAttribute: 'permitAll').save(flush: true);
            new Requestmap(name: '注册码管理', url: '/jcaptcha/**', configAttribute: 'permitAll').save(flush: true);
            //cas
            new Requestmap(name: 'cas proxy管理', url: '/secure/**', configAttribute: 'permitAll').save(flush: true);
            //application
            new Requestmap(name: '数据库控制台', url: '/dbconsole/**', configAttribute: "hasAnyRole('ROLE_ADMIN')").save(flush: true);
            new Requestmap(name: '代码生成控制台', url: '/webconsole/**', configAttribute: "hasAnyRole('ROLE_ADMIN')").save(flush: true);
            new Requestmap(name: '用户管理', url: '/baseUser/**', configAttribute: "hasAnyRole('ROLE_ADMIN')", isMenu: true).save(flush: true);
            new Requestmap(name: '角色管理', url: '/baseRole/**', configAttribute: "hasAnyRole('ROLE_ADMIN')", isMenu: true).save(flush: true)
            new Requestmap(name: '用户角色映射管理', url: '/baseUserBaseRole/**', configAttribute: "hasAnyRole('ROLE_ADMIN')", isMenu: false).save(flush: true);
            new Requestmap(name: '访问控制管理', url: '/requestmap/**', configAttribute: "hasAnyRole('ROLE_ADMIN')", isMenu: true).save(flush: true);
            new Requestmap(name: '系统登录日志', url: '/systemLoginRecord/**', configAttribute: "hasAnyRole('ROLE_ADMIN')", isMenu: true).save(flush: true);
            new Requestmap(name: '菜单管理', url: '/menuItem/**', configAttribute: 'isFullyAuthenticated()', isMenu: true).save(flush: true);
            //controllerfullyAuthenticated
            new Requestmap(name: '系统首页', url: '/', configAttribute: 'permitAll').save(flush: true);
            new Requestmap(name: '前端', url: '/front/**', configAttribute: 'permitAll').save(flush: true);
            new Requestmap(name: '系统控制台', url: '/workspace/**', configAttribute: 'isFullyAuthenticated()', isMenu: true).save(flush: true);
            new Requestmap(name: 'ws管理', url: '/ws/**', configAttribute: 'permitAll').save(flush: true);
            new Requestmap(name: 'simpleCaptcha管理', url: '/simpleCaptcha/**', configAttribute: 'permitAll').save(flush: true);
            new Requestmap(name: '/**管理', url: '/**', configAttribute: 'isFullyAuthenticated()').save(flush: true);
        }
    }

    private void initMenu() {
        if (MenuItem.count() == 0) {
            Requestmap.list().each {
                if (it.isMenu) {
                    MenuItem menuItem = new MenuItem();
                    menuItem.requestmap = it;
                    menuItem.name = it.name;
                    menuItem.url = (it.url.endsWith("/**")) ? (it.url.replace('/**', '/index')) : (it.url);
                    menuItem.save(flush: true);
                    menuItem.sequenceNum = menuItem.id.longValue();
                    menuItem.save(flush: true);
                }
            }
            BaseRole userRole = BaseRole.findByAuthority('ROLE_USER');
            BaseRole adminRole = BaseRole.findByAuthority('ROLE_ADMIN');
            if (!MenuItem.findByName('业务菜单')) {
                MenuItem menuItem = new MenuItem(name: '业务菜单');
                menuItem.save(flush: true);
                userRole.addToMenuItems(menuItem);
                userRole.save(flush: true);
                MenuItem menuItem2 = new MenuItem(name: '管理菜单');
                menuItem2.save(flush: true);
                ['用户管理', '角色管理', '访问控制管理', '系统登录日志', '菜单管理', '系统控制台'].each {
                    MenuItem one = MenuItem.findByName(it);
                    one.parent = menuItem2;
                    one.save('flush': true);
                    adminRole.addToMenuItems(one);
                }
                MenuItem.list().each {
                    adminRole.addToMenuItems(it);
                    adminRole.save(flush: true);
                }

            }
        }
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //保存入登录日志
        Map map = [:];
        String username = authentication.getPrincipal().username;
        map.remoteaddr = request.getRemoteAddr();
        map.sessionId = request.getSession().getId();
        map.loginTime = new Date();
        Timer timer = new Timer();
        //100毫秒后分离线程执行
        timer.runAfter(100) {
            SystemLoginRecord.withTransaction {
                SystemLoginRecord systemLoginRecord = new SystemLoginRecord(map);
                systemLoginRecord.baseUser = BaseUser.findByUsername(username);
                systemLoginRecord.save(flush: true);
            }
        }
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        //println "onAuthenticationFailure"
    }
}
