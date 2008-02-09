package com.mindalliance.channels.nk

import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.BeanList

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 9, 2008
* Time: 10:40:16 AM
*/
class Registry {

    static final Map CLASSES = [
            'environment.Person' : 'com.mindalliance.channels.metamodel.beans.environment.Person',
            // TEST BEANS
            'test.TestBean':'com.mindalliance.channels.metamodel.TestBean',
            'test.TestEnvironment':'com.mindalliance.channels.metamodel.TestEnvironment',
            'test.TestRunComponent':'com.mindalliance.channels.metamodel.TestRunComponent'
          ]
          
    static public Registry instance
    Map registeredClasses = [
                                BeanReference:BeanReference.class.name,
                                BeanList:BeanList.class.name,
                                String: String.class.name,
                                Date: Date.class.name,
                                Double: Double.class.name,
                                Integer: Integer.class.name,
                                BigDecimal: BigDecimal.class.name,
                                Boolean: Boolean.class.name,
                                Float: Float.class.name
                            ]

    static synchronized Registry getRegistry() {
        if (instance == null) {
            instance = new Registry()
            instance.registeredClasses += CLASSES
        }
        return instance
    }

    // If a class is registered unde the name, return the registered class, else return the class for name
    Class classFor(String name) {
        Class aClass
        String className = registeredClasses[name]
        if (className) {
            aClass = Class.forName(className)
        }
        else {
            aClass = Class.forName(name)
        }
        return aClass
    }

    String nameFor(Class aClass) {
        def entry = registeredClasses.find{key,val -> val == aClass.name}
        def name = (entry) ? entry.key : aClass.name
        return name
    }

}