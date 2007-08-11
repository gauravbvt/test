// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.definitions;

import com.mindalliance.channels.definitions.Category.Taxonomy;

/**
 * A policy issued by some organization and enforced possibly by
 * another.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 *
 * @composed - - * CategorySet
 * @navassoc - issuer 0..1 Organization
 * @navassoc - enforcer 0..1 Organization
 */
public class Policy extends TypedObject {

    private boolean forbidFirst;
    private Organization issuer;
    private Organization enforcer;
    private CategorySet forbidden = new CategorySet();
    private CategorySet obligated = new CategorySet();

    /**
     * Default constructor.
     */
    public Policy() {
        super( null, Taxonomy.Policy );
    }

    /**
     * Test if an object (event, task, etc) is forbidden by
     * this policy.
     * @param object the object
     */
    public boolean forbids( TypedObject object ) {
        CategorySet cats = object.getCategorySet();
        return cats.implies( getForbidden() )
            && !( isForbidFirst() && cats.implies( getObligated() ) );
    }

    /**
     * Test if an object (event, task, etc) is obligated
     * (i.e. must be enforced) by this policy.
     * @param object the object
     */
    public boolean obligates( TypedObject object ) {
        CategorySet cats = object.getCategorySet();
        return cats.implies( getObligated() )
            && !( isForbidFirst() && cats.implies( getForbidden() ) );
    }

    /**
     * Return the enforcing organization.
     */
    public Organization getEnforcer() {
        return enforcer;
    }

    /**
     * Set the enforcing organization.
     * @param enforcer the enforcer to set
     */
    public void setEnforcer( Organization enforcer ) {
        this.enforcer = enforcer;
    }

    /**
     * Return the forbidden targets.
     */
    public CategorySet getForbidden() {
        return forbidden;
    }

    /**
     * Set the forbidden targets.
     * @param forbidden the forbidden to set
     */
    public void setForbidden( CategorySet forbidden ) {
        this.forbidden = forbidden;
    }

    /**
     * Return the issuer organization.
     */
    public Organization getIssuer() {
        return issuer;
    }

    /**
     * Set the issuer organization.
     * @param issuer the issuer to set
     */
    public void setIssuer( Organization issuer ) {
        this.issuer = issuer;
    }

    /**
     * Return the obligated targets.
     */
    public CategorySet getObligated() {
        return obligated;
    }

    /**
     * Set the obligated targets.
     * @param obligated the obligated to set
     */
    public void setObligated( CategorySet obligated ) {
        this.obligated = obligated;
    }

    /**
     * Return the forbidFirst.
     * @return true if forbidden list is applied before the
     * obligated list; false if the other way around.
     */
    public boolean isForbidFirst() {
        return this.forbidFirst;
    }

    /**
     * Set the forbidFirst.
     * @param forbidFirst the forbidFirst
     */
    public void setForbidFirst( boolean forbidFirst ) {
        this.forbidFirst = forbidFirst;
    }
}
