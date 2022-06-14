package org.yunchen.gb.example.childmanager.service.core

import org.springframework.transaction.interceptor.TransactionAspectSupport
import org.yunchen.gb.core.PageParams
import org.yunchen.gb.example.childmanager.domain.core.Requestmap
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.BuildableCriteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
@Transactional
class RequestmapService {
    public long count(Closure logic) {
        BuildableCriteria buildableCriteria = Requestmap.createCriteria()
        logic.delegate = buildableCriteria
        long count = buildableCriteria.count {
            logic()
        }
        return count
    }

    public List list(PageParams pageParams, Closure logic) {
        BuildableCriteria buildableCriteria = Requestmap.createCriteria()
        logic.delegate = buildableCriteria
        List list = buildableCriteria.list {
            logic()
            order(pageParams.sort, pageParams.order)
            maxResults(pageParams.max)
            firstResult(pageParams.offset)
        }
        return list;
    }

    public Requestmap create(Map map = [:]) {
        return new Requestmap(map)
    }

    public Requestmap get(long id) {
        return Requestmap.get(id)
    }

    public Requestmap read(long id) {
        return Requestmap.read(id)
    }

    public boolean save(Requestmap requestmap, boolean flush = true) {
        return requestmap.save(flush: flush)
    }

    public boolean delete(Requestmap requestmap, boolean flush = true) {
        Requestmap.withTransaction { status ->
            try {
                requestmap.delete(flush: flush);
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
        Requestmap.withTransaction { status ->
            try {
                Requestmap.where {
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
