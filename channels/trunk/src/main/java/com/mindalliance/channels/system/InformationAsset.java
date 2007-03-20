// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.reference.ClearanceType;
import com.mindalliance.channels.reference.FormatType;
import com.mindalliance.channels.reference.TitleType;

/**
 * The format in which some information is held in custody
 * and the constraints imposed by the custodian upon its dissemination.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 * @composed - - 1 MetaInformation
 * @composed - - * DisseminationConstraint
 */
public class InformationAsset {

    private MetaInformation content;
    private List<FormatType> formats = new ArrayList<FormatType>();
    private List<DisseminationConstraint> disseminationConstraints =
                  new ArrayList<DisseminationConstraint>();

    /**
     * Default constructor.
     */
    InformationAsset() {
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
    public List<FormatType> getFormats() {
        return this.formats;
    }

    /**
     * Set the value of formats.
     * @param formats The new value of formats
     */
    public void setFormats( List<FormatType> formats ) {
        this.formats = formats;
    }

    /**
     * Add a format.
     * @param format the format
     */
    public void addFormat( FormatType format ) {
        this.formats.add( format );
    }

    /**
     * Remove a format.
     * @param format the format
     */
    public void removeFormat( FormatType format ) {
        this.formats.remove( format );
    }

    /**
     * Return the value of information.
     */
    public MetaInformation getContent() {
        return this.content;
    }

    /**
     * Set the value of information.
     * @param information The new value of information
     */
    public void setContent( MetaInformation information ) {
        this.content = information;
    }

    //==========================================
    /**
     * Constraint on how far information can flow and who
     * is authorized to view it.
     *
     * @opt attributes
     * @composed - - 0..1 SecrecyConstraint
     * @composed - - * PrivacyConstraint
     */
    public static class DisseminationConstraint {

        private SecrecyConstraint secrecyConstraint;
        private List<PrivacyConstraint> privacyConstraints;

        /**
         * Default constructor.
         */
        public DisseminationConstraint() {
        }

        /**
         * Return the value of privacy.
         */
        public List<PrivacyConstraint> getPrivacyConstraints() {
            return this.privacyConstraints;
        }

        /**
         * Set the value of privacy.
         * @param privacy The new value of privacy
         */
        public void setPrivacyConstraints( List<PrivacyConstraint> privacy ) {
            this.privacyConstraints = privacy;
        }

        /**
         * Add a privacy constraint.
         * @param privacy the constraint
         */
        public void addPrivacyConstraint( PrivacyConstraint privacy ) {
            this.privacyConstraints.add( privacy );
        }

        /**
         * Remove a privacy constraint.
         * @param privacy the constraint
         */
        public void removePrivacyConstraint( PrivacyConstraint privacy ) {
            this.privacyConstraints.remove( privacy );
        }

        /**
         * Return the value of secrecy.
         */
        public SecrecyConstraint getSecrecyConstraint() {
            return this.secrecyConstraint;
        }

        /**
         * Set the value of secrecy.
         * @param secrecy The new value of secrecy
         */
        public void setSecrecyConstraint( SecrecyConstraint secrecy ) {
            this.secrecyConstraint = secrecy;
        }
    }

    //==========================================
    /**
     * Restriction on information propagation.
     *
     * @opt attributes
     */
    public static class SecrecyConstraint {

        private ClearanceType classification;
        private Channel.Security channelSecurity;

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
        public ClearanceType getClassification() {
            return this.classification;
        }

        /**
         * Set the value of classification.
         * @param classification The new value of classification
         */
        public void setClassification( ClearanceType classification ) {
            this.classification = classification;
        }
    }

    //==========================================
    /**
     * Restrictions on who is allowed to view information.
     *
     * @opt attributes
     * @navassoc - - 1 Role
     */
    public static class PrivacyConstraint {

        private TitleType title;
        private Role role;

        /**
         * Default constructor.
         */
        public PrivacyConstraint() {
            super();
        }

        /**
         * Return the value of role.
         */
        public Role getRole() {
            return this.role;
        }

        /**
         * Set the value of role.
         * @param role The new value of role
         */
        public void setRole( Role role ) {
            this.role = role;
        }

        /**
         * Return the value of title.
         */
        public TitleType getTitle() {
            return this.title;
        }

        /**
         * Set the value of title.
         * @param title The new value of title
         */
        public void setTitle( TitleType title ) {
            this.title = title;
        }
    }
}
