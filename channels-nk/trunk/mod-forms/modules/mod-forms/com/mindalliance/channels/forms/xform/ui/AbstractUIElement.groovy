package com.mindalliance.channels.forms.xform.ui

import com.mindalliance.channels.forms.xform.XForm
import com.mindalliance.channels.forms.xform.BeanXForm
import com.mindalliance.channels.forms.xform.AbstractElement
import com.mindalliance.channels.forms.xform.model.Binding
import com.mindalliance.channels.nk.bean.IBeanPropertyValue

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 28, 2008
* Time: 8:14:58 PM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractUIElement extends AbstractElement {// Value editing control

    Expando metadata
    String cssClass // can be null
    String anyAttribute // can be null
    String appearance // can be null
    String bind // set unless referenced
    String ref // null unless referenced
    String label
    String hint // can be null
    String constraint // can be null
    boolean referenced = false // default

    AbstractUIElement(Expando metadata, XForm xform) {
        super(xform)
        this.metadata = metadata
    }

    void initialize() {
        super.initialize()
        this.id = metadata.id
        label = metadata.label ?: AbstractUIElement.makeLabel(metadata.propertyName)
        hint = metadata.hint
        bind = Binding.makeBindingId(id) // by default ui elements are bound - reset to null in repeated ui elements
        cssClass = metadata.cssClass // can be null
        appearance = metadata.appearance // may be null
        anyAttribute = metadata.anyAttribute // may be null
        constraint = metadata.constraint // may be null
    }

    // unset bind and set ref
    void becomeReferencedFrom(AbstractUIElement parent) {
        assert parent.ref
        String refPath = parent.ref
        bind = null
        assert metadata.path
        String path = metadata.path
        assert path.indexOf(refPath) == 0
        ref = path[refPath.size() + 1..<path.size()] // make reference relative
        referenced = true
    }

    Map getAttributes() {
        Map attributes = super.getAttributes()
        assert ref || bind
        if (bind) attributes += [bind: bind]
        if (ref) attributes += [ref: ref]
        if (cssClass) attributes += ['class': cssClass]
        if (anyAttribute) attributes += [anyAttribute: anyAttribute]
        if (appearance) attributes += [appearance: appearance]
        if (constraint) attributes += [constraint: constraint]
        return attributes
    }

    AbstractUIElement makeSubUIElement(IBeanPropertyValue propValue) {
        AbstractUIElement uiElement = BeanXForm.makeUIElement(propValue, (BeanXForm) this.xform)
        if (this.referenced) {
            uiElement.becomeReferencedFrom(this)
        }
        return uiElement
    }

    static String makeLabel(String propName) {
        return "${propName[0].toUpperCase()}${propName.substring(1)}"
    }

    void buildHint(def builder, String xf) {
        if (hint) {
            builder."$xf:hint"(hint)
        }
    }

}