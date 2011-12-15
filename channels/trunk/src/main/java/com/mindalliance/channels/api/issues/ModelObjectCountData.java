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

/**
 * Web service data element for model object count metric.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/15/11
 * Time: 10:18 AM
 */
@XmlType( propOrder = { "type", "value" })
public class ModelObjectCountData {

    public ModelObjectCountData() {
        // required
    }

    private Class<? extends ModelObject> modelObjectClass;
    private PlanService planService;

    public ModelObjectCountData( Class<? extends ModelObject> modelObjectClass, PlanService planService ) {
        this.modelObjectClass = modelObjectClass;
        this.planService = planService;
    }

    @XmlElement
    public String getType() {
        if ( Flow.class.isAssignableFrom( modelObjectClass ) ) {
            return "flow";
        } else if ( Part.class.isAssignableFrom( modelObjectClass ) ) {
            return "task";
        }  else if ( Actor.class.isAssignableFrom( modelObjectClass ) ) {
            return "agent";
        }   else if ( TransmissionMedium.class.isAssignableFrom( modelObjectClass ) ) {
            return "medium";
        }else {
            return modelObjectClass.getSimpleName().toLowerCase();
        }
    }

    @XmlElement
    public int getValue() {
       if ( Segment.class.isAssignableFrom( modelObjectClass ) ) {
            return planService.getPlan().getSegmentCount();
        } else if ( Part.class.isAssignableFrom( modelObjectClass ) ) {
            return partCount();
        }  else if ( Flow.class.isAssignableFrom( modelObjectClass ) ) {
            return flowCount();
        }  else if ( Connector.class.isAssignableFrom( modelObjectClass ) ) {
            return connectorCount();
        } else {
            return planService.list( modelObjectClass ).size();
        }
    }

    private int partCount() {
        int count = 0;
        for ( Segment segment : planService.getPlan().getSegments() ) {
            count += segment.listParts().size();
        }
        return count;
    }

    private int flowCount() {
         int count = 0;
         for ( Segment segment : planService.getPlan().getSegments() ) {
             count += segment.getAllSharingFlows().size();
         }
         return count;
     }

    private int connectorCount() {
        int count = 0;
        for ( Segment segment : planService.getPlan().getSegments() ) {
            count += segment.listConnectors().size();
        }
        return count;
    }

}
