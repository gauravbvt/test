package com.mindalliance.channels.playbook.pages.forms.panels;

import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import com.mindalliance.channels.playbook.ifm.definition.Definition;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 30, 2008
 * Time: 7:29:31 PM
 */
public class OrganizationSpecificationPanel extends AbstractSpecificationPanel {

    public OrganizationSpecificationPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected String getMatchingDomainName() {
        return null;  //Todo
     }

    protected IModel getEnumerationChoicesModel() {
        return null;  //Todo
    }

    protected AbstractComponentPanel makeDefinitionEditor(String id, String propPath) {
        return null;  //Todo
    }

    protected Definition makeNewDefinition() {
        return null;  //Todo
    }
}
