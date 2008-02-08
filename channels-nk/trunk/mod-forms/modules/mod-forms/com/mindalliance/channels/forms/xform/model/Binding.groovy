package com.mindalliance.channels.forms.xform.model

import com.mindalliance.channels.forms.xform.AbstractElement
import com.mindalliance.channels.forms.xform.XForm

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 28, 2008
* Time: 8:14:03 PM
* To change this template use File | Settings | File Templates.
*/
class Binding extends AbstractElement {

    String instanceId
    Expando metadata
    boolean required
    String type
    boolean readonly
    String nodeset

    Binding(String instanceId, Expando metadata, XForm xform) {
        super(xform)
        this.instanceId = instanceId
        this.metadata = metadata
        initialize()
    }

    void initialize() {
        super.initialize()
        assert metadata.id
        this.id = Binding.makeBindingId(metadata.id)
        assert metadata.path
        nodeset = metadata.path
        required = metadata.required ?: false
        String propType = metadata.type ?: 'string'
        type = (propType.startsWith(':')) ? "${xform.customSchemaPrefix}:${propType.substring(1)}" : "${xform.xsdSchemaPrefix}:$propType"
        readonly = metadata.readonly ?: false
    }

    Map getAttributes() {
        Map attributes = super.getAttributes()
        attributes += [nodeset: "instance($instanceId)$nodeset",
                       required: required ? 'true()' : 'false()',
                       readonly: readonly,
                       type:type]
        return attributes
    }

    void build(def builder, String xf) {
        builder."$xf:binding"(getAttributes())
    }

    static String makeBindingId(String uiElementId) {
       return "b_$uiElementId"
    }

}