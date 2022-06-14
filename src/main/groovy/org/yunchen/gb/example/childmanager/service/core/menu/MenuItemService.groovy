package org.yunchen.gb.example.childmanager.service.core.menu

import org.springframework.transaction.interceptor.TransactionAspectSupport
import org.yunchen.gb.core.PageParams
import org.yunchen.gb.example.childmanager.domain.core.menu.MenuItem
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.BuildableCriteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
@Transactional
class MenuItemService {

    public long count(Closure logic) {
        BuildableCriteria buildableCriteria = MenuItem.createCriteria()
        logic.delegate = buildableCriteria
        long count = buildableCriteria.count {
            logic()
        }
        return count
    }

    public List list(PageParams pageParams, Closure logic) {
        BuildableCriteria buildableCriteria = MenuItem.createCriteria()
        logic.delegate = buildableCriteria
        List list = buildableCriteria.list {
            logic()
            order(pageParams.sort, pageParams.order)
            maxResults(pageParams.max)
            firstResult(pageParams.offset)
        }
        return list;
    }

    public MenuItem create(Map map = [:]) {
        return new MenuItem(map)
    }

    public MenuItem get(long id) {
        return MenuItem.get(id)
    }

    public MenuItem read(long id) {
        return MenuItem.read(id)
    }

    public boolean save(MenuItem menuItem, boolean flush = true) {
        return menuItem.save(flush: flush)
    }

    public boolean delete(MenuItem menuItem, boolean flush = true) {
        MenuItem.withTransaction { status ->
            try {
                menuItem.delete(flush: flush);
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
        MenuItem.withTransaction { status ->
            try {
                MenuItem.where {
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
