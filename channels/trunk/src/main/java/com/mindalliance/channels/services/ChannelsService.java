/*
 * Created on Apr 25, 2007
 *
 */
package com.mindalliance.channels.services;

/**
 * Overall Channels functions and access to all Channels services.
 * @author jf
 *
 */
public interface ChannelsService extends Service {

	/**
	 * Get access to the RegistryService service.
	 * @return the RegistryService
	 */
	RegistryService getRegistryService();
	
	/**
	 * Get access to the DirectoryService service.
	 * @return the DirectoryService
	 */
	DirectoryService getDirectoryService();
	
	/**
	 * Get access to the PortfolioService service.
	 * @return the PortfolioService
	 */
	PortfolioService getPortfolioService();
	
	/**
	 * Get access to the LibraryService service.
	 * @return the LibraryService
	 */
	LibraryService getLibraryService();
	
	
	/**
	 * Get access to the HistoryService service.
	 * @return the HistoryService
	 */
	HistoryService getHistoryService();
	
	
	
	
}
