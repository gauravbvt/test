package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ifm.project.ProjectElement;
import com.mindalliance.channels.playbook.ifm.model.Model;
import com.mindalliance.channels.playbook.ifm.model.ModelElement;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 2:52:19 PM
 */
abstract public class AbstractModelElementForm extends AbstractElementForm {

    public AbstractModelElementForm(String id, Ref element) {
        super(id, element);
    }

    // ElementPanel

    public Model getIfmModel() {
        return (Model)((ModelElement)element.deref()).getModel().deref();
    }

    // End ElementPanel

}
