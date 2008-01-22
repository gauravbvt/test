package com.mindalliance.channels.nk

import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.BeanList
import groovy.xml.MarkupBuilder
import com.mindalliance.channels.nk.bean.IPersistentBean
import groovy.util.slurpersupport.GPathResult

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
            builder."${propKey}"(dataType: propValue.class.name, text)
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
            builder."${propKey}"(beanClass: component.class.name) {
                props.each {pKey, pValue -> buildProperty(pKey, pValue, builder)}
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

    // From xml to bean
    IPersistentBean persistentBeanFromXml(String doc) {
        GPathResult xml = new XmlSlurper().parseText(doc)
        String toEval = "${xml.@beanClass}.newInstance()"
        IPersistentBean bean = (IPersistentBean) Eval.me(toEval)
        initBeanFromXml(bean, xml)
        return bean
    }

    private void initBeanFromXml(IPersistentBean bean, GPathResult xml) {
        if (xml.@id) bean.id = xml.@id
        if (xml.@db) bean.db = xml.@db
        if (xml.@version) bean.version = xml.@version
        bean.rooted = xml.@rooted == 'true'
        reifyFromXml(bean, xml.children())
    }

    private void reifyFromXml(def bean, GPathResult children) {
        children.each {child ->
            if (child.@dataType.size()) {// data
                String value = child.text();
                if (value.size()) {
                    String toEval = "new ${child.@dataType}(\'$value\')"
                    bean."${child.name()}" = Eval.me(toEval) // assumes a constructor with args (String val)
                }}
            else if (child.@beanRef.size()) {// a reference
                def beanReference = new BeanReference(beanClass: child.@beanRef)
                if (child.@db.size()) beanReference.db = child.@db
                String beanId = child.text()
                if (beanId.size()) beanReference.id = beanId
                bean."${child.name()}" = beanReference
            }
            else if (child.@itemClass.size()) {// a list
                def beanList = new BeanList(itemClass: child.@itemClass)
                child.children().each {item ->
                    String aClass = child.@itemClass
                    String toEval = "new ${aClass}()"
                    def itemBean = Eval.me(toEval)
                    reifyFromXml(itemBean, item.children())
                    beanList.add(itemBean)
                }
                bean."${child.name()}" = beanList
            }
            else if (child.@beanClass.size()) {// a component (non-persistent) bean
                String aClass = child.@beanClass
                def component = Eval.me("new ${aClass}()")
                reifyFromXml(component, child.children())
                bean."${child.name()}" = component
            }
            else {
                println "Invalid xml for $bean"
            }
        }
    }

}