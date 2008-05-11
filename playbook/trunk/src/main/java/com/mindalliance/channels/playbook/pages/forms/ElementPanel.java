package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel;
import com.mindalliance.channels.playbook.ifm.playbook.Playbook;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 1, 2008
 * Time: 9:43:43 PM
 */
public interface ElementPanel {

    Ref getElement();
    void elementChanged(String propPath, AjaxRequestTarget target);
    void addOtherElement(Ref otherElement);
    AbstractElementForm getTopElementPanel();
    boolean isProjectPanel();
    boolean isModelPanel();
    boolean isPlaybookPanel();
    Project getProject();
    PlaybookModel getPlaybookModel();
    Playbook getPlaybook();
    Ref getScope(); // either Channels.instance(), a Project, an IFM PlaybookModel or a Playbook
    void edit(Ref ref, AjaxRequestTarget target);
}
