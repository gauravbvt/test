package com.mindalliance.channels.forms.xform

import com.mindalliance.channels.forms.xform.model.Model
import groovy.xml.MarkupBuilder
import com.mindalliance.channels.forms.xform.ui.Submit

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.forms.xform.model.Instance

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 1, 2008
* Time: 9:36:57 AM
* To change this template use File | Settings | File Templates.
*/
abstract class XForm {

    static public final String CONTROLS_MODEL_ID = 'controls'
    static public final String SUBMITS_CSS_CLASS = 'submits'

    Context context
    String xfPrefix
    String xsdSchemaPrefix
    String eventPrefix
    String customSchemaPrefix
    String customSchemaUrl
    String formCssClass

    Map models = [:]
    List uiElements = []
    List submits = []

    XForm(Context context) {
        this.context = context
    }

    abstract void createElements()

    abstract String subjectName() // a short name (no spaces) for the subject of this form

    abstract String getControlInstanceUriPrefix() // the start of the instance src uri for controls

    void addModel(Model model) {
        models[model.id] = model
    }

    void addControlInstance(String id, String src) {
        models[CONTROLS_MODEL_ID].addInstance(new Instance(id, src, this))
    }

    void addUIElement(AbstractElement element) {
        uiElements.add(element)
    }

    void addSubmit(Submit submit) {
        submits.add(submit)
    }

    String toXml() {
        StringWriter writer = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(writer)
        build(builder, xfPrefix)
        String xml = writer.toString().replaceAll('&apos;', '"').replaceAll('&amp;', '&').replaceAll('&lt;', '<').replaceAll('&gt;', '>')
        return xml
    }

    void build(def builder, String xf) {
        models.each {key, model ->
            model.build(builder, xf)
        }
        // build controls within a group
        builder."$xf:group"('class': formCssClass) {
            // Build controls
            uiElements.each {element -> element.build(builder, xf)}
            // Build submit triggers within a sub-group
            builder."$xf:group"('class': SUBMITS_CSS_CLASS) {
                submits.each {submit -> submit.build(builder, xf)}
            }
        }
    }

}