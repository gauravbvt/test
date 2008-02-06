package com.mindalliance.channels.forms.xform.ui

import com.mindalliance.channels.forms.xform.XForm

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 31, 2008
* Time: 7:10:05 PM
* To change this template use File | Settings | File Templates.
*/
class Textarea extends AbstractUIElement {

    Textarea(Expando metadata, XForm xform) {
        super(metadata, xform)
        initialize()
    }

    void build(def xf) {
        xf.textarea(getAttributes()) {
            label(label)
        }
    }

}