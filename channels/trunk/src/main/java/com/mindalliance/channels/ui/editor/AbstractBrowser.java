// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IllegalFormatConversionException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Vbox;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.reference.Type;
import com.mindalliance.channels.data.reference.TypeSet;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.ui.ObjectBrowser;
import com.mindalliance.channels.ui.ObjectBrowserListener;

/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public abstract class AbstractBrowser<T> extends Vbox implements ObjectBrowser<T>,
        PropertyComponent {

    private final List<ObjectBrowserListener> listeners = new ArrayList<ObjectBrowserListener>();
    private SystemService system;
    private User user;
    private BrowserListModel<T> model;
    private Listbox browser;
    private T selection;
    private EditorFactory factory;

    protected abstract void performEditAction();
    protected abstract void performAddAction();
    protected abstract void performRemoveAction();
    
    public AbstractBrowser( Class<T> type, SystemService system, User user ) {
        this.system = system;
        this.user = user;
        model = new BrowserListModel<T>( type );
        // model = new ListModelList();
        init();
    }

    public AbstractBrowser( Class<T> type, Class<T> collectionType,
            SystemService system, User user ) {
        this( type, system, user );
        model.setCollectionType( collectionType );
    }

    private void init() {
        browser = createBrowser();
        setSclass( "browser-list" );
        appendChild( browser );
        appendChild( createButtons() );
    }



    private Listbox createBrowser() {
        browser = new Listbox();
        browser.appendChild( generateHeader() );
        browser.setModel( model );
        browser.setItemRenderer( new BrowserListitemRenderer(
                model.getObjectClass() ) );
        browser.setMold( "paging" );
        setPageSize(5);
        setBrowserWidth("400px");
        browser.addEventListener( "onSelect", new EventListener() {
            public boolean isAsap() {
                return false;
            }
            public void onEvent( Event arg0 ) {
                int index = browser.getSelectedIndex();
                T oldSelection = selection;
                if (index >= 0) {
                    selection = (T)model.getElementAt( index );
                } else {
                    selection = null;
                }
                for ( ObjectBrowserListener<T> l : listeners ) {
                    l.selectionChanged( AbstractBrowser.this, oldSelection, selection );
                }
            }
            
        });
        return browser;
    }

    public void setPageSize(int size) {
        browser.setPageSize( size );
        browser.setRows( size+1 );
    }

    public void setBrowserWidth(String width) {
        browser.setWidth( width );
    }
    
    private Toolbar createButtons() {
        Toolbar buttonBox = new Toolbar();
        buttonBox.appendChild( createAddButton() );
        buttonBox.appendChild( createEditButton() );
        buttonBox.appendChild( createRemoveButton() );
        return buttonBox;
    }
    
    private Toolbarbutton createAddButton() {
        Toolbarbutton addButton = new Toolbarbutton( "Add" );
        addButton.setImage( "images/16x16/add2.png" );
        addButton.setTooltiptext( " Add a "
                + model.getObjectClass().getSimpleName() );
        
        addButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event arg0 ) {
                performAddAction();
            }

        } );
        return addButton;
    }

    private Toolbarbutton createEditButton() {
        Toolbarbutton editButton = new Toolbarbutton( "Edit" );
        editButton.setImage( "images/16x16/preferences.png" );
        editButton.setTooltiptext( "Edit the selected " + model.getObjectClass().getSimpleName() );
        editButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event arg0 ) {
                performEditAction();
            }

        } );
        return editButton;
    }

    protected EditorFactory getEditorFactory() {
        if (factory == null) {
            factory = new EditorFactory(getPage(), system, user);
        }
        return factory;
    }
    
    private Toolbarbutton createRemoveButton() {
        Toolbarbutton removeButton = new Toolbarbutton( "Remove" );
        removeButton.setImage( "images/16x16/delete2.png" );
        removeButton.setTooltiptext( "Remove the selected "
                + model.getObjectClass().getSimpleName() );

        removeButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event arg0 ) {
                performRemoveAction();
            }

        } );
        return removeButton;
    }

    
    
    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.ui.ObjectBrowser#addObject(java.lang.Object)
     */
    public void addObject( T object ) {
        model.add( object );
        for ( ObjectBrowserListener<T> l : listeners ) {
            l.objectAdded( this, object );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.ui.ObjectBrowser#addObjectBrowserListener(com.mindalliance.channels.ui.ObjectBrowserListener)
     */
    public void addObjectBrowserListener( ObjectBrowserListener listener ) {
        listeners.add( listener );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.ui.ObjectBrowser#getObjectClass()
     */
    public Class<T> getObjectClass() {
        return model.getObjectClass();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.ui.ObjectBrowser#getObjects()
     */
    public Collection<T> getObjects() {
        return model.getData();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.ui.ObjectBrowser#getSelection()
     */
    public T getSelection() {
        return selection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.ui.ObjectBrowser#removeObject(java.lang.Object)
     */
    public void removeObject( T object ) {
        model.remove( object );
        for ( ObjectBrowserListener<T> l : listeners ) {
            l.objectRemoved( this, object );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.ui.ObjectBrowser#removeObjectBrowserListener(com.mindalliance.channels.ui.ObjectBrowserListener)
     */
    public void removeObjectBrowserListener( ObjectBrowserListener listener ) {
        listeners.remove( listener );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mindalliance.channels.ui.ObjectBrowser#setObjects(java.util.Collection)
     */
    public void setObjects( Collection<T> objects ) {
        model.clear();
        model.addAll( objects );
        for ( ObjectBrowserListener<T> l : listeners ) {
            l.objectsChanged( this );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        return getObjects();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object vals ) throws IllegalFormatConversionException {
        setObjects( (Collection<T>) vals );

    }

    private Listhead generateHeader() {
        Listhead header = new Listhead();

        if ( AbstractElement.class.isAssignableFrom( model.getObjectClass() ) ) {
            Listheader name = new Listheader( "Name" );
            name.setSort( "auto" );
            header.appendChild( name );
            header.appendChild( new Listheader( "Description" ) );
            header.appendChild( new Listheader( "Types" ) );
        }
        else {
            Listheader name = new Listheader(
                    model.getObjectClass().getSimpleName() );
            name.setSort( "auto" );
            header.appendChild( name );
        }
        return header;
    }

    protected class BrowserListitemRenderer<T> implements ListitemRenderer {

        private Class<T> type;

        public BrowserListitemRenderer( Class<T> type ) {
            this.type = type;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.zkoss.zul.RowRenderer#render(org.zkoss.zul.Row,
         *      java.lang.Object)
         */
        public void render( Listitem row, Object obj ) throws Exception {
            if ( AbstractElement.class.isAssignableFrom( type ) ) {
                AbstractElement el = (AbstractElement) obj;
                generateLabel( el.getName() ).setParent( row );
                generateLabel( el.getDescription() ).setParent( row );
                if ( el.getTypeSet() == null ) {
                    generateLabel( "" ).setParent( row );
                }
                else {
                    generateLabel( generateTypeList(el.getTypeSet()) ).setParent( row );
                }
            }
            else {
                generateLabel( obj.toString() ).setParent( row );
            }
        }

        private Listcell generateLabel( String val ) {
            String truncVal = val;
            if ( truncVal == null ) {
                truncVal = "";
            }
            if ( truncVal.length() > 23 ) {
                truncVal = truncVal.substring( 0, 20 ) + "...";
            }
            Listcell label = new Listcell( truncVal );
            label.setTooltiptext( val );
            return label;
        }

        private String generateTypeList(TypeSet typeset) {
            String result = "";
            for (Iterator<Type> it = typeset.getTypes().iterator() ; it.hasNext() ; ) {
                Type type = it.next();
                result += type.getName();
                if (it.hasNext()) {
                    result += ", ";
                }
            }
            return result; 
        }
        
    }

    protected class BrowserListModel<T> extends ListModelList implements
            ListModel {

        private Class<T> type;
        private Class collType;

        public BrowserListModel( Class<T> type ) {
            this.type = type;
        }

        @Override
        public boolean addAll( Collection c ) {
            if ( c != null ) {
                collType = c.getClass();
                return super.addAll( c );
            }
            return false;
        }

        public Collection<T> getData() {
            Collection<T> result;
            if ( Set.class.isAssignableFrom( collType ) ) {
                result = new HashSet<T>();
                result.addAll( getInnerList() );
            }
            else {
                result = new ArrayList<T>();
                result.addAll( getInnerList() );
            }
            return result;
        }

        public Class<T> getObjectClass() {
            return type;
        }

        public void setCollectionType( Class type ) {
            collType = type;
        }

    }

    
    /**
     * Return the value of browser.
     */
    protected Listbox getBrowser() {
        return browser;
    }

    
    /**
     * Set the value of browser.
     * @param browser The new value of browser
     */
    protected void setBrowser( Listbox browser ) {
        this.browser = browser;
    }
    
    /**
     * Return the value of model.
     */
    protected BrowserListModel<T> getModel() {
        return model;
    }

    
    /**
     * Set the value of model.
     * @param model The new value of model
     */
    protected void setModel( BrowserListModel<T> model ) {
        this.model = model;
    }

    
    /**
     * Return the value of system.
     */
    protected SystemService getSystem() {
        return system;
    }

    
    /**
     * Set the value of system.
     * @param system The new value of system
     */
    protected void setSystem( SystemService system ) {
        this.system = system;
    }

    
    /**
     * Return the value of user.
     */
    protected User getUser() {
        return user;
    }

    
    /**
     * Set the value of user.
     * @param user The new value of user
     */
    protected void setUser( User user ) {
        this.user = user;
    }

    
    /**
     * Set the value of selection.
     * @param selection The new value of selection
     */
    protected void setSelection( T selection ) {
        this.selection = selection;
    }

}
