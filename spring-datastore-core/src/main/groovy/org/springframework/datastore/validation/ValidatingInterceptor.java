/* Copyright (C) 2010 SpringSource
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.datastore.validation;

import org.springframework.datastore.core.Datastore;
import org.springframework.datastore.engine.EntityInterceptor;
import org.springframework.datastore.mapping.PersistentEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * An {@link org.springframework.datastore.engine.EntityInterceptor} that uses
 * Spring's validation mechanism to evict objects if an error occurs
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public class ValidatingInterceptor implements EntityInterceptor{
    private Datastore datastore;

    public boolean beforeInsert(PersistentEntity entity, Object o) {
        return doValidate(entity, o);
    }

    public boolean beforeUpdate(PersistentEntity entity, Object o) {
        return doValidate(entity, o);
    }

    public boolean beforeDelete(PersistentEntity entity, Object obj) {
        return true; // do nothing for deletes
    }

    private boolean doValidate(PersistentEntity entity, Object o) {
        Validator v = datastore.getMappingContext().getEntityValidator(entity);
        if(v != null) {
            BeanPropertyBindingResult result = new BeanPropertyBindingResult(o, o.getClass().getName());
            v.validate(o, result);
            if(result.hasErrors()) {
                onErrors(o, result);
                return false;
            }
        }
        return true;
    }

    /**
     * Sub classes should override to receive error notifications
     *
     * @param object The object being validated
     * @param errors The errors instance
     */
    protected void onErrors(Object object, Errors errors) {
        // do nothing
    }

    public void setDatastore(Datastore datastore) {
        this.datastore = datastore;
    }
}
