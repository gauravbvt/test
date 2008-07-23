package com.mindalliance.channels.playbook.pages.forms.panels;

import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import com.mindalliance.channels.playbook.ifm.definition.Definition;
import com.mindalliance.channels.playbook.ifm.definition.OrganizationDefinition;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 30, 2008
 * Time: 7:29:31 PM
 */
public class OrganizationSpecificationPanel extends AbstractSpecificationPanel {

    public OrganizationSpecificationPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    protected String getMatchingDomainName() {
        return "organization";
     }

    protected IModel getEnumerationChoicesModel() {
        return new RefPropertyModel(getProject(), "organizations");
    }

    protected AbstractDefinitionPanel makeDefinitionEditor(String id, String propPath) {
        return new OrganizationDefinitionPanel(id, this, propPath);
    }

    protected Definition makeNewDefinition() {
        return new OrganizationDefinition();
    }

    protected boolean isEnumerable() {
        return true;
    }
}
