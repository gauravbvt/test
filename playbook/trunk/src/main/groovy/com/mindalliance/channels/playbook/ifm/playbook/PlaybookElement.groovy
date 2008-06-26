package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.ifm.project.Project

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 30, 2008
 * Time: 3:09:17 PM
 */
abstract class PlaybookElement extends IfmElement {

    Ref playbook

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['playbookElement', 'project']
    }



    boolean isPlaybookElement() {
        return true
    }

    Ref getProject() {
       assert playbook
       return playbook.project
    }

}