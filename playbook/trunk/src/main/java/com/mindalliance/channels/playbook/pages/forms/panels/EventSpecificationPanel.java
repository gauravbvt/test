package com.mindalliance.channels.playbook.pages.forms.panels;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import com.mindalliance.channels.playbook.ifm.definition.Definition;
import com.mindalliance.channels.playbook.ifm.definition.EventDefinition;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ref.Ref;

import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2008
 * Time: 9:55:48 AM
 */
public class EventSpecificationPanel extends AbstractSpecificationPanel {
    
    public EventSpecificationPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    protected String getMatchingDomainName() {
        return "event"; 
    }

    protected IModel getEnumerationChoicesModel() {
        if (isPlaybookPanel()) {
            return new RefPropertyModel(getPlaybook(), "events");
        }
        else {
            return new Model(new ArrayList<Ref>());
        }
    }

    protected AbstractDefinitionPanel makeDefinitionEditor(String id, String propPath) {
        return new EventDefinitionPanel(id, this, propPath);
    }

    protected Definition makeNewDefinition() {
        return new EventDefinition();
    }

    protected boolean isEnumerable() {
        return isPlaybookPanel();
    }
}
