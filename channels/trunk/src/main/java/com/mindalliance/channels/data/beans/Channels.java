/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import java.util.List;
import com.mindalliance.channels.data.elements.User;
import com.mindalliance.channels.data.Element;


/**
 * The Channels data model's root bean
 * @author jf
 *
 */
@SuppressWarnings("serial")
public class Channels extends AbstractQueryable {
	

    private Registry registry;
	private Directory directory;
	private History history;
	private Library library;
	private Portfolio portfolio;
	
	/**
	 * Find all users with authority over an element.
	 * @param project
	 * @return
	 */
	public List<User> findAuthoritativeUsers(Element element) {
		return null;
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
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	

}
