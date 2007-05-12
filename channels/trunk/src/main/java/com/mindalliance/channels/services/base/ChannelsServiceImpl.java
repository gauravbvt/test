/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.services.base;

import org.acegisecurity.annotation.Secured;

import com.mindalliance.channels.data.system.System;
import com.mindalliance.channels.services.ChannelsService;
import com.mindalliance.channels.services.DirectoryService;
import com.mindalliance.channels.services.HistoryService;
import com.mindalliance.channels.services.LibraryService;
import com.mindalliance.channels.services.PortfolioService;
import com.mindalliance.channels.services.RegistryService;

/**
 * Implementation of the System service.
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
	
	public ChannelsServiceImpl() {
		channelsService = this; // needed to implement the Service interface
		this.system = new System();
		directoryService = new DirectoryServiceImpl(channelsService, system);
		historyService = new HistoryServiceImpl(channelsService, system);
		portfolioService = new PortfolioServiceImpl(channelsService, system);
		libraryService = new LibraryServiceImpl(channelsService, system);
		registryService = new RegistryServiceImpl(channelsService, system);
	}


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


	/**
	 * @param directoryService the directoryService to set
	 */
	public void setDirectoryService(DirectoryService directoryService) {
		this.directoryService = directoryService;
	}


	/**
	 * @param historyService the historyService to set
	 */
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}


	/**
	 * @param libraryService the libraryService to set
	 */
	public void setLibraryService(LibraryService libraryService) {
		this.libraryService = libraryService;
	}


	/**
	 * @param portfolioService the portfolioService to set
	 */
	public void setPortfolioService(PortfolioService portfolioService) {
		this.portfolioService = portfolioService;
	}


	/**
	 * @param registryService the registryService to set
	 */
	public void setRegistryService(RegistryService registryService) {
		this.registryService = registryService;
	}


	/**
	 * @return the system (initialization only)
	 */
	@Secured( "ROLE_RUN_AS_SYSTEM")
	public System getSystem() {
		return system;
	}


	/**
	 * @param system the system to set  (initialization only)
	 */
	@Secured( "ROLE_RUN_AS_SYSTEM")
	public void setSystem(System system) {
		this.system = system;
	}



}
