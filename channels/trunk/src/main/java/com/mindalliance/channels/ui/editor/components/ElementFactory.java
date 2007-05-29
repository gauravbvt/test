// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.


package com.mindalliance.channels.ui.editor.components;

import java.net.URL;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;
import com.beanview.PropertyComponentFactory;
import com.mindalliance.channels.User;
import com.mindalliance.channels.data.elements.project.Scenario;
import com.mindalliance.channels.data.reference.Location;
import com.mindalliance.channels.data.reference.TypeSet;
import com.mindalliance.channels.data.support.Availability;
import com.mindalliance.channels.data.support.Distance;
import com.mindalliance.channels.data.support.Duration;
import com.mindalliance.channels.data.support.LatLong;
import com.mindalliance.channels.data.support.Latency;
import com.mindalliance.channels.ui.editor.ElementChooser;
import com.mindalliance.channels.util.AbstractJavaBean;


/**
 * @author <a href="mailto:dfeeney@mind-alliance.com">dfeeney</a>
 * @version $Revision:$
 */
public class ElementFactory implements PropertyComponentFactory {

    /* (non-Javadoc)
     * @see com.beanview.PropertyComponentFactory#getComponent(java.lang.String, java.lang.Class, com.beanview.BeanView)
     */
    public PropertyComponent getComponent(String key, Class type,
            BeanView bv) {

        if (Duration.class.isAssignableFrom(type)) {
            return new DurationEditor();
        }
        if (Distance.class.isAssignableFrom(type)) {
            return new DistanceEditor();
        }
        if (Latency.class.isAssignableFrom(type)) {
            return new LatencyEditor();
        }
        if (Availability.class.isAssignableFrom(type)) {
            return new AvailabilityEditor();
        }
        if (URL.class.isAssignableFrom(type)) {
            return new UrlEditor();
        }
        if (LatLong.class.isAssignableFrom(type)) {
            return new LatLongEditor();
        }
        if (Location.class.isAssignableFrom(type)) {
            return new LocationEditor();
        }
        if (TypeSet.class.isAssignableFrom(type)) {
            return new TypeSetEditor();
        }
        
        if (AbstractJavaBean.class.isAssignableFrom(type)) {
            return new ElementChooser(type, (System)bv.getContext( "system"), (Scenario)bv.getContext( "scenario" ), (User)bv.getContext( "user" ));
        }
        return null;
    }

}
