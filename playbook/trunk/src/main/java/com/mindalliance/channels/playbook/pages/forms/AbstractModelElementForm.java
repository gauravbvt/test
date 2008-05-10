package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel;
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

    public PlaybookModel getIfmModel() {
        return (PlaybookModel)((ModelElement)element.deref()).getModel().deref();
    }

    public boolean isModelPanel() {
        return true;
    }


    // End ElementPanel

}
