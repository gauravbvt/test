/*
 * Created on Apr 25, 2007
 */
package com.mindalliance.channels.services;

import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Element;

/**
 * Overall System functions and access to all System services.
 * 
 * @author jf
 */
public interface SystemService extends Service {

    /**
     * Get all users that have authority over an element.
     * 
     * @param element
     * @return
     */
    List<User> getAuthoritativeUsers( Element element );

    /**
     * Whether a user has authority over an element.
     * 
     * @param user
     * @param element
     * @return
     */
    boolean hasAuthority( User user, Element element );

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
