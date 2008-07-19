package com.mindalliance.channels.playbook.ifm.project

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.InProject

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 30, 2008
 * Time: 2:40:03 PM
 */
class ProjectElement extends IfmElement implements InProject {

    Ref project

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['resourceElement'])
    }

    Set hiddenProperties() {
        return (super.hiddenProperties() + ['resourceElement']) as Set
    }


    boolean isProjectElement() {
        return true
    }

    boolean isResourceElement() {
        return false
    }

}