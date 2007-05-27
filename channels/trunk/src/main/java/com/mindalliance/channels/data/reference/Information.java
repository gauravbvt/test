// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.reference;

import java.util.List;

import com.mindalliance.channels.data.elements.resources.Role;
import com.mindalliance.channels.data.support.Level;
import com.mindalliance.channels.util.AbstractJavaBean;
import com.mindalliance.channels.util.GUID;

/**
 * A descriptor for an element categorized by types. For each type
 * given or implied, content is described. There can also be ad hoc
 * content (not about a type). Information content is a tree of named
 * segments (e.g. profile), each containing elements of information
 * (e.g. name, age) and/or segment (e.g. patient.profile). An element
 * of information (EOI) has name, says if its value is known (and if
 * its predetermined), or if needs to be known. Privacy restrictions
 * and confidence values can be assigned at the level of the
 * information (as an aggregate of information content) or at the
 * segment level (for fine grained privacy and confidence valuation).
 * Privacy restrictions are ignored when information is used to
 * express a need to know. Confidence is used to express quality
 * requirements (e.g. I only want this information if there is at
 * least MEDIUM confidence in its accuracy).
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
@SuppressWarnings( "serial" )
public class Information extends AbstractJavaBean implements Describable {

    private TypeSet typesDescribed;
    private List<Content> contents;
    private Level confidence;
    private List<PrivacyConstraint> privacyConstraints;

    /**
     * Default constructor.
     */
    public Information() {
    }

    /**
     * Information is its own descriptor.
     */
    public Information getDescriptor() {
        return this;
    }

    /**
     * Return the confidence.
     */
    public Level getConfidence() {
        return confidence;
    }

    /**
     * Set the confidence.
     * @param confidence the confidence to set
     */
    public void setConfidence( Level confidence ) {
        this.confidence = confidence;
    }

    /**
     * Return the contents.
     */
    public List<Content> getContents() {
        return contents;
    }

    /**
     * Set the contents.
     * @param contents the contents to set
     */
    public void setContents( List<Content> contents ) {
        this.contents = contents;
    }

    /**
     * Add some content.
     * @param content the content
     */
    public void addContent( Content content ) {
        contents.add( content );
    }

    /**
     * Remove some content.
     * @param content the content
     */
    public void removeContent( Content content ) {
        contents.remove( content );
    }

    /**
     * Return the privacy constraints.
     */
    public List<PrivacyConstraint> getPrivacyConstraints() {
        return privacyConstraints;
    }

    /**
     * Set the privacy constraints.
     * @param privacyConstraints the privacyConstraints to set
     */
    public void setPrivacyConstraints(
            List<PrivacyConstraint> privacyConstraints ) {
        this.privacyConstraints = privacyConstraints;
    }

    /**
     * Add a privacy contraint.
     * @param privacyConstraint the constraint
     */
    public void addPrivacyConstraint( PrivacyConstraint privacyConstraint ) {
        privacyConstraints.add( privacyConstraint );
    }

    /**
     * Remove a privacy contraint.
     * @param privacyConstraint the constraint
     */
    public void removePrivacyConstraint( PrivacyConstraint privacyConstraint ) {
        privacyConstraints.remove( privacyConstraint );
    }

    /**
     * Return the types described.
     */
    public TypeSet getTypesDescribed() {
        return typesDescribed;
    }

    /**
     * Set the types described.
     * @param typesDescribed the typesDescribed to set
     */
    public void setTypesDescribed( TypeSet typesDescribed ) {
        this.typesDescribed = typesDescribed;
    }

    /**
     * A named chunk of information content with privacy restrictions
     * and confidence measure in its accuracy.
     */
    public class Segment {

        private String name;
        private List<Segment> subSegments;
        private Level confidence;
        private List<PrivacyConstraint> privacyConstraints;
        private TypeSet classification;

        /**
         * Default constructor.
         */
        public Segment() {
        }

        /**
         * Return the privacy constraints.
         */
        public List<PrivacyConstraint> getPrivacyConstraints() {
            return privacyConstraints;
        }

        /**
         * Set the privacy constraints.
         * @param privacyConstraints the privacyConstraints to set
         */
        public void setPrivacyConstraints(
                List<PrivacyConstraint> privacyConstraints ) {
            this.privacyConstraints = privacyConstraints;
        }

        /**
         * Add a privacy constraint.
         * @param privacyConstraint the constraint
         */
        public void addPrivacyConstraint(
                PrivacyConstraint privacyConstraint ) {

            privacyConstraints.add( privacyConstraint );
        }

        /**
         * Remove a privacy constraint.
         * @param privacyConstraint the constraint
         */
        public void removePrivacyConstraint(
                PrivacyConstraint privacyConstraint ) {

            privacyConstraints.remove( privacyConstraint );
        }

        /**
         * Return the confidence.
         */
        public Level getConfidence() {
            return confidence;
        }

        /**
         * Set the confidence.
         * @param confidence the confidence to set
         */
        public void setConfidence( Level confidence ) {
            this.confidence = confidence;
        }

        /**
         * Return the name.
         */
        public String getName() {
            return name;
        }

        /**
         * Set the name.
         * @param name the name to set
         */
        public void setName( String name ) {
            this.name = name;
        }

        /**
         * Return the subSegments.
         */
        public List<Segment> getSubSegments() {
            return subSegments;
        }

        /**
         * Set the subSegments.
         * @param subSegments the subSegments to set
         */
        public void setSubSegments( List<Segment> subSegments ) {
            this.subSegments = subSegments;
        }

        /**
         * Add a subSegment.
         * @param segment the segment
         */
        public void addSubSegment( Segment segment ) {
            subSegments.add( segment );
        }

        /**
         * Remove a subSegment.
         * @param segment the segment
         */
        public void removeSubSegment( Segment segment ) {
            subSegments.remove( segment );
        }

        /**
         * Return the value of classification.
         */
        public TypeSet getClassification() {
            return classification;
        }

        /**
         * Set the value of classification.
         * @param classification The new value of classification
         */
        public void setClassification( TypeSet classification ) {
            this.classification = classification;
        }
    }

    /**
     * An element of information has a name, a data type (specified
     * for now as a regex) and an indication that the EOI is known,
     * needs to be known or has a specific value.
     */
    class EOI {

        private String name;

        /** Either one of known or needed is true. */
        private boolean isKnown;
        private boolean isNeeded;

        /** At most one of regex or value is set. */
        private String regex;
        private String value;

        /**
         * Default constructor.
         */
        EOI() {
        }

        /**
         * Return if this element of information is known.
         */
        public boolean isKnown() {
            return isKnown;
        }

        /**
         * Set if this element of information is known.
         * @param isKnown the isKnown to set
         */
        public void setKnown( boolean isKnown ) {
            this.isKnown = isKnown;
            if ( isKnown )
                isNeeded = false;
        }

        /**
         * Return if this element of information is needed.
         */
        public boolean isNeeded() {
            return isNeeded;
        }

        /**
         * Set if this element of information is known.
         * @param isNeeded the isNeeded to set
         */
        public void setNeeded( boolean isNeeded ) {
            this.isNeeded = isNeeded;
            if ( isNeeded )
                isKnown = false;
        }

        /**
         * Return the name.
         */
        public String getName() {
            return name;
        }

        /**
         * Set the name.
         * @param name the name to set
         */
        public void setName( String name ) {
            this.name = name;
        }

        /**
         * Return the regex.
         */
        public String getRegex() {
            return regex;
        }

        /**
         * Set the regular expression.
         * @param regex the regex to set
         */
        public void setRegex( String regex ) {
            this.regex = regex;
            value = null;
        }

        /**
         * Return the value.
         */
        public String getValue() {
            return value;
        }

        /**
         * Set the value.
         * @param value the value to set
         */
        public void setValue( String value ) {
            this.value = value;
            regex = null;
        }
    }

    /**
     * Information content associated with a type, or ad hoc.
     */
    public class Content {

        /**
         * null if ad hoc content - if type with GUID deleted then ad hoc
         * content.
         */
        private GUID typeGUID;
        private Segment topSegment;

        /**
         * Default constructor.
         */
        public Content() {
        }

        /**
         * Return whether the typeGUID points to a non-deleted type.
         */
        public boolean isAdHoc() {
            // TODO
            return false;
        }

        /**
         * Return the topSegment.
         */
        public Segment getTopSegment() {
            return topSegment;
        }

        /**
         * Set the topSegment.
         * @param topSegment the topSegment to set
         */
        public void setTopSegment( Segment topSegment ) {
            this.topSegment = topSegment;
        }

        /**
         * Return the typeGUID.
         */
        public GUID getTypeGUID() {
            return typeGUID;
        }

        /**
         * Set the typeGUID.
         * @param typeGUID the typeGUID to set
         */
        public void setTypeGUID( GUID typeGUID ) {
            this.typeGUID = typeGUID;
        }
    }

    /**
     * A specification of the roles authorized to receive this
     * information.
     */
    public class PrivacyConstraint {

        private Pattern<Role> authorizedRole;

        /**
         * Default constructor.
         */
        public PrivacyConstraint() {
        }

        /**
         * Return the authorized role.
         */
        public Pattern<Role> getAuthorizedRole() {
            return authorizedRole;
        }

        /**
         * Set the authorized role.
         * @param authorizedRole the authorizedRole to set
         */
        public void setAuthorizedRole( Pattern<Role> authorizedRole ) {
            this.authorizedRole = authorizedRole;
        }

    }

}
