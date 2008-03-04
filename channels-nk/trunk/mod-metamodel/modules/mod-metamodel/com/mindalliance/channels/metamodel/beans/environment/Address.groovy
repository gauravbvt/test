package com.mindalliance.channels.metamodel.beans.environment

import com.mindalliance.channels.nk.bean.AbstractComponentBean
import com.mindalliance.channels.nk.bean.SimpleData

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Mar 4, 2008
* Time: 1:28:53 PM
* To change this template use File | Settings | File Templates.
*/
class Address  extends AbstractComponentBean {

    def street = new SimpleData()
    def city = new SimpleData()
    def state = new SimpleData()
    def code = new SimpleData()
    def country = new SimpleData()

    public Map getBeanProperties() {
        return [street:street, city:city, state:state, code:code, country:country]
    }

}