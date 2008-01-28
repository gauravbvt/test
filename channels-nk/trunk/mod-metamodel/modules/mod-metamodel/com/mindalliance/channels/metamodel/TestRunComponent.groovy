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

    def date = new SimpleData(Date.class)
    def tester = new SimpleData (String.class)

    Map getBeanProperties() {
        return [date:date, tester:tester]
    }

}