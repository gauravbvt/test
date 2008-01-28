package com.mindalliance.channels.metamodel

import com.mindalliance.channels.nk.bean.AbstractPersistentBean
import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.BeanList
import com.mindalliance.channels.nk.bean.SimpleData

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 19, 2008
* Time: 11:55:22 AM
*/
class TestBean extends AbstractPersistentBean {

    def name = new SimpleData(String.class)
    def successful = new SimpleData(Boolean.class)
    def score = new SimpleData(Double.class)
    def parent  = new BeanReference(beanClass: TestBean.class.name)
    def runs = new BeanList(itemClass: TestRunComponent.class.name)
    def subTests = new BeanList(itemClass: BeanReference.class.name)

    Map getBeanProperties() {
        return [name:name, successful:successful, score:score, parent:parent, runs:runs, subTests:subTests]
    }


    Map getMetaData() {
        [
            name: [hint: 'A name for this test' ],
            successful: [hint: 'Whether this test was successul' ],
            score: [hint: 'Between 0 (total failure) and 1 (total success)', domain: [0..100] ],
            parent: [label: '', required: false, hint: 'The integrating test' ],
            runs: [label: 'Test runs' ],
            subTests: [label: 'Unit tests' ]
        ]
    }




}