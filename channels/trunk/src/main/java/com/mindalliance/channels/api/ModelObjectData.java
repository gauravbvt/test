package com.mindalliance.channels.api;

import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;

/**
 * Web Service data element for a model object.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/11
 * Time: 10:49 AM
 */
abstract public class ModelObjectData {

    private ModelObject modelObject;
    private Plan plan;

    protected ModelObjectData(  ) {
        // required
    }

    public ModelObjectData( ModelObject modelObject, Plan plan ) {
        assert modelObject != null;
         this.modelObject = modelObject;
        this.plan = plan;
    }

    public long getId() {
        return modelObject.getId();
    }

    public String getName() {
        return modelObject.getName();
    }

    public DocumentationData getDocumentation() {
        return new DocumentationData( modelObject );
    }

    protected ModelObject getModelObject() {
        return modelObject;
    }

    protected Plan getPlan() {
        return plan;
    }

}
