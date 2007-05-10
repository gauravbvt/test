/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.services.base;

import com.mindalliance.channels.data.system.Library;
import com.mindalliance.channels.services.LibraryService;

/**
 * Implementation of the Library service.
 * @author jf
 *
 */
public class LibraryServiceImpl extends AbstractService implements
		LibraryService {
	
	private Library getLibrary() {
		return getSystem().getLibrary();
	}

}
