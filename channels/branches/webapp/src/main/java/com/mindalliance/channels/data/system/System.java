// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.system;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.definitions.Library;
import com.mindalliance.channels.data.definitions.LibraryService;
import com.mindalliance.channels.data.frames.Portfolio;
import com.mindalliance.channels.data.frames.PortfolioService;
import com.mindalliance.channels.data.profiles.Directory;
import com.mindalliance.channels.data.profiles.DirectoryService;
import com.mindalliance.channels.data.support.AuditedObject;
import com.mindalliance.channels.data.support.Element;
import com.mindalliance.channels.data.support.Query;
import com.mindalliance.channels.data.support.Queryable;
import com.mindalliance.channels.util.GUIDFactory;

/**
 * The System data model's root bean.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 *
 * @composed - - 1 Registry
 * @composed - - 1 Directory
 * @composed - - 1 History
 * @composed - - 1 Portfolio
 * @composed - - 1 GUIDFactory
 */
@SuppressWarnings( "serial" )
public class System extends AuditedObject
        implements SystemService, Queryable {

    private Registry registry = new Registry();
    private Directory directory = new Directory();
    private History history = new History();
    private Library library = new Library();
    private Portfolio portfolio = new Portfolio();
    private GUIDFactory guidFactory;

    /**
     * Default constructor.
     */
    public System() {
        super();
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
    }

    /**
     * Set the directory.
     * @param directory the directory to set
     */
    public void setDirectory( Directory directory ) {
        this.directory = directory;
    }

    /**
     * Set the history.
     * @param history the history to set
     */
    public void setHistory( History history ) {
        this.history = history;
    }

    /**
     * Set the library.
     * @param library the library to set
     */
    public void setLibrary( Library library ) {
        this.library = library;
    }

    /**
     * Set the portfolio.
     * @param portfolio the portfolio to set
     */
    public void setPortfolio( Portfolio portfolio ) {
        this.portfolio = portfolio;
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
        return true;
    }

    /**
     * Return the value of guidFactory.
     */
    public GUIDFactory getGuidFactory() {
        return this.guidFactory;
    }

    /**
     * Set the value of guidFactory.
     * @param guidFactory The new value of guidFactory
     */
    public void setGuidFactory( GUIDFactory guidFactory ) {
        this.guidFactory = guidFactory;
    }

    /**
     * Return the single result of a query.
     * @see Queryable#findAll(Query, java.util.Map)
     * @param query the query
     * @param bindings the parameters
     */
    public Iterator findAll( Query query, Map bindings ) {
        // TODO Auto-generated method stub
        return new Iterator() {

            public boolean hasNext() {
                return false;
            }

            public Object next() {
                return null;
            }

            public void remove() {
            }
        };
    }

    /**
     * Return the single result of a query, or null if no
     * result was found.
     * @see Queryable#indOne(Query, java.util.Map)
     * @param query the query
     * @param bindings the parameters
     */
    public Object findOne( Query query, Map bindings ) {
        // TODO Auto-generated method stub
        return null;
    }
}
