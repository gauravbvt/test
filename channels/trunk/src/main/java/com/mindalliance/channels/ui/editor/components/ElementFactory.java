// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.net.URL;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.reference.Location;
import com.mindalliance.channels.data.reference.TypeSet;
import com.mindalliance.channels.data.support.Availability;
import com.mindalliance.channels.data.support.Distance;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.data.support.LatLong;
import com.mindalliance.channels.data.support.Latency;
import com.mindalliance.channels.services.SystemService;
import com.mindalliance.channels.ui.editor.ElementChooser;
import com.mindalliance.channels.util.AbstractJavaBean;


/**
 * BeanView component factory for channels data elements.
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementFactory implements PropertyComponentFactory {

    /**
     * 
     * Default constructor.
     */
    public ElementFactory() {
        
    }
    /**
     * Generates custom editorrs for channels data elements.
     * @param key the name of the field to generate an editor for
     * @param type the name of the class to generate and editor for
     * @param bv the beanview to use
     * @see com.beanview.PropertyComponentFactory#getComponent(java.lang.String, java.lang.Class, com.beanview.BeanView)
     */
    public PropertyComponent getComponent(String key, Class type,
            BeanView bv) {

        PropertyComponent result = null;
        if (Duration.class.isAssignableFrom(type)) {
            result = new DurationEditor();
        } else if (Distance.class.isAssignableFrom(type)) {
            result = new DistanceEditor();
        } else if (Latency.class.isAssignableFrom(type)) {
            result = new LatencyEditor();
        } else if (Availability.class.isAssignableFrom(type)) {
            result = new AvailabilityEditor();
        } else if (URL.class.isAssignableFrom(type)) {
            result = new UrlEditor();
        } else if (LatLong.class.isAssignableFrom(type)) {
            result = new LatLongEditor();
//        } else if (Location.class.isAssignableFrom(type)) {
//            result = new LocationEditor();
        } else if (TypeSet.class.isAssignableFrom(type)) {
            result = new TypeSetEditor();
        } else if (AbstractJavaBean.class.isAssignableFrom(type)) {
            result = new SingleElementBrowser(type, (SystemService)bv.getContext( "system"), (User)bv.getContext( "user" ));
        }
        return result;
    }

}
