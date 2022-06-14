package org.yunchen.gb.example.childmanager.service.core

import org.yunchen.gb.core.GbSpringUtils
import org.springframework.stereotype.Service
import org.springframework.validation.FieldError

@Service
class UtilService {
    public Map collectionErrorMap(def entityClass, def entityInstance) {
        Map errors = new HashMap();
        entityInstance.errors.allErrors.each { FieldError error ->
            String message = GbSpringUtils.getI18nMessage(error.code, error.arguments.toList(), error.defaultMessage).toString();
            if (GbSpringUtils.isDomain(entityClass.gormPersistentEntity.persistentProperties.find { it.name == error.field }.type)) {
                errors.put("${error.field}.id", message);
            } else {
                errors.put(error.field, message);
            }
        }
        return errors
    }
}
