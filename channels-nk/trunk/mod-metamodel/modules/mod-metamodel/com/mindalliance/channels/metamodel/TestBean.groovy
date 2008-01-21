package com.mindalliance.channels.metamodel

import com.mindalliance.channels.nk.bean.AbstractPersistentBean
import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.BeanList

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 19, 2008
* Time: 11:55:22 AM
* To change this template use File | Settings | File Templates.
*/
class TestBean extends AbstractPersistentBean {

    public Map<String, Object> getBeanProperties() {
        return [name: name, successful: successful, score: score, parent: parent, runs: runs];
    }

    String name
    boolean successful
    Double score
    BeanReference parent = new BeanReference(beanClass: TestBean.class.name)
    BeanList runs = new BeanList(itemClass: TestRunComponent.class.name)

}