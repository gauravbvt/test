// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.util.GUID;

/**
 * A specification of the nature, composition and source of
 * information that is either needed, known or (to be) transmitted.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Information extends AbstractNamedObject {

    private String kind;
    private Source source;
    private List<String> elements = new ArrayList<String>();
    private List<Information> segments = new ArrayList<Information>();

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Information( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of elements.
     */
    public List<String> getElements() {
        return this.elements;
    }

    /**
     * Set the value of elements.
     * @param elements The new value of elements
     */
    public void setElements( List<String> elements ) {
        this.elements = elements;
    }

    /**
     * Return the value of kind.
     */
    public String getKind() {
        return this.kind;
    }

    /**
     * Set the value of kind.
     * @param kind The new value of kind
     */
    public void setKind( String kind ) {
        this.kind = kind;
    }

    /**
     * Return the value of segments.
     */
    public List<Information> getSegments() {
        return this.segments;
    }

    /**
     * Set the value of segments.
     * @param segments The new value of segments
     */
    public void setSegments( List<Information> segments ) {
        this.segments = segments;
    }

    /**
     * Return the value of source.
     */
    public Source getSource() {
        return this.source;
    }

    /**
     * Set the value of source.
     * @param source The new value of source
     */
    public void setSource( Source source ) {
        this.source = source;
    }

    //=====================================
    /**
     * A source of information.
     */
    public static class Source {

        // TODO fix type
        private String role;
        private AbstractNamedObject agent;
        private Organization organization;

        /**
         * Default constructor.
         */
        public Source() {
            super();
        }

        /**
         * Return the value of agent.
         */
        public AbstractNamedObject getAgent() {
            return this.agent;
        }

        /**
         * Set the value of agent.
         * @param agent The new value of agent
         */
        public void setAgent( AbstractNamedObject agent ) {
            this.agent = agent;
        }

        /**
         * Return the value of organization.
         */
        public Organization getOrganization() {
            return this.organization;
        }

        /**
         * Set the value of organization.
         * @param organization The new value of organization
         */
        public void setOrganization( Organization organization ) {
            this.organization = organization;
        }

        /**
         * Return the value of role.
         */
        public String getRole() {
            return this.role;
        }

        /**
         * Set the value of role.
         * @param role The new value of role
         */
        public void setRole( String role ) {
            this.role = role;
        }
    }
}
