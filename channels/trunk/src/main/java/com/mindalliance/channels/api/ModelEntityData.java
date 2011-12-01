package com.mindalliance.channels.api;

import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for a model entity.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/30/11
 * Time: 2:47 PM
 */
abstract public class ModelEntityData extends ModelObjectData {

    public ModelEntityData() {
    }

    public ModelEntityData( ModelObject modelObject ) {
        super( modelObject );
    }

    public List<Long> getCategories() {
        List<Long> typeIds = new ArrayList<Long>(  );
        for (ModelEntity type : getModelEntity().getAllTypes() ) {
            if ( !type.isUniversal() && !type.isUnknown() ) {
                typeIds.add( type.getId() );
            }
        }
        return typeIds;
    }

    public String getKind() {
        return getModelEntity().isActual() ? "actual" : "kind";
    }

    private ModelEntity getModelEntity() {
         return (ModelEntity)getModelObject();
    }

}
