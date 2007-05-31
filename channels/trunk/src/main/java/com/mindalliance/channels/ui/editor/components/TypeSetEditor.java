// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelSet;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Textbox;
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

    private Listbox list;
    private Textbox typeTextbox;
    private Button addButton;
    private Button deleteButton;
    private ListModelSet model;
    private TypeSet set;
    /**
     * 
     * Default constructor.
     */
    public TypeSetEditor() {
        init();
    }
    /**
     * Initialize the ZK components.
     *
     */
    private void init() {
        appendChild(createTextbox());
        appendChild(createTypeList());
    }
    
    private Box createTextbox() {
        Hbox hbox = new Hbox();
        typeTextbox = new Textbox();
        typeTextbox.setCols( 20 );
        hbox.appendChild(typeTextbox);
        addButton = new Button("Add");
        addButton.setImage("images/16x16/add2.png");
        addButton.setTooltiptext("Add a new type");
        addButton.addEventListener("onClick", new EventListener() {
            public boolean isAsap() {
                return false;
            }
            public void onEvent( Event arg0 ) {
                String name = typeTextbox.getText();
                Type type = new Type();
                type.setName(name);
                model.add( type );
            }
            
        });
        hbox.appendChild( addButton );
        return hbox;
    }
    
    private Box createTypeList() {
        Vbox box = new Vbox();
        list = new Listbox(); 
        list.setRows( 6 );
        list.setWidth( "400px" );
        list.setMold( "paging" );
        list.setPageSize( 5 );
        Listhead lh = new Listhead();
        
        Listheader name = new Listheader("Type");
        name.setSort( "auto" );
        lh.appendChild( name);
        list.appendChild(lh);
        box.appendChild(list);
        deleteButton = new Button("Remove");
        deleteButton.setImage( "images/16x16/delete2.png" );
        deleteButton.setTooltiptext( "Delete the selected type" );
        deleteButton.addEventListener( "onClick", new EventListener() {
            public boolean isAsap() {
                return false;
            }
            
            public void onEvent( Event arg0 ) {
                int index = list.getSelectedIndex();
                if ( index >= 0 ) {
                    Type type = (Type)model.getElementAt( index );
                    model.remove( type );
                }
            }
        });
        box.appendChild(deleteButton);
        return box;
    }

    /**
     * Returns the TypeSet instance being edited.
     * @return the instance
     * @see com.beanview.PropertyComponent#getValue()
     */
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
        if (set == null) {
            set = new TypeSet(new Typology());
        }
        model = new ListModelSet(set.getTypes());
        list.setModel( model );
    }

}
