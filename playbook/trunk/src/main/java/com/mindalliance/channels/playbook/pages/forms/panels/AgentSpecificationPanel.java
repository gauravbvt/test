package com.mindalliance.channels.playbook.pages.forms.panels;

import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import com.mindalliance.channels.playbook.ifm.definition.Definition;
import com.mindalliance.channels.playbook.ifm.definition.AgentDefinition;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
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

    public AgentSpecificationPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    protected String getMatchingDomainName() {
        return "agent";
    }

    protected IModel getEnumerationChoicesModel() {
        return new RefQueryModel(getScope(), new Query("findAllAgents"));
    }

    protected AbstractDefinitionPanel makeDefinitionEditor(String id, String propPath) {
        return new AgentDefinitionPanel(id, this, propPath);
    }

    protected Definition makeNewDefinition() {
        return new AgentDefinition();
    }

    protected boolean isEnumerable() {
        return true;
    }
}
