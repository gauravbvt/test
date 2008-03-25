package com.mindalliance.channels.playbook.support.models

import org.apache.wicket.model.IModel
import com.mindalliance.channels.playbook.support.PathExpression
import org.apache.wicket.model.IChainingModel

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 5:29:44 PM
*/
class RefPropertyModel implements IChainingModel {

    def target
    String expression

    RefPropertyModel(def obj, String expression) {
        target = obj
        this.expression = expression
    }

    @Override
    Object getObject() {
        def holder = getPropertyHolder()
        try {
            return PathExpression.getNestedProperty(holder, expression)
        }
        catch (Exception e) {
            System.out.println("*** Failed to eval expression $expression on $target : $e")
            throw e
        }
    }

    private def getPropertyHolder() {
        return (target instanceof IModel) ? ((IModel)target).getObject() : target
    }

    public void setObject(def obj) {
        def holder = getPropertyHolder()
        try {
            PathExpression.setNestedProperty(holder, expression, obj)
        }
        catch (Exception e) {
            System.out.println("*** Failed to set $target at $expression to $obj : $e")
            throw e
        }
    }

    public void detach() {
        // Detach chained model
        if (target instanceof IModel) target.detach()
    }

    public void setChainedModel(IModel model) {
        target = model;        
    }

    public IModel getChainedModel() {
        return (target instanceof IModel) ? (IModel)target : null
    }

}