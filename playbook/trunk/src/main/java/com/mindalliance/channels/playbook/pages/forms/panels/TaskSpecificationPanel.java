package com.mindalliance.channels.playbook.pages.forms.panels;

import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import com.mindalliance.channels.playbook.ifm.definition.Definition;
import com.mindalliance.channels.playbook.ifm.definition.TaskDefinition;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2008
 * Time: 4:06:45 PM
 */
public class TaskSpecificationPanel extends AbstractSpecificationPanel {

    public TaskSpecificationPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected String getMatchingDomainName() {
        return "task";
    }

    protected IModel getEnumerationChoicesModel() {
        return new RefQueryModel(getPlaybook(), new Query("findInformationActsOfType", "Task"));
    }

    protected AbstractDefinitionPanel makeDefinitionEditor(String id, String propPath) {
        return new TaskDefinitionPanel(id, this, propPath, isReadOnly(), feedback);
    }

    protected Definition makeNewDefinition() {
        return new TaskDefinition();
    }

    protected boolean isEnumerable() {
        return isPlaybookPanel();
    }
}
