package com.mindalliance.channels.forms.xform

import com.mindalliance.channels.forms.xform.model.Model
import groovy.xml.MarkupBuilder
import groovy.xml.NamespaceBuilder
import com.mindalliance.channels.forms.xform.ui.Submit

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 1, 2008
* Time: 9:36:57 AM
* To change this template use File | Settings | File Templates.
*/
abstract class XForm {

    static public final String CONTROLS_MODEL_ID = 'controls'
    static public final String XFORMS_NAMESPACE_URL = 'http://www.w3.org/2002/xforms'
    static public final String XFORMS_NAMESPACE_PREFIX = 'xf'
    static public final String SUBMITS_CSS_CLASS = 'submits'

    String xsdSchemaPrefix
    String eventPrefix
    String customSchemaPrefix
    String customSchemaUrl
    String formCssClass

    Map models = [:]
    List uiElements = []
    List submits = []

    abstract void createElements()

    abstract String subjectName() // a short name (no spaces) for the subject of this form

    abstract String getControlInstanceUriPrefix() // the start of the instance src uri for controls

    void addModel(Model model) {
        models[model.id] = model
    }

    void addUIElement(AbstractElement element) {
        uiElements.add(element)
    }

    void addSubmit(Submit submit) {
        submits.add(submit)
    }

    String toXml() {
        StringWriter writer = new StringWriter()
        MarkupBuilder mkBuilder = new MarkupBuilder(writer)
        NamespaceBuilder builder = new NamespaceBuilder(mkBuilder)
        def xf = builder.namespace(XFORMS_NAMESPACE_URL, XFORMS_NAMESPACE_PREFIX)
        build(xf)
        return writer.toString()
    }

    void build(def xf) {
        models.each {key, model ->
            model.build(xf)
        }
        // build controls within a group
        xf.group('class': formCssClass) {
            // Build controls
            uiElements.each {element -> element.build(xf)}
            // Build submit triggers within a sub-group
            xf.group('class': SUBMITS_CSS_CLASS) {
                submits.each {submit -> submit.build(xf)}
            }
        }
    }

}