package com.mindalliance.channels.forms.xform.ui.custom

import com.mindalliance.channels.forms.xform.ui.AbstractUIElement
import com.mindalliance.channels.nk.bean.IBeanList
import com.mindalliance.channels.forms.xform.BeanXForm

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 3, 2008
* Time: 3:32:59 PM
* To change this template use File | Settings | File Templates.
*/
class BeanListRepeat extends AbstractUIElement {

    IBeanList beanList
    int number // Maximum number of items to show
    String nodeset
    AbstractUIElement repeatedElement

    BeanListRepeat(IBeanList beanList, BeanXForm xform) {
        super((Expando) beanList.metadata, xform)
        this.beanList = beanList
        this.xform = xform
        initialize()
    }

    void initialize() {
        super.initialize()
        id = metadata.id
        if (!referenced) {    // make sure to use ref, not bind
            ref = metadata.path
            bind = null
        }
        number = metadata.number ?: BeanXForm.DEFAULT_REPEAT_NUMBER
        repeatedElement = makeSubUIElement(beanList.getActivatedItemPrototype()) // sub-element becomes referenced too
    }

    Map getAttributes() {
        Map attributes = super.getAttributes()
        attributes += [model:BeanXForm.BEAN_MODEL_ID, ref:ref, number:number, startindex:1]
        return attributes
    }

    void build(def xf) {
        // Wrap repeat and add/remove/scroll triggers into an anonymous group
        xf.group() {
            xf.repeat(getAttributes()) {      
                // build item prototype with refs, not binds
                repeatedElement.build(xf)
            }
            // Build add/remove/scroll triggers within an anonymous group
            xf.group() {
               trigger() {
                   label('Add')
                   insert(nodeset:repeatedElement.ref,
                          at:"index('${this.id}')",
                          position:'after',
                          "${this.xform.eventPrefix}:event":'DOMActivate')
               }
               trigger() {
                    label('Delete')
                    delete(nodeset:repeatedElement.ref,
                           at:"index('${this.id}')",
                           "${this.xform.eventPrefix}:event":'DOMActivate')
                }
                trigger() {
                     label('Scroll torward')
                     setindex(repeat:"${this.id}",
                             index:"index('${this.id}')+1",
                            "${this.xform.eventPrefix}:event":'DOMActivate')
                 }
                trigger() {
                      label('Scroll backward')
                      setindex(repeat:"${this.id}",
                              index:"index('${this.id}')-1",
                             "${this.xform.eventPrefix}:event":'DOMActivate')
                  }
            }
        }
    }

}