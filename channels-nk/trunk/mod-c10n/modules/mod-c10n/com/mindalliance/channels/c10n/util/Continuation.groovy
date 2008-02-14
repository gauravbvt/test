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
    List followUps = [ ]
    private boolean aborted = false
    private boolean committed = false

    Continuation(String id) {
        this.id = id
    }

    static IContinuation fromXml(String doc, INKFConvenienceHelper context) {
        GPathResult xml = new XmlSlurper().parseText(doc)
        Continuation c10n = new Continuation("${xml.@id}")
        c10n.date = new Date("${xml.@date}")
        c10n.aborted = new Boolean("${xml.@aborted}")
        c10n.committed = new Boolean("${xml.@committed}")
        assert !(c10n.aborted && c10n.committed)
        if (xml.@previous.size()) {
           c10n.previous = "${xml.@previous}"
        }
        xml.followUps.id.each {id ->
            c10n.addFollowUp(id.text())
        }
        Map map = [:]
        xml.state.children().each {
            def child = it
            String className = "${child.@itemClass}"
            Class aClass = Class.forName(className)
            if (IURAspect.class.isAssignableFrom(aClass)) {
                String s = child.text()
                // transrept thru XML to desired aspect
                def aspect = context.transrept(new StringAspect(s), aClass)
                map["${child.name()}"] = aspect
            }
            else {
                def value
                if (aClass == String.class) {
                    value = child.text()
                }
                else {
                    value = aClass.newInstance(child.text())
                }
                map["${child.name()}"] = value
            }
        }
        c10n.state = map
        return c10n
    }

    void addFollowUp(String id) {
        followUps.add(id)
    }

    void removeFollowUp(String id) {
        followUps.remove(id)
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
        Map args = [id:id, date:"$date", aborted:"$aborted", committed:"$committed"]
        if (previous) args += [previous:previous]
        builder.continuation(args) {
           builder.followUps() {
               followUps.each {id ->
                builder.id(id)
               }
           }
           builder.state() {
               state.each {key,val ->
                 String content
                 String className
                 if (val instanceof IURAspect) {
                    content = ((IAspectString)(context.transrept((IURAspect)val,IAspectString.class))).getString()
                    className = makeAspectInterfaceName(val.class.name)
                 }
                 else {
                     className = val.class.name
                     content = "$val"
                 }
                 builder."$key"(itemClass:className, content)
               }
           }
        }
        return writer.toString()
    }

    // ASSUMES: An Aspect used as value in the c10n's state has a class XYZAspect that implements IAspectXYZ in the same package
    private String makeAspectInterfaceName(String className) {
        def match = (className =~ /(.*\.)([^\.]+)Aspect/)
        String interfaceName = "${match[0][1]}IAspect${match[0][2]}"
        try {
            Class.forName(interfaceName)
        }
        catch(Exception e) {
            throw IllegalArgumentException("$className is not supported in continuations")
        }
        return interfaceName
    }

}