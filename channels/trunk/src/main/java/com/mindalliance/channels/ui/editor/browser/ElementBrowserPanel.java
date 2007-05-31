// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.browser;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Row;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.User;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;
import com.mindalliance.zk.beanview.ZkBeanViewPanelBase;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementBrowserPanel<V> extends ZkBeanViewPanel<V> {
    public ElementBrowserPanel(Class<V> c, SystemService system,  User user) {
        this.setContext( "class", c );
        this.setContext( "system", system );
        this.setContext( "user", user );
    }
    
    /**
     * Retrieves the appropriate editable entry for a particular bean
     * member and sticks it into the provided row.
     * @see ZkBeanViewPanelBase#setUpEntryField(java.lang.String, int)
     */
    @Override
    protected void setUpEntryField(String key, Row currentRow) {
        // Set up entry field
        ElementBrowserComponentFactory factory = new ElementBrowserComponentFactory(key, helper
                .getPropertyType(key), this);

        PropertyComponent newComponent = factory.getSettable();
        Component zkComponent = (Component) newComponent;

        helper.getComponents().put(key, newComponent);
        currentRow.appendChild(zkComponent);
    }
}
