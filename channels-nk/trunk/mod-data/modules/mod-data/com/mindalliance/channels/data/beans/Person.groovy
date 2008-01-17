package com.mindalliance.channels.data.beans

import com.mindalliance.channels.data.BeanReference
import groovy.util.slurpersupport.GPathResult
import com.mindalliance.channels.data.AbstractPersistentBean

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 14, 2008
* Time: 3:44:56 PM
* To change this template use File | Settings | File Templates.
*/
class Person extends AbstractPersistentBean {

    String firstName
    String middleName
    String lastName
    BeanReference friend  = new BeanReference (beanClass: Person.class.name)


}