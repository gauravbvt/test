package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ifm.InProject;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 2, 2008
 * Time: 8:02:49 AM
 */
abstract public class AbstractProjectElementForm extends AbstractElementForm {

    public AbstractProjectElementForm(String id, Ref element) {
        super(id, element);
    }

    // ElementPanel

    public boolean isProjectPanel() {
        return true;
    }


    public Project getProject() {
        return (Project)((InProject)element.deref()).getProject().deref();
    }

    // End ElementPanel


}
