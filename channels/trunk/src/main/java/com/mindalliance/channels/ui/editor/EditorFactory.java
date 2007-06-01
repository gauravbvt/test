// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor;

import java.util.Collection;

import org.zkoss.zk.ui.Page;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.User;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.ui.ObjectBrowser;
import com.mindalliance.channels.ui.ObjectBrowserListener;
import com.mindalliance.channels.ui.ObjectEditor;

/**
 * General purpose editor creator.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class EditorFactory {

    private SystemService system;
    private User user;
    private Page page;

    /**
     * Default constructor.
     */
    public EditorFactory() {
    }

    /**
     * Convenience constructor.
     * @param page the page
     * @param system the system
     * @param user the user
     */
    public EditorFactory( Page page, SystemService system, User user ) {
        setSystem( system );
        setUser( user );
        setPage( page );
    }

    /**
     * Test if a kind of object can be edited.
     * @param object the object
     */
    public boolean supports( Object object ) {
        return object != null;
    }

    /**
     * Create an embedded editor for the given object.
     * @param object the object
     */
    public ObjectEditor createEditor( JavaBean object ) {

        // Uncomment the following to catch all the missing hooks to
        // the model.
        //
        // if ( object == null )
        // throw new NullPointerException();

        return new ElementEditorPanel<JavaBean>( object, system, user, true );
    }

    /**
     * Create a browser on a list of objects.
     * @param objects the objects. The collection may be added to or
     *            deleted (this may not be a good idea...)
     * @param beanClass the class of the objects in the collection
     * @param listener an initial listener (maybe null)
     * @param <T> the type of the objects
     */
    public <T> ObjectBrowser<T> createBrowser( Collection<T> objects,
            Class<T> beanClass, ObjectBrowserListener<T> listener ) {

        ObjectBrowserImpl<T> browser = new ObjectBrowserImpl<T>(
                beanClass, system, user );
        browser.setObjects( objects );

        if ( listener != null )
            browser.addObjectBrowserListener( listener );

        return browser;
    }

    /**
     * Popup an editor dialog on the given object. Blocks until the
     * user saves or cancels.
     * @param object the object
     * @return the edited object or null if the user cancelled.
     */
    public JavaBean popupEditor( JavaBean object ) {
        JavaBean result = null;
        if ( supports( object ) )
            try {
                ElementEditorPanel panel =
                    new ElementEditorPanel<JavaBean>(
                        object, system, user, false );
                panel.setDialog( true );
                panel.setPage( this.getPage() );
                panel.doModal();
                if ( panel.isOk() )
                    result = object;

            } catch ( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        return result;
    }

    /**
     * Return the value of page.
     */
    public Page getPage() {
        return this.page;
    }

    /**
     * Set the value of page.
     * @param page The new value of page
     */
    public void setPage( Page page ) {
        this.page = page;
    }

    /**
     * Return the value of user.
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Set the value of user.
     * @param user The new value of user
     */
    public void setUser( User user ) {
        this.user = user;
    }

    /**
     * Return the value of system.
     */
    public SystemService getSystem() {
        return this.system;
    }

    /**
     * Set the value of system.
     * @param system The new value of system
     */
    public void setSystem( SystemService system ) {
        this.system = system;
    }
}
