package com.mindalliance.channels.metamodel

import com.mindalliance.channels.nk.bean.AbstractPersistentBean
import com.mindalliance.channels.nk.bean.BeanList
import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.BeanDomain

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 4, 2008
* Time: 6:47:28 PM
* To change this template use File | Settings | File Templates.
*/
class TestEnvironment extends AbstractPersistentBean {    // TODO - move to com.mindalliance.channels.metamodel.test

    // This bean's tests property serves as a basis for defining other beans' domains, so it's own domain is undefined
    def tests = new BeanList(itemPrototype: new BeanReference(beanClass: TestBean.class.name, domain: BeanDomain.UNDEFINED), itemName:'test')

    public Map getBeanProperties() {
        return [tests:tests]
    }

}