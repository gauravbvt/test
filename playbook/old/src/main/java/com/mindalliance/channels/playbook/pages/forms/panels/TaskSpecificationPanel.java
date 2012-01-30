package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.definition.Definition;
import com.mindalliance.channels.playbook.ifm.definition.TaskDefinition;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import org.apache.wicket.model.IModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2008
 * Time: 4:06:45 PM
 */
public class TaskSpecificationPanel extends AbstractSpecificationPanel {

    private static final long serialVersionUID = 4606384303074912251L;

    public TaskSpecificationPanel(
            String id, AbstractPlaybookPanel parentPanel, String propPath ) {
        super( id, parentPanel, propPath );
    }

    @Override
    protected String getMatchingDomainName() {
        return "task";
    }

    @Override
    protected IModel<?> getEnumerationChoicesModel() {
        return new RefQueryModel(
                getPlaybook(),
                new Query( "findInformationActsOfType", "Task" ) );
    }

    @Override
    protected AbstractDefinitionPanel makeDefinitionEditor(
            String id, String propPath ) {
        return new TaskDefinitionPanel( id, this, propPath );
    }

    @Override
    protected Definition makeNewDefinition() {
        return new TaskDefinition();
    }

    @Override
    protected boolean isEnumerable() {
        return isPlaybookPanel();
    }
}
