/*
 * Created on Apr 25, 2007
 */
package com.mindalliance.channels.data.elements.reference;

import java.util.HashSet;
import java.util.Set;

import com.mindalliance.channels.data.Describable;
import com.mindalliance.channels.data.components.Information;
import com.mindalliance.channels.data.support.TypeSet;
import com.mindalliance.channels.util.GUID;

/**
 * A categorization of an element.
 * 
 * @author jf
 */
public class Type extends ReferenceElement implements Describable {

    public static final String MISSION = "Mission";
    public static final String OBJECTIVE = "Objective";
    public static final String EVENT = "Event";
    public static final String DOMAIN = "Domain";
    public static final String ISSUE = "Issue";
    public static final String STANDARD = "Standard";
    public static final String CLEARANCE = "Clearance";
    public static final String FORMAT = "Format";

    private Typology typology;
    private Information descriptor;
    private TypeSet domains = new TypeSet( Type.DOMAIN, TypeSet.SINGLETON );// the
                                                                            // domain
                                                                            // of
                                                                            // this
                                                                            // type
    private TypeSet standards = new TypeSet( Type.STANDARD ); // the
                                                                // standard
                                                                // where
                                                                // this
                                                                // type
                                                                // is
                                                                // defined
    private TypeSet implied = new TypeSet( typology ); // types this
                                                        // one
                                                        // implies,
                                                        // other than
                                                        // the root of
                                                        // the
                                                        // typology
    private TypeSet eventTypes = new TypeSet( Type.EVENT ); // types
                                                            // of
                                                            // events
                                                            // that
                                                            // can be
                                                            // raised
                                                            // by
                                                            // elements
                                                            // of this
                                                            // type
    private TypeSet issueTypes = new TypeSet( Type.ISSUE ); // types
                                                            // of
                                                            // issues
                                                            // that
                                                            // can be
                                                            // associated
                                                            // with
                                                            // elements
                                                            // of this
                                                            // type

    public Type() {
        super();
    }

    public Type( GUID guid ) {
        super( guid );
    }

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
    public TypeSet getDomains() {
        return domains;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomains( TypeSet domains ) {
        this.domains = domains;
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
    public TypeSet getStandards() {
        return standards;
    }

    /**
     * @param standards the standards to set
     */
    public void setStandards( TypeSet standards ) {
        this.standards = standards;
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
        if ( domains != null ) {
            allDomains.addAll( domains.getTypes() );
            if ( implied != null ) {
                for ( Type type : implied.getTypes() ) {
                    allDomains.addAll( type.getAllDomains() );
                }
            }
        }
        return allDomains;
    }
}
