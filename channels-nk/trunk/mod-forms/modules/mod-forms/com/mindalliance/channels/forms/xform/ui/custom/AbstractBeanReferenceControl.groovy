package com.mindalliance.channels.forms.xform.ui.custom

import com.mindalliance.channels.forms.xform.ui.AbstractUIElement
import com.mindalliance.channels.nk.bean.IBeanReference
import com.mindalliance.channels.forms.xform.BeanXForm

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 29, 2008
* Time: 9:47:53 AM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractBeanReferenceControl  extends AbstractUIElement {

    IBeanReference beanReference

    AbstractBeanReferenceControl(IBeanReference beanReference, BeanXForm xform) {
        super((Expando) beanReference.metadata, xform)
        this.beanReference = beanReference
    }


}