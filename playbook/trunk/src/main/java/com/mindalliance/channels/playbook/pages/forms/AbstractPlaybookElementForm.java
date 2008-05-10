package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.playbook.Playbook;
import com.mindalliance.channels.playbook.ifm.playbook.PlaybookElement;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 1:15:28 PM
 */
abstract public class AbstractPlaybookElementForm extends AbstractProjectElementForm {

    public AbstractPlaybookElementForm(String id, Ref element) {
        super(id, element);
    }

    // ElementPanel

    public Playbook getPlaybook() {
        return (Playbook) ((PlaybookElement)element.deref()).getPlaybook().deref();
    }

    public boolean isPlaybookPanel() {
        return true;
    }


    // End ElementPanel
}
