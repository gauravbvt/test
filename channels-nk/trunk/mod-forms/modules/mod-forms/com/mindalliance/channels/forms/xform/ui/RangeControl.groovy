package com.mindalliance.channels.forms.xform.ui

import com.mindalliance.channels.forms.xform.XForm

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 31, 2008
* Time: 7:11:50 PM
* To change this template use File | Settings | File Templates.
*/
class RangeControl extends AbstractUIElement {

    Number step
    Range range

    RangeControl (Expando metadata, XForm xform) {
        super(metadata, xform)
        initialize()
    }

    void initialize() {
        super.initialize()
        assert metadata.range
        range = metadata.range
        step = metadata.step ?: 1
    }

    Map getAttributes() {
        Map attributes = super.getAttributes()
        attributes += [start:range[0], end:range[range.size()-1], step:step]
        return attributes
    }

    void build(def builder, String xf) {
        builder."$xf:range"(getAttributes()) {
            builder."$xf:label"(this.label)
        }
    }
    
}