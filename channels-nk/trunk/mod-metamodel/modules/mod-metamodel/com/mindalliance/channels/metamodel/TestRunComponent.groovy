package com.mindalliance.channels.metamodel

import com.mindalliance.channels.nk.bean.AbstractComponentBean

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 21, 2008
* Time: 2:10:32 PM
* To change this template use File | Settings | File Templates.
*/
class TestRunComponent extends AbstractComponentBean {

    Date date
    String tester

    public Map getBeanProperties() {
        return [date: date, tester: tester]
    }

}