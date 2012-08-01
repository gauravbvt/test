package com.mindalliance.channels.pages.components.plan.requirements;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.AddRequirement;
import com.mindalliance.channels.core.command.commands.RemoveRequirement;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanRequirementDefinitionsPanel.class );

    private Requirement selectedRequirement;

    private RequirementsTable requirementsTable;
    private Component requirementEditPanel;
    private ConfirmedAjaxFallbackLink removeButton;
    private static final int PAGE_SIZE = 6;
    /**
     * Filters on flow attributes that are identifiable.
     */
    private Map<String, Identifiable> identifiableFilters = new HashMap<String, Identifiable>();
    private AjaxLink<String> newButton;


    public PlanRequirementDefinitionsPanel(
            String id,
            Model<Requirement> requirementModel,
            Set<Long> expansions
    ) {
        super( id, requirementModel, expansions );
        init(  );
    }

    private void init(  ) {
        selectedRequirement = (Requirement)getModel().getObject();
        if ( selectedRequirement.isUnknown() ) selectedRequirement = null;
        addRequirementsTable();
        addRequirementEditPanel( );
        addButtons();
    }

    private void addRequirementsTable() {
        requirementsTable = new RequirementsTable(
                "requirements",
                new PropertyModel<List<RequirementWrapper>>( this, "requirementWrappers" ),
                PAGE_SIZE,
                this
        );
        addOrReplace( requirementsTable );
    }

    private void addRequirementEditPanel( ) {
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
                target.add( requirementsTable );
                update( target, change );
            }
        };
        // makeVisible( newButton, isLockedByUser( getPlan() ) );
        add( newButton );
        removeButton = new ConfirmedAjaxFallbackLink<String>( "remove", "Remove requirement?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = doCommand( new RemoveRequirement(
                        getUsername(),
                        selectedRequirement ) );
                addRequirementsTable();
                target.add( requirementsTable );
                updateComponents( target );
                update( target, change );
            }
        };
        // newButton.setEnabled( this.isLockedByUser( getPlan() ) );
        makeVisible( removeButton, selectedRequirement != null && isLockedByUser( selectedRequirement ) );
        add( removeButton );
    }

    private void updateComponents( AjaxRequestTarget target ) {
        addRequirementsTable();
        addRequirementEditPanel();
        target.add( requirementEditPanel );
        makeVisible( removeButton, selectedRequirement != null && isLockedByUser( selectedRequirement ) );
        // makeVisible( newButton, isLockedByUser( getPlan() ) );
        target.add( newButton );
        target.add( removeButton );
        target.add( requirementsTable );
        target.add( this );
    }

    @SuppressWarnings( "unchecked" )
    public List<RequirementWrapper> getRequirementWrappers() {
        List<RequirementWrapper> wrappers = new ArrayList<RequirementWrapper>(  );
        List<Requirement> requirements =  (List<Requirement>) CollectionUtils.select(
                getQueryService().list( Requirement.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !isFilteredOut( (Requirement) object );
                    }
                }
        );
        for ( Requirement requirement : requirements ) {
            wrappers.add( new RequirementWrapper( requirement ) );
        }
        return wrappers;
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
        target.add( requirementsTable );
    }

    @Override
    public boolean isFiltered( Identifiable identifiable, String property ) {
        ModelObject mo = (ModelObject) identifiableFilters.get( property );
        return mo != null && mo.equals( identifiable );
    }

    @Override
    public void changed( Change change ) {
        if ( change.isForInstanceOf( RequirementWrapper.class ) && change.isExpanded() ) {
            RequirementWrapper wrapper = (RequirementWrapper)change.getSubject( getQueryService() );
            Requirement requirement = wrapper.getRequirement();
            if ( selectedRequirement != null && requirement.equals( selectedRequirement )) {
                setSelectedRequirement( null );
            } else {
                if (!isLockedByOtherUser( requirement ))
                    setSelectedRequirement( requirement );
            }
        }
        else if ( change.isForInstanceOf( Requirement.class ) ) {
            Requirement requirement = (Requirement) change.getSubject( getQueryService() );
            if ( change.isAdded() ) {
                setSelectedRequirement( requirement );
            } else if ( change.isRemoved() ) {
                releaseAnyLockOn( selectedRequirement );
                selectedRequirement = null;
            }
        }
        super.changed( change );
    }

    private void setSelectedRequirement( Requirement requirement ) {
        unlockRequirement();
        selectedRequirement = requirement == null || requirement.isUnknown()
                ? null
                : requirement;
        lockRequirement();
    }

    private void unlockRequirement() {
        if ( selectedRequirement != null ) {
            releaseAnyLockOn( selectedRequirement );
        }
    }

    private void lockRequirement() {
        if ( selectedRequirement != null ) {
            requestLockOn( selectedRequirement );
        }
    }


    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( RequirementWrapper.class ) && change.isDisplay() ) {
            updateComponents( target );
        } else if ( change.isForInstanceOf( Requirement.class ) ) {
            if ( change.isAdded() ||  change.isRemoved() ) {
                updateComponents( target );
            } else if ( change.isUpdated() ) {
                target.add( requirementsTable );
            }
        } else if ( change.isExists() && change.isForInstanceOf( Issue.class ) ) {
            addRequirementsTable();
            target.add( requirementsTable );
        }
        super.updateWith( target, change, updated );
    }

    public class RequirementWrapper implements Identifiable {

        private Requirement requirement;

        public RequirementWrapper( Requirement requirement ) {
            this.requirement = requirement;
        }

        @Override
        public String getClassLabel() {
            return requirement.getClassLabel();
        }

        @Override
        public long getId() {
            return requirement.getId();
        }

        @Override
        public String getDescription() {
            return requirement.getDescription();
        }

        @Override
        public String getTypeName() {
            return requirement.getTypeName();
        }

        @Override
        public boolean isModifiableInProduction() {
            return false;
        }

        public Requirement getRequirement() {
            return requirement;
        }

        public String getName() {
            return requirement.getName();
        }

        public Requirement.AssignmentSpec getCommitterSpec() {
            return requirement.getCommitterSpec();
        }

        public Requirement.AssignmentSpec getBeneficiarySpec() {
            return requirement.getBeneficiarySpec();
        }

        public String getInformationAndEois() {
            return requirement.getInformationAndEois();
        }

        public String getInfoTagsAsString() {
            return requirement.getInfoTagsAsString();
        }

        public String getExpandLabel() {
            return selectedRequirement != null && selectedRequirement.equals( requirement )
                    ? "Close"
                    : isLockedByOtherUser( requirement )
                    ? ( getUserFullName( getLockOwner( requirement ) ) + " editing" )
                    : "Edit";
        }


    }

    /**
     * Requirements table.
     */
    private class RequirementsTable extends AbstractTablePanel<Requirement> {

        private final IModel<List<RequirementWrapper>> requirementsModel;
        private final Filterable filterable;

        private RequirementsTable(
                String id,
                IModel<List<RequirementWrapper>> requirementsModel,
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
            columns.add( makeAnalysisColumn( "Issues", "requirement", "unwaivedIssuesCount", "?" ) );
            columns.add( makeExpandLinkColumn( "", "", "@expandLabel" ) );
            List<RequirementWrapper> requirements = requirementsModel.getObject();
            add( new AjaxFallbackDefaultDataTable( "requirements",
                    columns,
                    new SortableBeanProvider<RequirementWrapper>( requirements, "name" ),
                    getPageSize() ) );
        }
    }


}
