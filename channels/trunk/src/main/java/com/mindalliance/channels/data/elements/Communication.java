/*
 * Created on May 1, 2007
 *
 */
package com.mindalliance.channels.data.elements;

import java.util.List;

import com.mindalliance.channels.data.Contactable;
import com.mindalliance.channels.data.beans.ContactInfo;
import com.mindalliance.channels.data.beans.Information;

/**
 * The movement of an asset from a source to a recipient over one or more
 * interoperable channels.
 * A communication may depend on the successful completion of one or more communications.
 * A communication is part of a flow that fulfills, partially or fully, an exchange.
 * @author jf
 *
 */
public class Communication extends AbstractOccurrence {

	private Flow flow; // Flow it's in
	private List<Communication> dependsOn; // Communication that must precede it
	private Contactable source; 
	private Contactable recipient;
	private Information information; // What's communicated
	private Channel sourceChannel;
	private ContactInfo recipientContactInfo;
}
