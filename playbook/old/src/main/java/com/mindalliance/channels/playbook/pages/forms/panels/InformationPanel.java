package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.info.Information;
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary
 * and Confidential.
 * <p/>
 * User: jf Date: May 9, 2008 Time: 2:47:26 PM
 */
public class InformationPanel extends AbstractComponentPanel {

    private Information information;
    private AjaxCheckBox affirmedCheckBox;
    private AjaxCheckBox negatedCheckBox;
    private DynamicFilterTree eventTree; // what the info is about
    private DynamicFilterTree eventTypesTree;
    private EOIsPanel eoisPanel;  // content
    private DynamicFilterTree sourceAgentsTree;
    private TimingPanel timeToLivePanel;
    private static final long serialVersionUID = 6506457086665774035L;

    public InformationPanel(
            String id, AbstractPlaybookPanel parentPanel, String propPath ) {
        super( id, parentPanel, propPath );
    }

    @Override
    protected void load() {
        super.load();
        information = (Information) getComponent();
        affirmedCheckBox = new AjaxCheckBox(
                "affirmed", new Model<Boolean>( information.getAffirmed() ) ) {
            private static final long serialVersionUID = -5219278948923060533L;

            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                boolean isAffirmed = affirmedCheckBox.getModelObject();
                RefUtils.set( information, "affirmed", isAffirmed );
                elementChanged( propPath + ".affirmed", target );
                negatedCheckBox.setModelObject( !isAffirmed );
                target.addComponent( negatedCheckBox );
            }
        };
        addReplaceable( affirmedCheckBox );
        negatedCheckBox = new AjaxCheckBox(
                "negated", new Model<Boolean>( !information.getAffirmed() ) ) {
            private static final long serialVersionUID = 8904568923465753161L;

            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                boolean isNegated = negatedCheckBox.getModelObject();
                RefUtils.set( information, "affirmed", !isNegated );
                elementChanged( propPath + ".affirmed", target );
                affirmedCheckBox.setModelObject( !isNegated );
                target.addComponent( affirmedCheckBox );
            }
        };
        addReplaceable( negatedCheckBox );
        eventTree = new DynamicFilterTree(
                "event",
                new RefPropertyModel( getElement(), propPath + ".event" ),
                new RefQueryModel(
                        getPlaybook(), new Query( "findAllOccurrences" ) ),
                SINGLE_SELECTION ) {
            private static final long serialVersionUID = -1471649452064789098L;

            @Override
            public void onFilterSelect(
                    AjaxRequestTarget target, Filter filter ) {
                setProperty( "event", eventTree.getNewSelection() );
            }
        };
        addReplaceable( eventTree );
        eventTypesTree = new DynamicFilterTree(
                "eventTypes",
                new RefPropertyModel( getElement(), propPath + ".eventTypes" ),
                new RefQueryModel(
                        getProject(),
                        new Query( "findAllTypes", "EventType" ) ) ) {
            private static final long serialVersionUID = -3878267947442943231L;

            @Override
            public void onFilterSelect(
                    AjaxRequestTarget target, Filter filter ) {
                setProperty( "eventTypes", eventTypesTree.getNewSelections() );
                target.addComponent( eoisPanel );
            }
        };
        addReplaceable( eventTypesTree );
        RefQueryModel topicChoicesModel = new RefQueryModel(
                EventType.class, new Query(
                "findAllTopicsIn", new RefPropertyModel(
                getElement(), propPath + ".eventTypes" ) ) );
        eoisPanel = new EOIsPanel(
                "eventDetails",
                this,
                propPath + ".eventDetails",
                topicChoicesModel );
        addReplaceable( eoisPanel );
        sourceAgentsTree = new DynamicFilterTree(
                "sourceAgents",
                new RefPropertyModel( getComponent(), "sourceAgents" ),
                new RefQueryModel(
                        getScope(), new Query( "findAllAgents" ) ) ) {
            private static final long serialVersionUID = 4657138649455989795L;

            @Override
            public void onFilterSelect(
                    AjaxRequestTarget target, Filter filter ) {
                setProperty( "sourceAgents", sourceAgentsTree.getNewSelections() );
            }
        };
        addReplaceable( sourceAgentsTree );
        timeToLivePanel =
                new TimingPanel( "timeToLive", this, propPath + ".timeToLive" );
        addReplaceable( timeToLivePanel );
    }

    @Override
    public void elementChanged( String propPath, AjaxRequestTarget target ) {
        super.elementChanged( propPath, target );
        if ( propPath.endsWith( ".eventTypes" ) ) {
            target.addComponent( eoisPanel );
        }
    }
}

