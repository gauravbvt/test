// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor;

import java.util.Collection;

import org.zkoss.zhtml.Text;
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
     * Create an embedded editor for the given object.
     * @param object the object
     */
    public ObjectEditor createEditor( JavaBean object ) {

// Uncomment the following to catch all the missing hooks to the model.
//
//        if ( object == null )
//            throw new NullPointerException();

        // TODO Create a real editor
        return new PlaceHolderEditor( object );
    }

    /**
     * Create a browser on a list of objects.
     * @param objects the objects. The collection may be added to or deleted
     * (this may not be a good idea...)
     * @param beanClass the class of the objects in the collection
     * @param listener an initial listener (maybe null)
     * @param <T> the type of the objects
     */
    public <T> ObjectBrowser<T> createBrowser(
            Collection<T> objects, Class<T> beanClass,
            ObjectBrowserListener<T> listener ) {

        ObjectBrowserImpl<T> browser = new ObjectBrowserImpl<T>(beanClass, system, user);
        browser.setObjects( objects );
        
        if ( listener != null )
            browser.addObjectBrowserListener( listener );

        return browser;
    }

    /**
     * Popup an editor dialog on the given object.
     * @param object the object
     * @throws UserCancelledException when user cancels the dialog
     */
    public void popupEditor( JavaBean object ) throws UserCancelledException {
        if ( object == null )
            throw new NullPointerException();

        // TODO implement popup
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

    /**
     * Temporary bogus browser...
     */
    public class PlaceHolderBrowser<T> extends Text
        implements ObjectBrowser<T> {

        private Collection<T> objects;
        private Class<T> objectClass;

        /**
         * Default constructor.
         * @param objects the objects to show
         * @param objectClass the class of the objects.
         */
        public PlaceHolderBrowser(
                Collection<T> objects, Class<T> objectClass ) {
            super( "TBD: " + objectClass.getSimpleName() + " list" );
            this.objects = objects;
            this.objectClass = objectClass;
        }

        /**
         * Return the value of objects.
         */
        public Collection<T> getObjects() {
            return this.objects;
        }

        /**
         * Set the value of objects.
         * @param objects The new value of objects
         */
        public void setObjects( Collection<T> objects ) {
            this.objects = objects;
        }

        /**
         * Return the value of objectClass.
         */
        public Class<T> getObjectClass() {
            return this.objectClass;
        }

        /**
         * Add an object.
         * @param object the object
         */
        public void addObject( T object ) {
            objects.add( object );
        }

        /**
         * Remove a listener.
         * @param listener listener
         */
        public void removeObjectBrowserListener(
                ObjectBrowserListener<T> listener ) {
        }

        /**
         * Add a listener.
         * @param listener listener
         */
        public void addObjectBrowserListener(
                ObjectBrowserListener<T> listener ) {
        }

        /**
         * Get the current selection.
         */
        public T getSelection() {
            return null;
        }

        /**
         * Remove an object.
         * @param object the object
         */
        public void removeObject( T object ) {
            objects.remove( object );
        }
    }

    /**
     * Temporary bogus editor...
     */
    public class PlaceHolderEditor extends Text implements ObjectEditor {

        private JavaBean object;

        /**
         * Default constructor.
         * @param object the edited object
         */
        public PlaceHolderEditor( JavaBean object ) {
            super( "TBD: " + object );
            this.object = object;
        }

        /**
         * Return the value of object.
         */
        public JavaBean getObject() {
            return this.object;
        }
    }
}
