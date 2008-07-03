package com.mindalliance.channels.playbook.pages.forms.panels;

import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import com.mindalliance.channels.playbook.ifm.definition.Definition;
import com.mindalliance.channels.playbook.ifm.definition.AgentDefinition;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 30, 2008
 * Time: 3:35:04 PM
 */
public class AgentSpecificationPanel extends AbstractSpecificationPanel {

    public AgentSpecificationPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected String getMatchingDomainName() {
        return "agent";
    }

    protected IModel getEnumerationChoicesModel() {
        return new RefQueryModel(getScope(), new Query("findAllAgents"));
    }

    protected AbstractDefinitionPanel makeDefinitionEditor(String id, String propPath) {
        return new AgentDefinitionPanel(id, this, propPath, isReadOnly(), feedback);
    }

    protected Definition makeNewDefinition() {
        return new AgentDefinition();
    }
}
