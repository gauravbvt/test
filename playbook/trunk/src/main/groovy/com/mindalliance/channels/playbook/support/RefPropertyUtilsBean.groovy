package com.mindalliance.channels.playbook.support

import org.apache.commons.beanutils.PropertyUtilsBean

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 24, 2008
 * Time: 9:06:46 AM
 */
class RefPropertyUtilsBean extends PropertyUtilsBean {

    @Override
    def getSimpleProperty(def bean, String prop) {
        def result = bean."$prop"
        return result
    }

    @Override
    void setSimpleProperty(def bean, String prop, def obj) {
        bean."$prop" = obj
    }

    @Override
    def getMappedProperty(def obj, String name, String key) {
        def result
        switch (obj) {
            case Collection.class: result = obj.find {it."$name" == key}; break
            default: result = super.getMappedProperty(obj, name, key)
        }
        return result
    }

    def getIndexedProperty(def bean, String name, int index) {
        def result
        def list = getSimpleProperty(bean, name)
        switch (list) {
            case List.class: result = list[index]; break
            default: result = super.getIndexedProperty(list, name, index)
        }
        return result
    }

}