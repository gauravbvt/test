package com.mindalliance.channels.metamodel

import com.mindalliance.channels.nk.bean.AbstractComponentBean
import com.mindalliance.channels.nk.bean.SimpleData

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 21, 2008
* Time: 2:10:32 PM
* To change this template use File | Settings | File Templates.
*/
class TestRunComponent extends AbstractComponentBean {

    def date = new SimpleData(dataClass: Date.class)
    def tester = new SimpleData(dataClass: String.class)

    Map getBeanProperties() {
        return [date: date, tester: tester]
    }

    void initialize() {
        defaultMetadata = [
                date: [required: true, hint: 'The date the test was run']
        ]
    }
}