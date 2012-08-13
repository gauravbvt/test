package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.query.QueryService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The specification for the location of an assigned task.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/19/11
 * Time: 9:56 AM
 */
public class AssignedLocation implements Serializable {

    public enum Kind {
        /**
         * A named place, actual or type.
         */
        NamedPlace,
        /**
         * A place communicated as an element of information.
         */
        CommunicatedPlace,
        /**
         * The jurisdiction of the role of the assigned agent.
         */
        EmploymentJurisdiction,
        /**
         * The jurisdiction of the organization of the assigned agent.
         */
        OrganizationJurisdiction;

        public String getLabel() {
            switch ( this ) {
                case NamedPlace:
                    return "Named";
                case CommunicatedPlace:
                    return "Communicated";
                case EmploymentJurisdiction:
                    return "Agent's jurisdiction";
                case OrganizationJurisdiction:
                    return "Organization's jurisdiction";
                default:
                    throw new RuntimeException();
            }
        }
    }

    private Kind kind = Kind.NamedPlace;
    private Place namedPlace;
    private Subject subject = new Subject();

    public AssignedLocation() {
        kind = Kind.NamedPlace;
        subject = new Subject();
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind( Kind kind ) {
        this.kind = kind;
    }
    
    public String getDisplayName() {
        return kind == Kind.NamedPlace
                ? namedPlace != null
                    ? namedPlace.getName()
                    : ""
                : kind.getLabel().toLowerCase();
    }

    public Place getNamedPlace() {
        return kind == Kind.NamedPlace ? namedPlace : null;
    }

    public void setNamedPlace( Place namedPlace ) {
        assert kind == Kind.NamedPlace;
        this.namedPlace = namedPlace;
    }

    public Subject getSubject() {
        return kind == Kind.CommunicatedPlace ? subject : new Subject();
    }

    public void setSubject( Subject subject ) {
        assert kind == Kind.CommunicatedPlace;
        this.subject = subject == null ? new Subject() : subject;
    }

    public boolean isNamed() {
        return kind == Kind.NamedPlace && namedPlace != null;
    }

    public boolean isCommunicated() {
        return kind == Kind.CommunicatedPlace && !subject.getInfo().isEmpty();
    }

    public boolean isOrganizationJurisdiction() {
        return kind == Kind.OrganizationJurisdiction;
    }

    public boolean isAgentJurisdiction() {
        return kind == Kind.EmploymentJurisdiction;
    }

    public Place getPlaceBasis() {
        return isNamed() ? namedPlace.getPlaceBasis() : null;
    }

    public String getName() {
        return isNamed() ? namedPlace.getName() : "";
    }

    public Map<String, Object> mapState() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "kind", kind.name() );
        if ( isNamed() ) {
            map.put( "name", namedPlace.getName() );
            map.put( "type", namedPlace.isType() );
        } else if ( isCommunicated() ) {
            map.put( "info", subject.getInfo() );
            map.put( "eoi", subject.getContent() );
        }
        return map;
    }

    public void initFromMap( Map<String, Object> map, QueryService queryService ) {
        String kindName = (String) map.get( "kind" );
        if ( kindName.equals( Kind.NamedPlace.name() ) ) {
            kind = Kind.NamedPlace;
            if ( map.containsKey( "type" ) && map.containsKey( "name" ) ) {
                boolean isType = (Boolean) map.get( "type" );
                String name = (String) map.get( "name" );
                namedPlace = isType
                        ? queryService.findEntityType( Place.class, name )
                        : queryService.findActualEntity( Place.class, name );
            }
        } else if ( kindName.equals( Kind.CommunicatedPlace.name() ) ) {
            kind = Kind.CommunicatedPlace;
            if ( map.containsKey( "info" ) ) {
                String info = (String) map.get( "info" );
                String eoi = map.containsKey( "eoi" )
                        ? (String) map.get( "eoi" )
                        : "";
                subject = new Subject( info, eoi );
            }
        } else if ( kindName.equals( Kind.EmploymentJurisdiction.name() ) ) {
            kind = Kind.EmploymentJurisdiction;
        } else if ( kindName.equals( Kind.OrganizationJurisdiction.name() ) ) {
            kind = Kind.OrganizationJurisdiction;
        } else {
            throw new RuntimeException();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if ( isNamed() ) {
            sb.append( getNamedPlace().isType()
                    ? "at a location of type \""
                    : "at the location named \"" );
            sb.append( getNamedPlace().getName() );
            sb.append( "\"" );
        } else if ( isCommunicated() ) {
            sb.append( "at the location communicated as " );
            sb.append( getSubject().toString() );
        } else if ( isAgentJurisdiction() ) {
            sb.append( "within the agent's jurisdiction" );
        } else if ( isOrganizationJurisdiction() ) {
            sb.append( "within the organization's jurisdiction" );
        }
        return sb.toString();
    }

    public boolean hasInfo() {
        return !getSubject().getInfo().isEmpty();
    }

    @Override
    public boolean equals( Object object ) {
        if ( object instanceof AssignedLocation ) {
            AssignedLocation other = (AssignedLocation) object;
            if ( kind != other.getKind() ) return false;
            if ( kind == Kind.NamedPlace ) {
                return ModelObject.areEqualOrNull( namedPlace, other.getNamedPlace() );
            } else if ( kind == Kind.CommunicatedPlace ) {
                return subject.equals( other.getSubject() );
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + kind.hashCode();
        if ( kind == Kind.NamedPlace && namedPlace != null ) {
            hash = hash * 31 + namedPlace.hashCode();
        } else if ( kind == Kind.CommunicatedPlace ) {
            hash = hash * 31 + subject.hashCode();
        }
        return hash;
    }

}
