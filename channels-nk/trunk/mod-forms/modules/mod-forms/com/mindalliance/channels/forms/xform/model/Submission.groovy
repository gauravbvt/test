package com.mindalliance.channels.forms.xform.model

import com.mindalliance.channels.forms.xform.XForm
import com.mindalliance.channels.forms.xform.AbstractElement

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 28, 2008
* Time: 8:13:50 PM
* To change this template use File | Settings | File Templates.
*/
class Submission extends AbstractElement {

    String url

    Submission(String id, String url, XForm xform) {
        super(xform)
        this.url = url
        this.id = id
        initialize()
    }

    void initialize() {
        super.initialize()
    }

    Map getAttributes() {
         Map attributes = super.getAttributes()
         attributes += [method:'post',
                        action:url,
                        'omit-xml-declaration':'true',
                        includenamespaceprefixes:"#default" ]
        return attributes
     }

    void build(def builder, String xf) {
        builder."$xf:submission"(getAttributes())
    }

}