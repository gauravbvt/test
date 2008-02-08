package com.mindalliance.channels.forms.xform
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 31, 2008
 * Time: 7:50:33 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractElement {

    XForm xform
    String id // May be null
 
    AbstractElement(XForm xform) {
        this.xform = xform
    }

    void initialize() { }   // Default

    Map getAttributes() {
        Map attributes = [:]
        if (id) attributes += [id:id]
        return attributes
    }

    abstract void build(def builder, String xf)

}