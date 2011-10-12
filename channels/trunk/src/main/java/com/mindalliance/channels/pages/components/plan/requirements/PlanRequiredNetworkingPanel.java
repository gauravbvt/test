package com.mindalliance.channels.pages.components.plan.requirements;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.segment.CommitmentsTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 9/29/11
 * Time: 2:15 PM
 */
public class PlanRequiredNetworkingPanel extends AbstractUpdatablePanel implements Filterable {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanRequiredNetworkingPanel.class );
    private static String ANY_EVENT = "Any event";
    private static final int PAGE_SIZE = 6;
    private static final String DOM_PREFIX_IDENTIFIER = ".req-network";

    private Phase.Timing selectedTiming;
    private Event selectedEvent;
    private Organization selectedOrganization;
    private RequirementRelationship selectedRequirementRel;
    private Requirement selectedAppliedRequirement;
    /**
     * Filters on flow attributes that are identifiable.
     */
    private Map<String, Identifiable> identifiableFilters = new HashMap<String, Identifiable>();
    private Label appliedRequirementsLabel;
    private AppliedRequirementsTable appliedRequirementsTable;
    private WebMarkupContainer commitmentsContainer;
    private RequiredNetworkingPanel requiredNetworkingPanel;


    public PlanRequiredNetworkingPanel( String id, Model<Plan> planModel, Set<Long> expansions ) {
        super( id, planModel, expansions );
        init();
    }

    private void init() {
        addTimingChoice();
        addEventChoice();
        addRequiredNetworkPanel();
        addAppliedRequirements();
        addRequiredCommitments();
    }

    private void addTimingChoice() {
        DropDownChoice<String> timingChoices = new DropDownChoice<String>(
                "timing",
                new PropertyModel<String>( this, "timingName" ),
                getTimingChoices()
        );
        timingChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                refreshAll( target );
            }
        } );
        add( timingChoices );
    }

    private void refreshAll( AjaxRequestTarget target ) {
        selectedAppliedRequirement = null;
        selectedOrganization = null;
        selectedRequirementRel = null;
        refreshAppliedRequirements( target );
        addRequiredNetworkPanel();
        addRequiredCommitments();
        target.addComponent( commitmentsContainer );
        target.addComponent( requiredNetworkingPanel );
    }

    private List<String> getTimingChoices() {
        List<String> timingChoices = new ArrayList<String>();
        timingChoices.add( Phase.Timing.ANY_TIME );
        for ( Phase.Timing timing : Phase.Timing.values() ) {
            timingChoices.add( Phase.Timing.translateTiming( timing ) );
        }
        return timingChoices;
    }


    private void addEventChoice() {
        DropDownChoice<String> eventChoices = new DropDownChoice<String>(
                "event",
                new PropertyModel<String>( this, "eventName" ),
                getEventChoices()
        );
        eventChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                refreshAll( target );
            }
        } );
        add( eventChoices );
    }

    private List<String> getEventChoices() {
        List<Event> events = new ArrayList<Event>();
        for ( Event event : getQueryService().list( Event.class ) ) {
            if ( !event.isUnknown() ) events.add( event );
        }
        Collections.sort(
                events,
                new Comparator<Event>() {
                    @Override
                    public int compare( Event e1, Event e2 ) {
                        return e1.getName().toLowerCase().compareTo( e2.getName().toLowerCase() );
                    }
                } );
        List<String> eventChoices = new ArrayList<String>();
        eventChoices.add( ANY_EVENT );
        for ( Event event : events ) {
            eventChoices.add( event.getName() );
        }
        return eventChoices;
    }

    private void addRequiredNetworkPanel() {
        requiredNetworkingPanel = new RequiredNetworkingPanel(
                "reqNetwork",
                new Model<Phase.Timing>( selectedTiming ),
                new Model<Event>( selectedEvent ),
                selectedOrganization,
                selectedRequirementRel,
                null,
                DOM_PREFIX_IDENTIFIER
        );
        requiredNetworkingPanel.setOutputMarkupId( true );
        addOrReplace( requiredNetworkingPanel );
    }

    private void addAppliedRequirements() {
        appliedRequirementsLabel = new Label(
                "appliedReqsTitle",
                new PropertyModel<String>( this, "networkingRequirementsTitle" ) );
        appliedRequirementsLabel.setOutputMarkupId( true );
        addOrReplace( appliedRequirementsLabel );
        appliedRequirementsTable = new AppliedRequirementsTable(
                "appliedReqs",
                new PropertyModel<List<Requirement>>( this, "appliedRequirements" ),
                PAGE_SIZE,
                this );
        addOrReplace( appliedRequirementsTable );
    }

    public String getNetworkingRequirementsTitle() {
        StringBuilder sb = new StringBuilder();
        sb.append( "All requirements to share information " );
        sb.append( Phase.Timing.translateTiming( selectedTiming ).toLowerCase() );
        sb.append( selectedEvent == null
                ? " in any event"
                : ( " in event " + ChannelsUtils.smartUncapitalize( selectedEvent.getName() ) ) );
        if ( selectedRequirementRel != null ) {
            sb.append( " by " );
            sb.append( selectedRequirementRel.getFromIdentifiable( getQueryService() ).getName() );
            sb.append( " with " );
            sb.append( selectedRequirementRel.getToIdentifiable( getQueryService() ).getName() );
        } else if ( selectedOrganization != null ) {
            sb.append( " involving " );
            sb.append( selectedOrganization.getName() );
        }
        sb.append( '.' );
        return sb.toString();
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
        addAppliedRequirements();
        target.addComponent( appliedRequirementsLabel );
        target.addComponent( appliedRequirementsTable );
    }

    @Override
    public boolean isFiltered( Identifiable identifiable, String property ) {
        ModelObject mo = (ModelObject) identifiableFilters.get( property );
        return mo != null && mo.equals( identifiable );
    }

    @SuppressWarnings( "unchecked" )
    public List<Requirement> getAppliedRequirements() {
        return (List<Requirement>) CollectionUtils.select(
                getSelectedAppliedRequirements(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !isFilteredOut( (Requirement) object );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<Requirement> getSelectedAppliedRequirements() {
        List<RequirementRelationship> reqRels = new ArrayList<RequirementRelationship>();
        if ( selectedRequirementRel != null ) {
            reqRels.add( selectedRequirementRel );
        } else {
            reqRels.addAll( (List<RequirementRelationship>) CollectionUtils.select(
                    getAnalyst().findRequirementRelationships( selectedTiming, selectedEvent, getQueryService() ),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            RequirementRelationship reqRel = (RequirementRelationship) object;
                            return selectedOrganization == null
                                    || reqRel.getToIdentifiable() == selectedOrganization.getId()
                                    || reqRel.getFromIdentifiable() == selectedOrganization.getId();
                        }
                    } ) );
        }
        List<Requirement> applierRequirements = new ArrayList<Requirement>();
        QueryService queryService = getQueryService();
        Place planLocale = queryService.getPlan().getLocale();
        for ( RequirementRelationship reqRel : reqRels ) {
            for ( Requirement req : reqRel.getRequirements() ) {
                Requirement appliedReq = req.transientCopy();
                appliedReq.setCommitterOrganization( (Organization) reqRel.getFromIdentifiable( queryService ) );
                appliedReq.setBeneficiaryOrganization( (Organization) reqRel.getToIdentifiable( queryService ) );
                appliedReq.setSituationIfAppropriate( selectedTiming, selectedEvent, planLocale );
                applierRequirements.add( appliedReq );
            }
        }
        return applierRequirements;
    }

    private void addRequiredCommitments() {
        commitmentsContainer = new WebMarkupContainer( "commitmentsContainer" );
        commitmentsContainer.setOutputMarkupId( true );
        makeVisible( commitmentsContainer, selectedAppliedRequirement != null );
        addOrReplace( commitmentsContainer );
        CommitmentsTablePanel commitmentsTablePanel = new CommitmentsTablePanel(
                "commitments",
                new PropertyModel<List<Commitment>>( this, "commitments" )
        );
        commitmentsContainer.add( commitmentsTablePanel );
    }

    public List<Commitment> getCommitments() {
        if ( selectedAppliedRequirement != null ) {
            return getQueryService().getAllCommitments()
                    .inSituation( selectedTiming, selectedEvent, getQueryService().getPlan().getLocale() )
                    .satisfying( selectedAppliedRequirement ).toList();
        } else {
            return new ArrayList<Commitment>();
        }
    }

    /**
     * Get phase event timing name.
     *
     * @return a string
     */
    public String getTimingName() {
        return Phase.Timing.translateTiming( selectedTiming );
    }

    /**
     * Set phase event timing.
     *
     * @param name an event timing name
     */
    public void setTimingName( String name ) {
        selectedTiming = Phase.Timing.translateTiming( name );
    }

    public String getEventName() {
        return selectedEvent == null ? ANY_EVENT : selectedEvent.getName();
    }

    public void setEventName( String name ) {
        if ( name == null || name.trim().isEmpty() || name.equals( ANY_EVENT ) )
            selectedEvent = null;
        else {
            selectedEvent = getQueryService().findEntityType( Event.class, name );
        }
    }

    @Override
    public void changed( Change change ) {
        if ( change.isSelected() ) {
            if ( change.isForInstanceOf( Organization.class ) ) {
                selectedOrganization = (Organization) change.getSubject( getQueryService() );
                selectedRequirementRel = null;
                selectedAppliedRequirement = null;
            } else if ( change.isForInstanceOf( RequirementRelationship.class ) ) {
                selectedRequirementRel = (RequirementRelationship) change.getSubject( getQueryService() );
                selectedOrganization = null;
                selectedAppliedRequirement = null;
            } else if ( change.isForInstanceOf( Plan.class ) ) {
                selectedRequirementRel = null;
                selectedOrganization = null;
                selectedAppliedRequirement = null;
            } else {
                super.changed( change );
            }
        } else if ( change.isExpanded() && change.isForInstanceOf( Requirement.class ) ) {
            selectedAppliedRequirement = makeAppliedRequirement(
                    (Requirement) change.getSubject( getQueryService() ),
                    change.getQualifiers() );
        } else {
            super.changed( change );
        }
    }

    private Requirement makeAppliedRequirement( Requirement requirement, Map<String, Serializable> qualifiers ) {
        try {
            Requirement appliedReq = requirement.transientCopy();
            if ( qualifiers.containsKey( "committerOrganization.id" ) ) {
                Long id = (Long) qualifiers.get( "committerOrganization.id" );
                Organization committerOrg = getQueryService().find( Organization.class, id );
                appliedReq.setCommitterOrganization( committerOrg );
            }
            if ( qualifiers.containsKey( "beneficiaryOrganization.id" ) ) {
                Long id = (Long) qualifiers.get( "beneficiaryOrganization.id" );
                Organization beneficiaryOrg = getQueryService().find( Organization.class, id );
                appliedReq.setBeneficiaryOrganization( beneficiaryOrg );
            }
            if ( qualifiers.containsKey( "timing" ) ) {
                Phase.Timing timing = (Phase.Timing) qualifiers.get( "timing" );
                appliedReq.setSituationIfAppropriate( timing, null, getQueryService().getPlan().getLocale() );
            }
            if ( qualifiers.containsKey( "event" ) ) {
                Long id = (Long) qualifiers.get( "event" );
                Event event = getQueryService().find( Event.class, id );
                appliedReq.setSituationIfAppropriate( null, event, getQueryService().getPlan().getLocale() );
            }
            return appliedReq;
        } catch ( NotFoundException e ) {
            LOG.warn( "Organization to which requirement is applied was not found" );
            return null;
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected() ) {
            if ( change.isForInstanceOf( Organization.class )
                    || change.isForInstanceOf( RequirementRelationship.class )
                    || change.isForInstanceOf( Plan.class ) ) {
                addRequiredNetworkPanel();
                target.addComponent( requiredNetworkingPanel );
                refreshAppliedRequirements( target );
            } else {
                super.updateWith( target, change, updated );
            }
        } else if ( change.isExpanded() && change.isForInstanceOf( Requirement.class ) ) {
            addRequiredCommitments();
            target.addComponent( commitmentsContainer );
        } else {
            super.updateWith( target, change, updated );
        }
    }

    private void refreshAppliedRequirements( AjaxRequestTarget target ) {
        addAppliedRequirements();
        target.addComponent( appliedRequirementsLabel );
        target.addComponent( appliedRequirementsTable );
        addRequiredCommitments();
        target.addComponent( commitmentsContainer );
    }

    private class AppliedRequirementsTable extends AbstractTablePanel<Requirement> {

        private final IModel<List<Requirement>> appliedRequirementsModel;
        private final Filterable filterable;

        public AppliedRequirementsTable( String id,
                                         IModel<List<Requirement>> appliedRequirementsModel,
                                         int pageSize,
                                         Filterable filterable ) {
            super( id, pageSize );
            this.appliedRequirementsModel = appliedRequirementsModel;
            this.filterable = filterable;
            init();
        }

        @SuppressWarnings( "unchecked" )
        private void init() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            columns.add( makeColumn( "Requirement", "name", EMPTY ) );
            columns.add( makeColumn( "to share info", "informationAndEois", EMPTY ) );
            columns.add( makeColumn( "Tagged", "infoTagsAsString", EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "By organization",
                    "committerSpec.organization",
                    "committerSpec.organization.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeAnalysisColumn(
                    "Satisfied?",
                    "committerSatisfaction",
                    "?",
                    selectedTiming,
                    selectedEvent ) );
            columns.add( makeFilterableLinkColumn(
                    "With organization",
                    "beneficiarySpec.organization",
                    "beneficiarySpec.organization.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeAnalysisColumn(
                    "Satisfied?",
                    "beneficiarySatisfaction",
                    "?",
                    selectedTiming,
                    selectedEvent ) );
            columns.add( makeFilterableLinkColumn(
                    "In event",
                    "beneficiarySpec.event",
                    "beneficiarySpec.event.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeAnalysisColumn(
                    "Commitments",
                    "commitmentsCount",
                    "?",
                    selectedTiming,
                    selectedEvent ) );
            columns.add( makeExpandLinkColumn(
                    "",
                    "",
                    "view",
                    "committerOrganization.id",
                    "beneficiaryOrganization.id",
                    "beneficiarySpec.timing",
                    "beneficiarySpec.event" ) );
            List<Requirement> requirements = appliedRequirementsModel.getObject();
            add( new AjaxFallbackDefaultDataTable( "requirements",
                    columns,
                    new SortableBeanProvider<Requirement>( requirements, "name" ),
                    getPageSize() ) );
        }
    }
}
