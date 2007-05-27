// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.Element;
import com.mindalliance.channels.services.DirectoryService;
import com.mindalliance.channels.services.HistoryService;
import com.mindalliance.channels.services.LibraryService;
import com.mindalliance.channels.services.PortfolioService;
import com.mindalliance.channels.services.RegistryService;
import com.mindalliance.channels.services.SystemService;

/**
 * The System data model's root bean.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
@SuppressWarnings( "serial" )
public class System extends AbstractQueryable implements SystemService {

    private Registry registry;
    private Directory directory;
    private History history;
    private Library library;
    private Portfolio portfolio;

    /**
     * Default constructor.
     */
    public System() {
        super();

        setRegistry( new Registry( this ) );
        setDirectory( new Directory( this ) );
        setHistory( new History( this ) );
        setLibrary( new Library( this ) );
        setPortfolio( new Portfolio( this ) );
    }

    /**
     * Return the directory.
     */
    public Directory getDirectory() {
        return directory;
    }

    /**
     * Return the history.
     */
    public History getHistory() {
        return history;
    }

    /**
     * Return the library.
     */
    public Library getLibrary() {
        return library;
    }

    /**
     * Return the portfolio.
     */
    public Portfolio getPortfolio() {
        return portfolio;
    }

    /**
     * Return the registry.
     */
    public Registry getRegistry() {
        return registry;
    }

    /**
     * Set the registry.
     * @param registry the registry to set
     */
    public void setRegistry( Registry registry ) {
        this.registry = registry;
        registry.setSystem( this );
    }

    /**
     * Set the directory.
     * @param directory the directory to set
     */
    public void setDirectory( Directory directory ) {
        this.directory = directory;
        directory.setSystem( this );
    }

    /**
     * Set the history.
     * @param history the history to set
     */
    public void setHistory( History history ) {
        this.history = history;
        history.setSystem( this );
    }

    /**
     * Set the library.
     * @param library the library to set
     */
    public void setLibrary( Library library ) {
        this.library = library;
        library.setSystem( this );
    }

    /**
     * Set the portfolio.
     * @param portfolio the portfolio to set
     */
    public void setPortfolio( Portfolio portfolio ) {
        this.portfolio = portfolio;
        portfolio.setSystem( this );
    }

    /**
     * Return the directory service.
     */
    public DirectoryService getDirectoryService() {
        return getDirectory();
    }

    /**
     * Return the history service.
     */
    public HistoryService getHistoryService() {
        return getHistory();
    }

    /**
     * Return the library service.
     */
    public LibraryService getLibraryService() {
        return getLibrary();
    }

    /**
     * Return the portfolio service.
     */
    public PortfolioService getPortfolioService() {
        return getPortfolio();
    }

    /**
     * Return the registry service.
     */
    public RegistryService getRegistryService() {
        return getRegistry();
    }

    /**
     * Return a list of users having authority over an element.
     * @param element the element
     */
    public List<User> getAuthoritativeUsers( Element element ) {
        // TODO
        return new ArrayList<User>();
    }

    /**
     * Test if a user has authority over a given element.
     * @param user the user
     * @param element the element
     */
    public boolean hasAuthority( User user, Element element ) {
        // TODO
        return false;
    }

}
