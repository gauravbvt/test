package com.mindalliance.channels.playbook.ref.impl
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 26, 2008
 * Time: 3:48:57 PM
 */
class MetaProperty implements Serializable {

    String propertyName
    Class valueClass

    MetaProperty(String name, Class clazz) {
        propertyName = name
        valueClass = clazz
    }

    boolean equals(def obj) {
        if (!obj instanceof MetaProperty) return false
        if (propertyName != obj.propertyName) return false
        if (valueClass != obj.valueClass) return false
        return true
    }

    int hashCode() {
         int hash = 1
         hash = hash * 31 + propertyName.hashCode()
         hash * 31 + this.valueClass.hashCode()
         return hash
     }

    boolean isScalar() {
        return !Collection.class.isAssignableFrom(valueClass)
    }

}