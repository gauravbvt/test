package com.mindalliance.channels.forms.xform

import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.forms.xform.model.Model
import com.mindalliance.channels.forms.xform.ui.AbstractUIElement
import com.mindalliance.channels.forms.xform.model.Instance
import com.mindalliance.channels.forms.xform.model.Submission
import com.mindalliance.channels.forms.xform.model.Binding
import com.mindalliance.channels.forms.xform.ui.Submit
import com.mindalliance.channels.nk.bean.IBeanPropertyValue
import com.mindalliance.channels.nk.bean.ISimpleData
import com.mindalliance.channels.forms.xform.ui.Input
import com.mindalliance.channels.forms.xform.ui.SelectOneOrMany
import com.mindalliance.channels.forms.xform.ui.RangeControl
import com.mindalliance.channels.nk.bean.IComponentBean
import com.mindalliance.channels.nk.bean.IBeanList
import com.mindalliance.channels.nk.bean.IBeanReference
import com.mindalliance.channels.forms.xform.ui.custom.BeanComponentGroup
import com.mindalliance.channels.forms.xform.ui.custom.BeanListRepeat
import com.mindalliance.channels.forms.xform.ui.custom.BeanReferenceControl
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 28, 2008
* Time: 8:12:56 PM
* To change this template use File | Settings | File Templates.
*/
class BeanXForm extends XForm {

    static public final String BEAN_INSTANCE_ID = 'bean'
    static public final String BEAN_MODEL_ID = 'bean'
    static public final String ACCEPT_LABEL = 'Accept'
    static public final String CANCEL_LABEL = 'Cancel'
    static public final String ACCEPT_ID = 'accept'
    static public final String CANCEL_ID = 'cancel'
    static public final int DEFAULT_REPEAT_NUMBER = 3

    static public final String METAMODEL_QUERY_URI_PREFIX = 'query/meta/'    // TODO to be supported by mod-modeler
    static public final String INTERNAL_METAMODEL_QUERY_URI_PREFIX = 'ffcpl:/com/mindalliance/channels/metamodel/queries'

    static public final boolean MANY = true

    IPersistentBean bean // the bean to edit
    String beanInstanceUrl // where the xform gets its bean instance to edit
    String acceptSubmissionUrl // where to post the edited bean
    String cancelSubmissionUrl // where to announce abort of edit
    String internalQueryUriPrefix


    BeanXForm(IPersistentBean bean, Map settings, Context context) {
        super(context)
        this.bean = bean
        settings.each {key, value ->
            this."$key" = value
        }
        createElements()
    }

    void createElements() {
        // create bean model with its bean instance
        Model beanModel = new Model(BEAN_MODEL_ID, this.customSchemaUrl, this)
        Instance beanInstance = new Instance(BEAN_INSTANCE_ID, beanInstanceUrl, this)
        beanModel.addInstance(beanInstance)
        addModel(beanModel)
        Model controlsModel = new Model(CONTROLS_MODEL_ID, customSchemaUrl, this)
        addModel(controlsModel)
        bean.getBeanProperties().each {propName, propValue ->
            // Generate bean model bindings for all bean property values
            propValue.accept([:], {args, self -> // Apply visitor
                def metadata = self.metadata
                Binding binding = new Binding(beanInstance.id, metadata, this)
                beanModel.addBinding(binding)
            })
            // Generate form ui elements for the bean's immediate properties
            AbstractUIElement element = BeanXForm.makeUIElement(propValue, this)
            addUIElement(element)
        }
        // Add bean form submissions to bean model
        Submission acceptSubmission = new Submission(ACCEPT_ID, acceptSubmissionUrl, this)
        beanModel.addSubmission(acceptSubmission)
        Submission cancelSubmission = new Submission(CANCEL_ID, cancelSubmissionUrl, this)
        beanModel.addSubmission(cancelSubmission)
        // Add submit controls
        Submit acceptSubmit = new Submit(ACCEPT_LABEL, acceptSubmission.id, this)
        addSubmit(acceptSubmit)
        Submit cancelSubmit = new Submit(CANCEL_LABEL, cancelSubmission.id, this)
        addSubmit(cancelSubmit)
    }

    static AbstractUIElement makeUIElement(IBeanPropertyValue propValue, BeanXForm xform) {
        AbstractUIElement element
        switch (propValue) {
            case ISimpleData: element = makeSimpleControl((ISimpleData) propValue, xform); break
            case IBeanReference: element = new BeanReferenceControl((IBeanReference) propValue, xform); break
            case IBeanList: element = makeBeanListControl((IBeanList) propValue, xform); break
            case IComponentBean: element = new BeanComponentGroup((IComponentBean) propValue, xform); break
            default: throw new IllegalArgumentException("Can't associate bean's $propValue to a XForm control")
        }
        return element
    }

    static AbstractUIElement makeSimpleControl(ISimpleData simpleData, BeanXForm xform) {
        AbstractUIElement control
        Expando metadata = (Expando) simpleData.metadata
        assert metadata.path
        if (metadata.formControl) {// the type of control is dictated, e.g. 'textArea' or 'custom.sprocket'
            String controlClassName = controlClassNameFor(metadata.formControl)
            control = (AbstractUIElement) Class.forName(controlClassName).newInstance([metadata, xform])
        }
        else if (callsForRange(simpleData)) {// Check if data is a number within a range
            control = new RangeControl(metadata, xform)
        }
        else if (metadata.choices) {// Check if list of choices defined
            control = new SelectOneOrMany(!MANY, metadata, xform)
        }
        else {
            control = new Input(metadata, xform)
        }
        return control
    }

    static AbstractUIElement makeBeanListControl(IBeanList beanList, BeanXForm xform) {
        AbstractUIElement control
        Expando metadata = (Expando) beanList.metadata
        if (metadata.choices) {
            control = new SelectOneOrMany(MANY, metadata, xform) // multiple choice fromresults of a query
        }
        else {
            control = new BeanListRepeat(beanList, xform)
        }
        return control
    }

    static boolean callsForRange(ISimpleData simpleData) {
        Expando metadata = (Expando) simpleData.metadata
        if (metadata.range) {
            assert Number.class.isAssignableFrom(simpleData.dataClass)
            assert metadata.range instanceof Range
            return true
        }
        else {
            return false
        }
    }

    static controlClassNameFor(String controlType) {
        // get basepath for all controls
        String str = AbstractUIElement.class.name
        String basepath = str[0..str.lastIndexOf('.') - 1]
        // get relative path, if any
        String relativePath = ''
        String controlClassName = controlType
        int dot = relativePath.lastIndexOf('.')
        assert dot != 0
        if (dot > 0) {
            relativePath = controlType[0..<dot]
            controlClassName = controlType.substring(dot + 1)
        }
        // Capitalize control class name if needed
        String capitalized = controlClassName[0].toUpperCase() + controlClassName.substring(1)
        // Return fully qualified control class name
        return "${basepath}.${relativePath}.${capitalized}"
    }


    String subjectName() {
        String longName = bean.class.name
        String shortName = longName.substring(longName.lastIndexOf('.') + 1)
        return shortName
    }

    String getControlInstanceUriPrefix()  { // the start of the instance src uri for controls
         return METAMODEL_QUERY_URI_PREFIX
    }
}