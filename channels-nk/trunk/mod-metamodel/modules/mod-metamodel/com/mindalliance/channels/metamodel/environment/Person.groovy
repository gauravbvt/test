package com.mindalliance.channels.metamodel

import com.mindalliance.channels.nk.bean.AbstractPersistentBean
import com.mindalliance.channels.nk.bean.BeanReference

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 14, 2008
* Time: 3:44:56 PM
* To change this template use File | Settings | File Templates.
*/
class Person extends AbstractPersistentBean {

    public Map<String, Object> getBeanProperties() {
        return [firstName: firstName, middleName: middleName, lastName: lastName]
    }

    String firstName
    String middleName
    String lastName


}