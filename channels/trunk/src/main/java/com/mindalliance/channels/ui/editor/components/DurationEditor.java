// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.util.IllegalFormatConversionException;

import org.zkoss.zul.Hbox;
import org.zkoss.zul.Textbox;

import com.beanview.BeanViewGroup;
import com.beanview.PropertyComponent;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;


/**
 * Editor for a Duration instance.
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class DurationEditor extends Hbox implements PropertyComponent {

    private BeanViewGroup<Duration> group;
    private ZkBeanViewPanel<Duration> numberPanel;
    private ZkBeanViewPanel<Duration> unitPanel;
    private Duration duration;
    private final int editorCols = 5;
    
    /**
     * 
     * Default constructor.
     */
    public DurationEditor() {
        group = new BeanViewGroup<Duration>();
        
        numberPanel = new ZkBeanViewPanel<Duration>();
        numberPanel.setSubView( new String[] {"number"}, false, false );
        group.addBeanView( numberPanel );

        this.appendChild( numberPanel );
        
        unitPanel = new ZkBeanViewPanel<Duration>();
        unitPanel.setSubView( new String[]{"unit"}, false, false );

        group.addBeanView( unitPanel );
        this.appendChild( unitPanel );
    }
    /**
     * Retrieves the Duration instance being edited.
     * @return the Duration instance
     * @see com.beanview.PropertyComponent#getValue()
     */
    public Object getValue() {
        group.updateObjectFromPanels();
        if (duration.getUnit() == Duration.Unit.msec && duration.getNumber() == 0.0) {
            return null;
        }
        return duration;
    }

    /**
     * Sets the edited Duration instance.
     * @param arg0 the instance to be edited
     * @see com.beanview.PropertyComponent#setValue(java.lang.Object)
     */
    public void setValue( Object arg0 ) {
        if (!(arg0 instanceof Duration) && arg0 != null) {
            throw new IllegalFormatConversionException('d', Duration.class);
        } 
        duration = (Duration)arg0;
        
        if (duration == null) {
            duration = new Duration();
        }
        
        group.setDataObject(duration);
        Textbox box = (Textbox)numberPanel.getPropertyComponent( "number" );
        box.setCols( editorCols );
    }

}
