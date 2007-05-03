/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.services.base;

import com.mindalliance.channels.data.system.Channels;
import com.mindalliance.channels.services.Service;

/**
 * A service with access to the Channels data model.
 * @author jf
 *
 */
abstract public class AbstractService implements Service {
	
	private Channels channels;

	/**
	 * @return the channels
	 */
	public Channels getChannels() {
		return channels;
	}

	/**
	 * @param channels the channels to set
	 */
	public void setChannels(Channels channels) {
		this.channels = channels;
	}

}
