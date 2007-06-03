// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui.editor.components;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.ListModelSet;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.data.reference.Type;
import com.mindalliance.channels.data.reference.TypeSet;
import com.mindalliance.channels.data.reference.Typology;

/**
 * A stand-in TypeSet editor component.
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class TypeSetEditor extends Vbox implements PropertyComponent {

    private static final int LIST_PAGESIZE = 5;
    private static final int TEXT_COLUMNS = 20;
    private static final int LIST_ROWS = 6;

    private Listbox list;
    private Textbox typeTextbox;
    private Toolbarbutton addButton;
    private Toolbarbutton deleteButton;
    private ListModelSet model;
    private TypeSet set;

    /**
     * Default constructor.
     */
    public TypeSetEditor() {
        init();
    }

    /**
     * Initialize the ZK components.
     */
    private void init() {
        appendChild( createTypeList() );
        appendChild( createToolbar() );
        setSclass( "browser-list" );
    }

    private Toolbar createToolbar() {
        Toolbar toolBar = new Toolbar();

        typeTextbox = new Textbox();
        typeTextbox.setCols( TEXT_COLUMNS );

        addButton = new Toolbarbutton( "Add" );
        addButton.setImage( "images/16x16/add2.png" );
        addButton.setTooltiptext( "Add a new type" );
        addButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event arg0 ) {
                Type type = new Type();
                type.setName( typeTextbox.getText() );
                typeTextbox.setText( "" );
                model.add( type );
            }

        } );

        deleteButton = new Toolbarbutton( "Remove" );
        deleteButton.setImage( "images/16x16/delete2.png" );
        deleteButton.setTooltiptext( "Delete the selected type" );
        deleteButton.addEventListener( "onClick", new EventListener() {

            public boolean isAsap() {
                return false;
            }

            public void onEvent( Event arg0 ) {
                int index = list.getSelectedIndex();
                if ( index >= 0 ) {
                    Type type = (Type) model.getElementAt( index );
                    model.remove( type );
                }
            }
        } );

        toolBar.appendChild( typeTextbox );
        toolBar.appendChild( addButton );
        toolBar.appendChild( deleteButton );
        return toolBar;
    }

    private Listbox createTypeList() {
        list = new Listbox();
        list.setRows( LIST_ROWS );
        list.setWidth( "400px" );
        list.setMold( "paging" );
        list.setPageSize( LIST_PAGESIZE );
        Listhead lh = new Listhead();

        Listheader name = new Listheader( "Type" );
        name.setSort( "auto" );
        lh.appendChild( name );
        list.appendChild( lh );

        return list;
    }

    /**
     * Returns the TypeSet instance being edited.
     * @return the instance
     * @see com.beanview.PropertyComponent#getValue()
     */
    @SuppressWarnings( "unchecked" )
    public Object getValue() {
        set.setTypes( model.getInnerSet() );
        return set;
    }

    /**
     * Sets the TypeSet instance to be edited.
     * @param arg0 the instance
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object arg0 ) {
        set = (TypeSet) arg0;
        if ( set == null ) {
            set = new TypeSet( new Typology() );
        }
        model = new ListModelSet( set.getTypes() );
        list.setModel( model );
    }
}
