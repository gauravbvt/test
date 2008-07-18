package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.support.RefUtils
import com.mindalliance.channels.playbook.support.drools.RuleBaseSession

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 12:36:45 PM
*/
/*abstract*/ class IfmElement extends ReferenceableImpl implements Serializable {

    Date createdOn = new Date()
    Date lastModified = new Date()

    @Override
    protected List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['projectElement', 'taxonomyElement', 'playbookElement',
                                                             'elementIssues', 'elementInvalidations', 'problems'])
    }

    void makeRoot() {
        Ref root = ApplicationMemory.getRoot()
        this.id = root.id
        this.db = root.db
    }

    void addElement(IfmElement element) {
        String type = element.type
        String field = "${RefUtils.decapitalize(type)}s"
        doAddToField(field, element)
    }

    void initializeFromContext(IfmElement context) {
        // Do nothing  -- override this
    }

    void changed() {
       super.changed()
       lastModified = new Date()
    }

    boolean isProjectElement() {
        return false
    }

    boolean isTaxonomyElement() {
        return false
    }

    boolean isPlaybookElement() {
        return false
    }

    List getElementIssues() {
         return RuleBaseSession.current().query("elementIssues", [this.id], "_issue")
    }

    List getElementInvalidations() {
        return RuleBaseSession.current().query("elementInvalids", [this.id], "_invalid")
    }

    List getProblems() {
        return (List)(getElementIssues() + getElementInvalidations())
    }

}