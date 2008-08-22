package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.definition.EventSpecification;
import com.mindalliance.channels.playbook.ifm.definition.InformationDefinition;
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification;
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation;
import com.mindalliance.channels.playbook.ifm.taxonomy.EventType;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary
 * and Confidential.
 * <p/>
 * User: jf Date: Jul 1, 2008 Time: 12:55:35 PM
 */
public class InformationDefinitionPanel extends AbstractDefinitionPanel {

    private InformationDefinition informationDefinition;
    private AjaxCheckBox anyEventCheckBox;
    private WebMarkupContainer eventSpecificationDiv;
    private EventSpecificationPanel eventSpecificationPanel;
    private AjaxCheckBox anyEventTypeCheckBox;
    private WebMarkupContainer eventTypesDiv;
    private DynamicFilterTree eventTypesTree;
    private AjaxCheckBox anySourceCheckBox;
    private WebMarkupContainer sourceSpecificationDiv;
    private Component sourceSpecificationPanel;
    private AjaxCheckBox anyEoiCheckBox;
    private WebMarkupContainer eoisDiv;
    private EOIsPanel eoisPanel;
    private static final long serialVersionUID = 7428071321128173540L;

    public InformationDefinitionPanel(
            String id, AbstractPlaybookPanel parentPanel, String propPath ) {
        super( id, parentPanel, propPath );
    }

    @Override
    protected void load() {
        super.load();
        informationDefinition = (InformationDefinition) getComponent();
        anyEventCheckBox = new AjaxCheckBox(
                "anyEvent",
                new Model<Boolean>( informationDefinition.getEventSpec().matchesAll() ) ) {
            private static final long serialVersionUID = 4409747005699355908L;

            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                boolean anyEvent = anyEventCheckBox.getModelObject();
                if ( anyEvent ) {
                    setProperty(
                            "eventSpecification", new EventSpecification() );
                    eventSpecificationPanel = new EventSpecificationPanel(
                            "eventSpecification",
                            InformationDefinitionPanel.this,
                            propPath + ".eventSpec" );
                    addReplaceableTo(
                            eventSpecificationPanel, eventSpecificationDiv );
                }
                setVisibility( eventSpecificationDiv, !anyEvent, target );
            }
        };
        addReplaceable( anyEventCheckBox );
        eventSpecificationDiv =
                new WebMarkupContainer( "eventSpecificationDiv" );
        setVisibility(
                eventSpecificationDiv,
                !informationDefinition.getEventSpec().matchesAll() );
        addReplaceable( eventSpecificationDiv );
        eventSpecificationPanel = new EventSpecificationPanel(
                "eventSpecification", this, propPath + ".eventSpec" );
        addReplaceableTo( eventSpecificationPanel, eventSpecificationDiv );

        anyEventTypeCheckBox = new AjaxCheckBox(
                "anyEventType",
                new Model<Boolean>( informationDefinition.getEventTypes().isEmpty() ) ) {
            private static final long serialVersionUID = -2968279914542624514L;

            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                boolean anyEventType = anyEventTypeCheckBox.getModelObject();
                if ( anyEventType ) {
                    setProperty( "eventTypes", new ArrayList<Ref>() );
                }
                setVisibility( eventTypesDiv, !anyEventType, target );
            }
        };
        addReplaceable( anyEventTypeCheckBox );
        eventTypesDiv = new WebMarkupContainer( "eventTypesDiv" );
        setVisibility(
                eventTypesDiv,
                !informationDefinition.getEventTypes().isEmpty() );
        addReplaceable( eventTypesDiv );
        eventTypesTree = new DynamicFilterTree(
                "eventTypes",
                new RefPropertyModel( getElement(), propPath + ".eventTypes" ),
                new RefQueryModel(
                        getProject(),
                        new Query( "findAllTypes", "EventType" ) ) ) {
            private static final long serialVersionUID = -7979507318863845154L;

            @Override
            public void onFilterSelect(
                    AjaxRequestTarget target, Filter filter ) {
                setProperty(
                        "eventTypes",
                        eventTypesTree.getNewSelections(),
                        target );
            }
        };
        addReplaceableTo( eventTypesTree, eventTypesDiv );

        anySourceCheckBox = new AjaxCheckBox(
                "anySource",
                new Model<Boolean>( informationDefinition.getSourceAgentSpec().matchesAll() ) ) {
            private static final long serialVersionUID = -35681272053545972L;

            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                boolean anySource = anySourceCheckBox.getModelObject();
                if ( anySource ) {
                    setProperty( "sourceAgentSpec", new AgentSpecification() );
                    sourceSpecificationPanel = new AgentSpecificationPanel(
                            "sourceSpecification",
                            InformationDefinitionPanel.this,
                            propPath + ".sourceAgentSpec" );
                    addReplaceableTo(
                            sourceSpecificationPanel, sourceSpecificationDiv );
                }
                setVisibility( sourceSpecificationDiv, !anySource, target );
            }
        };
        addReplaceable( anySourceCheckBox );
        sourceSpecificationDiv =
                new WebMarkupContainer( "sourceSpecificationDiv" );
        setVisibility(
                sourceSpecificationDiv,
                !informationDefinition.getSourceAgentSpec().matchesAll() );
        addReplaceable( sourceSpecificationDiv );
        sourceSpecificationPanel = new AgentSpecificationPanel(
                "sourceSpecification", this, propPath + ".sourceAgentSpec" );
        addReplaceableTo( sourceSpecificationPanel, sourceSpecificationDiv );

        anyEoiCheckBox = new AjaxCheckBox(
                "anyEoi",
                new Model<Boolean>( informationDefinition.getElementsOfInformation().isEmpty() ) ) {
            private static final long serialVersionUID = 1666552398114804547L;

            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                boolean anyEoi = anyEoiCheckBox.getModelObject();
                if ( anyEoi ) {
                    setProperty(
                            "elementsOfInformation",
                            new ArrayList<ElementOfInformation>() );
                    eoisPanel = new EOIsPanel(
                            "eois",
                            InformationDefinitionPanel.this,
                            propPath + ".elementsOfInformation",
                            getTopicChoicesModel() );
                    addReplaceableTo( eoisPanel, eoisDiv );
                }
                setVisibility( eoisDiv, !anyEoi, target );
            }
        };
        addReplaceable( anyEoiCheckBox );
        eoisDiv = new WebMarkupContainer( "eoisDiv" );
        setVisibility(
                eoisDiv,
                !informationDefinition.getElementsOfInformation().isEmpty() );
        addReplaceable( eoisDiv );
        eoisPanel = new EOIsPanel(
                "eois",
                this,
                propPath + ".elementsOfInformation",
                getTopicChoicesModel() );
        addReplaceableTo( eoisPanel, eoisDiv );
    }

    private IModel getTopicChoicesModel() {
        return new RefQueryModel( this, new Query( "findAllKnownTopics" ) );
    }

    @SuppressWarnings( { "unchecked" } )
    private List<String> findAllKnownTopics() {
        return (List<String>) Query.execute(EventType.class, "findAllTopicsIn", informationDefinition.getEventTypes());
    }

    @Override
    public void elementChanged( String propPath, AjaxRequestTarget target ) {
        super.elementChanged( propPath, target );
        if ( propPath.matches(
                ".*\\.informationSpec\\.eventSpec\\.definitions\\[\\d+\\]\\.description" ) ) {
            target.addComponent( eoisPanel );
        }
    }
}
