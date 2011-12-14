package com.mindalliance.channels.api.plan;

import com.mindalliance.channels.api.entities.AgentData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Participation;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web service data element for a plan summary.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/12/11
 * Time: 1:36 PM
 */
@XmlType( propOrder = {"planIdentifier", "description", "participation", "supervised", "documentation"} )

public class PlanSummaryData {

    private PlanService planService;

    public PlanSummaryData() {
        // required
    }

    public PlanSummaryData( PlanService planService ) {
        this.planService = planService;
    }

    @XmlElement
    public PlanIdentifierData getPlanIdentifier() {
        return new PlanIdentifierData( getPlan() );
    }

    @XmlElement
    public String getDescription() {
        return getPlan().getDescription();
    }

    @XmlElement( name = "participatingAs" )
    public AgentData getParticipation() {
        Actor participant = getParticipant();
        return participant == null
                ? null
                : new AgentData( participant, getPlan() );
    }

    @XmlElement( name = "supervised" )
    public List<AgentData> getSupervised() {
        List<AgentData> underlings = new ArrayList<AgentData>(  );
        for ( Actor underling : findSupervised() ) {
            underlings.add(  new AgentData( underling, getPlan() ) );
        }
        return underlings;
    }

    @XmlElement
    public DocumentationData getDocumentation() {
        return new DocumentationData( getPlan() );
    }

    private Actor getParticipant() {
        Participation participation = planService.findParticipation( User.current().getUsername() );
        if ( participation != null ) {
            return participation.getActor();
        } else {
            return null;
        }
    }

    private List<Actor> findSupervised() {
        Actor actor = getParticipant();
        if ( actor == null ) {
            return new ArrayList<Actor>(  );
        } else {
            return planService.findSupervised( actor );
        }
    }

    private Plan getPlan() {
        return planService.getPlan();
    }
}
