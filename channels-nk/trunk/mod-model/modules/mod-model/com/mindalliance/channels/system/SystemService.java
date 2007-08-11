// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.definitions.LibraryService;
import com.mindalliance.channels.frames.PortfolioService;
import com.mindalliance.channels.profiles.DirectoryService;
import com.mindalliance.channels.support.Element;
import com.mindalliance.channels.support.GUIDFactory;
import com.mindalliance.channels.support.Service;

/**
 * Overall System functions and access to all System services.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public interface SystemService extends Service {

    /**
     * Get all users that have authority over an element.
     *
     * @param element the element
     */
    List<User> getAuthoritativeUsers( Element element );

    /**
     * Whether a user has authority over an element.
     *
     * @param user the user
     * @param element an alement
     */
    boolean hasAuthority( User user, Element element );

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

    /**
     * Get the official GUID factory.
     */
    GUIDFactory getGuidFactory();
}
