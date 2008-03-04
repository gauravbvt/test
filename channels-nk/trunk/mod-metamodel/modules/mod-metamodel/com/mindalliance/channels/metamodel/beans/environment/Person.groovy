package com.mindalliance.channels.metamodel.beans.environment

import com.mindalliance.channels.nk.bean.AbstractPersistentBean
import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.SimpleData

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 14, 2008
* Time: 3:44:56 PM
* To change this template use File | Settings | File Templates.
*/
class Person extends AbstractPersistentBean {

    def name = new SimpleData(dataClass: String.class, calculate: 'calculate_name')
    def description = new SimpleData(dataClass: String.class, calculate: 'calculate_description')
    def firstName  = new SimpleData(dataClass: String.class)
    def middleName = new SimpleData(dataClass: String.class)
    def lastName  = new SimpleData(dataClass: String.class)

    Map getBeanProperties() {
        return [name:name, description: description, firstName:firstName, middleName:middleName, lastName:lastName]
    }

    void initialize() {
        defaultMetadata = [
            firstName: [required: true],
            lastName: [required: true]
        ]
    }

}