package com.mindalliance.channels.forms.xform.ui.custom

import com.mindalliance.channels.forms.xform.model.Binding
import com.mindalliance.channels.nk.bean.IBeanReference
import com.mindalliance.channels.forms.xform.BeanXForm
import com.mindalliance.channels.forms.xform.ui.Input
import com.mindalliance.channels.forms.xform.ui.Textarea

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 29, 2008
* Time: 9:43:57 AM
* To change this template use File | Settings | File Templates.
*/
class OwnedBeanReferenceControl extends AbstractBeanReferenceControl {// either owned or calculated

    def nameMetadata = new Expando()
    def descriptionMetadata = new Expando()
    def danglingMetadata = new Expando()
    def setMetadata = new Expando()
    Binding nameBinding
    Binding descriptionBinding
    Binding danglingBinding
    Binding setBinding
    Input nameInput
    Textarea descriptionTextarea

    OwnedBeanReferenceControl(IBeanReference beanReference, BeanXForm xform) {
        super(beanReference, xform)
        // Create metadata for reference name and description pseudo-properties (they are properties of the referenced bean)
        nameMetadata.id = "${metadata.id}_name"
        nameMetadata.path = "${metadata.path}/ref/name"
        nameMetadata.readonly = true
        nameMetadata.relevant = "${metadata.path}/ref/id"
        nameMetadata.propertyName = 'name'

        descriptionMetadata.id = "${metadata.id}_description"
        descriptionMetadata.path = "${metadata.path}/ref/description"
        descriptionMetadata.readonly = true
        descriptionMetadata.relevant = "${metadata.path}/ref/id"
        descriptionMetadata.propertyName = 'description'

        initialize()
    }

    /*
        Expects owned reference to look like this when not dangling
        ...
            <prop beanRef='someClass'>
              <ref>
                  <id>foo</id>
                  <db>bar</id>
                  <name>something</name>
                  <description>about something</description>
              </ref>
            </prop>
        ...
    */

    void initialize() {
        super.initialize()
        // Create and install bindings for them
        nameBinding = new Binding(BeanXForm.BEAN_INSTANCE_ID, nameMetadata, xform)
        descriptionBinding = new Binding(BeanXForm.BEAN_INSTANCE_ID, descriptionMetadata, xform)
        xform.getBeanModel().addBinding(nameBinding)
        xform.getBeanModel().addBinding(descriptionBinding)

        // Create name and description controls
        nameInput = new Input(nameMetadata, xform)
        descriptionTextarea = new Textarea(descriptionMetadata, xform)

        // Create metadata for triggers
        danglingMetadata.id = "${metadata.id}_dangling"
        danglingMetadata.relevant = "not(${metadata.path}/ref/id)"
        danglingMetadata.path = metadata.path
        setMetadata.id = "${metadata.id}_set"
        setMetadata.relevant = "${metadata.path}/ref/id"
        setMetadata.path = metadata.path
        // Create bindings for trigger
        danglingBinding = new Binding(BeanXForm.BEAN_INSTANCE_ID, danglingMetadata, xform)
        setBinding = new Binding(BeanXForm.BEAN_INSTANCE_ID, setMetadata, xform)
    }

    void build(def builder, String xf) {
        builder."$xf:group"() {
            builder."$xf:label"(label)
            nameInput.build(builder, xf)
            descriptionTextarea.build(builder, xf)
            String beanId = xform.bean.id
            String beanDb = xform.bean.db
            // New, Edit and Delete triggers
            builder."$xf:trigger"([bind: danglingBinding.id]) {// New
                builder."$xf:label"('New')
                // Trigger javascript function that start action of creating a PB and setting it as the value of a bean's reference property
                // modeler.action.createReference(beanId, beanDb, propertyPath)
                builder.load([resource: "javascript:modeler.action.start('addReference','${metadata.propertyName}', '$beanId','$beanDb')",
                        "${xform.eventPrefix}:event": 'DOMActivate'])
            }
            builder."$xf:trigger"([bind: setBinding.id]) {// Edit
                builder."$xf:label"('Edit')
                // Trigger javascript function that start action of creating a PB and setting it as the value of a bean's reference property
                // modeler.action.createReference(beanId, beanDb, propertyPath)
                builder.load([resource: "javascript:modeler.action.start('editReference','${metadata.propertyName}', '$beanId','$beanDb')",
                        "${xform.eventPrefix}:event": 'DOMActivate'])
            }
            // Delete
            builder."$xf:trigger"([bind: setBinding.id]) {// Delete
                builder."$xf:label"('Delete')
                // Trigger javascript function that start action of creating a PB and setting it as the value of a bean's reference property
                // modeler.action.createReference(beanId, beanDb, propertyPath)
                builder.load([resource: "javascript:modeler.action.start('deleteReference','${metadata.propertyName}', '$beanId','$beanDb')",
                        "${xform.eventPrefix}:event": 'DOMActivate'])
            }
        }
    }
}