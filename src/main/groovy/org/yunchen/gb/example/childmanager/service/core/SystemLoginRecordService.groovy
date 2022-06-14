package org.yunchen.gb.example.childmanager.service.core

import org.yunchen.gb.core.PageParams
import org.yunchen.gb.example.childmanager.domain.core.SystemLoginRecord
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.query.api.BuildableCriteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Slf4j
@Service
@Transactional
class SystemLoginRecordService {
    public long count(Closure logic) {
        BuildableCriteria buildableCriteria = SystemLoginRecord.createCriteria()
        logic.delegate = buildableCriteria
        long count = buildableCriteria.count {
            logic()
        }
        return count
    }

    public List list(PageParams pageParams, Closure logic) {
        BuildableCriteria buildableCriteria = SystemLoginRecord.createCriteria()
        logic.delegate = buildableCriteria
        List list = buildableCriteria.list {
            logic()
            order(pageParams.sort, pageParams.order)
            maxResults(pageParams.max)
            firstResult(pageParams.offset)
        }
        return list;
    }

    public SystemLoginRecord get(long id) {
        return SystemLoginRecord.get(id)
    }

    public SystemLoginRecord read(long id) {
        return SystemLoginRecord.read(id)
    }

    public void logout(String sessionId) {
        SystemLoginRecord systemLoginRecord = SystemLoginRecord.findBySessionId(sessionId);
        if (systemLoginRecord) {
            systemLoginRecord.logoutTime = new Date();
            systemLoginRecord.save(flush: true);
        }
    }
}
