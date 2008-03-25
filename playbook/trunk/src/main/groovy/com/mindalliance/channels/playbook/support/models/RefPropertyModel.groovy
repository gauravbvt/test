package com.mindalliance.channels.playbook.support.models

import org.apache.wicket.model.IModel
import com.mindalliance.channels.playbook.support.PathExpression
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 5:29:44 PM
*/
class RefPropertyModel implements IModel {

    def source
    String expression

    RefPropertyModel(def obj, String expression) {
        source = obj
        this.expression = expression
    }

    @Override
    Object getObject() {
        try {
            return PathExpression.eval(source, expression)
        }
        catch (Exception e) {
            System.out.println("*** Failed to apply expression $expression to $source : $e")
            throw e
        }
    }

    public void setObject(def obj) {
        source = obj
    }

    public void detach() {
        // Nothing to do
    }

}