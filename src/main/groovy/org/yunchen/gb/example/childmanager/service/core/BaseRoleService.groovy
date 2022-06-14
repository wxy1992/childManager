package org.yunchen.gb.example.childmanager.service.core

import org.springframework.transaction.interceptor.TransactionAspectSupport
import org.yunchen.gb.example.childmanager.domain.core.BaseRole
import org.yunchen.gb.core.PageParams
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.BuildableCriteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Slf4j
@Service
@Transactional
class BaseRoleService {

    public long count(Closure logic) {
        BuildableCriteria buildableCriteria = BaseRole.createCriteria()
        println buildableCriteria.class.name
        logic.delegate = buildableCriteria
        long count = buildableCriteria.count {
            logic()
        }
        return count
    }

    public List list(PageParams pageParams, Closure logic) {
        BuildableCriteria buildableCriteria = BaseRole.createCriteria()
        logic.delegate = buildableCriteria
        List list = buildableCriteria.list {
            logic()
            order(pageParams.sort, pageParams.order)
            maxResults(pageParams.max)
            firstResult(pageParams.offset)
        }
        return list;
    }

    public BaseRole create(Map map = [:]) {
        return new BaseRole(map)
    }

    public BaseRole get(String id) {
        return BaseRole.get(id)
    }

    public BaseRole read(String id) {
        return BaseRole.read(id)
    }

    public boolean save(BaseRole baseRole, boolean flush = true) {
        return baseRole.save(flush: flush)
    }

    public boolean delete(BaseRole baseRole, boolean flush = true) {
        BaseRole.withTransaction { status ->
            try {
                baseRole.delete(flush: flush);
                return true
            } catch (Exception e) {
                status.setRollbackOnly()
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                return false
            }
        }
    }

    public boolean deletes(String ids, boolean flush = true) {
        List list = ids?.tokenize(',')?.unique()?.collect { it.toLong() };
        BaseRole.withTransaction { status ->
            try {
                BaseRole.where {
                    id in list
                }.deleteAll();
                return true;
            } catch (e) {
                status.setRollbackOnly()
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                return false
            }
        }
    }
}
