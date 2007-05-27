// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.reference;

import java.util.List;

import com.mindalliance.channels.data.Regulatable;
import com.mindalliance.channels.data.elements.resources.Organization;

/**
 * A policy issued by some organization and enforced possibly by
 * another.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Policy extends TypedReferenceData {

    private Organization issuer;
    private Organization enforcer;
    private List<Target> forbidden;
    private List<Target> obligated;

    /**
     * Default constructor.
     */
    public Policy() {
        super();
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
    public List<Target> getForbidden() {
        return forbidden;
    }

    /**
     * Set the forbidden targets.
     * @param forbidden the forbidden to set
     */
    public void setForbidden( List<Target> forbidden ) {
        this.forbidden = forbidden;
    }

    /**
     * Add a forbidden target.
     * @param target the target
     */
    public void addForbidden( Target target ) {
        forbidden.add( target );
    }

    /**
     * Remove a forbidden target.
     * @param target the target
     */
    public void removeForbidden( Target target ) {
        forbidden.remove( target );
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
    public List<Target> getObligated() {
        return obligated;
    }

    /**
     * Set the obligated targets.
     * @param obligated the obligated to set
     */
    public void setObligated( List<Target> obligated ) {
        this.obligated = obligated;
    }

    /**
     * Add an obligated target.
     * @param target the target
     */
    public void addObligated( Target target ) {
        obligated.add( target );
    }

    /**
     * Remove an obligated target.
     * @param target the target
     */
    public void removeObligated( Target target ) {
        obligated.remove( target );
    }

    /**
     * A target.
     */
    public class Target {

        private Pattern<Regulatable> regulatablePattern;

        /**
         * Default constructor.
         */
        public Target() {
        }

        /**
         * Test if a regulatable object is targeted.
         * @param regulatable the object
         */
        public boolean isRegulated( Regulatable regulatable ) {
            return regulatablePattern.matches( regulatable );
        }

        /**
         * Return the value of regulatablePattern.
         */
        public Pattern<Regulatable> getRegulatablePattern() {
            return this.regulatablePattern;
        }

        /**
         * Set the value of regulatablePattern.
         * @param regulatablePattern The new value of regulatablePattern
         */
        public void setRegulatablePattern(
                Pattern<Regulatable> regulatablePattern ) {

            this.regulatablePattern = regulatablePattern;
        }
    }

}
