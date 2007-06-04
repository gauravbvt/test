// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Window;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.ElementFactory;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.util.GUIDFactoryImpl;

/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementChooser<T> extends Window {

    private ChooserBrowser<T> browser;
    private Button okButton;
    private Button cancelButton;
    private User user;
    private SystemService system;
    private Class<T> type;
    private ChooserHelper chooserHelper;
    private boolean ok = false;
    private ElementFactory elementFactory;
    
    public ElementChooser( Class<T> c, SystemService system,
            User user ) {
        super("Chooser", "normal", false);
        this.system = system;
        this.user = user;
        type = c;
        init();
    }

    private void init() {
        browser = new ChooserBrowser<T>(type, system, user);
        chooserHelper = new ChooserHelper();
        browser.setValue( findObjects() );
        appendChild(browser);
        okButton = new Button("OK");
        okButton.addEventListener( "onClick", new EventListener() {
            public boolean isAsap() {
                return false;
            }
            public void onEvent( Event event ) {
                ok = true;
                ElementChooser.this.setVisible( false );
            }
        });
        cancelButton = new Button("Cancel");
        cancelButton.addEventListener( "onClick", new EventListener() { 
            public boolean isAsap() {
                return false;
            }
            public void onEvent( Event event ) {
                ok = false;
                ElementChooser.this.setVisible( false );
            }
        });
        Hbox box = new Hbox();
        box.appendChild(okButton);
        box.appendChild(cancelButton);
        appendChild(box);
        this.setWidth( "415px" );
    }
    
    public T getSelection() {
        return browser.getSelection();
    }
    
    public Collection<T> findObjects() {
        Collection<T> result = new ArrayList<T>();

        try {
            Class[] paramTypes = { SystemService.class, User.class };
            Object[] args = new Object[] { system, user };
            Method m = chooserHelper.getClass().getMethod(
                    "find" + type.getSimpleName(), paramTypes );
            result = (Collection<T>) m.invoke( chooserHelper, args );
        } catch ( Exception e ) {
            // Do nothing -- This class hasn't been mapped yet.
            // Return an empty list.
        }

        return result;
    }

    
    /**
     * Return the value of ok.
     */
    public boolean isOk() {
        return ok;
    }
    
    private ElementFactory getElementFactory() {
        if (elementFactory == null) {
            elementFactory = new ElementFactory();
            elementFactory.setGuidFactory( new GUIDFactoryImpl() );
        }
        return elementFactory;
    }
    
    private class ChooserBrowser<T> extends AbstractBrowser<T> {

        public ChooserBrowser( Class<T> type, Class collectionType, SystemService system, User user ) {
            super( type, collectionType, system, user );
        }

        public ChooserBrowser( Class<T> type, SystemService system, User user ) {
            super( type, system, user );
        }

        /* (non-Javadoc)
         * @see com.mindalliance.channels.ui.editor.AbstractBrowser#performAddAction()
         */
        @Override
        protected void performAddAction() {
            //JavaBean result = getEditorFactory().popupEditor( (JavaBean) getModel().getObjectClass().newInstance());
            JavaBean instance;
            Class concrete = type;
            if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
                concrete = getEditorFactory().popupInterfaceChooser( type );
            }
            if (concrete != null) {
                
                //instance = getEditorFactory().popupEditor( getElementFactory().newInstance( concrete ) );
                instance = getEditorFactory().popupEditor( newInstance( concrete ) );
                if (instance != null) {
                    getModel().add( instance );
                }
            }
        }

        /* (non-Javadoc)
         * @see com.mindalliance.channels.ui.editor.AbstractBrowser#performEditAction()
         */
        @Override
        protected void performEditAction() {
            int index = getBrowser().getSelectedIndex();
            if ( index >= 0 ) {
                JavaBean result = getEditorFactory().popupEditor(  (JavaBean)getModel().getElementAt( index ) );
                if (result != null) {
                    setObjects(getObjects());
                }
            }
            
            // TODO do whatever needs to be done for persistence
        }

        /* (non-Javadoc)
         * @see com.mindalliance.channels.ui.editor.AbstractBrowser#performRemoveAction()
         */
        @Override
        protected void performRemoveAction() {
            int index = getBrowser().getSelectedIndex();
            if ( index >= 0 ) {
                getModel().remove( getModel().getElementAt( index ) );
            }
            
            // TODO update the global data model
        }

    }
    
    private JavaBean newInstance(Class type) {
        try {
            return (JavaBean)type.newInstance();
        } catch ( InstantiationException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
}
