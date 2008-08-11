package com.mindalliance.channels.playbook.ifm.project

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.InProject
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ifm.Channels

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 30, 2008
 * Time: 2:40:03 PM
 */
class ProjectElement extends IfmElement implements InProject {
    private static final long serialVersionUID = -1L;

    private Ref cachedProject

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['resourceElement', 'project', 'cachedProject'])
    }

    Set hiddenProperties() {
        return (super.hiddenProperties() + ['resourceElement']) as Set
    }

    void detach() {
        super.detach()
        cachedProject = null
    }

    boolean isProjectElement() {
        return true
    }

    boolean isResourceElement() {
        return false
    }

    Ref getProject() {
        if (cachedProject == null) {
            cachedProject = (Ref)Query.execute(Channels.instance(), "findProjectOfElement", this.reference)
        }
        return cachedProject

    }

}