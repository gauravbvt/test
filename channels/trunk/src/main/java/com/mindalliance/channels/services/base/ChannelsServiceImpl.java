/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.services.base;

import com.mindalliance.channels.services.ChannelsService;
import com.mindalliance.channels.services.DirectoryService;
import com.mindalliance.channels.services.HistoryService;
import com.mindalliance.channels.services.LibraryService;
import com.mindalliance.channels.services.PortfolioService;
import com.mindalliance.channels.services.RegistryService;

/**
 * Implementation of the Channels service.
 * @author jf
 *
 */
public class ChannelsServiceImpl extends AbstractService implements
		ChannelsService {
	
	private DirectoryService directoryService;
	private HistoryService historyService;
	private PortfolioService portfolioService;
	private LibraryService libraryService;
	private RegistryService registryService;


	public DirectoryService getDirectoryService() {
		return directoryService;
	}


	public HistoryService getHistoryService() {
		return historyService;
	}

	public LibraryService getLibraryService() {
		return libraryService;
	}

	public PortfolioService getPortfolioService() {
		return portfolioService;
	}

	public RegistryService getRegistryService() {
		return registryService;
	}


}
