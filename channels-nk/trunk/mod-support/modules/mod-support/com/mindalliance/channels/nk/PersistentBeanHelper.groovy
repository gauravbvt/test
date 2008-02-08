package com.mindalliance.channels.nk

import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.BeanList
import groovy.xml.MarkupBuilder
import com.mindalliance.channels.nk.bean.IPersistentBean
import groovy.util.slurpersupport.GPathResult
import com.mindalliance.channels.nk.bean.IBeanReference
import com.mindalliance.channels.nk.bean.IBeanList
import com.mindalliance.channels.nk.bean.IComponentBean
import com.mindalliance.channels.nk.bean.ISimpleData
import com.mindalliance.channels.nk.bean.SimpleData
import com.mindalliance.channels.nk.bean.IBeanPropertyValue
import com.mindalliance.channels.nk.bean.BeanDomain


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
        switch (propValue) {
            case ISimpleData:
                def simpleData = propValue
                def text = (simpleData.@value != null) ? "${simpleData.@value}" : '';
                builder."${propKey}"(dataType: "${simpleData.@dataClass.name}", text);
                break;
            case IBeanReference:
                def beanReference = propValue
                def id = (beanReference.id != null) ? "${beanReference.id}" : '';
                def db = (beanReference.db != null) ? "${beanReference.db}" : '';
                builder."${propKey}"(beanRef: beanReference.beanClass, domain: beanReference.domain.toString()) {
                    builder.db(db)
                    builder.id(id)
                };
                break;
            case IBeanList:
                def beanList = propValue
                IBeanPropertyValue proto = beanList.itemPrototype
                String itemClass = proto.getClass().name
                Map attributes = [itemClass: itemClass, itemName:beanList.itemName]
                if (proto instanceof IBeanReference) {
                    attributes += [itemDomain: proto.domain.toString()]
                }
                builder."${propKey}"(attributes) {
                    beanList.each {item ->
                        buildProperty(beanList.itemName, item, builder)
                    }
                };
                break;
            case IComponentBean:
                def component = propValue;
                def props = component.getBeanProperties();
                builder."${propKey}"(beanClass: component.class.name) {
                    props.each {pKey, pValue -> buildProperty(pKey, pValue, builder)}
                };
                break;
            default:    // id, db, version, createdOn, rooted
                def text = (propValue != null) ? "${propValue}" : '';
                builder."${propKey}"(text)
        }
    }

    private String elementName(def bean) {
        String name = bean.class.name.tokenize('.').reverse()[0]
        "${name[0].toLowerCase()}${name[1..<name.size()]}"
    }

    // From xml to bean
    IPersistentBean persistentBeanFromXml(String doc) {
        GPathResult xml = new XmlSlurper().parseText(doc)
        IPersistentBean bean = (IPersistentBean)Class.forName("${xml.@beanClass}").newInstance() // (IPersistentBean) Eval.me("${xml.@beanClass}.newInstance()")
        initBeanFromXml(bean, xml)
        bean.activate()    // make sure all properties are initialized
        return bean
    }

    private void initBeanFromXml(IPersistentBean bean, GPathResult xml) {
        if (xml.@id.size()) bean.id = xml.@id
        if (xml.@db.size()) bean.db = xml.@db
        if (xml.@version.size()) bean.version = xml.@version
        if (xml.@createdOn.size()) bean.createdOn = new Date("${xml.@createdOn}")
        bean.rooted = xml.@rooted == 'true'
        xml.children().each {child ->
            def propValue = reifyFromXml(child)
            bean."${child.name()}" = propValue
        }
    }

    private def reifyFromXml(def node) {
        if (node.@dataType.size()) {// data
            String value = node.text()
            if (value.size()) {
                Class dataClass = Class.forName("${node.@dataType}") // (Class)Eval.me("${node.@dataType}.class")
                def data = SimpleData.from(dataClass, value)
                return data
            }
            else return null
        }
        else if (node.@beanRef.size()) {// a reference
            String beanClass =  node.@beanRef
            BeanDomain domain = (node.@domain.size()) ? BeanDomain.fromString("${node.@domain}") : BeanDomain.UNDEFINED
            def beanReference = new BeanReference(beanClass: beanClass, domain: domain)
            String db = node.db
            String id = node.id
            if (db.size()) beanReference.@db = db
            if (id.size()) beanReference.@id = id
             return beanReference
        }
        else if (node.@itemClass.size()) {// a list
            String aClass = node.@itemClass
            IBeanPropertyValue itemPrototype = (IBeanPropertyValue)Class.forName(aClass).newInstance()
            if (itemPrototype instanceof IBeanReference) {
                BeanDomain domain = (node.@itemDomain.size()) ? BeanDomain.fromString("${node.@itemDomain}") : BeanDomain.UNDEFINED
                itemPrototype.domain = domain
            }
            def beanList = new BeanList(itemPrototype: itemPrototype, itemName: node.@itemName)
            node.children().each {item ->
                def itemBean = reifyFromXml(item)
                beanList.add(itemBean)
            }
            return beanList
        }
        else if (node.@beanClass.size()) {// a component (non-persistent) bean
            String aClass = node.@beanClass
            def component = Class.forName(aClass).newInstance() // Eval.me("new ${aClass}()")
            assert component.isComponent()
            node.children().each {child ->
                def propValue = reifyFromXml(child)
                component."${child.name()}" = propValue
            }
            return component
        }
        else {
            throw new Exception("Reifying from invalid xml")
        }
    }

}