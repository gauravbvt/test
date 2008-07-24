package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ifm.Channels
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 30, 2008
 * Time: 3:09:17 PM
 */
abstract class PlaybookElement extends ProjectElement {

    private Ref cachedPlaybook

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['playbookElement', 'project', 'cachedPlaybook', 'playbook'])
    }

    void detach() {
        super.detach()
        cachedPlaybook = null
    }


    boolean isPlaybookElement() {
        return true
    }

    Ref getProject() {
        Ref pb = getPlaybook()
        return (pb as boolean) ? pb.project : null
    }

    Ref getPlaybook() {
        if (cachedPlaybook == null) {
            cachedPlaybook = (Ref)Query.execute(Channels.instance(), "findPlaybookOfElement", this.reference)
        }
        return cachedPlaybook
    }


}