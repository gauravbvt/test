/*
 * Created on May 8, 2007
 *
 */
package com.mindalliance.channels.data.user;

import java.util.List;

import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.data.elements.resources.Person;
import com.mindalliance.channels.util.GUID;

/**
 * Certification that a person does in fact play given roles in an organization.
 * @author jf
 *
 */
public class RoleCertification extends Certification {

	private Person person;
	private Organization organization;
	private List<GUID> roleGUIDs;
	
}
