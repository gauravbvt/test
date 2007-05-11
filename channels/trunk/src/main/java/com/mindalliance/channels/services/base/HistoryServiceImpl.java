/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.services.base;

import com.mindalliance.channels.data.system.History;
import com.mindalliance.channels.data.system.System;
import com.mindalliance.channels.services.ChannelsService;
import com.mindalliance.channels.services.HistoryService;

/**
 * Implementation of the History service.
 * @author jf
 *
 */
public class HistoryServiceImpl extends AbstractService implements
		HistoryService {
	
	public HistoryServiceImpl(ChannelsService channelsService, System system) {
		super(channelsService, system);
	}

	private History getHistory() {
		return system.getHistory();
	}

}
