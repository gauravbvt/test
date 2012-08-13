package com.mindalliance.channels.api.issues;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.PlanService;

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
public class PlanMetricsData  implements Serializable {


    private static Class[] ModelObjectClasses = {
            Event.class, Phase.class, Actor.class, Organization.class, Role.class, Place.class,
            TransmissionMedium.class, Segment.class, Part.class, Flow.class, Connector.class, Requirement.class
    };
    private List<ModelObjectCountData> counts;

    public PlanMetricsData() {
        // required
    }

    public PlanMetricsData( PlanService planService ) {
        init( planService );
    }

    private void init( PlanService planService ) {
        counts = new ArrayList<ModelObjectCountData>();
        for ( Class moClass : ModelObjectClasses ) {
            counts.add( new ModelObjectCountData( (Class<? extends ModelObject>) moClass, planService ) );
        }
    }

    @XmlElement( name = "count" )
    public List<ModelObjectCountData> getModelObjectCounts() {
        return counts;
    }

}
