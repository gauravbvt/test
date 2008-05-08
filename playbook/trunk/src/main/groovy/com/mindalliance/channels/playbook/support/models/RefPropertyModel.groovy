package com.mindalliance.channels.playbook.support.models

import org.apache.wicket.model.IModel
import com.mindalliance.channels.playbook.support.RefUtils
import org.apache.wicket.model.IChainingModel
import org.apache.log4j.Logger

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
    def defaultObject

    RefPropertyModel(def obj, String expression) {
        target = obj
        this.expression = expression
    }

    RefPropertyModel(def obj, String expression, def defaultObject) {
        this(obj, expression)
        this.defaultObject = defaultObject
    }

    @Override
    Object getObject() {
        def holder = getPropertyHolder()
        try {
            def object =  RefUtils.get(holder, expression)
            if (object == null && defaultObject) object = defaultObject
            return object
        }
        catch (Exception e) {
            Logger.getLogger(this.class.name).warn("Failed to eval expression $expression on $target", e)
            throw e
        }
    }

    public def getPropertyHolder() {
        return (target instanceof IModel) ? ((IModel)target).getObject() : target
    }

    public void setObject(def obj) {
        def holder = getPropertyHolder()
        try {
            RefUtils.set(holder, expression, obj)
        }
        catch (Exception e) {
            Logger.getLogger(this.class.name).warn("Failed to set $target at $expression to $obj", e)
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