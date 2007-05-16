/*
 * Created on Apr 25, 2007
 */
package com.mindalliance.channels.services;

import org.acegisecurity.annotation.Secured;

import com.mindalliance.channels.data.system.System;

/**
 * Overall System functions and access to all System services.
 * 
 * @author jf
 */
public interface SystemService extends Service {

    /**
     * @return the system
     */
    System getSystem();

    /**
     * @param system the system to set (initialization only)
     */
    @Secured( "ROLE_RUN_AS_SYSTEM")
    public void setSystem( System system );

    /**
     * Get access to the RegistryService service.
     * 
     * @return the RegistryService
     */
    RegistryService getRegistryService();

    /**
     * Get access to the DirectoryService service.
     * 
     * @return the DirectoryService
     */
    DirectoryService getDirectoryService();

    /**
     * Get access to the PortfolioService service.
     * 
     * @return the PortfolioService
     */
    PortfolioService getPortfolioService();

    /**
     * Get access to the LibraryService service.
     * 
     * @return the LibraryService
     */
    LibraryService getLibraryService();

    /**
     * Get access to the HistoryService service.
     * 
     * @return the HistoryService
     */
    HistoryService getHistoryService();

}
