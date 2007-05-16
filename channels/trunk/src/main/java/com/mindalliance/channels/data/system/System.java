/*
 * Created on Apr 28, 2007
 */
package com.mindalliance.channels.data.system;

import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.data.elements.ElementFactory;
import com.mindalliance.channels.util.GUIDFactory;
import com.mindalliance.channels.util.GUIDFactoryImpl;

/**
 * The System data model's root bean
 * 
 * @author jf
 */
@SuppressWarnings( "serial")
public class System extends AbstractQueryable {

    private Registry registry;
    private Directory directory;
    private History history;
    private Library library;
    private Portfolio portfolio;

    public System() {
        setRegistry( new Registry() );
        setDirectory( new Directory() );
        setHistory( new History() );
        setLibrary( new Library() );
        setPortfolio( new Portfolio() );
    }
    
    /**
     * Find all users with authority over an element.
     * 
     * @param project
     * @return
     */
    public List<User> findAuthoritativeUsers( Element element ) {
        return null; // TODO
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

}
