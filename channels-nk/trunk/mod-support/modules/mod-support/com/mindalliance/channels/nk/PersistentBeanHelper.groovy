package com.mindalliance.channels.nk

import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.BeanList
import groovy.xml.MarkupBuilder
import com.mindalliance.channels.nk.bean.IPersistentBean

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 20, 2008
* Time: 7:46:45 PM
* To change this template use File | Settings | File Templates.
*/
class PersistentBeanHelper {

    String xmlFromBean(IPersistentBean bean) {
        StringWriter writer = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(writer)
        buildXml(bean, builder)
        return writer.toString()
    }

    // XML build
    private void buildXml(def bean, MarkupBuilder builder) {
        assert bean.isPersistent()
        def props = bean.getBeanProperties()
        builder."${elementName(bean)}"(db: bean.db, id: bean.id, version: bean.version, beanClass: bean.class.name, createdOn: bean.createdOn.toString(), root: bean.rooted) {
             props.each {propKey, propValue -> buildProperty(propKey, propValue, builder)}
        }
    }

    private void buildProperty(String propKey, def propValue, MarkupBuilder builder) {
        String valueClassName = propValue.class.name
        if (isDataBean(propValue)) {
            def text = (propValue != null) ? "$propValue" : ''
            builder."${propKey}"(type: propValue.class.name, text)
        }
        else if (valueClassName == BeanReference.class.name) {
            def beanReference = propValue
            def id = (beanReference.id != null) ? "$beanReference.id" : ''
            builder."${propKey}"(db: beanReference.db, beanRef: beanReference.beanClass, id)
        }
        else if (valueClassName == BeanList.class.name) {
            def beanList = propValue;
            builder."${propKey}"(itemClass: beanList.itemClass) {
                beanList.each {item ->
                    buildProperty('item', item, builder)
                }
            }
        }
        else {
            def component = propValue
            assert component.isComponent()
            def props = component.getBeanProperties()
            builder."${propKey}" {
                props.each {pKey, pValue -> buildProperty(pKey, pValue, builder) }
            }
        }
    }


    private String elementName(def bean) {
        String name = bean.class.name.tokenize('.').reverse()[0]
        "${name[0].toLowerCase()}${name[1..<name.size()]}"
    }

    private boolean isDataBean(def bean) {
        return [Date.class, String.class, Integer.class, Boolean.class, Double.class].contains(bean.class) // TODO  add more as needed
    }

}