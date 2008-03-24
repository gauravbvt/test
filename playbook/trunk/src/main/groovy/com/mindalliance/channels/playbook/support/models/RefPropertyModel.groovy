package com.mindalliance.channels.playbook.support.models

import org.apache.wicket.model.IModel
import com.mindalliance.channels.playbook.support.PathExpression

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
        try {
            return PathExpression.eval(ref, expression)
        }
        catch (Exception e) {
            System.out.println("*** Failed to apply expression $expression to $ref : $e")
            throw e
        }
    }

    public void setObject(Object ref) {
        this.ref = ref
    }

    public void detach() {
        // Nothing to do
    }

}