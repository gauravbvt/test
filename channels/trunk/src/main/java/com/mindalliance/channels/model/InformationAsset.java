// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.util.GUID;

/**
 * The format in which some information is held in custody
 * and the constraints imposed by the custodian upon its dissemination.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class InformationAsset extends AbstractNamedObject {

    private Information information;
    private List<String> formats = new ArrayList<String>();
    private List<DisseminationConstraint> disseminationConstraints =
                  new ArrayList<DisseminationConstraint>();

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    InformationAsset( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of disseminationConstraints.
     */
    public List<DisseminationConstraint> getDisseminationConstraints() {
        return this.disseminationConstraints;
    }

    /**
     * Set the value of disseminationConstraints.
     * @param disseminationConstraints The new value of disseminationConstraints
     */
    public void setDisseminationConstraints(
            List<DisseminationConstraint> disseminationConstraints ) {
        this.disseminationConstraints = disseminationConstraints;
    }

    /**
     * Return the value of formats.
     */
    public List<String> getFormats() {
        return this.formats;
    }

    /**
     * Set the value of formats.
     * @param formats The new value of formats
     */
    public void setFormats( List<String> formats ) {
        this.formats = formats;
    }

    /**
     * Return the value of information.
     */
    public Information getInformation() {
        return this.information;
    }

    /**
     * Set the value of information.
     * @param information The new value of information
     */
    public void setInformation( Information information ) {
        this.information = information;
    }

    //==========================================
    /**
     * Constraint on how far information can flow and who
     * is authorized to view it.
     */
    public static class DisseminationConstraint {

        private List<SecrecyConstraint> secrecy;
        private List<PrivacyConstraint> privacy;

        /**
         * Default constructor.
         */
        public DisseminationConstraint() {
        }

        /**
         * Return the value of privacy.
         */
        public List<PrivacyConstraint> getPrivacy() {
            return this.privacy;
        }

        /**
         * Set the value of privacy.
         * @param privacy The new value of privacy
         */
        public void setPrivacy( List<PrivacyConstraint> privacy ) {
            this.privacy = privacy;
        }

        /**
         * Return the value of secrecy.
         */
        public List<SecrecyConstraint> getSecrecy() {
            return this.secrecy;
        }

        /**
         * Set the value of secrecy.
         * @param secrecy The new value of secrecy
         */
        public void setSecrecy( List<SecrecyConstraint> secrecy ) {
            this.secrecy = secrecy;
        }
    }

    //==========================================
    /**
     * Restriction on information propagation.
     */
    public static class SecrecyConstraint {

        // TODO clearance types
        private String classification;
        private Channel.Security channelSecurity;
        private Organization organization;
        // TODO title types
        private String title;

        /**
         * Default constructor.
         */
        public SecrecyConstraint() {
            super();
        }

        /**
         * Return the value of channelSecurity.
         */
        public Channel.Security getChannelSecurity() {
            return this.channelSecurity;
        }

        /**
         * Set the value of channelSecurity.
         * @param channelSecurity The new value of channelSecurity
         */
        public void setChannelSecurity( Channel.Security channelSecurity ) {
            this.channelSecurity = channelSecurity;
        }

        /**
         * Return the value of classification.
         */
        public String getClassification() {
            return this.classification;
        }

        /**
         * Set the value of classification.
         * @param classification The new value of classification
         */
        public void setClassification( String classification ) {
            this.classification = classification;
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
         * Return the value of title.
         */
        public String getTitle() {
            return this.title;
        }

        /**
         * Set the value of title.
         * @param title The new value of title
         */
        public void setTitle( String title ) {
            this.title = title;
        }
    }

    //==========================================
    /**
     * Restrictions on who is allowed to view information.
     */
    public static class PrivacyConstraint {

        /**
         * Privacy constraint types.
         */
        public enum Constraint { allow, prohibit }

        private Constraint constraint;
        // TODO organization types
        private String organizationKind;
        // TODO role types
        private String agentKind;

        /**
         * Default constructor.
         */
        public PrivacyConstraint() {
            super();
        }

        /**
         * Return the value of agentKind.
         */
        public String getAgentKind() {
            return this.agentKind;
        }

        /**
         * Set the value of agentKind.
         * @param agentKind The new value of agentKind
         */
        public void setAgentKind( String agentKind ) {
            this.agentKind = agentKind;
        }

        /**
         * Return the value of constraint.
         */
        public Constraint getConstraint() {
            return this.constraint;
        }

        /**
         * Set the value of constraint.
         * @param constraint The new value of constraint
         */
        public void setConstraint( Constraint constraint ) {
            this.constraint = constraint;
        }

        /**
         * Return the value of organizationKind.
         */
        public String getOrganizationKind() {
            return this.organizationKind;
        }

        /**
         * Set the value of organizationKind.
         * @param organizationKind The new value of organizationKind
         */
        public void setOrganizationKind( String organizationKind ) {
            this.organizationKind = organizationKind;
        }
    }
}
