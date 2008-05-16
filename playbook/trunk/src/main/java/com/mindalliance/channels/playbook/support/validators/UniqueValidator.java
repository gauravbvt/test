package com.mindalliance.channels.playbook.support.validators;

import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.IValidatable;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 8, 2008
 * Time: 10:13:25 AM
 */
public class UniqueValidator {

    static public class UniqueInQueryValidator extends AbstractValidator {

        private Object object;
        private Query query;

        public UniqueInQueryValidator(Object object, Query query) {
            this.object = object;
            this.query = query;
        }

        @Override
        protected String resourceKey() {
            return "Unique";
        }

        protected void onValidate(IValidatable validatable) {
            Object item = validatable.getValue();
            List results = (List) query.execute(object);
            if (!isUnique(item, results)) {
                error(validatable);
            }
        }

        private boolean isUnique(Object item, List results) {
            for (Object result : results) {
               if (result instanceof String) {
                   String stringItem = (String)item;
                   if (((String)item).equalsIgnoreCase((String)result)) {
                       return false;
                   }
               }
               else {
                   if (result == item) {
                       return false;
                   }
               }
            }
            return true;
        }
    }

    public static UniqueInQueryValidator inQuery(Object object, Query query) {
        return new UniqueInQueryValidator(object, query);
    }


}
