package com.mindalliance.channels.api.issues;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.ModelService;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element containing plan metrics.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/14/11
 * Time: 6:10 PM
 */
public class ModelMetricsData implements Serializable {


    private static Class[] ModelObjectClasses = {
            Event.class, Phase.class, Actor.class, Organization.class, Role.class, Place.class,
            InfoProduct.class, InfoFormat.class, TransmissionMedium.class,
            Segment.class, Part.class, Flow.class, Connector.class
    };
    private List<ModelObjectCountData> counts;

    public ModelMetricsData() {
        // required
    }

    public ModelMetricsData( CommunityService communityService ) {
        init( communityService.getModelService() );
    }

    private void init( ModelService modelService ) {
        counts = new ArrayList<ModelObjectCountData>();
        for ( Class moClass : ModelObjectClasses ) {
            counts.add( new ModelObjectCountData( (Class<? extends ModelObject>) moClass, modelService ) );
        }
    }

    @XmlElement( name = "count" )
    public List<ModelObjectCountData> getModelObjectCounts() {
        return counts;
    }

}
