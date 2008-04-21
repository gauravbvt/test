package com.mindalliance.channels.playbook.support.persistence;

import java.util.Map;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 20, 2008
 * Time: 6:51:42 PM
 *
 * YAML imposes the following constraints on objects it serializes:
 * They must be simple types (String, int etc.), Maps, Lists or JavaBeans, and be composed of the same.
 * 
 * To control which properties get persisted and for other reasons, YAML-serializable objects are instances of interface
 * Mappable (e.g. Bean implements Mappable).Mappables are first converted to Maps that are then
 * serialized to YAML (Mappable.toMap()).
 * They are also initializable from Maps (Mappable.initFromMap(Map map)) that are reified from YAML.
 * For this to work, Mappable classes *must* have an empty constructor.
 */
public interface Mappable {
    public static final String CLASS_NAME_KEY = "_bean_class_";    

    // Converts self to a map with key = property name and value = a JavaBean or simple data type
    Map toMap();
    // Initializes self from a map
    void initFromMap(Map map);

}
