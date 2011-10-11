package com.mindalliance.channels.pages.components.plan.requirements;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.AddRequirement;
import com.mindalliance.channels.core.command.commands.RemoveRequirement;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.pages.components.Filterable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Plan requirements management panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/29/11
 * Time: 2:13 PM
 */
public class PlanRequirementDefinitionsPanel extends AbstractCommandablePanel implements Filterable {

    private Requirement selectedRequirement;

    private RequirementsTable requirementsTable;
    private Component requirementEditPanel;
    private ConfirmedAjaxFallbackLink removeButton;
    private static final int PAGE_SIZE = 10;
    /**
     * Filters on flow attributes that are identifiable.
     */
    private Map<String, Identifiable> identifiableFilters = new HashMap<String, Identifiable>();
    private AjaxLink<String> newButton;


    public PlanRequirementDefinitionsPanel( String id, Model<Plan> planModel, Set<Long> expansions ) {
        super( id, planModel, expansions );
        init();
    }

    private void init() {
        addRequirementsTable();
        addRequirementEditPanel();
        makeVisible( requirementEditPanel, false );
        addButtons();
    }

    private void addRequirementsTable() {
        requirementsTable = new RequirementsTable(
                "requirements",
                new PropertyModel<List<Requirement>>( this, "requirements" ),
                PAGE_SIZE,
                this
        );
        addOrReplace( requirementsTable );
    }

    private void addRequirementEditPanel() {
        if ( selectedRequirement != null ) {
            requirementEditPanel = new RequirementEditPanel(
                    "requirement",
                    new Model<Requirement>( selectedRequirement ),
                    getExpansions() );
        } else {
            requirementEditPanel = new Label( "requirement", "" );
            requirementEditPanel.setOutputMarkupId( true );
        }
        makeVisible( requirementEditPanel, selectedRequirement != null );
        addOrReplace( requirementEditPanel );
    }

    private void addButtons() {
        newButton = new AjaxLink<String>( "new", new Model<String>( "New" ) ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = doCommand( new AddRequirement( getUsername() ) );
                selectedRequirement = (Requirement) change.getSubject( getQueryService() );
                updateComponents( target );
                addRequirementsTable();
                target.addComponent( requirementsTable );
                update( target, change );
            }
        };
        makeVisible( newButton, isLockedByUser( getPlan() ) );
        add( newButton );
        removeButton = new ConfirmedAjaxFallbackLink<String>( "remove", "Remove requirement?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = doCommand( new RemoveRequirement(
                        getUsername(),
                        selectedRequirement ) );
                selectedRequirement = null;
                addRequirementsTable();
                target.addComponent( requirementsTable );
                updateComponents( target );
                update( target, change );
            }
        };
        newButton.setEnabled( this.isLockedByUser( getPlan() ) );
        makeVisible( removeButton, false );
        add( removeButton );
    }

    private void updateComponents( AjaxRequestTarget target ) {
        addRequirementEditPanel();
        target.addComponent( requirementEditPanel );
        makeVisible( removeButton, selectedRequirement != null );
        makeVisible( newButton, isLockedByUser( getPlan() ) );
        target.addComponent( newButton );
        target.addComponent( removeButton );
        target.addComponent( requirementsTable );
        target.addComponent( this );
    }

    @SuppressWarnings( "unchecked" )
    public List<Requirement> getRequirements() {
        return (List<Requirement>) CollectionUtils.select(
                getQueryService().list( Requirement.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !isFilteredOut( (Requirement) object );
                    }
                }
        );
    }

    private boolean isFilteredOut( Requirement requirement ) {
        for ( String property : identifiableFilters.keySet() ) {
            if ( !ModelObject.areEqualOrNull( (ModelObject) identifiableFilters.get( property ),
                    (ModelObject) ChannelsUtils.getProperty( requirement, property, null ) ) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        assert property != null;
        if ( identifiable == null || isFiltered( identifiable, property ) ) {
            identifiableFilters.remove( property );
        } else {
            identifiableFilters.put( property, identifiable );
        }
        addRequirementsTable();
        target.addComponent( requirementsTable );
    }

    @Override
    public boolean isFiltered( Identifiable identifiable, String property ) {
        ModelObject mo = (ModelObject) identifiableFilters.get( property );
        return mo != null && mo.equals( identifiable );
    }

    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( Requirement.class ) ) {
            Requirement requirement = (Requirement) change.getSubject( getQueryService() );
            if ( change.isAdded() || change.isExpanded() ) {
                selectedRequirement = requirement;
                updateComponents( target );
            } else if ( change.isRemoved() ) {
                selectedRequirement = null;
                updateComponents( target );
            } else if ( change.isUpdated() ) {
                target.addComponent( requirementsTable );
            }
        } else if ( change.isExists() && change.isForInstanceOf( Issue.class ) ) {
            addRequirementsTable();
            target.addComponent( requirementsTable );
        }
        super.updateWith( target, change, updated );
    }

    /**
     * Requirements table.
     */
    private class RequirementsTable extends AbstractTablePanel<Requirement> {

        private final IModel<List<Requirement>> requirementsModel;
        private final Filterable filterable;

        private RequirementsTable(
                String id,
                IModel<List<Requirement>> requirementsModel,
                int pageSize,
                Filterable filterable ) {
            super( id, pageSize );
            this.requirementsModel = requirementsModel;
            this.filterable = filterable;
            init();
        }

        @SuppressWarnings( "unchecked" )
        private void init() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            columns.add( makeColumn( "Name", "name", EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "Organization(s)",
                    "committerSpec.organization",
                    "committerSpec.organization.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeColumn( "Shall share info", "informationAndEois", EMPTY ) );
            columns.add( makeColumn( "Tagged", "infoTagsAsString", EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "With organization(s)",
                    "beneficiarySpec.organization",
                    "beneficiarySpec.organization.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeFilterableLinkColumn(
                    "In event",
                    "beneficiarySpec.event",
                    "beneficiarySpec.event.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeAnalysisColumn( "Issues", "unwaivedIssuesCount", "?" ) );
            columns.add( makeExpandLinkColumn( "", "", "edit" ) );
            List<Requirement> requirements = requirementsModel.getObject();
            add( new AjaxFallbackDefaultDataTable( "requirements",
                    columns,
                    new SortableBeanProvider<Requirement>( requirements, "name" ),
                    getPageSize() ) );
        }
    }


}
