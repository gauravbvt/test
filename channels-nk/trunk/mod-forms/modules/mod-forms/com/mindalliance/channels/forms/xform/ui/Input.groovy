package com.mindalliance.channels.forms.xform.ui

import com.mindalliance.channels.forms.xform.XForm

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 31, 2008
* Time: 7:09:22 PM
* To change this template use File | Settings | File Templates.
*/
class Input extends AbstractUIElement {

    Input(Expando metadata, XForm xform) {
        super(metadata, xform)
        initialize()
    }

    void build(def builder, String xf) {
       builder."$xf:input"(getAttributes()) {
           builder."$xf:label"(label)
            buildHint(builder, xf)
       }
    }

}