// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IllegalFormatConversionException;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.User;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementList<T> extends Vbox implements PropertyComponent {

    private Collection<T> values;
    private Listbox box;
    private Class<T> type;
    private System system;
    private User user;
    protected Toolbarbutton createButton;

    protected Toolbarbutton deleteButton;
    
    public ElementList(Class<T> type, System system, User user) {

        this.system = system;
        this.user = user;
        this.type = type;
        init();
        
    }
    
    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        return values;
    }

    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object arg0 ) throws IllegalFormatConversionException {
        if (arg0 == null) {
            values = new ArrayList();
            return;
        }
        if (!(arg0 instanceof Collection)) {
            throw new IllegalFormatConversionException('c', Collection.class);
        }
        values = (Collection<T>)arg0;
        
        //box.clearSelection();
        box.setModel( new SimpleListModel(values.toArray()) );
    }
    
    private void init() {
        
        box = new Listbox();
        box.setRows( 5 );
        box.setWidth( "200px" );
        appendChild(box);
        createButton = new Toolbarbutton();
        createButton.setImage("images/16x16/add2.png");
        createButton.setTooltiptext("Create a new " + type.getSimpleName());

        deleteButton = new Toolbarbutton();
        deleteButton.setImage("images/16x16/delete2.png");
        deleteButton.setTooltiptext("Delete the selected " +  type.getSimpleName());

        org.zkoss.zul.Toolbar toolbar = new org.zkoss.zul.Toolbar();
        toolbar.appendChild(createButton);
        toolbar.appendChild(deleteButton);

        appendChild(box);
        appendChild(toolbar);

    }

}
