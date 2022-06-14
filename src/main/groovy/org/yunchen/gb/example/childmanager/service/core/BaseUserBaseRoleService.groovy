package org.yunchen.gb.example.childmanager.service.core

import org.yunchen.gb.core.PageParams
import org.yunchen.gb.example.childmanager.domain.core.BaseUser
import org.yunchen.gb.example.childmanager.domain.core.BaseRole
import org.yunchen.gb.example.childmanager.domain.core.BaseUserBaseRole
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.BuildableCriteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
@Transactional
class BaseUserBaseRoleService {
    public long count(Closure logic) {
        BuildableCriteria buildableCriteria = BaseUserBaseRole.createCriteria()
        logic.delegate = buildableCriteria
        long count = buildableCriteria.count {
            logic()
        }
        return count
    }

    public List list(PageParams pageParams, Closure logic) {
        BuildableCriteria buildableCriteria = BaseUserBaseRole.createCriteria()
        logic.delegate = buildableCriteria
        List list = buildableCriteria.list {
            logic()
            order(pageParams.sort, pageParams.order)
            maxResults(pageParams.max)
            firstResult(pageParams.offset)
        }
        return list;
    }

    public void authRoleToUser(BaseUser baseUser, List roles) {
        BaseUserBaseRole.removeAll(baseUser, true);
        //添加角色
        roles.each { roleId ->
            if (roleId) {
                BaseUserBaseRole.create(baseUser, BaseRole.read(roleId), true);
            }
        }
    }
}
