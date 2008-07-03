package com.mindalliance.channels.playbook.pages.forms.panels;

import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import com.mindalliance.channels.playbook.ifm.definition.Definition;
import com.mindalliance.channels.playbook.ifm.definition.EventDefinition;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2008
 * Time: 9:55:48 AM
 */
public class EventSpecificationPanel extends AbstractSpecificationPanel {
    
    public EventSpecificationPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected String getMatchingDomainName() {
        return "event"; 
    }

    protected IModel getEnumerationChoicesModel() {
        return new RefPropertyModel(getPlaybook(), "events");
    }

    protected AbstractDefinitionPanel makeDefinitionEditor(String id, String propPath) {
        return new EventDefinitionPanel(id, this, propPath, isReadOnly(), feedback);
    }

    protected Definition makeNewDefinition() {
        return new EventDefinition();
    }
}
