// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor;

import java.util.Collection;

import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

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
     * Test if a kind of object can be edited.
     * @param object the object
     */
    public boolean supports( Object object ) {
        return object != null ;
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

        ObjectBrowserImpl<T> browser =
            new ObjectBrowserImpl<T>( beanClass, system, user );
        browser.setObjects( objects );

        if ( listener != null )
            browser.addObjectBrowserListener( listener );

        return browser;
    }

    /**
     * Popup an editor dialog on the given object.
     * Blocks until the user saves or cancels.
     * @param object the object
     * @return the edited object or null if the user cancelled.
     */
    public JavaBean popupEditor( JavaBean object ) {
        JavaBean result = null;
        if ( supports( object ) )
            try {
                PopupWrapper wrapper =
                    new PopupWrapper( createEditor( object ) );
                wrapper.doModal();
                if ( wrapper.isOk() )
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

    /**
     * Popup wrapper around an editor/browser.
     */
    public class PopupWrapper extends Window {

        private static final String WIDTH = "50%";

        private Component content;
        private boolean ok;

        /**
         * Default constructor.
         * @param content the actual editor
         */
        public PopupWrapper( Component content ) {
            super( "Editor", "normal", false );
            this.content = content;
            this.setSizable( true );

            Button okButton = new Button( "Ok" );
            okButton.addEventListener( "onClick", new EventListener() {
                public boolean isAsap() {
                    return true;
                }

                public void onEvent( Event event ) {
                    setVisible( false );
                    setOk( true );
                }
            } );

            Button cancelButton = new Button( "Cancel" );
            cancelButton.addEventListener( "onClick", new EventListener() {
                public boolean isAsap() {
                    return true;
                }

                public void onEvent( Event event ) {
                    setVisible( false );
                }
            } );

            Hbox buttons = new Hbox();
            buttons.appendChild( okButton );
            buttons.appendChild( cancelButton );

            Vbox vbox = new Vbox();
            vbox.appendChild( content );
            vbox.appendChild( buttons );

            this.appendChild( vbox );
            this.setPage( EditorFactory.this.getPage() );
            this.setWidth( WIDTH );
        }

        /**
         * Return the value of ok.
         */
        public boolean isOk() {
            return this.ok;
        }

        /**
         * Set the value of ok.
         * @param ok The new value of ok
         */
        public void setOk( boolean ok ) {
            this.ok = ok;
        }

        /**
         * Return the value of content.
         */
        public Component getContent() {
            return this.content;
        }
    }
}
