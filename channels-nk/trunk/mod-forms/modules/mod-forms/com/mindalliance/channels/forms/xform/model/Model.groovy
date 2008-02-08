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

    String customSchemaUrl
    List instances = [ ]
    Map bindings = [:]
    List submissions = [ ]

    Model(String id, String customSchemaUrl, BeanXForm xform) {
        super(xform)
        this.id = id
        this.customSchemaUrl = customSchemaUrl
        initialize()
    }

    void addInstance(Instance instance) {
        instances.add(instance)
    }

    void addBinding(Binding binding) {
        bindings[binding.id] = binding
    }

    void addSubmission(Submission submission) {
        submissions.add(submission)
    }

    void initialize() {
        super.initialize()
    }

    Map getAttributes() {
        Map attributes = super.getAttributes()
        attributes += [schema:customSchemaUrl]
        return attributes
    }

    void build(def builder, String xf) {
       builder."$xf:model" (getAttributes()) {
           instances.each {instance -> instance.build(builder, xf)}
           bindings.each {key, binding -> binding.build(builder, xf)}
           submissions.each {submission -> submission.build(builder, xf)}
       }
    }

}