package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.definition.Definition;
import com.mindalliance.channels.playbook.ifm.definition.EventDefinition;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2008
 * Time: 9:55:48 AM
 */
public class EventSpecificationPanel extends AbstractSpecificationPanel {

    private static final long serialVersionUID = 8416857074094237612L;

    public EventSpecificationPanel(
            String id, AbstractPlaybookPanel parentPanel, String propPath ) {
        super( id, parentPanel, propPath );
    }

    @Override
    protected String getMatchingDomainName() {
        return "event";
    }

    @Override
    protected IModel<ArrayList<Ref>> getEnumerationChoicesModel() {
        if ( isPlaybookPanel() ) {
            return new RefPropertyModel( getPlaybook(), "events" );
        } else {
            return new Model<ArrayList<Ref>>( new ArrayList<Ref>() );
        }
    }

    @Override
    protected AbstractDefinitionPanel makeDefinitionEditor(
            String id, String propPath ) {
        return new EventDefinitionPanel( id, this, propPath );
    }

    @Override
    protected Definition makeNewDefinition() {
        return new EventDefinition();
    }

    @Override
    protected boolean isEnumerable() {
        return isPlaybookPanel();
    }
}
