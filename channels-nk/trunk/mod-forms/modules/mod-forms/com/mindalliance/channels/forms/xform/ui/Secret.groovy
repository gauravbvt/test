package com.mindalliance.channels.forms.xform.ui

import com.mindalliance.channels.forms.xform.XForm

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 31, 2008
* Time: 7:11:24 PM
* To change this template use File | Settings | File Templates.
*/
class Secret extends AbstractUIElement {

    Secret(Expando metadata, XForm xform) {
        super(metadata, xform)
        initialize()
    }

    void build(def builder, String xf) {
        builder."$xf:secret"(getAttributes()) {
            builder."$xf:label"(this.label)
            buildHint(builder, xf)
        }

    }
    
}