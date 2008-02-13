package com.mindalliance.channels.c10n.util

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import groovy.xml.MarkupBuilder
import com.ten60.netkernel.urii.IURAspect
import com.ten60.netkernel.urii.aspect.IAspectString
import groovy.util.slurpersupport.GPathResult
import com.ten60.netkernel.urii.aspect.StringAspect

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 11, 2008
 * Time: 4:35:41 PM
 * To change this template use File | Settings | File Templates.
 */
class Continuation implements IContinuation {

    String id
    Map state =[:]   // values must be either literals (recreate-able from SomeClass(someInstance.tostring()) or aspects that can transrept to and from StringAspect
    Date date = new Date()
    String previous
    private boolean aborted = false
    private boolean committed = false

    Continuation(String id) {
        this.id = id
    }

    static Continuation fromXml(String doc, INKFConvenienceHelper context) {
        GPathResult xml = new XmlSlurper().parseText(doc)
        Continuation c10n = new Continuation(xml.@id)
        c10n.date = new Date(xml.@createdOn)
        c10n.aborted = new Boolean("${xml.@aborted}")
        c10n.committed = new Boolean("${xml.@committed}")
        assert !(c10n.aborted && c10n.committed)
        if (xml.@previous.size()) {
           c10n.previous = xml.@previous
        }
        Map map = [:]
        xml.children().each {
            def child = it
            Class aClass = Class.forName(child.@itemClass)
            if (IURAspect.class.isAssignableFrom(aClass)) {
                String s = it.text()
                IURAspect aspect = context.transrept(new StringAspect(s), aClass)
                map["${child.name}"] = aspect
            }
            else {
                def value = aClass.newInstance([value])
                map["${child.name}"] = value
            }
        }
        c10n.state = map
        return c10n
    }

    boolean isAborted() {
        return aborted
    }

    boolean isCommitted() {
        return committed
    }

    void setAborted(boolean val) {
        aborted = val
    }

    void setCommitted(boolean val) {
        committed = val
    }

    public String toXml(INKFConvenienceHelper context) {
        StringWriter writer = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(writer)
        Map args = [id:id, createdOn:"$createdOn", aborted:"$aborted", committed:"$committed"]
        if (previous) args += [previous:previous]
        builder.continuation(args) {
           state.each {key,val ->
             String content
             if (val instanceof IURAspect) {
                content = ((IAspectString)(context.transrept((IURAspect)val,IAspectString.class))).getString()
             }
             else {
                 content = "$val"
             }
             builder."$key"(itemClass:val.class.name, content)
           }
        }
        return writer.toString()
    }

}