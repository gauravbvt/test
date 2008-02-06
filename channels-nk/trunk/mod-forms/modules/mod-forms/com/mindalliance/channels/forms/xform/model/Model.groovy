package com.mindalliance.channels.forms.xform.model

import com.mindalliance.channels.forms.xform.BeanXForm
import com.mindalliance.channels.forms.xform.AbstractElement

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 28, 2008
* Time: 8:13:29 PM
* To change this template use File | Settings | File Templates.
*/
class Model extends AbstractElement {

    String schemaUrl
    List instances = [ ]
    List bindings = [ ]
    List submissions = [ ]

    Model(String id, String schemaUrl, BeanXForm xform) {
        super(xform)
        this.id = id
        this.schemaUrl = schemaUrl
        initialize()
    }

    void addInstance(Instance instance) {
        instances.add(instance)
    }

    void addBinding(Binding binding) {
        bindings.add(binding)
    }

    void addSubmission(Submission submission) {
        submissions.add(submission)
    }

    void initialize() {
        super.initialize()
    }

    Map getAttributes() {
        Map attributes = super.getAttributes()
        attributes += [schema:schemaUrl]
        return attributes
    }

    void build(def xf) {
       xf.model (getAttributes()) {
           instances.each {instance -> instance.build(xf)}
           bindings.each {binding -> binding.build(xf)}
           submissions.each {submission -> submission.build(xf)}
       }
    }

}