/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.services.base;

import com.mindalliance.channels.data.system.History;
import com.mindalliance.channels.services.ChannelsService;
import com.mindalliance.channels.services.HistoryService;

/**
 * Implementation of the History service.
 * @author jf
 *
 */
public class HistoryServiceImpl extends AbstractService implements
		HistoryService {
	
	public HistoryServiceImpl(ChannelsService channelsService) {
		super(channelsService);
	}

	private History getHistory() {
		return getSystem().getHistory();
	}

}
