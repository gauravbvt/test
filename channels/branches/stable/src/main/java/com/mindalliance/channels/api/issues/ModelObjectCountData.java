package com.mindalliance.channels.api.issues;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Web service data element for model object count metric.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/15/11
 * Time: 10:18 AM
 */
@XmlType( propOrder = {"type", "value"} )
public class ModelObjectCountData  implements Serializable {

    private int value;
    private int partCount;
    private int flowCount;
    private int connectorCount;

    public ModelObjectCountData() {
        // required
    }

    private Class<? extends ModelObject> modelObjectClass;

    public ModelObjectCountData( Class<? extends ModelObject> modelObjectClass, PlanService planService ) {
        this.modelObjectClass = modelObjectClass;
        init( planService );
    }

    private void init( PlanService planService ) {
        initValue( planService );
        initCounts( planService );
    }

    private void initCounts( PlanService planService ) {
        connectorCount = 0;
        for ( Segment segment : planService.getPlan().getSegments() ) {
            connectorCount += segment.listConnectors().size();
        }
        flowCount = 0;
        for ( Segment segment : planService.getPlan().getSegments() ) {
            flowCount += segment.getAllSharingFlows().size();
        }
        partCount = 0;
        for ( Segment segment : planService.getPlan().getSegments() ) {
            partCount += segment.listParts().size();
        }
    }

    private void initValue( PlanService planService ) {
        if ( Segment.class.isAssignableFrom( modelObjectClass ) ) {
            value = planService.getPlan().getSegmentCount();
        } else if ( Part.class.isAssignableFrom( modelObjectClass ) ) {
            value = partCount();
        } else if ( Flow.class.isAssignableFrom( modelObjectClass ) ) {
            value = flowCount();
        } else if ( Connector.class.isAssignableFrom( modelObjectClass ) ) {
            value = connectorCount();
        } else {
            value = planService.list( modelObjectClass ).size();
        }

    }

    @XmlElement
    public String getType() {
        if ( Flow.class.isAssignableFrom( modelObjectClass ) ) {
            return "flow";
        } else if ( Part.class.isAssignableFrom( modelObjectClass ) ) {
            return "task";
        } else if ( Actor.class.isAssignableFrom( modelObjectClass ) ) {
            return "agent";
        } else if ( TransmissionMedium.class.isAssignableFrom( modelObjectClass ) ) {
            return "medium";
        } else {
            return modelObjectClass.getSimpleName().toLowerCase();
        }
    }

    @XmlElement
    public int getValue() {
        return value;
    }

    private int partCount() {
        return partCount;
    }

    private int flowCount() {
        return flowCount;
    }

    private int connectorCount() {
        return connectorCount;
    }

}