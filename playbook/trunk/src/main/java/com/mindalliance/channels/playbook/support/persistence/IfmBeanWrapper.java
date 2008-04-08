package com.mindalliance.channels.playbook.support.persistence;

import org.ho.yaml.wrapper.DefaultBeanWrapper;
import com.mindalliance.channels.playbook.ref.Bean;

import java.beans.PropertyDescriptor;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 7, 2008
 * Time: 4:15:08 PM
 */
public class IfmBeanWrapper extends DefaultBeanWrapper {

    public IfmBeanWrapper(Class type) {
        super(type);
    }

    public boolean hasProperty(String name) {
        Bean ifmBean = (Bean)getObject();
        return name.equals("id") || name.equals("db") || ifmBean.beanProperties().keySet().contains(name);
    }


}
