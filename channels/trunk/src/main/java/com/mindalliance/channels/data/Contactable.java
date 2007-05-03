/*
 * Created on May 2, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.List;

/**
 * Can be contacted
 * @author jf
 *
 */
public interface Contactable extends Resource {
	
	List<ContactInfo> getContactInfos();

}
