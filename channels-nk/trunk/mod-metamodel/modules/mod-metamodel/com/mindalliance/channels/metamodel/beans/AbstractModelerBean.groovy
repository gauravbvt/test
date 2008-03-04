package com.mindalliance.channels.metamodel.beans

import com.mindalliance.channels.nk.bean.AbstractPersistentBean
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Mar 3, 2008
* Time: 12:18:09 PM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractModelerBean  extends AbstractPersistentBean {


    void edit(String step, Map c10nState) {
        switch (step) {
            case 'start':
                c10nState['editedBean'] = new PersistentBeanAspect(this)
                break
            case 'commit': break // do nothing
            case 'abort': break // do nothing
            default: throw new IllegalArgumentException("Unsupported step $step for action addHelpTopic")
        }
    }


}