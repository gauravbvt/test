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

    def firstName  = new SimpleData(String.class)
    def middleName = new SimpleData(String.class)
    def lastName  = new SimpleData(String.class)

    Map getBeanProperties() {
        return [firstName:firstName, middleName:middleName, lastName:lastName]
    }


}