/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.data.system;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;

import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.Element;
import com.mindalliance.channels.data.support.Query;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * Holds queryable, top-level javabeans (i.e. not contained in others).
 * @author jf
 *
 */
abstract public class AbstractQueryable extends AbstractJavaBean implements Queryable {
	
	private Channels channels;
	
	/**
	 * @return the channels
	 */
	public Channels getChannels() {
		return channels;
	}

	/**
	 * @param channels the channels to set
	 */
	public void setChannels(Channels channels) {
		this.channels = channels;
	}

	public Iterator<Element> findAll(Query query, Map bindings) {
		return null;
	}

	public Element findOne(Query query, Map bindings) {
		return null;
	}
	
	protected List<User> getAuthoritativeUsers(Element element) {
		return channels.findAuthoritativeUsers(element);
	}

	
	/**
	 * Get authenticated user in current thread or null if none.
	 * @return
	 */
	protected User getAuthenticatedUser() {
        User user = null ;
        SecurityContext context = SecurityContextHolder.getContext();
        if ( context != null ) {
            Authentication authentication = context.getAuthentication();
            if ( authentication != null )
                user = (User) authentication.getPrincipal();
        }
        return user;
	}

}
