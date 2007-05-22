// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.DisplayAs;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.data.Named;
import com.mindalliance.channels.data.elements.assertions.Assertion;
import com.mindalliance.channels.data.reference.TypeSet;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.channels.util.GUID;

/**
 * A generic element.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
@SuppressWarnings( "serial" )
public abstract class AbstractElement extends AbstractJavaBean
    implements Element {

    private GUID guid;
    private String name;
    private String description;
    private TypeSet typeSet;
    private boolean inferred;
    private List<Assertion> assertions = new ArrayList<Assertion>();
    private List<Issue> issues = new ArrayList<Issue>();

    /**
     * Default constructor.
     */
    public AbstractElement() {
    }

    /**
     * Default constructor.
     * @param guid the guid
     */
    public AbstractElement( GUID guid ) {
        this.guid = guid;
    }

    /**
     * Compares this named object with the specified named object for
     * order.
     *
     * @param named the named object to compare to
     */
    public int compareTo( Named named ) {
        return getName().compareTo( named.getName() );
    }

    /**
     * Get the domains.
     */
    public TypeSet getDomains() {
        return getTypeSet().getDomains();
    }

    /**
     * Test if a user has authority over this object.
     * @param user the user
     */
    public boolean hasAuthority( User user ) {
        // TODO
        return false;
    }

    /**
     * Set the guid.
     * @param guid the guid to set
     */
    public void setGuid( GUID guid ) {
        this.guid = guid;
    }

    /**
     * Return the guid.
     */
    public GUID getGuid() {
        return guid;
    }

    /**
     * Set the types.
     * @param typeSet the types to set
     */
    public void setTypeSet( TypeSet typeSet ) {
        this.typeSet = typeSet;
    }

    /**
     * Return the type set.
     */
    @DisplayAs( direct = "types:",
                reverse = "type set for {1}",
                reverseMany = "type set for:"
                )
    public TypeSet getTypeSet() {
        return typeSet;
    }

    /**
     * Set the description.
     * @param description the description to set
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * Return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the name.
     * @param name the name to set
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Mark this element as inferred.
     * @param inferred the inferred to set
     */
    public void setInferred( boolean inferred ) {
        this.inferred = inferred;
    }

    /**
     * Test if this element is inferred.
     */
    public boolean isInferred() {
        return inferred;
    }

    /**
     * Get the issues attached to this element.
     */
    public List<Issue> getIssues() {
        return issues;
    }

    /**
     * Set the issues.
     * @param issues the issues to set
     */
    public void setIssues( List<Issue> issues ) {
        this.issues = issues;
    }

    /**
     * Add an issue.
     * @param issue the issue
     */
    public void addIssue( Issue issue ) {
        issues.add( issue );
    }

    /**
     * Remove an issue.
     * @param issue the issue
     */
    public void removeIssue( Issue issue ) {
        issues.remove( issue );
    }

    /**
     * Get the assertions.
     * @return the assertions
     */
    public List<Assertion> getAssertions() {
        return assertions;
    }

    /**
     * Set the assertions.
     * @param assertions the assertions to set
     */
    public void setAssertions( List<Assertion> assertions ) {
        this.assertions = assertions;
    }

    /**
     * Add an assertion.
     * @param assertion the assertion
     */
    public void addAssertion( Assertion assertion ) {
        assertions.add( assertion );
    }

    /**
     * Remove an assertion.
     * @param assertion the assertion
     */
    public void removeAssertion( Assertion assertion ) {
        assertions.remove( assertion );
    }

    /** Provide a printed form.
     */
    @Override
    public String toString() {
        return getName();
    }
}
