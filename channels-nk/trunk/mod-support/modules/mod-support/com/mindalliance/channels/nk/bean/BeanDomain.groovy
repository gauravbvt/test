package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 4, 2008
 * Time: 1:22:23 PM
 */
class BeanDomain implements IBeanDomain {

    String id  // optional, determined from context if not set
    String db // ditto 
    Map args = [:]
    /* Must eval to a closure {bean, args, builder -> ...}  that returns
        <beans>
            <bean id="..." db="...">label</bean>
            ...
        </beans>

        Note that the element names can vary, but not the attribute names
    */
    String query // the name of the query that produces <beans><bean id="someID" db="someDB">label</bean>...</beans>
}