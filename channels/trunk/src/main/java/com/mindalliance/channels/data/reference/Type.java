/*
 * Created on Apr 25, 2007
 */
package com.mindalliance.channels.data.reference;

import java.util.HashSet;
import java.util.Set;

import com.mindalliance.channels.data.Describable;
import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.support.TypeSet;

/**
 * A categorization of an element.
 * 
 * @author jf
 */
public class Type extends ReferenceData implements Describable {

    public static final String MISSION = "Mission";
    public static final String OBJECTIVE = "Objective";
    public static final String EVENT = "Event";
    public static final String DOMAIN = "Domain";
    public static final String ISSUE = "Issue";
    public static final String STANDARD = "Standard";
    public static final String CLEARANCE = "Clearance";
    public static final String FORMAT = "Format";
    public static final String LOCATION = "Location";

    private Typology typology;
    private Information descriptor;
    // The domain this type belong to
    private TypeSet domain = new TypeSet( Type.DOMAIN, TypeSet.SINGLETON );
    // The standard this type is part of
    private TypeSet standard = new TypeSet( Type.STANDARD, TypeSet.SINGLETON  );
    // The types this one implies (restricted to the same typology)
    private TypeSet implied;
    // Types of events an element of this type could raise
    private TypeSet eventTypes = new TypeSet( Type.EVENT );
    // Types of issues an element of this type could suffer from
    private TypeSet issueTypes = new TypeSet( Type.ISSUE );
    /**
     * @return the descriptor
     */
    public Information getDescriptor() {
        return descriptor;
    }

    /**
     * @param descriptor the descriptor to set
     */
    public void setDescriptor( Information descriptor ) {
        this.descriptor = descriptor;
    }

    /**
     * @return the domain
     */
    public TypeSet getDomain() {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomains( TypeSet domain ) {
        this.domain = domain;
    }

    /**
     * @return the implied
     */
    public TypeSet getImplied() {
        return implied;
    }

    /**
     * @param implied the implied to set
     */
    public void setImplied( TypeSet implied ) {
        this.implied = implied;
    }

    /**
     * @return the standards
     */
    public TypeSet getStandard() {
        return standard;
    }

    /**
     * @param standards the standards to set
     */
    public void setStandard( TypeSet standard ) {
        this.standard = standard;
    }

    /**
     * @return the typology
     */
    public Typology getTypology() {
        return typology;
    }

    /**
     * @param typology the typology to set
     */
    public void setTypology( Typology typology ) {
        this.typology = typology;
        implied = new TypeSet( typology );
    }

    /**
     * @return the eventTypes
     */
    public TypeSet getEventTypes() {
        return eventTypes;
    }

    /**
     * @param eventTypes the eventTypes to set
     */
    public void setEventTypes( TypeSet eventTypes ) {
        this.eventTypes = eventTypes;
    }

    /**
     * @return the issueTypes
     */
    public TypeSet getIssueTypes() {
        return issueTypes;
    }

    /**
     * @param issueTypes the issueTypes to set
     */
    public void setIssueTypes( TypeSet issueTypes ) {
        this.issueTypes = issueTypes;
    }

    /**
     * Get all domains, including those of implied types
     * 
     * @return
     */
    public Set<Type> getAllDomains() {
        Set<Type> allDomains = new HashSet<Type>();
        if ( domain != null ) {
            allDomains.addAll( domain.getTypes() );
            if ( implied != null ) {
                for ( Type type : implied.getTypes() ) {
                    allDomains.addAll( type.getAllDomains() );
                }
            }
        }
        return allDomains;
    }
}
