package com.mindalliance.channels.playbook.support.models

import com.mindalliance.channels.playbook.mem.SessionCategory
import org.apache.wicket.model.IModel

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 5:29:44 PM
*/
class RefPropertyModel implements IModel {

    def ref
    String expression

    RefPropertyModel(def ref, String expression) {
       this.ref = ref
       this.expression = expression
    }
    
    @Override
    Object getObject() {
        use(SessionCategory) {
            try {
                return applyExpression(expression)
            }
            catch (Exception e) {
                System.out.println("*** Failed to apply expression $expression to $ref : $e")
                throw e
            }
        }
    }

    public void setObject(Object ref) {
        this.ref = ref
    }

    public void detach() {
        // Nothing to do
    }

    private def applyExpression(String expression) {
        List properties = expression.tokenize('.')
        def result = ref
        properties.each {
            result = result[it]
        }
        return result
    }
}