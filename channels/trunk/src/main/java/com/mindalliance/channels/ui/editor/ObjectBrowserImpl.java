// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Vbox;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.ui.ObjectBrowser;
import com.mindalliance.channels.ui.ObjectBrowserListener;

/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ObjectBrowserImpl<T> extends Vbox implements ObjectBrowser<T>,
        PropertyComponent {

    private final List<ObjectBrowserListener> listeners = new ArrayList<ObjectBrowserListener>();
    private SystemService system;
    private User user;
    private BrowserListModel<T> model;
    private Listbox browser;
    private T selection;

    public ObjectBrowserImpl( Class<T> type, SystemService system, User user ) {
        this.system = system;
        this.user = user;
        model = new BrowserListModel<T>( type );
        // model = new ListModelList();
        init();
    }

    public ObjectBrowserImpl( Class<T> type, Class<T> collectionType,
            SystemService system, User user ) {
        this( type, system, user );
        model.setCollectionType( collectionType );
    }

    private void init() {
        browser = createBrowser();
        appendChild( browser );
        appendChild( createButtons() );
    }



    private Listbox createBrowser() {
        final Listbox browser = new Listbox();
        browser.appendChild( generateHeader() );
        browser.setModel( model );
        browser.setItemRenderer( new BrowserListitemRenderer(
                model.getObjectClass() ) );
        browser.setRows( 6 );
        browser.setWidth( "400px" );
        browser.setMold( "paging" );
        browser.setPageSize( 5 );
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
                    l.selectionChanged( ObjectBrowserImpl.this, oldSelection, selection );
                }
            }
            
        });
        return browser;
    }

    private Box createButtons() {
        Hbox buttonBox = new Hbox();
        buttonBox.appendChild( createAddButton() );
        buttonBox.appendChild( createEditButton() );
        buttonBox.appendChild( createRemoveButton() );
        return buttonBox;
    }
    
    private Button createAddButton() {
        Button addButton = new Button( "Add" );
        addButton.setImage( "images/16x16/add2.png" );
        addButton.setTooltiptext( " Add a "
                + model.getObjectClass().getSimpleName() );
        return addButton;
    }

    private Button createEditButton() {
        Button editButton = new Button( "Edit" );
        editButton.setImage( "images/16x16/preferences.png" );
        editButton.setTooltiptext( "Edit the selected " + model.getObjectClass().getSimpleName() );
        editButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event arg0 ) {
                int index = browser.getSelectedIndex();
                if ( index >= 0 ) {
                    try {
                        Object object = model.getElementAt( index );
                        ElementEditorPanel panel = new ElementEditorPanel((JavaBean)object, system, user);
                        panel.setDialog(true);
                        panel.setPage( ObjectBrowserImpl.this.getPage() );
                        panel.doModal();
                    } catch ( InterruptedException e ) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        } );
        return editButton;
    }

    private Button createRemoveButton() {
        Button removeButton = new Button( "Remove" );
        removeButton.setImage( "images/16x16/delete2.png" );
        removeButton.setTooltiptext( "Remove the selected "
                + model.getObjectClass().getSimpleName() );

        removeButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event arg0 ) {
                int index = browser.getSelectedIndex();
                if ( index >= 0 ) {
                    model.remove( model.getElementAt( index ) );
                }
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

    private class BrowserListitemRenderer<T> implements ListitemRenderer {

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
                    generateLabel( el.getTypeSet().toString() ).setParent( row );
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

    }

    private class BrowserListModel<T> extends ListModelList implements
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

}
