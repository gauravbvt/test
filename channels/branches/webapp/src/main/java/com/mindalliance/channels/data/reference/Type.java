// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.reference;

import java.util.HashSet;
import java.util.Set;

import com.mindalliance.channels.data.reference.Typology.Category;

import static com.mindalliance.channels.data.reference.Typology.Category.*;

/**
 * A categorization of an element.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Type extends ReferenceData implements Describable {

    private Typology typology;
    private Category category;
    private Information descriptor;

    private TypeSet domain;
    private TypeSet standard;
    private TypeSet implied;
    private TypeSet eventTypes;
    private TypeSet issueTypes;

    /**
     * Default constructor.
     */
    public Type() {
        super();
        this.category = Any;
    }

    /**
     * Default constructor.
     * @param typology the typology
     * @param category the category of this type (and subtypes)
     */
    public Type( Typology typology, Category category ) {
        super();

        if ( typology == null )
            throw new NullPointerException();

        this.category = category;
        this.typology = typology;

        domain = new TypeSet( typology, Domain, true );
        standard = new TypeSet( typology, Standard, true );
        eventTypes = new TypeSet( typology, Event, false );
        implied = new TypeSet( typology, category, false );
        issueTypes = new TypeSet( typology, Issue, false );
    }

    /**
     * Convenience constructor.
     * @param name the name of the type
     * @param supertype the default supertype
     * @param category the category
     */
    public Type( String name, Type supertype, Category category ) {
        this( supertype.getTypology(), category );
        setName( name );
        supertype.getImplied().addType( this );
    }

    /**
     * Convenience constructor.
     * @param name the name of the type
     * @param supertype the default supertype
     */
    public Type( String name, Type supertype ) {
        this( name, supertype, supertype.getCategory() );
    }

    /**
     * Return the descriptor.
     */
    public Information getDescriptor() {
        return descriptor;
    }

    /**
     * Set the descriptor.
     * @param descriptor the descriptor to set
     */
    public void setDescriptor( Information descriptor ) {
        this.descriptor = descriptor;
    }

    /**
     * Return the domains this type belongs to.
     */
    public TypeSet getDomains() {
        return domain;
    }

    /**
     * Set the domains.
     * @param domain the domain to set
     */
    public void setDomains( TypeSet domain ) {
        if ( standard.getCategory() != Domain )
            throw new IllegalArgumentException();

        this.domain = domain;
    }

    /**
     * Return the types this one implies (restricted to the same typology).
     */
    public TypeSet getImplied() {
        return implied;
    }

    /**
     * Set the implied types.
     * @param implied the implied to set
     */
    public void setImplied( TypeSet implied ) {
        if ( implied.getTypology() != getTypology()
                || ( getCategory() != Any
                    && implied.getCategory() != getCategory() ) )
            throw new IllegalArgumentException();

        this.implied = implied;

        // TODO Check for cycles
    }

    /**
     * Return the standard.
     */
    public TypeSet getStandard() {
        return standard;
    }

    /**
     * Set the standard.
     * @param standard the standard
     */
    public void setStandard( TypeSet standard ) {
        if ( standard.getCategory() != Standard )
            throw new IllegalArgumentException();

        this.standard = standard;
    }

    /**
     * Return the typology.
     */
    public Typology getTypology() {
        return typology;
    }

    /**
     * Set the value of typology.
     * @param typology The new value of typology
     */
    public void setTypology( Typology typology ) {
        this.typology = typology;
    }

    /**
     * Return the types of events an element of this type could raise.
     */
    public TypeSet getEventTypes() {
        return eventTypes;
    }

    /**
     * Set the event types.
     * @param eventTypes the eventTypes to set
     */
    public void setEventTypes( TypeSet eventTypes ) {
        if ( standard.getCategory() != Event )
            throw new IllegalArgumentException();

        this.eventTypes = eventTypes;
    }

    /**
     * Return the types of issues an element of this type could suffer from.
     */
    public TypeSet getIssueTypes() {
        return issueTypes;
    }

    /**
     * Set the issue types.
     * @param issueTypes the issueTypes to set
     */
    public void setIssueTypes( TypeSet issueTypes ) {
        if ( standard.getCategory() != Issue )
            throw new IllegalArgumentException();

        this.issueTypes = issueTypes;
    }

    /**
     * Get all domains, including those of implied types.
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

    /**
     * Return the value of category.
     */
    public Category getCategory() {
        return this.category;
    }

    /**
     * Set the value of category.
     * @param category The new value of category
     */
    public void setCategory( Category category ) {
        this.category = category;
    }
}
