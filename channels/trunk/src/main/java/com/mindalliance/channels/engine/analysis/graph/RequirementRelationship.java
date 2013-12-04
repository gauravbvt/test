package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CollaborationPlanAnalyst;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Required networking relationship between organizations.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/6/11
 * Time: 10:03 AM
 */
public class RequirementRelationship implements Identifiable {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( RequirementRelationship.class );


    private static final String SEPARATOR = ",";  // must be a comma
    /**
     * Requirements for an agency to share information with another.
     */

    private String relationshipId;
    private List<Requirement> requirements = new ArrayList<Requirement>();
    private Phase.Timing timing = null;
    private Event event = null;

    private String label;

    public RequirementRelationship() {
    }

    public RequirementRelationship(
            Agency fromAgency,
            Agency toAgency,
            Phase.Timing timing,
            Event event ) {
        relationshipId = fromAgency.getUid() + SEPARATOR + toAgency.getUid();
        this.timing = timing;
        this.event = event;
        this.label = "Requirement relationship from " + fromAgency.getName() + " to " + toAgency.getName();
    }


    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements( List<Requirement> requirements ) {
        this.requirements = requirements;
    }

    public Event getEvent() {
        return event;
    }

    public Phase.Timing getTiming() {
        return timing;
    }

    public void addRequirement( Requirement req ) {
        getRequirements().add( req );
    }

    public boolean isEmpty() {
        return requirements.isEmpty();
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId( String id, CommunityService communityService ) {
        this.relationshipId = id;
        CollaborationPlanAnalyst analyst = communityService.getCollaborationPlanAnalyst();
        RequirementRelationship reqRel =
                analyst.findRequirementRelationship(
                        getFromAgency( communityService ),
                        getToAgency( communityService ),
                        timing,
                        event,
                        communityService );
        if ( reqRel != null ) {
            requirements = reqRel.getRequirements();
            label = reqRel.toString();
        }
    }

    private String[] parseId( String id ) throws Exception {
        String[] names = new String[2];
        int index = id.indexOf( SEPARATOR );
        if ( index < 1
                || index == id.length() - SEPARATOR.length() )
            throw new Exception( "Bad requirement relationship id " + id );
        names[0] = id.substring( 0, index );
        names[1] = id.substring( index + SEPARATOR.length() );
        return names;
    }

    public Agency getFromAgency( CommunityService communityService ) {
        try {
            String[] names = parseId( relationshipId );
            return getAgency( names[0], communityService );
        } catch ( Exception e ) {
            LOG.warn( "Failed to get from-agency from relationship " + relationshipId );
            return Agency.UNKNOWN;
        }
    }

    public Agency getToAgency( CommunityService communityService ) {
        try {
            String[] names = parseId( relationshipId );
            return getAgency(  names[1], communityService );
        } catch ( Exception e ) {
            LOG.warn( "Failed to get to-agency from relationship " + relationshipId );
            return Agency.UNKNOWN;
        }
    }

    private Agency getAgency( String id, CommunityService communityService ) {
        try {
            return communityService.getParticipationManager().findAgencyById( id, communityService );
        } catch ( NotFoundException e ) {
            LOG.warn(  "Agency not found at " + id );
            return null;
        }
    }

    public boolean hasUnfulfilledRequirements(
            final Phase.Timing timing,
            final Event event,
            final CommunityService communityService ) {
        return CollectionUtils.exists(
                getRequirements(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((Requirement)object).measureSatisfaction( timing, event, communityService ).isFailed();
                    }
                }
        );
    }

    //////


    @Override
    public String getClassLabel() {
        return getTypeName();
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getTypeName() {
        return "requirement relationship";
    }

    @Override
    public boolean isModifiableInProduction() {
        return true;
    }

    @Override
    public String getName() {
        return "Requirement " + relationshipId;
    }

    @Override
    public String getKindLabel() {
        return getTypeName();
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof RequirementRelationship ) {
            RequirementRelationship other = (RequirementRelationship)object;
          return relationshipId.equals( other.getRelationshipId() )
                  && ChannelsUtils.areEqualOrNull( timing, other.getTiming() )
                  && ChannelsUtils.areEqualOrNull( event, other.getEvent() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash + 31 * relationshipId.hashCode();
        if ( timing != null )
            hash = hash + 31 * timing.hashCode();
        if ( event != null )
            hash = hash + 31 * event.hashCode();
        return hash;
    }

}
