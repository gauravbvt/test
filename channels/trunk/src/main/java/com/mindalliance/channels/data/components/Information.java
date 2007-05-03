/*
 * Created on Apr 25, 2007
 *
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
 * A descriptor for an element categorized by types. 
 * For each type given or implied, content is described. There can also be ad hoc content (not about a type).
 * Information content is a tree of named segments (e.g. profile), 
 * each containing elements of information (e.g. name, age) and/or segment (e.g. patient.profile).
 * An element of information (EOI) has name, says if its value is known (and if its predetermined), or if needs to be
 * known.
 * Privacy restrictions and confidence values can be assigned at the level of the information (as an aggregate of
 * information content) or at the segment level (for fine grained privacy and confidence valuation). Privacy restrictions
 * are ignored when information is used to express a need to know. Confidence is used to express quality requirements
 * (e.g. I only want this information with at least MEDIUM confidence in its accuracy).
 * 
 * @author jf
 *
 */
@SuppressWarnings("serial")
public class Information extends AbstractJavaBean implements Describable {
	
	/**
	 * A specification of the roles authorized to receive this information.
	 * @author jf
	 *
	 */
	public class PrivacyConstraint {
		
		private Pattern<Role> authorizedRole;
		private Level confidence;
		private List<PrivacyConstraint> privacyConstraints;
		
	}
	/**
	 * Information content associated with a type, or ad hoc
	 * @author jf
	 *
	 */
	public class Content {
		
		private GUID typeGUID; // null if ad hoc content - if type with GUID deleted then ad hoc content
		private Segment topSegment;
		
		public boolean isAdHoc() {
			return false;
		}
	}
	/**
	 * A named chunk of information content with privacy restrictions and confidence measure in its accuracy.
	 * @author jf
	 *
	 */
	public class Segment {
		
		private String name;
		private List<Segment> subSegments;
		private Level confidence;
		private List<PrivacyConstraint> privacyConstraints;
	}
	/**
	 * An element of information has a name, a data type (specified for now as a regex) and an
	 * indication that the EOI is known, needs to be known or has a specific value.
	 * @author jf
	 *
	 */
	class EOI {
		
		// Either one is true
		private boolean isKnown;
		private boolean isNeeded;
		
		// At most one is set
		private String regex;
		private String value;
		
	}
	
	private TypeSet typesDescribed;
	private List<Content> contents;
	private Level confidence;
	private List<PrivacyConstraint> privacyConstraints;


	/**
	 * Information is its own descriptor.
	 * @return self
	 */
	public Information getDescriptor() {
		return this;
	}
	
}
