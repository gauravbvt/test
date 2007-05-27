// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

/**
 * A listener to an object browser.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 * @param <T> the type of objects shown in the browser.
 */
public interface ObjectBrowserListener<T> {

    /**
     * Called when the selection changed in the browser.
     *
     * @param browser the browser
     * @param oldSelection the old selection (may be null)
     * @param newSelection the new selection (may be null)
     */
    void selectionChanged(
            ObjectBrowser<T> browser, T oldSelection, T newSelection );

    /**
     * Called when a single object was added.
     * @param browser the browser
     * @param newObject the object
     */
    void objectAdded( ObjectBrowser<T> browser, T newObject );

    /**
     * Called when a single object was added.
     * @param browser the browser
     * @param removedObject the object
     */
    void objectRemoved( ObjectBrowser<T> browser, T removedObject );

    /**
     * Called when all the objects in the browser have changed.
     * @param browser the browser
     */
    void objectsChanged( ObjectBrowser<T> browser );
}
