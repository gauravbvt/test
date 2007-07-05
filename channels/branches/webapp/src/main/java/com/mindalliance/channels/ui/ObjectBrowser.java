// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import java.util.Collection;

/**
 * A list editor for a collection of objects of similar types.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 * @param <T> the type of the objects in the browser
 */
public interface ObjectBrowser<T> {

    /**
     * Return the common class of the objects.
     */
    Class<T> getObjectClass();

    /**
     * Return the objects shown in this browser.
     */
    Collection<T> getObjects();

    /**
     * Set the objects shown in this browser.
     * @param objects the new objects
     */
    void setObjects( Collection<T> objects );

    /**
     * Add an object to this browser.
     * @param object the object
     */
    void addObject( T object );

    /**
     * Remove an object from this browser.
     * @param object the object
     */
    void removeObject( T object );

    /**
     * Get the current selection from the browser.
     * @return null if none
     */
    T getSelection();

    /**
     * Add an object browser listener.
     * @param listener the listener
     */
    void addObjectBrowserListener( ObjectBrowserListener<T> listener );

    /**
     * Remove an object browser listener.
     * @param listener the listener
     */
    void removeObjectBrowserListener( ObjectBrowserListener<T> listener );
}
