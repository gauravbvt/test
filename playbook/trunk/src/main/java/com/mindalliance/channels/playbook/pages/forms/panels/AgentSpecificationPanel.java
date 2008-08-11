package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.definition.AgentDefinition;
import com.mindalliance.channels.playbook.ifm.definition.Definition;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import org.apache.wicket.model.IModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary
 * and Confidential.
 * <p/>
 * User: jf Date: Jun 30, 2008 Time: 3:35:04 PM
 */
public class AgentSpecificationPanel extends AbstractSpecificationPanel {

    private static final long serialVersionUID = 2599041785571258042L;

    public AgentSpecificationPanel(
            String id, AbstractPlaybookPanel parentPanel, String propPath ) {
        super( id, parentPanel, propPath );
    }

    @Override
    protected String getMatchingDomainName() {
        return "agent";
    }

    @Override
    protected IModel<?> getEnumerationChoicesModel() {
        return new RefQueryModel( getScope(), new Query( "findAllAgents" ) );
    }

    @Override
    protected AbstractDefinitionPanel makeDefinitionEditor(
            String id, String propPath ) {
        return new AgentDefinitionPanel( id, this, propPath );
    }

    @Override
    protected Definition makeNewDefinition() {
        return new AgentDefinition();
    }

    @Override
    protected boolean isEnumerable() {
        return true;
    }
}
