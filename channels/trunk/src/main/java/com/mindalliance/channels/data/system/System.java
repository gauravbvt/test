/*
 * Created on Apr 28, 2007
 */
package com.mindalliance.channels.data.system;

import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.services.DirectoryService;
import com.mindalliance.channels.services.HistoryService;
import com.mindalliance.channels.services.LibraryService;
import com.mindalliance.channels.services.PortfolioService;
import com.mindalliance.channels.services.RegistryService;
import com.mindalliance.channels.services.SystemService;

/**
 * The System data model's root bean
 * 
 * @author jf
 */
@SuppressWarnings( "serial")
public class System extends AbstractQueryable implements SystemService {

    private Registry registry;
    private Directory directory;
    private History history;
    private Library library;
    private Portfolio portfolio;

    public System() {
        setRegistry( new Registry(this) );
        setDirectory( new Directory(this) );
        setHistory( new History(this) );
        setLibrary( new Library(this) );
        setPortfolio( new Portfolio(this) );
    }
    
    /**
     * @return the directory
     */
    public Directory getDirectory() {
        return directory;
    }

    /**
     * @return the history
     */
    public History getHistory() {
        return history;
    }

    /**
     * @return the library
     */
    public Library getLibrary() {
        return library;
    }

    /**
     * @return the portfolio
     */
    public Portfolio getPortfolio() {
        return portfolio;
    }

    /**
     * @return the registry
     */
    public Registry getRegistry() {
        return registry;
    }

    /**
     * @param registry the registry to set
     */
    public void setRegistry( Registry registry ) {
        this.registry = registry;
    }

    /**
     * @param directory the directory to set
     */
    public void setDirectory( Directory directory ) {
        this.directory = directory;
    }

    /**
     * @param history the history to set
     */
    public void setHistory( History history ) {
        this.history = history;
    }

    /**
     * @param library the library to set
     */
    public void setLibrary( Library library ) {
        this.library = library;
    }

    /**
     * @param portfolio the portfolio to set
     */
    public void setPortfolio( Portfolio portfolio ) {
        this.portfolio = portfolio;
    }

    public DirectoryService getDirectoryService() {
        return directory;
    }

    public HistoryService getHistoryService() {
        return history;
    }

    public LibraryService getLibraryService() {
        return library;
    }

    public PortfolioService getPortfolioService() {
        return portfolio;
    }

    public RegistryService getRegistryService() {
        return registry;
    }

    public List<User> getAuthoritativeUsers( Element element ) {
        return null;
    }

    public boolean hasAuthority( User user, Element element ) {
        return false;
    }

}
