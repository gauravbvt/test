// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.browser;

import java.util.Collection;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.zk.beanview.model.ZkCollectionModel;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class BrowserListModel extends ZkCollectionModel {
    public BrowserListModel(Collection arg0, Class arg1, Object arg2,
            boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }
    
    public Object get(int inx)
    {
        AbstractElement element = (AbstractElement)this.getElementAt(inx);
        Listitem item = new Listitem();
        item.setId( element.toString() );
        item.appendChild( new Listcell(element.getName()) );
        return item;
    }
}
