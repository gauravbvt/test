package com.mindalliance.channels.core.community;

import com.mindalliance.channels.core.ModelObjectContext;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.db.data.communities.RegisteredOrganization;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A plan community.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/29/12
 * Time: 3:23 PM
 */
public class PlanCommunity extends ModelObject implements ModelObjectContext {

    public static final String ANY_URI = "__ANY__";

    private static final String UNNAMED = "UNNAMED";

    private long id = -1;
    private String uri;

    /**
     * History of shifts in assignable id lower bounds.
     */
    private Map<Date, Long> idShifts = new HashMap<Date, Long>();


    private String name;
    private String description;
    private String planUri;
    private int planVersion;
    private boolean closed;
    private List<LocationBinding> locationBindings = new ArrayList<LocationBinding>();
    private String plannerSupportCommunity = "";
    private String userSupportCommunity = "";
    private String communityCalendar = "";
    private String communityCalendarHost = "";
    private String communityCalendarPrivateTicket = "";
    private boolean development;
    private Date dateCreated;

    public PlanCommunity() {

    }

    public PlanCommunity( // Plan community for domain planners
                          Plan plan ) {
        uri = plan.getUri();
        planUri = plan.getUri();
        planVersion = plan.getVersion();
        id = plan.getId();
        name = plan.getName();
        development = plan.isDevelopment();
    }

    public PlanCommunity( PlanCommunity planCommunity ) {
        uri = planCommunity.getUri();
        planUri = planCommunity.getPlanUri();
        planVersion = planCommunity.getPlanVersion();
        id = planCommunity.getId();
        name = planCommunity.getName();
        development = planCommunity.isDevelopment();
        idShifts = planCommunity.getIdShifts();
        locationBindings = planCommunity.copyLocationBindings();
        description = planCommunity.getDescription();
        closed = planCommunity.isClosed();
        dateCreated = planCommunity.getDateCreated();
        plannerSupportCommunity = planCommunity.getPlannerSupportCommunity();
        userSupportCommunity = planCommunity.getUserSupportCommunity();
        communityCalendar = planCommunity.getCommunityCalendar();
        communityCalendarHost = planCommunity.getCommunityCalendarHost();
        communityCalendarPrivateTicket = planCommunity.getCommunityCalendarPrivateTicket();
    }

    private List<LocationBinding> copyLocationBindings() {
        List<LocationBinding> copy = new ArrayList<LocationBinding>();
        for ( LocationBinding locationBinding : locationBindings ) {
            copy.add( new LocationBinding( locationBinding ) );
        }
        return copy;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public void setDateCreated( Date dateCreated ) {
        this.dateCreated = dateCreated;
    }

    public Date getDateCreated() {
        return dateCreated == null ? new Date() : dateCreated;
    }

    public void setUri( String uri ) {
        this.uri = uri;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed( boolean closed ) {
        this.closed = closed;
    }

    public List<LocationBinding> getLocationBindings() {
        return locationBindings;
    }

    public void setLocationBindings( List<LocationBinding> locationBindings ) {
        this.locationBindings = locationBindings;
    }

    public void addLocationBinding( Place locationPlaceholder, Place actualLocation ) {
        assert actualLocation.isActual() && !actualLocation.isPlaceholder();
        assert locationPlaceholder.isPlaceholder();
        LocationBinding locationBinding = new LocationBinding( locationPlaceholder, actualLocation );
        if ( !locationBindings.contains( locationBinding ) ) {
            locationBindings.add( locationBinding );
        }
    }

    public Place getLocationBoundTo( Place locationPlaceholder ) {
        for ( LocationBinding locationBinding : locationBindings ) {
            if ( locationBinding.getPlaceholder().equals( locationPlaceholder ) )
                return locationBinding.getLocation();
        }
        return null;
    }

    public List<Place> getBoundLocationPlaceholders() {
        List<Place> boundPlaces = new ArrayList<Place>();
        for ( LocationBinding locationBinding : locationBindings ) {
            if ( locationBinding.isBound() )
                boundPlaces.add( locationBinding.getPlaceholder() );
        }
        return boundPlaces;
    }

    @Override
    public void recordIdShift( long lowerBound ) {
        idShifts.put( new Date(), lowerBound );
    }

    public Map<Date, Long> getIdShifts() {
        return idShifts;
    }

    public void setIdShifts( Map<Date, Long> idShifts ) {
        this.idShifts = idShifts;
    }

    @Override
    public long getIdShiftSince( Date dateOfRecord ) {
        long shift = 0;
        for ( Date shiftDate : getIdShifts().keySet() ) {
            if ( dateOfRecord.before( shiftDate ) ) {
                shift = shift + idShifts.get( shiftDate );
            }
        }
        return shift;
    }

    public Place getLocale( CommunityService communityService ) {
        Place templateLocale = communityService.getPlan().getLocale();
        if ( templateLocale != null ) {
            return getLocationBoundTo( templateLocale );
        } else {
            return null;
        }
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getName() {
        return name == null ? UNNAMED : name;
    }

    public void setName( String name ) {
        this.name = name;
    }


    public String getUri() {
        return uri;
    }

    public int getPlanVersion() {
        return planVersion;
    }

    public String getPlanUri() {
        return planUri;
    }

    public String getCommunityCalendar() {
        return communityCalendar == null ? "" : communityCalendar;
    }

    public void setCommunityCalendar( String communityCalendar ) {
        this.communityCalendar = communityCalendar;
    }

    public String getCommunityCalendarHost() {
        return communityCalendarHost == null ? "" : communityCalendarHost;
    }

    public void setCommunityCalendarHost( String communityCalendarHost ) {
        this.communityCalendarHost = communityCalendarHost;
    }

    public String getUserSupportCommunity() {
        return userSupportCommunity == null ? "" : userSupportCommunity;
    }

    public void setUserSupportCommunity( String supportCommunity ) {
        this.userSupportCommunity = supportCommunity;
    }

    public String getUserSupportCommunity( String defaultName ) {
        String name = getUserSupportCommunity();
        return name.isEmpty() ? defaultName : name;
    }

    public String getPlannerSupportCommunity() {
        return plannerSupportCommunity == null ? "" : plannerSupportCommunity;
    }

    public void setPlannerSupportCommunity( String plannerSupportCommunity ) {
        this.plannerSupportCommunity = plannerSupportCommunity;
    }

    public String getCommunityCalendar( String defaultCalendar ) {
        String name = getCommunityCalendar();
        return name.isEmpty() ? defaultCalendar : name;
    }

    public String getCommunityCalendarHost( String defaultCalendarHost ) {
        String name = getCommunityCalendarHost();
        return name.isEmpty() ? defaultCalendarHost : name;
    }

    public String getCommunityCalendarPrivateTicket(
            String defaultCommunityCalendarPrivateTicket ) {

        String ticket = getCommunityCalendarPrivateTicket();
        return ticket.isEmpty() ? defaultCommunityCalendarPrivateTicket : ticket;
    }

    public String getCommunityCalendarPrivateTicket() {
        return communityCalendarPrivateTicket == null ? "" : communityCalendarPrivateTicket;
    }

    public void setCommunityCalendarPrivateTicket( String communityCalendarPrivateTicket ) {
        this.communityCalendarPrivateTicket = communityCalendarPrivateTicket;
    }


    ////////


    @Override
    public String getClassLabel() {
        return getClass().getSimpleName();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getTypeName() {
        return "Plan community";
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
    }

    public boolean isEditableBy( ChannelsUser user ) {
        if ( isDomainCommunity() ) {
            return isDevelopment() && user.isPlannerOrAdmin( getPlanUri() );
        } else {
            return user.isPlannerOrAdmin( getUri() );
        }
    }

    public boolean isOrganizationLead( ChannelsUser user,
                                       RegisteredOrganization registeredOrganization,
                                       CommunityService communityService ) {
        return communityService.isCommunityPlanner( user ); // todo - change when organization leaders implemented
    }

    public void setPlanUri( String planUri ) {
        this.planUri = planUri;
    }

    public void setPlanVersion( int planVersion ) {
        this.planVersion = planVersion;
    }

    public boolean isDomainCommunity() {
        return getUri().equals( getPlanUri() );
    }

    @Override
    public boolean references( final ModelObject mo ) {
        for ( LocationBinding locationBinding : locationBindings ) {
            return ModelObject.areIdentical( locationBinding.getPlaceholder(), mo )
                    ||  ModelObject.areIdentical( locationBinding.getLocation(), mo );
        }
        return false;
    }

    //////////////////


    @Override
    public boolean isSegmentObject() {
        return false;
    }

    public boolean isDevelopment() {
        return development;
    }

    public void setDevelopment( boolean development ) {
        this.development = development;
    }

    public String toString() {
        return getName();
    }

    @Override
    public boolean equals( Object obj ) {
        return this == obj
                || obj != null && obj instanceof PlanCommunity
                && getUri().equals( ( (PlanCommunity) obj ).getUri() )
                && getPlanUri().equals( ( (PlanCommunity) obj ).getPlanUri() )
                && getPlanVersion() == ( ( (PlanCommunity) obj ).getPlanVersion() );
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash + 31 * getUri().hashCode();
        hash = hash + 31 * getPlanUri().hashCode();
        hash = hash + 31 * Long.valueOf( getPlanVersion() ).hashCode();
        return hash;
    }

    public boolean canBeOpenedForParticipation( CommunityService communityService ) {
        return getLocale( communityService ) != null;
    }
}
