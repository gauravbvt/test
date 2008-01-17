package com.mindalliance.channels.data


import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import groovy.util.slurpersupport.GPathResult
import com.mindalliance.channels.nk.channels.IPersistentBean
import groovy.xml.MarkupBuilder

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 16, 2008
 * Time: 7:15:53 PM
 */
class BeanXMLConverter {

    private Context context;

    BeanXMLConverter(Context context) {
        this.context = context
    }

    void initBeanFromXml(IPersistentBean bean, GPathResult xml) {
        assert bean.id.size() != 0
        assert bean.db.size() != 0
        bean.version = xml.@version
        bean.createdOn = new Date(xml.@createdOn)
        bean.rooted = xml.@rooted == 'true'
        reifyFromXml(bean, xml.children())
    }

    String xmlFromBean (IPersistentBean bean) {
        StringWriter writer = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(writer)
        buildXml(bean, builder)
        return writer.toString()
    }

    void reifyFromXml(def bean, GPathResult children) {
         children.each {child ->
             if (child.@type.size()) { // data
                 String value = child.text();
                 if (value.size()) {
                     bean."${child.name()}" = Eval.me("new ${child.@type}(\'$value\')")        // assumes a constructor with args (String val)
                 }
             }
             else if (child.@beanRef.size()) { // a reference
                 BeanReference beanReference = new BeanReference(beanClass: child.@bean, db: child.@db)
                 String beanId = child.text()
                 if (beanId.size()) beanReference.id = beanId
                 bean."${child.name()}" = beanReference
             }
             else if (child.@itemClass.size()) { // a list
                 BeanList beanList = new BeanList(itemClass: child.@itemClass)
                 child.children().each {item ->
                     String aClass = child.@list
                     def component = Eval.me("new ${aClass}()")
                     XmlBean.reifyFromXml(component, item.children())
                     beanList.add(bean)
                 }
                 bean."${child.name()}" =  beanList
             }
             else if (child.@bean.size()) {// a component (non-persistent) bean
                 def component = Eval.me("new ${aClass}()")
                 reifyFromXml(component, item.children())
                 bean."${child.name()}" = component
             }
             else {
                 throw new IllegalArgumentException("Invalid xml for $bean")
             }
         }
     }

    // XML build
    void buildXml(def bean, MarkupBuilder builder) {
        builder."${bean.elementName()}"(db: bean.db, id: bean.id, version: bean.version, beanClass: bean.class.name, createdOn: bean.createdOn.toString(), root: bean.rooted) {
                buildProperties(bean, builder)
            }
    }

    private void buildProperties(def bean, MarkupBuilder builder) {
         bean.properties {
                    def property = it
                    switch (property.value) {
                        case {[Date.class, String.class, Integer.class].contains(it.class)}:       // TODO  add more as needed
                            value = property.value
                            builder."${property.key}"(type: value.class.name) {
                                    if (value) value.toString()
                                }
                        case BeanReference.class:
                            BeanReference beanReference = property.value
                            builder."${property.key}"(db: beanReference.db, beanRef: beanReference.beanClass) {
                                if (beanReference.id) beanReference.id
                            }
                            break;
                        case BeanList.class:
                            BeanList beanList = property.value
                            builder."${property.key}"(itemClass: beanList.itemClass) {
                                beanList.each { item ->
                                    item.buildXML()(builder)
                                }
                            }
                            break;
                        default:  // some component object
                            IPersistentBean component = property.value
                            builder."${property.key}" {
                                buildXml(component, builder)
                            }
                            break;

                     }
                }
    }

    private String elementName() {
        String name = this.class.name.tokenize('.').reverse()[0]
        "${name[0].toLowerCase()}${name[1..<name.size()]}"
    }


}