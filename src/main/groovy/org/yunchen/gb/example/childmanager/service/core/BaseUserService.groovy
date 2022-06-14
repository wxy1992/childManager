package org.yunchen.gb.example.childmanager.service.core

import org.springframework.transaction.interceptor.TransactionAspectSupport
import org.yunchen.gb.core.PageParams
import org.yunchen.gb.example.childmanager.domain.core.BaseUser
import org.yunchen.gb.example.childmanager.domain.core.BaseUserBaseRole
import org.yunchen.gb.example.childmanager.domain.core.SystemLoginRecord
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.BuildableCriteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
@Transactional
class BaseUserService {
    public long count(Closure logic) {
        BuildableCriteria buildableCriteria = BaseUser.createCriteria()
        logic.delegate = buildableCriteria
        long count = buildableCriteria.count {
            logic()
        }
        return count
    }

    public List list(PageParams pageParams, Closure logic) {
        BuildableCriteria buildableCriteria = BaseUser.createCriteria()
        logic.delegate = buildableCriteria
        List list = buildableCriteria.list {
            logic()
            order(pageParams.sort, pageParams.order)
            maxResults(pageParams.max)
            firstResult(pageParams.offset)
        }
        return list;
    }

    public BaseUser get(Closure logic) {
        BuildableCriteria buildableCriteria = BaseUser.createCriteria()
        logic.delegate = buildableCriteria
        return buildableCriteria.get {
            logic()
        }
    }

    public BaseUser create(Map map = [:]) {
        return new BaseUser(map)
    }

    public BaseUser get(long id) {
        return BaseUser.get(id)
    }

    public BaseUser read(long id) {
        return BaseUser.read(id)
    }

    public boolean save(BaseUser baseUser, boolean flush = true) {
        return baseUser.save(flush: flush)
    }

    public boolean delete(BaseUser baseUserInstance, boolean flush = true) {
        //事务处理
        BaseUser.withTransaction { status ->
            try {
                BaseUserBaseRole.removeAll(baseUserInstance, true);
                SystemLoginRecord.where {
                    baseUser == baseUserInstance
                }.deleteAll();
                baseUserInstance.delete(flush: true);
                //@todo 如果有其他和用户关联的数据，都从此处删除
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
        BaseUser.withTransaction { status ->
            try {
                list.each { id ->
                    BaseUser baseUserInstance = BaseUser.get(id);
                    BaseUserBaseRole.removeAll(baseUserInstance, true);
                    SystemLoginRecord.where {
                        baseUser == baseUserInstance
                    }.deleteAll();
                    baseUserInstance.delete(flush: true);
                    //@todo 如果有其他和用户关联的数据，都从此处删除
                }
                return true
            } catch (e) {
                status.setRollbackOnly()
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
                return false
            }
        }
    }
}
