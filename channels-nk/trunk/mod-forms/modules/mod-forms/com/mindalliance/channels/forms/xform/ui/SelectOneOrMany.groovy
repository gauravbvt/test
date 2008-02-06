package com.mindalliance.channels.forms.xform.ui

import com.mindalliance.channels.forms.xform.XForm
import com.mindalliance.channels.forms.xform.model.Instance

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 31, 2008
* Time: 7:12:17 PM
*/
class SelectOneOrMany extends AbstractUIElement {

    boolean many
    def choices
    boolean open
    Instance instance // set if select gets its items from an itemset in an instance obtained from a query

    SelectOneOrMany(boolean many, Expando metadata, XForm xform) {
        super(metadata, xform)
        this.many = many
        initialize()
    }

    void initialize() {
        super.initialize()
        assert metadata.choices  // can't be empty or null
        choices = metadata.choices
        this.appearance = appearance ?: 'minimal'
        if (isChoicesFromQuery()) {  // build an instance for the select's itemset
            String instanceId = "i_${this.id}"
            String shortBeanClassName = this.contextBean.class.name
            instance = new Instance(instanceId, "${this.xform.getControlInstanceUriPrefix()}${this.xform.subjectName()}/$choices", this.xform)
            this.xform.models[XForm.CONTROLS_MODEL_ID].addInstance(instance)
        }
    }

    Map getAttributes() {
        Map attributes = super.getAttributes()
        if (open) attributes +=[selection:'open']
        return attributes
    }

    private String selectTag() {
        return many ? 'select' : 'select1'
    }

    void build(def xf) {
        xf."${selectTag()}"(getAttributes()) {
            xf.label(this.label)
            if (isEnumeratedChoices()) {
                buildEnumeratedChoices(xf)
            }
            else {
                buildItemset(xf)
            }
         }
    }

    private void buildEnumeratedChoices(def xf) {
        if (isFlatChoices()) {
            choices.each {item ->
                 xf.item {
                     label(item.toString())
                     value(item.toString())
                 }
             }
        }
        else { // [ [label, val, val...] ... ] => <choices><label>aLabel</label><item>anItem</item><value>aValue</value>... </choices>...
           choices.each {branch ->
              xf.choices {
                  label(branch[0])
                  branch[1..<branch.size()].each {
                      xf.item(it)
                      xf.value(it)
                  }
              }
           }
        }
    }
    /*
    instance contains:
        <items>
            <item label="aLabel">a value, possibly xml to be copied</item>
            ...
        </items>
    */
    private void buildItemset(def xf) {
      xf.itemset(model:XForm.CONTROLS_MODEL_ID, nodeset="instance('${instance.id}')/items/item") {
          label(ref:'@label')
          copy(ref:'.')
      }
    }

    private boolean isFlatChoices() {
       return !choices[0] instanceof List
    }

    private boolean isChoicesFromQuery() {
        return choices && choices instanceof String
    }

}