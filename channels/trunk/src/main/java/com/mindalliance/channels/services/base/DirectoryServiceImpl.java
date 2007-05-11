/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.services.base;

import com.mindalliance.channels.data.system.Directory;
import com.mindalliance.channels.data.system.System;
import com.mindalliance.channels.services.ChannelsService;
import com.mindalliance.channels.services.DirectoryService;

/**
 * Implementation of the Directory service.
 * @author jf
 *
 */
public class DirectoryServiceImpl extends AbstractService implements
		DirectoryService {
	
	

	public DirectoryServiceImpl(ChannelsService channelsService, System system) {
		super(channelsService, system);
	}

	private Directory getDirectory() {
		return system.getDirectory();
	}

}
