package com.mindalliance.channels.forms.xform.ui.custom

import com.mindalliance.channels.nk.bean.IComponentBean
import com.mindalliance.channels.forms.xform.BeanXForm
import com.mindalliance.channels.forms.xform.ui.AbstractUIElement

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 3, 2008
* Time: 10:51:49 AM
* To change this template use File | Settings | File Templates.
*/
class BeanComponentGroup extends AbstractUIElement {

    IComponentBean componentBean
    List uiElements

    BeanComponentGroup(componentBean, BeanXForm xform) {
        super(componentBean.metadata, xform)
        this.componentBean = componentBean
        initialize()
    }

    void initialize() {
        super.initialize()
        uiElements = []
        createElements()
    }

    void createElements() {
        // Create group's ui elements
        componentBean.getBeanProperties().each {propName, propValue ->
            AbstractUIElement uiElement = makeSubUIElement(propValue)
            uiElements.add(uiElement)
        }
    }

    void build(def xf) {
        xf.group(getAttributes()) {
            label(label)
            uiElements.each {el ->
                el.build(xf)
            }
        }
    }

}