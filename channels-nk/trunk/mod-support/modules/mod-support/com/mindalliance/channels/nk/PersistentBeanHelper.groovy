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
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context


/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 20, 2008
* Time: 7:46:45 PM
* To change this template use File | Settings | File Templates.
*/
class PersistentBeanHelper {

    Context context

    PersistentBeanHelper(Context context) {
        this.context = context
    }

    Registry registry = Registry.getRegistry()

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
        builder."${bean.elementName()}"(db: bean.db, id: bean.id, version: bean.version, beanClass: registry.nameFor(bean.class), createdOn: bean.createdOn.toString(), root: bean.rooted) {
            props.each {propKey, propValue -> buildProperty(propKey, propValue, builder)}
        }
    }

    // Calculated properties are neither serialized not reified (they are 100% class-defined)
    private void buildProperty(String propKey, def propValue, MarkupBuilder builder) {
        switch (propValue) {
            case ISimpleData:
                buildSimpleData(propKey, propValue, builder)
                break;
            case IBeanReference:
                buildBeanReference(propKey, propValue, builder)
                break;
            case IBeanList:
                buildBeanList(propKey, propValue, builder)
                break;
            case IComponentBean:
                buildComponentBean(propKey, propValue, builder)
                break;
            default:    // id, db, version, createdOn, rooted
                def text = (propValue != null) ? "${propValue}" : '';
                builder."${propKey}"(text)
        }
    }

    void buildSimpleData(String propKey, ISimpleData simpleData, MarkupBuilder builder) {
        if (!simpleData.isCalculated()) {
            Map attributes = [dataType: "${registry.nameFor(simpleData.@dataClass)}"]
            def text = (simpleData.@value != null) ? "${simpleData.@value}" : '';
            builder."${propKey}"(attributes, text);
        }
    }

    void buildBeanReference(String propKey, IBeanReference beanReference, MarkupBuilder builder)  {
        if (!beanReference.isCalculated()) {
            Map attributes = [beanRef: beanReference.beanClass]
            def id = (beanReference.id != null) ? "${beanReference.id}" : '';
            def db = (beanReference.db != null) ? "${beanReference.db}" : '';
            if (beanReference.isDomainBound()) attributes += [domain: beanReference.domain.toString()]
            builder."${propKey}"(attributes) {
                builder.ref() {
                    builder.db(db)
                    builder.id(id)
                }
            }
        }
    }

    void buildBeanList(String propKey, IBeanList beanList, MarkupBuilder builder)  {
        if (!beanList.isCalculated()) {
            Map attributes = [itemName:beanList.itemName]
            IBeanPropertyValue proto = beanList.itemPrototype
            String itemClass = "${registry.nameFor(proto.getClass())}"
            attributes += [itemClass: itemClass]
            if (proto instanceof IBeanReference) {
                attributes += [itemDomain: proto.domain.toString()]
            }
            builder."${propKey}"(attributes) {
                beanList.each {item ->
                    buildProperty(beanList.itemName, item, builder)
                }
            }
        }
    }

    void buildComponentBean(String propKey, IComponentBean component, MarkupBuilder builder)  {
        def props = component.getBeanProperties();
        builder."${propKey}"(beanClass: registry.nameFor(component.class)) {
            props.each {pKey, pValue -> buildProperty(pKey, pValue, builder)}
        }
    }

    // From xml to bean
    IPersistentBean persistentBeanFromXml(String doc) {
        GPathResult xml = new XmlSlurper().parseText(doc)
        IPersistentBean bean = (IPersistentBean)registry.classFor("${xml.@beanClass}").newInstance()
        initBeanFromXml(bean, xml)
        bean.activate()    // make sure all properties are initialized
        bean.context = context
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
            Class dataClass = registry.classFor("${node.@dataType}")
            def data = new SimpleData(dataClass:dataClass)
            String value = node.text()
            if (value.size()) {
                data.value = dataClass.newInstance(value)
            }
            return data
        }
        else if (node.@beanRef.size()) {// a reference
            String beanClass =  node.@beanRef
            def beanReference = new BeanReference(beanClass: beanClass)
            if (node.@domain.size()) {
                beanReference.domain = BeanDomain.fromString("${node.@domain}")
            }
            String db = node.ref.db
            String id = node.ref.id
            if (db.size()) beanReference.@db = db
            if (id.size()) beanReference.@id = id
            return beanReference
        }
        else if (node.@itemName.size()) {// a list
            def beanList = new BeanList(itemName: node.@itemName)
            assert node.@itemClass.size()
            String aClassName = "${node.@itemClass}"
            IBeanPropertyValue itemPrototype = (IBeanPropertyValue)registry.classFor(aClassName).newInstance()
            if (itemPrototype instanceof IBeanReference) {
                BeanDomain domain = (node.@itemDomain.size()) ? BeanDomain.fromString("${node.@itemDomain}") : BeanDomain.UNDEFINED
                itemPrototype.domain = domain
            }
            beanList.itemPrototype = itemPrototype
            node.children().each {item ->
                def itemBean = reifyFromXml(item)
                beanList.add(itemBean)
            }
            return beanList
        }
        else if (node.@beanClass.size()) {// a component (non-persistent) bean
            String aClass = node.@beanClass
            def component = registry.classFor(aClass).newInstance()
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