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
    def choices  // A list of items or a list of named choices (i.e. a tree)
    boolean open
    Instance instance // set if select gets its items from an itemset in an instance obtained from a query

    SelectOneOrMany(boolean many, Expando metadata, XForm xform) {
        super(metadata, xform)
        this.many = many
        initialize()
    }

    void initialize() {
        super.initialize()
        assert metadata.choices != null  // can be empty but not null
        choices = metadata.choices
        this.appearance = appearance ?: 'minimal'
        if (isChoicesFromQuery()) {  // build an instance for the select's itemset
            String instanceId = "i_${this.id}"
            instance = new Instance(instanceId, choices, this.xform)
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

    void build(def builder, String xf) {
        builder."$xf:${selectTag()}"(getAttributes()) {
            builder."$xf:label"(this.label)
            buildHint(builder, xf)
            if (isChoicesFromQuery()) {
                buildItemset(builder, xf)
            }
            else {
                buildEnumeratedChoices(builder, xf)
            }
         }
    }

    private void buildEnumeratedChoices(def builder, String xf) {
        if (isFlatChoices()) {
            choices.each {item ->
                 builder."$xf:item "{
                     builder."$xf:label"(itemLabel(item))
                     builder."$xf:value"(itemValue(item))
                 }
             }
        }
        // TODO - support trees of arbitrary depth - now just 1
        else { // [ [label, val, val...] ... ] => <choices><label>aLabel</label><item>anItem</item><value>aValue</value>... </choices>...
           choices.each {branch ->
              builder."$xf:choices "{
                  builder."$xf:label"(branch[0])
                  branch[1..<branch.size()].each {
                      builder."$xf:item"(it)
                      builder."$xf:value"(it)
                  }
              }
           }
        }
    }

    private String itemLabel(def item) {
        switch(item) {
            case Map.Entry: return item.key; break
            default: return item.toString()
        }
    }

    private String itemValue(def item) {
        switch(item) {
            case Map.Entry: return item.value; break
            default: return item.toString()
        }
    }

    /*
    instance contains:
        <items>
            <item label="aLabel">a value, possibly xml to be copied</item>
            ...
        </items>
    */
    private void buildItemset(def builder, String xf) {
      builder."$xf:itemset"(model:XForm.CONTROLS_MODEL_ID, nodeset:"instance('${instance.id}')/items/item") {
          builder."$xf:label"(ref:'@label')
          builder."$xf:copy"(ref:'./*')
      }
    }

    private boolean isFlatChoices() {
       return choices && !(choices[0] instanceof List)
    }

    private boolean isChoicesFromQuery() {
        return choices instanceof String || choices instanceof GString
    }

}