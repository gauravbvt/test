// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.browser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IllegalFormatConversionException;
import java.util.List;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

import com.beanview.model.ConvertingSelectionModel;
import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.zk.component.SettableZkList;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class BrowserList extends SettableZkList {

    private static final long serialVersionUID = -3921775129440642578L;

    public BrowserList() {
        this.setRows(12);
        this.setWidth( "200px" );
        //this.setHeight( "100px" );
        this.setMold( "paging" );
        this.setPageSize( 10 );
        Listhead lh = new Listhead();
        
        Listheader name = new Listheader("Name");
        name.setSort( "auto" );
        lh.appendChild( name);
        appendChild(lh);
    }

    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        if (this.getSelectedItems() == null)
            return null;
        if (this.getSelectedItems().size() == 0)
            return null;

        ConvertingSelectionModel converter = (ConvertingSelectionModel) this.getModel();

        List<Object> selection = new ArrayList<Object>();
        for (Object obj : this.getSelectedItems()) {
            if (obj instanceof Listitem) {
                selection.add(((Listitem)obj).getValue());
            } else {
                selection.add(obj);
            }
        }
        return converter.returnSelection(selection.toArray());
    }

    /* (non-Javadoc)
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue(Object in) throws IllegalFormatConversionException {

        if (in == null)
        {
            this.clearSelection();
            return;
        }
        
        if (in instanceof Collection)
        {
            Collection valuesToSet = (Collection) in;
        
            this.clearSelection();
            ListModel available = this.getModel();
            for (int i = 0; i < available.getSize(); i++)
            {
                for (Object currentSetValue : valuesToSet)
                {
                    if (currentSetValue instanceof Listitem) {
                        Listitem li = (Listitem)currentSetValue;
                        //if (li.getId().equals(((AbstractElement)available.getElementAt(i)).getGuid().toString()))
                        if (li.getId().equals( ((AbstractElement)available.getElementAt(i)).toString() ))
                            this.addItemToSelection(li);
                    } else if (currentSetValue.equals(available.getElementAt(i))) {
                        this.setSelectedIndex(i);
                    }
                }
        
            }
        }
    }

}
