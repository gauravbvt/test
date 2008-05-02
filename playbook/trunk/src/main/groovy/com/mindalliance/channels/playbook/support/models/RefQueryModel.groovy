package com.mindalliance.channels.playbook.support.models

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.query.Query
import org.apache.wicket.model.IChainingModel
import org.apache.wicket.model.IModel

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 2, 2008
 * Time: 2:40:03 PM
 */
class RefQueryModel  implements IChainingModel {

    def target
    Query query

    RefQueryModel(def target, Query query) {
        this.target = target
        this.query = query
    }

    @Override
    Object getObject() {
        Ref holder = (Ref)getHolder()
        try {
            return query.execute(holder)
        }
        catch (Exception e) {
            Logger.getLogger(this.class.name).warn("Failed to eval query $query on $target", e)
            throw e
        }
    }

    public def getHolder() {
        return (target instanceof IModel) ? ((IModel)target).getObject() : target
    }

    public void setObject(def obj) {  // this model is ReadOnly
        Logger.getLogger(this.class.name).warn("Attempting to set queried $target to $obj")
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