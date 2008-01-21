package com.mindalliance.channels.nk.transreptors

import com.mindalliance.channels.nk.bean.IPersistentBean
import org.ten60.netkernel.layer1.nkf.impl.NKFTransreptorImpl
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.ten60.netkernel.urii.IURRepresentation
import org.ten60.netkernel.xml.representation.IAspectXDA
import com.mindalliance.channels.nk.XDAHelper
import groovy.util.slurpersupport.GPathResult
import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.BeanList
import com.mindalliance.channels.nk.aspects.IAspectPersistentBean
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import org.ten60.netkernel.layer1.nkf.INKFResponse
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly
import com.mindalliance.channels.nk.bean.IPersistentBean

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 19, 2008
* Time: 8:51:36 PM
* To change this template use File | Settings | File Templates.
*/
class XmlToPersistentBean  extends NKFTransreptorImpl {

    public boolean supports(IURRepresentation aFrom, Class aTo) {
         return aFrom.hasAspect(IAspectXDA.class) && aTo.isAssignableFrom(IAspectPersistentBean.class)
     }
     
    protected void transrepresent(INKFConvenienceHelper context) {
        IAspectXDA xda = (IAspectXDA)context.sourceAspect(INKFRequestReadOnly.URI_SYSTEM, IAspectXDA.class)
        String doc = new XDAHelper(context).asXML(xda)
        GPathResult xml = new XmlSlurper().parseText(doc)
        IPersistentBean bean = (IPersistentBean)Eval.me("${beanClass}.newInstance()")
        initBeanFromXml(bean, xml)
        IAspectPersistentBean persistenBeanAspect = new PersistentBeanAspect(bean)
        INKFResponse response = context.createResponseFrom(persistenBeanAspect)
        response.setMineType("text/xml")
    }

    private void initBeanFromXml(IPersistentBean bean, GPathResult xml) {
        bean.id = xml.@id
        bean.db = xml.@db
        assert bean.id.size() != 0
        assert bean.db.size() != 0
        bean.version = xml.@version
        bean.createdOn = new Date(xml.@createdOn)
        bean.rooted = xml.@rooted == 'true'
        reifyFromXml(bean, xml.children())
    }

    private void reifyFromXml(def bean, GPathResult children) {
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


}