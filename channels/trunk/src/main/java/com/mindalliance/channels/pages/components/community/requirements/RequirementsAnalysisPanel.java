package com.mindalliance.channels.pages.components.community.requirements;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.protocols.CommunityCommitment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.guide.Guidable;
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
public class RequirementsAnalysisPanel extends AbstractUpdatablePanel implements Filterable, Guidable {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( RequirementsAnalysisPanel.class );
    private static String ANY_EVENT = "Any event";
    private static final int PAGE_SIZE = 6;
    private static final String DOM_PREFIX_IDENTIFIER = ".req-network";

    private Phase.Timing selectedTiming;
    private Event selectedEvent;
    private Agency selectedAgency;
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


    public RequirementsAnalysisPanel( String id, Set<Long> expansions ) {
        this( id, null, expansions );
    }


    public RequirementsAnalysisPanel( String id, Model<Plan> planModel, Set<Long> expansions ) {
        super( id, planModel, expansions );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "scoping";  // todo move to community guide
    }

    @Override
    public String getHelpTopicId() {
        return "requirements"; // todo move to community guide
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
        refreshAppliedRequirements( target );
        addRequiredNetworkPanel();
        addRequiredCommitments();
        target.add( commitmentsContainer );
        target.add( requiredNetworkingPanel );
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
                selectedAgency,
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
                ? " any event"
                : ( " event " + ChannelsUtils.smartUncapitalize( selectedEvent.getName() ) ) );
        if ( selectedRequirementRel != null ) {
            sb.append( " by " );
            sb.append( selectedRequirementRel.getFromAgency( getCommunityService() ).getName() );
            sb.append( " with " );
            sb.append( selectedRequirementRel.getToAgency( getCommunityService() ).getName() );
        } else if ( selectedAgency != null ) {
            sb.append( " involving " );
            sb.append( selectedAgency.getName() );
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
        target.add( appliedRequirementsLabel );
        target.add( appliedRequirementsTable );
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
                        Requirement requirement = (Requirement)object;
                        return !isFilteredOut( requirement );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<Requirement> getSelectedAppliedRequirements() {
        CommunityService communityService = getCommunityService();
        List<RequirementRelationship> reqRels = new ArrayList<RequirementRelationship>();
        if ( selectedRequirementRel != null ) {
            reqRels.add( selectedRequirementRel );
        } else {
            reqRels.addAll( (List<RequirementRelationship>) CollectionUtils.select(
                    communityService.getCollaborationPlanAnalyst().findRequirementRelationships(
                            selectedTiming,
                            selectedEvent,
                            communityService ),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            RequirementRelationship reqRel = (RequirementRelationship) object;
                            return selectedAgency == null
                                    || reqRel.getToAgency( getCommunityService() ).equals( selectedAgency )
                                    || reqRel.getFromAgency( getCommunityService() ).equals( selectedAgency );
                        }
                    } ) );
        }
        List<Requirement> appliedRequirements = new ArrayList<Requirement>();
        Place planLocale = getPlanLocale();
        for ( RequirementRelationship reqRel : reqRels ) {
            for ( Requirement req : reqRel.getRequirements() ) {
                Requirement appliedReq = req.transientCopy();
                appliedReq.setSituationIfAppropriate( selectedTiming, selectedEvent, planLocale );
                appliedReq.initialize( getCommunityService() );
                appliedRequirements.add( appliedReq );
            }
        }
        return appliedRequirements;
    }

    private void addRequiredCommitments() {
        commitmentsContainer = new WebMarkupContainer( "commitmentsContainer" );
        commitmentsContainer.setOutputMarkupId( true );
        makeVisible( commitmentsContainer, selectedAppliedRequirement != null );
        addOrReplace( commitmentsContainer );
        CommunityCommitmentsTablePanel commitmentsTablePanel = new CommunityCommitmentsTablePanel(
                "commitments",
                new PropertyModel<List<CommunityCommitment>>( this, "commitments" )
        );
        commitmentsContainer.add( commitmentsTablePanel );
    }

    public List<CommunityCommitment> getCommitments() {
        if ( selectedAppliedRequirement != null ) {
            return getCommunityService().getAllCommitments( false )
                    .inSituation( selectedTiming, selectedEvent, getPlanLocale() )
                    .satisfying( selectedAppliedRequirement, getCommunityService() ).toList();
        } else {
            return new ArrayList<CommunityCommitment>();
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

    private Requirement makeAppliedRequirement( Requirement requirement, Map<String, Serializable> qualifiers ) {
        try {
            ParticipationManager participationManager = getCommunityService().getParticipationManager();
            Requirement appliedReq = requirement.transientCopy();
            if ( qualifiers.containsKey( "committerAgency.uid" ) ) {
                String id = (String) qualifiers.get( "committerAgency.uid" );
                Agency committerOrg = participationManager.findAgencyById( id, getCommunityService() );
                appliedReq.setCommitterAgency( committerOrg );
            }
            if ( qualifiers.containsKey( "beneficiaryAgency.uid" ) ) {
                String id = (String) qualifiers.get( "beneficiaryAgency.uid" );
                Agency beneficiaryOrg = participationManager.findAgencyById( id, getCommunityService() );
                appliedReq.setBeneficiaryAgency( beneficiaryOrg );
            }
            if ( qualifiers.containsKey( "timing" ) ) {
                Phase.Timing timing = (Phase.Timing) qualifiers.get( "timing" );
                appliedReq.setSituationIfAppropriate( timing, null, getPlanLocale() );
            }
            if ( qualifiers.containsKey( "event" ) ) {
                Long id = (Long) qualifiers.get( "event" );
                Event event = getQueryService().find( Event.class, id );
                appliedReq.setSituationIfAppropriate( null, event, getPlanLocale() );
            }
            appliedReq.initialize( getCommunityService() );
            return appliedReq;
        } catch ( NotFoundException e ) {
            LOG.warn( "Organization to which requirement is applied was not found" );
            return null;
        }
    }


    @Override
    public void changed( Change change ) {
        if ( change.isSelected() ) {  // in diagram
            if ( change.isForInstanceOf( Agency.class ) ) {
                selectedAgency = (Agency) change.getSubject( getCommunityService() );
                selectedRequirementRel = null;
                selectedAppliedRequirement = null;
            } else if ( change.isForInstanceOf( PlanCommunity.class ) ) {
                selectedRequirementRel = null;
                selectedAgency = null;
                selectedAppliedRequirement = null;
            } else if ( change.isForInstanceOf( RequirementRelationship.class ) ) {
                selectedRequirementRel = (RequirementRelationship) change.getSubject( getCommunityService() );
                selectedAgency = null;
                selectedAppliedRequirement = null;
            } else {
                super.changed( change );
            }
        } else if ( change.isExpanded() ) {  // from table of requirements
            if ( change.isForInstanceOf( Requirement.class ) ) {
                Requirement req = (Requirement) change.getSubject( getCommunityService() );
                selectedAppliedRequirement = selectedAppliedRequirement != null && selectedAppliedRequirement.equals( req )
                        ? null
                        : req;
            }
        } else {
            super.changed( change );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected() ) {
            if ( change.isForInstanceOf( Agency.class )
                    || change.isForInstanceOf( RequirementRelationship.class )
                    || change.isForInstanceOf( PlanCommunity.class ) ) {
                addRequiredNetworkPanel();
                target.add( requiredNetworkingPanel );
                addRequiredCommitments();
                target.add( commitmentsContainer );
                refreshAppliedRequirements( target );
            } else {
                super.updateWith( target, change, updated );
            }
        } else if ( change.isExpanded() && change.isForInstanceOf( Requirement.class ) ) {
            addRequiredCommitments();
            target.add( commitmentsContainer );
        } else {
            super.updateWith( target, change, updated );
        }
    }

    private void refreshAppliedRequirements( AjaxRequestTarget target ) {
        addAppliedRequirements();
        target.add( appliedRequirementsLabel );
        target.add( appliedRequirementsTable );
        addRequiredCommitments();
        target.add( commitmentsContainer );
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
            columns.add( makeFilterableColumn(
                    "By organization",
                    "committerSpec.agency",
                    "committerSpec.agency.name",
                    EMPTY,
                    "committerSpec.agency.description",
                    filterable ) );
            columns.add( makeFilterableColumn(
                    "With organization",
                    "beneficiarySpec.agency",
                    "beneficiarySpec.agency.name",
                    EMPTY,
                    "beneficiarySpec.agency.description",
                    filterable ) );
            columns.add( makeFilterableColumn(
                    "In event",
                    "beneficiarySpec.event",
                    "beneficiarySpec.event.name",
                    EMPTY,
                    "beneficiarySpec.event.description",
                    filterable ) );
            columns.add( makeParticipationAnalystColumn(
                    "Satisfied?",
                    null,
                    "requirementSatisfaction",
                    "?",
                    "satisfactionSummary",
                    selectedTiming,
                    selectedEvent ) );
            columns.add( makeParticipationAnalystColumn(
                    "By commitments",
                    null,
                    "requiredCommitmentsCount",
                    "?",
                    null,
                    selectedTiming,
                    selectedEvent ) );
            columns.add( makeExpandLinkColumn(
                    "",
                    "",
                    "view",
                    "committerAgency.uid",
                    "beneficiaryAgency.uid",
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
