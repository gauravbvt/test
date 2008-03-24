package com.mindalliance.channels.playbook.support

import org.apache.commons.beanutils.PropertyUtilsBean

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 24, 2008
* Time: 8:19:06 AM
*/
class PathExpression {

    /*
    From org.apache.commons.beanutils.PropertyUtilsBean:
    
    Five formats for referencing a particular property value of a bean are defined, with the layout of an identifying String in parentheses:

    * Simple (name) - The specified name identifies an individual property of a particular JavaBean. The name of the actual getter or setter method to be used is determined using standard JavaBeans instrospection, so that (unless overridden by a BeanInfo class, a property named "xyz" will have a getter method named getXyz() or (for boolean properties only) isXyz(), and a setter method named setXyz().
    * Nested (name1.name2.name3) The first name element is used to select a property getter, as for simple references above. The object returned for this property is then consulted, using the same approach, for a property getter for a property named name2, and so on. The property value that is ultimately retrieved or modified is the one identified by the last name element.
    * Indexed (name[index]) - The underlying property value is assumed to be an array, or this JavaBean is assumed to have indexed property getter and setter methods. The appropriate (zero-relative) entry in the array is selected. List objects are now also supported for read/write. You simply need to define a getter that returns the List
    * Mapped (name(key)) - The JavaBean is assumed to have an property getter and setter methods with an additional attribute of type java.lang.String.
    * Combined (name1.name2[index].name3(key)) - Combining mapped, nested, and indexed references is also supported.

    */

    static def eval(def obj, String path) {
        def result
        RefPropertyUtilsBean utils = new RefPropertyUtilsBean()
        result = utils.getNestedProperty(obj, path)
        return result
    }

}