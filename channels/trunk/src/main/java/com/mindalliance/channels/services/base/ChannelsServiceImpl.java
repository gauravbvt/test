/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.services.base;

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
	 * @return the system
	 */
	public System getChannels() {
		return system;
	}


	/**
	 * @param system the system to set
	 */
	public void setChannels(System system) {
		this.system = system;
	}


	/**
	 * @param directoryService the directoryService to set
	 */
	public void setDirectoryService(DirectoryService directoryService) {
		this.directoryService = directoryService;
		this.directoryService.setSystem(system);
		this.directoryService.setChannelsService(this);
	}


	/**
	 * @param historyService the historyService to set
	 */
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
		this.historyService.setSystem(system);
		this.historyService.setChannelsService(this);
	}


	/**
	 * @param libraryService the libraryService to set
	 */
	public void setLibraryService(LibraryService libraryService) {
		this.libraryService = libraryService;
		this.libraryService.setSystem(system);
		this.libraryService.setChannelsService(this);
	}


	/**
	 * @param portfolioService the portfolioService to set
	 */
	public void setPortfolioService(PortfolioService portfolioService) {
		this.portfolioService = portfolioService;
		this.portfolioService.setSystem(system);
		this.portfolioService.setChannelsService(this);
	}


	/**
	 * @param registryService the registryService to set
	 */
	public void setRegistryService(RegistryService registryService) {
		this.registryService = registryService;
		this.registryService.setSystem(system);
		this.registryService.setChannelsService(this);
	}



}
