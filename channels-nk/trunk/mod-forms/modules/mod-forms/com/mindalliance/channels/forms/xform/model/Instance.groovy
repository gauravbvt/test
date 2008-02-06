package com.mindalliance.channels.forms.xform.model

import com.mindalliance.channels.forms.xform.AbstractElement
import com.mindalliance.channels.forms.xform.XForm

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 31, 2008
* Time: 7:49:42 PM
* To change this template use File | Settings | File Templates.
*/
class Instance extends AbstractElement {

    String url

    Instance(String id, String url, XForm xform) {
        super(xform)
        this.id = id
        this.url = url
        initialize()
    }

    void initialize() {
       super.initialize()
    }

    Map getAttributes() {
        Map attributes = super.getAttributes()
        attributes += [url:url]
        return attributes
    }

    void build(def xf) {
        xf.instance(getAttributes())
    }

}