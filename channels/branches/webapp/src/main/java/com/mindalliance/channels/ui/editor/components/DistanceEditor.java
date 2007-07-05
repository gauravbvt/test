// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.util.IllegalFormatConversionException;

import org.zkoss.zul.Hbox;
import org.zkoss.zul.Textbox;

import com.beanview.BeanViewGroup;
import com.beanview.PropertyComponent;
import com.mindalliance.channels.data.support.Distance;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;


/**
 * BeanView editor for Distance instances.
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class DistanceEditor extends Hbox implements PropertyComponent {

    private BeanViewGroup<Distance> group;
    private ZkBeanViewPanel<Distance> numberPanel;
    private ZkBeanViewPanel<Distance> unitPanel;
    private Distance duration;
    private final int editorCols = 5;
    
    /**
     * 
     * Default constructor.
     */
    public DistanceEditor() {
        group = new BeanViewGroup<Distance>();
        
        numberPanel = new ZkBeanViewPanel<Distance>();
        numberPanel.setSubView( new String[] {"value"}, false, false );
        group.addBeanView( numberPanel );

        this.appendChild( numberPanel );
        
        unitPanel = new ZkBeanViewPanel<Distance>();
        unitPanel.setSubView( new String[]{"unit"}, false, false );

        group.addBeanView( unitPanel );
        this.appendChild( unitPanel );
    }
    /**
     * Returns the Distance instance being edited.
     * @return the Distance instance
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        group.updateObjectFromPanels();
        if (duration.getUnit() == null && duration.getValue() == null) {
            return null;
        }
        return duration;
    }

    /**
     * Sets the Distance instance to be edited.
     * @param arg0 the instance to be edited
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object arg0 ) {
        if (!(arg0 instanceof Duration) && arg0 != null) {
            throw new IllegalFormatConversionException('d', Distance.class);
        } 
        duration = (Distance)arg0;
        
        if (duration == null) {
            duration = new Distance();
        }
        
        group.setDataObject(duration);
        Textbox box = (Textbox)numberPanel.getPropertyComponent( "value" );
        box.setCols( editorCols );
    }

}
