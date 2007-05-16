/*
 * Created on Apr 25, 2007
 */
package com.mindalliance.channels.data.components;

import java.util.List;

import com.mindalliance.channels.data.Describable;
import com.mindalliance.channels.data.elements.resources.Role;
import com.mindalliance.channels.data.support.Level;
import com.mindalliance.channels.data.support.Pattern;
import com.mindalliance.channels.data.support.TypeSet;
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
 * @author jf
 */
@SuppressWarnings( "serial")
public class Information extends AbstractJavaBean implements Describable {

    /**
     * A specification of the roles authorized to receive this
     * information.
     * 
     * @author jf
     */
    public class PrivacyConstraint {

        private Pattern<Role> authorizedRole;

        /**
         * @return the authorizedRole
         */
        public Pattern<Role> getAuthorizedRole() {
            return authorizedRole;
        }

        /**
         * @param authorizedRole the authorizedRole to set
         */
        public void setAuthorizedRole( Pattern<Role> authorizedRole ) {
            this.authorizedRole = authorizedRole;
        }

    }

    /**
     * Information content associated with a type, or ad hoc
     * 
     * @author jf
     */
    public class Content {

        private GUID typeGUID; // null if ad hoc content - if type
                                // with GUID deleted then ad hoc
                                // content
        private Segment topSegment;

        /**
         * Whether the typeGUID points to a non-deleted type.
         * 
         * @return
         */
        public boolean isAdHoc() {
            return false; // TODO
        }

        /**
         * @return the topSegment
         */
        public Segment getTopSegment() {
            return topSegment;
        }

        /**
         * @param topSegment the topSegment to set
         */
        public void setTopSegment( Segment topSegment ) {
            this.topSegment = topSegment;
        }

        /**
         * @return the typeGUID
         */
        public GUID getTypeGUID() {
            return typeGUID;
        }

        /**
         * @param typeGUID the typeGUID to set
         */
        public void setTypeGUID( GUID typeGUID ) {
            this.typeGUID = typeGUID;
        }
    }

    /**
     * A named chunk of information content with privacy restrictions
     * and confidence measure in its accuracy.
     * 
     * @author jf
     */
    public class Segment {

        private String name;
        private List<Segment> subSegments;
        private Level confidence;
        private List<PrivacyConstraint> privacyConstraints;

        /**
         * @return the privacyConstraints
         */
        public List<PrivacyConstraint> getPrivacyConstraints() {
            return privacyConstraints;
        }

        /**
         * @param privacyConstraints the privacyConstraints to set
         */
        public void setPrivacyConstraints(
                List<PrivacyConstraint> privacyConstraints ) {
            this.privacyConstraints = privacyConstraints;
        }

        /**
         * @param privacyConstraint
         */
        public void addPrivacyConstraint( PrivacyConstraint privacyConstraint ) {
            privacyConstraints.add( privacyConstraint );
        }

        /**
         * @param privacyConstraint
         */
        public void removePrivacyConstraint( PrivacyConstraint privacyConstraint ) {
            privacyConstraints.remove( privacyConstraint );
        }

        /**
         * @return the confidence
         */
        public Level getConfidence() {
            return confidence;
        }

        /**
         * @param confidence the confidence to set
         */
        public void setConfidence( Level confidence ) {
            this.confidence = confidence;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName( String name ) {
            this.name = name;
        }

        /**
         * @return the subSegments
         */
        public List<Segment> getSubSegments() {
            return subSegments;
        }

        /**
         * @param subSegments the subSegments to set
         */
        public void setSubSegments( List<Segment> subSegments ) {
            this.subSegments = subSegments;
        }

        /**
         * @param segment
         */
        public void addSubSegment( Segment segment ) {
            subSegments.add( segment );
        }

        /**
         * @param segment
         */
        public void removeSubSegment( Segment segment ) {
            subSegments.remove( segment );
        }
    }

    /**
     * An element of information has a name, a data type (specified
     * for now as a regex) and an indication that the EOI is known,
     * needs to be known or has a specific value.
     * 
     * @author jf
     */
    class EOI {

        private String name;
        // Either one is true
        private boolean isKnown;
        private boolean isNeeded;

        // At most one is set
        private String regex;
        private String value;

        /**
         * @return the isKnown
         */
        public boolean isKnown() {
            return isKnown;
        }

        /**
         * @param isKnown the isKnown to set
         */
        public void setKnown( boolean isKnown ) {
            this.isKnown = isKnown;
            if ( isKnown )
                isNeeded = false;
        }

        /**
         * @return the isNeeded
         */
        public boolean isNeeded() {
            return isNeeded;
        }

        /**
         * @param isNeeded the isNeeded to set
         */
        public void setNeeded( boolean isNeeded ) {
            this.isNeeded = isNeeded;
            if ( isNeeded )
                isKnown = false;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName( String name ) {
            this.name = name;
        }

        /**
         * @return the regex
         */
        public String getRegex() {
            return regex;
        }

        /**
         * @param regex the regex to set
         */
        public void setRegex( String regex ) {
            this.regex = regex;
            value = null;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * @param value the value to set
         */
        public void setValue( String value ) {
            this.value = value;
            regex = null;
        }

    }

    private TypeSet typesDescribed;
    private List<Content> contents;
    private Level confidence;
    private List<PrivacyConstraint> privacyConstraints;

    public Information() {
    }

    /**
     * Information is its own descriptor.
     * 
     * @return self
     */
    public Information getDescriptor() {
        return this;
    }

    /**
     * @return the confidence
     */
    public Level getConfidence() {
        return confidence;
    }

    /**
     * @param confidence the confidence to set
     */
    public void setConfidence( Level confidence ) {
        this.confidence = confidence;
    }

    /**
     * @return the contents
     */
    public List<Content> getContents() {
        return contents;
    }

    /**
     * @param contents the contents to set
     */
    public void setContents( List<Content> contents ) {
        this.contents = contents;
    }

    /**
     * @param content
     */
    public void addContent( Content content ) {
        contents.add( content );
    }

    /**
     * @param content
     */
    public void removeContent( Content content ) {
        contents.remove( content );
    }

    /**
     * @return the privacyConstraints
     */
    public List<PrivacyConstraint> getPrivacyConstraints() {
        return privacyConstraints;
    }

    /**
     * @param privacyConstraints the privacyConstraints to set
     */
    public void setPrivacyConstraints(
            List<PrivacyConstraint> privacyConstraints ) {
        this.privacyConstraints = privacyConstraints;
    }

    /**
     * @param privacyConstraint
     */
    public void addPrivacyConstraint( PrivacyConstraint privacyConstraint ) {
        privacyConstraints.add( privacyConstraint );
    }

    /**
     * @param privacyConstraint
     */
    public void removePrivacyConstraint( PrivacyConstraint privacyConstraint ) {
        privacyConstraints.remove( privacyConstraint );
    }

    /**
     * @return the typesDescribed
     */
    public TypeSet getTypesDescribed() {
        return typesDescribed;
    }

    /**
     * @param typesDescribed the typesDescribed to set
     */
    public void setTypesDescribed( TypeSet typesDescribed ) {
        this.typesDescribed = typesDescribed;
    }

}
