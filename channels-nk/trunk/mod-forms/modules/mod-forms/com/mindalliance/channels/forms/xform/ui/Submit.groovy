package com.mindalliance.channels.forms.xform.ui

import com.mindalliance.channels.forms.xform.XForm
import com.mindalliance.channels.forms.xform.AbstractElement


/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 1, 2008
* Time: 12:39:35 PM
* To change this template use File | Settings | File Templates.
*/
class Submit extends AbstractElement {    // not an AbstractUIElement because does not represent a bean property

    String submissionId
    String label

    Submit(String submissionId, String submitLabel, XForm xform) {
        super(xform)
        this.submissionId = submissionId
        label = submitLabel
        initialize()
    }

    void build(def builder, String xf) {
        Map attributes = getAttributes()
        attributes += [submission:submissionId]
        builder."$xf:submit"(attributes){
            builder."$xf:label"(label)
        }
    }

}