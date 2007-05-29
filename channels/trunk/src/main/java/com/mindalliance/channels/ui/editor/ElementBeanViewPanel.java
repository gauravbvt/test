// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Row;

import com.beanview.PropertyComponent;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.ui.editor.components.ElementComponentFactory;
import com.mindalliance.channels.ui.editor.picker.ElementPicker;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;
import com.mindalliance.zk.beanview.ZkBeanViewPanelBase;
import com.mindalliance.zk.beanview.ZkComponentFactory;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementBeanViewPanel<V> extends ZkBeanViewPanel<V> {
    
    public ElementBeanViewPanel(Class<V> c, System system, Scenario scenario, User user) {
        this.setContext( "class", c );
        this.setContext( "system", system );
        this.setContext( "scenario", scenario );
        this.setContext( "user", user );
    }
    
    public ElementBeanViewPanel(Class<V> c) {

        this.setContext( "class", c );
    }
    
    /**
     * Retrieves the appropriate editable entry for a particular bean
     * member and sticks it into the provided row.
     * @see ZkBeanViewPanelBase#setUpEntryField(java.lang.String, int)
     */
    @Override
    protected void setUpEntryField(String key, Row currentRow) {
        // Set up entry field
        ElementComponentFactory factory = new ElementComponentFactory(key, helper
                .getPropertyType(key), this);

        PropertyComponent newComponent = factory.getSettable();
        Component zkComponent = (Component) newComponent;

        helper.getComponents().put(key, newComponent);
        currentRow.appendChild(zkComponent);
        
    }
}
