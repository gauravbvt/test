package com.mindalliance.channels.playbook.ref.impl
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 26, 2008
 * Time: 6:45:44 PM
 */
class RefMetaProperty implements Serializable, Comparable {

    String propertyName
    Class type

    RefMetaProperty(String name, Class type) {
        this.propertyName = name
        this.type = type
    }

    boolean equals(def obj) {
        if (!obj instanceof RefMetaProperty) return false
        if (propertyName != obj.propertyName) return false
        if (type != obj.type) return false
        return true
    }

    int hashCode() {
        int hash = 1
        hash = hash * 31 + propertyName.hashCode()
        hash = hash * 31 + type.hashCode()
        return hash
    }

    public int compareTo(Object obj) {
        if (!obj instanceof RefMetaProperty) throw new IllegalArgumentException("Can't compare to $obj")
        return propertyName.compareTo(obj.propertyName)
    }

     boolean isScalar() {
        return !Collection.class.isAssignableFrom(type)
    }

}