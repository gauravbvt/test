package com.mindalliance.channels.nk

import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.BeanList
import groovy.xml.MarkupBuilder

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
        builder."${elementName(bean)}"(db: bean.db, id: bean.id, version: bean.version, beanClass: bean.class.name, createdOn: bean.createdOn.toString(), root: bean.rooted) {
            buildProperties(bean, builder)
        }
    }

    private void buildProperties(def bean, MarkupBuilder builder) {
        def props = bean.getProperties()
        bean.getBeanProperties().each {propKey, propValue ->
            String valueClassName = propValue.class.name
            if ([Date.class, String.class, Integer.class, Boolean.class, Double.class].contains(propValue.class)) {// TODO  add more as needed
                def text = (propValue != null) ? "$propValue"  : ''
                builder."${propKey}"(type: propValue.class.name, text)
            }
            else if (valueClassName == BeanReference.class.name) {
                def beanReference = propValue
                builder."${propKey}"(db: beanReference.db, beanRef: beanReference.beanClass) {
                    if (beanReference.id) beanReference.id
                };
            }
            else if (valueClassName == BeanList.class.name ) {
                def beanList = propValue;
                builder."${propKey}"(itemClass: beanList.itemClass) {
                    beanList.each {item ->
                        item.buildXML()(builder)
                    }
                }
            }
            else {
                def component = propValue
                builder."${propKey}" {
                    buildXml(component, builder)
                }
            }

        }
    }


    private String elementName(def bean) {
        String name = bean.class.name.tokenize('.').reverse()[0]
        "${name[0].toLowerCase()}${name[1..<name.size()]}"
    }

}