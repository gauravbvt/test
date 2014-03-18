/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Delay;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Taggable;
import com.mindalliance.channels.core.model.asset.AssetConnectable;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.ModelPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.DelayPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.pages.components.TagsPanel;
import com.mindalliance.channels.pages.components.entities.EntityReferencePanel;
import com.mindalliance.channels.pages.components.plan.floating.ModelSearchingFloatingPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A view on a Part.
 */
public class ExpandedPartPanel extends AbstractCommandablePanel {

    /**
     * The task property.
     */
    private static final String TASK_PROPERTY = "task";                     // NON-NLS

    /**
     * The function property.
     */
    private static final String FUNCTION_PROPERTY = "function";                   // NON-NLS
    /**
     * The actor property.
     */
    private static final String ACTOR_PROPERTY = "actor";                   // NON-NLS

    /**
     * The role property.
     */
    private static final String ROLE_PROPERTY = "role";                     // NON-NLS

    /**
     * The organization property.
     */
    private static final String ORG_PROPERTY = "organization";              // NON-NLS

    /**
     * The jurisdiction property.
     */
    private static final String JURISDICTION_PROPERTY = "jurisdiction";     // NON-NLS

    /**
     * The initiated event property.
     */
    private static final String INITIATED_EVENT_PROPERTY = "initiatedEvent";             // NON-NLS

    private static String[] EntityProps = {
            ACTOR_PROPERTY, FUNCTION_PROPERTY, ROLE_PROPERTY, JURISDICTION_PROPERTY, ORG_PROPERTY,
            INITIATED_EVENT_PROPERTY
    };

    /**
     * The empty string.
     */
    private static final String EMPTY_STRING = "";

    /**
     * Null category
     */
    private static final String NO_CATEGORY = "Unspecified";

    /**
     * String comparator for equality tests.
     */
    private static final Collator COMPARATOR = Collator.getInstance();

    static {
        COMPARATOR.setStrength( Collator.PRIMARY );
    }

    /**
     * Links to entities that are part proeperty values.
     */
    private Map<String, ModelObjectLink> entityLinks = new HashMap<String, ModelObjectLink>();

    /**
     * The part edited by this form.
     */
    private IModel<Part> model;

    /**
     * Entity reference fields.
     */
    private List<EntityReferencePanel<? extends ModelEntity>> entityFields =
            new ArrayList<EntityReferencePanel<? extends ModelEntity>>();

    /**
     * Part issues panel.
     */
    private IssuesPanel partIssuesPanel;

    /**
     * Part description.
     */
    private TextArea<String> partDescription;

    /**
     * Task.
     */
    private TextField<String> taskField;

    /**
     * Initiated event.
     */
    private TextField<String> initiatedEventField;

    /**
     * Repetition.
     */
    private DelayPanel repeatsEveryPanel;

    /**
     * Completion.
     */
    private DelayPanel completionTimePanel;

    /**
     * Self-termination.
     */
    private CheckBox selfTerminatingCheckBox;

    /**
     * Whether repeating.
     */
    private CheckBox repeatingCheckBox;

    /**
     * Whether executed as team by assignees.
     */
    private CheckBox asTeamCheckBox;

    /**
     * Whether starts with segment.
     */
    private CheckBox startWithSegmentCheckBox;

    /**
     * Whether is ongoing.
     */
    private CheckBox ongoingCheckBox;

    /**
     * Whether terminates event segment responds to.
     */
    private CheckBox terminatesSegmentCheckBox;

    /**
     * Goals achieved by part.
     */
    private Component taskGoalsPanel;

    /**
     * Part summary.
     */
    private PartSummaryPanel summaryPanel;

    /**
     * Attachments panel.
     */
    private AttachmentPanel attachmentsPanel;

    /**
     * Category choice.
     */
    private DropDownChoice<String> categoryChoice;

    /**
     * Prohibited checkbox.
     */
    private CheckBox prohibitedCheckBox;

    /**
     * Whether the part was updated.
     */
    private boolean partUpdated = false;

    /**
     * Tags fields container.
     */
    private WebMarkupContainer tagsContainer;

    /**
     * Classification fields container.
     */
    private WebMarkupContainer classificationContainer;

    /**
     * Execution field container.
     */
    private WebMarkupContainer executionContainer;

    /**
     * Timing field container.
     */
    private WebMarkupContainer timingContainer;
    /**
     * Terminates segment container.
     */
    private WebMarkupContainer terminatesEventPhaseContainer;

    /**
     * Location link.
     */
    private ModelObjectLink locationLink;

    /**
     * Show simple vs advanced form label.
     */
    private Label simpleAdvanced;
    /**
     * Assets container.
     */
    private WebMarkupContainer assetsContainer;

    /**
     * The containing plan page.
     */
    private ModelPage modelPage;

    public ExpandedPartPanel( String id, IModel<Part> model, Set<Long> expansions, ModelPage modelPage ) {
        super( id, model, expansions );
        super.setOutputMarkupPlaceholderTag( false );
        setOutputMarkupId( true );
        this.model = model;
        this.modelPage = modelPage;
        addSummaryPanel();
        addSimpleAdvanced();
        addTagsPanel();
        addPartDescription();
        addTaskField();
        addClassificationFields();
        addEntityFields();
        addEventInitiation();
        addExecution();
        addTerminatesSegmentFields();
        addTimingFields();
        addGoalsLink();
        addGoals();
        addAssets();
        addIssuesPanel();
        addAttachments();
        adjustFields();
    }

    private ModelPage getModelPage() {
        return modelPage;
    }

    private boolean isShowSimpleForm() {
        return getModelPage().isShowSimpleForm( getPart() );
    }

    private void setShowSimpleForm( boolean val ) {
        getModelPage().setShowSimpleForm( getPart(), val );
    }

    private void addClassificationFields() {
        classificationContainer = new WebMarkupContainer( "classificationContainer" );
        classificationContainer.setOutputMarkupId( true );
        makeVisible( classificationContainer, !isShowSimpleForm() );
        add( classificationContainer );
        addCategoryField();
        addProhibitedField();
    }

    private void addTagsPanel() {
        tagsContainer = new WebMarkupContainer( "tagsContainer" );
        tagsContainer.setOutputMarkupId( true );
        // makeVisible( tagsContainer, !isShowSimpleForm() );
        add( tagsContainer );
        TagsPanel tagsPanel = new TagsPanel( "tags", new Model<Taggable>( getPart() ) );
        tagsContainer.add( tagsPanel );
        AjaxLink tagsLink = new AjaxLink( "tagsLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.AspectViewed, Channels.MODEL_SEARCHING, ModelSearchingFloatingPanel.TAGS ) );
            }
        };
        tagsLink.add( new AttributeModifier( "class", new Model<String>( "model-object-link" ) ) );
        tagsContainer.add( tagsLink );
    }

    private void addSummaryPanel() {
        summaryPanel = new PartSummaryPanel( "partSummary", new PropertyModel<Part>( this, "part" ) );
        summaryPanel.setOutputMarkupId( true );
        summaryPanel.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Collapsed, getPart() ) );
            }
        } );
        summaryPanel.add( new AttributeModifier( "class", new Model<String>( getCssClasses() ) ) );
        addOrReplace( summaryPanel );
    }

    private void addSimpleAdvanced() {
        simpleAdvanced = new Label( "simpleAdvanced",
                new Model<String>( isShowSimpleForm() ?
                        "Show advanced form" :
                        "Show simple form" ) );
        simpleAdvanced.setOutputMarkupId( true );
        simpleAdvanced.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                setShowSimpleForm( !isShowSimpleForm() );
                adjustSimpleAdvancedFields( target );
                addSimpleAdvanced();
                target.add( simpleAdvanced );
            }
        } );
        addOrReplace( simpleAdvanced );
    }

    private void adjustSimpleAdvancedFields( AjaxRequestTarget target ) {
        boolean showSimpleForm = isShowSimpleForm();
        // makeVisible( tagsContainer, !showSimpleForm );
        // target.add( tagsContainer );
        makeVisible( classificationContainer, !showSimpleForm );
        target.add( classificationContainer );
        makeVisible( executionContainer, !showSimpleForm );
        target.add( executionContainer );
        makeVisible( timingContainer, !showSimpleForm );
        target.add( timingContainer );
        makeVisible( assetsContainer, !showSimpleForm );
        target.add( assetsContainer );
    }

    private String getCssClasses() {
        Level priority = getQueryService().computePartPriority( getPart() );
        return "summary " + priority.getNegativeLabel().toLowerCase();
    }

    private void addPartDescription() {
        partDescription = new TextArea<String>( "description",                            // NON-NLS
                new PropertyModel<String>( this, "description" ) );
        partDescription.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "description" ) );
            }
        } );
        partDescription.setOutputMarkupId( true );
        addInputHint( partDescription, "Brief advice" );
        add( partDescription );
    }

    private void addCategoryField() {
        categoryChoice = new DropDownChoice<String>( "category",
                new PropertyModel<String>( this, "categoryLabel" ),
                getCategoryLabels() );
        categoryChoice.setOutputMarkupId( true );
        Part.Category category = getPart().getCategory();
        if ( category != null )
            addTipTitle( categoryChoice, new Model<String>( category.getHint() ) );
        categoryChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "category" ) );
            }
        } );
        classificationContainer.add( categoryChoice );
    }

    private void addProhibitedField() {
        prohibitedCheckBox = new CheckBox( "prohibited", new PropertyModel<Boolean>( this, "prohibited" ) );
        classificationContainer.add( prohibitedCheckBox );
        prohibitedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "prohibited" ) );
            }
        } );
    }

    private List<String> getCategoryLabels() {
        List<String> labels = new ArrayList<String>();
        labels.add( NO_CATEGORY );
        labels.addAll( Part.Category.getAllLabels() );
        return labels;
    }

    private void addEntityFields() {
        entityFields = new ArrayList<EntityReferencePanel<? extends ModelEntity>>();
        addFunctionField();
        addActorField();
        addRoleField();
        addOrganizationField();
        addJurisdictionField();
        addLocationField();
        addLocationLink();
        addEntityLinks();
    }

    public List<String> getAllTasks() {
        return getQueryService().findAllTasks();
    }

    public List<String> getAllActorNames() {
        return getQueryService().findAllEntityNames( Actor.class );
    }

    public List<String> getAllRoleNames() {
        return getQueryService().findAllEntityNames( Role.class );
    }

    public List<String> getAllFunctionNames() {
        return getQueryService().findAllEntityNames( Function.class );
    }

    public List<String> getAllOrganizationNames() {
        return getQueryService().findAllEntityNames( Organization.class );
    }

    public List<String> getAllPlaceNames() {
        return getQueryService().findAllEntityNames( Place.class );
    }

    public List<String> getAllEventNames() {
        return getQueryService().findAllEntityNames( Event.class );
    }

    private void addIssuesPanel() {
        partIssuesPanel = new IssuesPanel( "issues", model, getExpansions() );        // NON-NLS
        partIssuesPanel.setOutputMarkupId( true );
        addOrReplace( partIssuesPanel );
    }

    private void addAttachments() {
        attachmentsPanel = new AttachmentPanel( "attachments", model );
        add( attachmentsPanel );
    }

    private void adjustFields() {
        Part part = getPart();
        boolean lockedByUser = isLockedByUser( part );
        taskField.setEnabled( lockedByUser );
        categoryChoice.setEnabled( lockedByUser );
        prohibitedCheckBox.setEnabled( lockedByUser );
        for ( EntityReferencePanel entityReferencePanel : entityFields )
            entityReferencePanel.enable( lockedByUser );
        repeatsEveryPanel.enable( part.isRepeating() && lockedByUser );
        completionTimePanel.enable( part.isSelfTerminating() && lockedByUser );
        selfTerminatingCheckBox.setEnabled( lockedByUser && !part.isOngoing() );
        repeatingCheckBox.setEnabled( lockedByUser && !part.isOngoing() );
        startWithSegmentCheckBox.setEnabled( lockedByUser && !part.isOngoing() );
        ongoingCheckBox.setEnabled( lockedByUser );
        asTeamCheckBox.setEnabled( lockedByUser );
        terminatesSegmentCheckBox.setEnabled( lockedByUser );
        initiatedEventField.setEnabled( lockedByUser );
        partDescription.setEnabled( lockedByUser );
        boolean partHasIssues = getCommunityService().getDoctor().hasIssues( getCommunityService(), part, false );
        makeVisible( partIssuesPanel, partHasIssues );
    }

    private void addFunctionField() {
        EntityReferencePanel<Function> field = new EntityReferencePanel<Function>( FUNCTION_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllFunctionNames(),
                FUNCTION_PROPERTY,
                Function.class );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addActorField() {
        EntityReferencePanel<Actor> field = new EntityReferencePanel<Actor>( ACTOR_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllActorNames(),
                ACTOR_PROPERTY,
                Actor.class );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addRoleField() {
        EntityReferencePanel<Role> field = new EntityReferencePanel<Role>( ROLE_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllRoleNames(),
                ROLE_PROPERTY,
                Role.class );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addOrganizationField() {
        EntityReferencePanel<Organization> field = new EntityReferencePanel<Organization>( ORG_PROPERTY,
                new PropertyModel<Part>( this,
                        "part" ),
                getAllOrganizationNames(),
                ORG_PROPERTY,
                Organization.class );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addJurisdictionField() {
        EntityReferencePanel<Place> field = new EntityReferencePanel<Place>( JURISDICTION_PROPERTY,
                new PropertyModel<Part>( this, "part" ),
                getAllPlaceNames(),
                JURISDICTION_PROPERTY,
                Place.class );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addLocationField() {
        AssignedLocationPanel field = new AssignedLocationPanel(
                "location",
                new PropertyModel<Part>( this, "part" ) );
        addOrReplace( field );
    }

    private ModelObjectLink addLocationLink() {
        Part part = getPart();
        locationLink = new ModelObjectLink( "location-link",
                new PropertyModel<ModelEntity>( part, "knownLocation" ),
                new Model<String>( "Location" ) );
        locationLink.setOutputMarkupId( true );
        addOrReplace( locationLink );
        return locationLink;
    }


    private void addTaskField() {
        final PropertyModel<List<String>> choices = new PropertyModel<List<String>>( this, "allTasks" );
        taskField =
                new AutoCompleteTextField<String>(
                        TASK_PROPERTY,
                        new PropertyModel<String>( this, TASK_PROPERTY ),
                        getAutoCompleteSettings() ) {
                    @Override
                    protected Iterator<String> getChoices( String s ) {
                        List<String> candidates = new ArrayList<String>();
                        for ( String choice : choices.getObject() ) {
                            if ( getQueryService().likelyRelated( s, choice ) )
                                candidates.add( choice );
                        }
                        return candidates.iterator();
                    }
                };
        taskField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "task" ) );
            }
        } );
        addInputHint( taskField, "The task to be done" );
        add( taskField );
    }

    private void addEventInitiation() {
        final PropertyModel<List<String>> choices = new PropertyModel<List<String>>( this, "allEventNames" );
        initiatedEventField = new AutoCompleteTextField<String>(
                "initiatedEvent",
                new PropertyModel<String>( this, "initiatedEventName" ),
                getAutoCompleteSettings() ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices.getObject() ) {
                    if ( getQueryService().likelyRelated( s, choice ) )
                        candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        initiatedEventField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "initiatedEvent" ) );
            }
        } );
        addInputHint( initiatedEventField, "The name of the event the task may trigger" );
        add( initiatedEventField );
    }

    private void addEntityLinks() {
        for ( String prop : EntityProps ) {
            addEntityLink( prop );
        }
    }

    private ModelObjectLink addEntityLink( String prop ) {
        Part part = getPart();
        ModelObjectLink moLink = new ModelObjectLink( prop + "-link",
                new PropertyModel<ModelEntity>( part, prop ),
                new Model<String>( prop.equals( "initiatedEvent" ) ?
                        "Causes event" :
                        WordUtils.capitalize( prop.equals( "actor" ) ?
                                "agent" :
                                prop ) ) );
        moLink.setOutputMarkupId( true );
        entityLinks.put( prop, moLink );
        addOrReplace( moLink );
        return moLink;
    }

    private void addTimingFields() {
        timingContainer = new WebMarkupContainer( "timingContainer" );
        timingContainer.setOutputMarkupId( true );
        makeVisible( timingContainer, !isShowSimpleForm() );
        addOrReplace( timingContainer );
        repeatsEveryPanel =
                new DelayPanel( "repeats-every", new PropertyModel<ModelObject>( this, "part" ), "repeatsEvery" );
        repeatsEveryPanel.setOutputMarkupId( true );
        timingContainer.add( repeatsEveryPanel );
        completionTimePanel =
                new DelayPanel( "completion-time", new PropertyModel<ModelObject>( this, "part" ), "completionTime" );
        completionTimePanel.setOutputMarkupId( true );
        timingContainer.add( completionTimePanel );
        selfTerminatingCheckBox =
                new CheckBox( "self-terminating", new PropertyModel<Boolean>( this, "selfTerminating" ) );
        selfTerminatingCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                completionTimePanel.enable( getPart().isSelfTerminating() && isLockedByUser( getPart() ) );
                target.add( completionTimePanel );
                update( target, new Change( Change.Type.Updated, getPart(), "selfTerminating" ) );
            }
        } );
        timingContainer.add( selfTerminatingCheckBox );
        repeatingCheckBox = new CheckBox( "repeating", new PropertyModel<Boolean>( this, "repeating" ) );
        timingContainer.add( repeatingCheckBox );
        repeatingCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                repeatsEveryPanel.enable( getPart().isRepeating() );
                target.add( repeatsEveryPanel );
                update( target, new Change( Change.Type.Updated, getPart(), "onclick" ) );
            }
        } );
        startWithSegmentCheckBox =
                new CheckBox( "startsWithSegment", new PropertyModel<Boolean>( this, "startsWithSegment" ) );
        timingContainer.add( startWithSegmentCheckBox );
        startWithSegmentCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "startsWithSegment" ) );
            }
        } );
        timingContainer.add( new ModelObjectLink( "event-link",
                new PropertyModel<Event>( this, "part.segment.event" ),
                new PropertyModel<String>( this, "part.segment.event.name" ) ) );
        timingContainer.add( new Label( "event-timing", new PropertyModel<String>( this, "eventTiming" ) ) );
        ongoingCheckBox =
                new CheckBox( "ongoing", new PropertyModel<Boolean>( this, "ongoing" ) );
        timingContainer.add( ongoingCheckBox );
        ongoingCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                Part part = getPart();
                boolean lockedByUser = isLockedByUser( part );
                startWithSegmentCheckBox.setEnabled( lockedByUser && !part.isOngoing() );
                repeatsEveryPanel.enable( part.isRepeating() && lockedByUser );
                completionTimePanel.enable( part.isSelfTerminating() && lockedByUser );
                selfTerminatingCheckBox.setEnabled( lockedByUser && !part.isOngoing() );
                repeatingCheckBox.setEnabled( lockedByUser && !part.isOngoing() );
                target.add(
                        startWithSegmentCheckBox,
                        repeatingCheckBox,
                        repeatsEveryPanel,
                        selfTerminatingCheckBox,
                        completionTimePanel );
                update( target, new Change( Change.Type.Updated, getPart(), "ongoing" ) );
            }
        } );

    }

    private void addTerminatesSegmentFields() {
        terminatesEventPhaseContainer = new WebMarkupContainer( "terminatesEventPhaseContainer" );
        terminatesEventPhaseContainer.setOutputMarkupId( true );
        addOrReplace( terminatesEventPhaseContainer );
        terminatesSegmentCheckBox =
                new CheckBox( "terminatesEventPhase", new PropertyModel<Boolean>( this, "terminatesEventPhase" ) );
        terminatesEventPhaseContainer.add( terminatesSegmentCheckBox );
        terminatesSegmentCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "terminatesEventPhase" ) );
            }
        } );
        WebMarkupContainer phaseEnding = new WebMarkupContainer( "phase-ending" );
        phaseEnding.add( new ModelObjectLink( "end-phase-link",
                new PropertyModel<Event>( this, "part.segment.phase" ),
                new PropertyModel<String>( this, "part.segment.phase.name" ) ) );
        phaseEnding.setVisible( getPart().getSegment().getPhase().isPostEvent() );
        terminatesEventPhaseContainer.add( phaseEnding );
        WebMarkupContainer eventEnding = new WebMarkupContainer( "event-ending" );
        eventEnding.add( new Label( "end-event-effect", getEventEndingEffect() ) );
        eventEnding.add( new ModelObjectLink( "end-event-link",
                new PropertyModel<Event>( this, "part.segment.event" ),
                new PropertyModel<String>( this, "part.segment.event.name" ) ) );
        terminatesEventPhaseContainer.add( eventEnding );
    }

    private Phase getPhase() {
        return getPart().getSegment().getPhase();
    }

    public String getEventTiming() {
        return getPhase().isPreEvent()
                ? "possibility"
                : getPhase().isConcurrent()
                ? "start"
                : "end";
    }

    private String getEventEndingEffect() {
        Phase phase = getPhase();
        return phase.isPreEvent() ? "prevent event" : phase.isConcurrent() ? "end event" : "of event";
    }

    private void addExecution() {
        executionContainer = new WebMarkupContainer( "executionContainer" );
        executionContainer.setOutputMarkupId( true );
        makeVisible( executionContainer, !isShowSimpleForm() );
        add( executionContainer );
        asTeamCheckBox = new CheckBox( "asTeam", new PropertyModel<Boolean>( this, "asTeam" ) );
        executionContainer.add( asTeamCheckBox );
        asTeamCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPart(), "asTeam" ) );
            }
        } );
    }

    private void addAssets() {
        assetsContainer = new WebMarkupContainer( "assetsContainer" );
        assetsContainer.setOutputMarkupId( true );
        makeVisible( assetsContainer, !isShowSimpleForm() );
        add( assetsContainer );
        AjaxLink assetsLink = new AjaxLink( "assets-link" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.AspectViewed, getPart(), AssetConnectable.ASSETS ) );
            }
        };
        assetsContainer.add( assetsLink );
        Label assetsLabel = new Label(
                "assets-text",
                StringUtils.capitalize( getPart().getAssetConnections().getLabel( ) ) );
        assetsContainer.add( assetsLabel );
    }

    private void addGoalsLink() {
        AjaxLink segmentGoalsLink = new AjaxLink( "segment-goals-link" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, getPart().getSegment(), SegmentEditPanel.GOALS ) );
            }
        };
        segmentGoalsLink.add( new AttributeModifier( "class", new Model<String>( "segment-link" ) ) );
        add( segmentGoalsLink );
    }

    private void addGoals() {
        if ( getPart().getSegment().getGoals().isEmpty() ) {
            taskGoalsPanel = new Label( "goals", new Model<String>( "(No goals)" ) );
        } else {
            taskGoalsPanel = new PartGoalsPanel( "goals", new PropertyModel<Part>( this, "part" ), getExpansions() );
        }
        taskGoalsPanel.setOutputMarkupId( true );
        addOrReplace( taskGoalsPanel );
    }

    //====================================

    /**
     * Get the actor string.
     *
     * @return the name of the actor, or the empty string if null
     */
    public String getActor() {
        final Actor actor = getPart().getActor();
        return actor == null ? EMPTY_STRING : actor.getName();
    }

    /**
     * Get the jurisdiction string.
     *
     * @return the name of the jurisdiction, or the empty string if null
     */
    public String getJurisdiction() {
        final Place jurisdiction = getPart().getJurisdiction();
        return jurisdiction == null ? EMPTY_STRING : jurisdiction.getName();
    }

    /**
     * Get the location string.
     *
     * @return the name of the location, or the empty string if null
     */
    public String getLocationName() {
        final Place location = getPart().getKnownLocation();
        return location == null ? EMPTY_STRING : location.getName();
    }

    /**
     * Get the organization string.
     *
     * @return the name of the organization, or the empty string if null
     */
    public String getOrganization() {
        final Organization organization = getPart().getOrganization();
        return organization == null ? EMPTY_STRING : organization.getName();
    }

    /**
     * Get the role string.
     *
     * @return the name of the role, or the empty string if null
     */
    public String getRole() {
        final Role role = getPart().getRole();
        return role == null ? EMPTY_STRING : role.getName();
    }

    public String getTask() {
        return getPart().getTask();
    }

    /**
     * Set the part's actor.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setActor( String name ) {
        Actor oldActor = getPart().getActor();
        String oldName = oldActor == null ? "" : oldActor.getName();
        Actor newActor = null;
        if ( name == null || name.trim().isEmpty() )
            newActor = null;
        else {
            if ( oldActor == null || !isSame( name, oldName ) )
                newActor = getQueryService().safeFindOrCreate( Actor.class, name );
        }
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "actor", newActor ) );
        getCommander().cleanup( Actor.class, oldName );
    }

    /**
     * Set the part's jurisdiction.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setJurisdiction( String name ) {
        Place oldPlace = getPart().getJurisdiction();
        String oldName = oldPlace == null ? "" : oldPlace.getName();
        Place newPlace = null;
        if ( name == null || name.trim().isEmpty() )
            newPlace = null;
        else {
            if ( oldPlace == null || !isSame( name, oldName ) )
                newPlace = getQueryService().safeFindOrCreate( Place.class, name );
        }
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "jurisdiction", newPlace ) );
        getCommander().cleanup( Place.class, oldName );
    }

    /**
     * Set the part's organization.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setOrganization( String name ) {
        Organization oldOrg = getPart().getOrganization();
        String oldName = oldOrg == null ? "" : oldOrg.getName();
        Organization newOrg = null;
        if ( name == null || name.trim().isEmpty() )
            newOrg = null;
        else {
            if ( oldOrg == null || !isSame( name, oldName ) )
                newOrg = getQueryService().safeFindOrCreate( Organization.class, name );
        }
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "organization", newOrg ) );
        getCommander().cleanup( Organization.class, oldName );
    }

    /**
     * Set the part's role.
     *
     * @param name if null or empty, set to null; otherwise, only set if different.
     */
    public void setRole( String name ) {
        Role oldRole = getPart().getRole();
        String oldName = oldRole == null ? "" : oldRole.getName();
        Role newRole = null;
        if ( name == null || name.trim().isEmpty() )
            newRole = null;
        else {
            if ( oldRole == null || !isSame( name, oldName ) )
                newRole = getQueryService().safeFindOrCreate( Role.class, name );
        }
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "role", newRole ) );
        getCommander().cleanup( Role.class, oldName );
    }

    /**
     * Set the part's task.
     *
     * @param task the task
     */
    public void setTask( String task ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "task", task ) );
    }

    public Delay getRepeatsEvery() {
        return getPart().getRepeatsEvery();
    }

    /**
     * Sets repeat period.
     *
     * @param delay a delay
     */
    public void setRepeatsEvery( Delay delay ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "repeatsEvery", delay ) );
    }

    public Delay getCompletionTime() {
        return getPart().getCompletionTime();
    }

    /**
     * Sets completion time.
     *
     * @param delay a delay
     */
    public void setCompletionTime( Delay delay ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "completionTime", delay ) );
    }

    /**
     * Is part self-terminating?
     *
     * @return a boolean
     */
    public boolean isSelfTerminating() {
        return getPart().isSelfTerminating();
    }

    /**
     * Sets whether self terminating.
     *
     * @param val a boolean
     */
    public void setSelfTerminating( boolean val ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "selfTerminating", val ) );
    }

    /**
     * Is part repeating?
     *
     * @return a boolean
     */
    public boolean isRepeating() {
        return getPart().isRepeating();
    }

    /**
     * Sets whether repeating.
     *
     * @param val a boolean
     */
    public void setRepeating( boolean val ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "repeating", val ) );
    }

    /**
     * Does part start with segment?
     *
     * @return a boolean
     */
    public boolean isStartsWithSegment() {
        return getPart().isStartsWithSegment();
    }

    /**
     * Sets whether starts with segment.
     *
     * @param val a boolean
     */
    public void setStartsWithSegment( boolean val ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "startsWithSegment", val ) );
    }

    /**
     * Is part ongoing?
     *
     * @return a boolean
     */
    public boolean isOngoing() {
        return getPart().isOngoing();
    }

    /**
     * Sets whether part is ongoing.
     *
     * @param val a boolean
     */
    public void setOngoing( boolean val ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "ongoing", val ) );
    }

    /**
     * Do assignees execute as team?
     *
     * @return a boolean
     */
    public boolean isAsTeam() {
        return getPart().isAsTeam();
    }

    /**
     * Sets whether assignees execute as team.
     *
     * @param val a boolean
     */
    public void setAsTeam( boolean val ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "asTeam", val ) );
    }

    /**
     * Does part terminate event?
     *
     * @return a boolean
     */
    public boolean isTerminatesEventPhase() {
        return getPart().isTerminatesEventPhase();
    }

    /**
     * Sets whether terminates event.
     *
     * @param val a boolean
     */
    public void setTerminatesEventPhase( boolean val ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "terminatesEventPhase", val ) );
    }

    /**
     * Event initiated by part, if any.
     *
     * @return a plan event
     */
    public Event getInitiatedEvent() {
        return getPart().getInitiatedEvent();
    }

    /**
     * Name of event initiated by part, if any.
     *
     * @return a plan event name
     */
    public String getInitiatedEventName() {
        Event event = getInitiatedEvent();
        return event != null ? event.getName() : "";
    }

    /**
     * Sets whether terminates the segment's event.
     *
     * @param name a plan event name
     */
    public void setInitiatedEventName( String name ) {
        Event oldEvent = getPart().getInitiatedEvent();
        String oldName = oldEvent == null ? "" : oldEvent.getName();
        Event newEvent = null;
        if ( name == null || name.trim().isEmpty() )
            newEvent = null;
        else {
            if ( oldEvent == null || !isSame( name, oldName ) )
                newEvent = getQueryService().safeFindOrCreateType( Event.class, name );
        }
        doCommand( new UpdateSegmentObject( getUsername(), getPart(), "initiatedEvent", newEvent ) );
        getCommander().cleanup( Event.class, oldName );
    }

    /**
     * Get part description.
     *
     * @return a string
     */
    public String getDescription() {
        return getPart().getDescription();
    }

    /**
     * Set part description via command.
     *
     * @param val a string
     */
    public void setDescription( String val ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "description", val ) );
    }

    /**
     * Get label for part's task category.
     *
     * @return a string
     */
    public String getCategoryLabel() {
        Part.Category category = getPart().getCategory();
        return category == null ? NO_CATEGORY : category.getLabel();
    }

    /**
     * Set the part's task category given the category's label.
     *
     * @param label a string
     */
    public void setCategoryLabel( String label ) {
        if ( label != null ) {
            Part.Category category;
            category = label.equals( NO_CATEGORY ) ? null : Part.Category.valueOfLabel( label );
            doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "category", category ) );
        }
    }

    public boolean isProhibited() {
        return getPart().isProhibited();
    }

    public void setProhibited( boolean val ) {
        if ( val != isProhibited() ) {
            doCommand( new UpdateSegmentObject( getUser().getUsername(), getPart(), "prohibited", val ) );
        }
    }

    /**
     * Get edited part.
     *
     * @return a part
     */
    public final Part getPart() {
        return model.getObject();
    }

    /**
     * Refresh part panel.
     *
     * @param target an ajax request target
     */
    public void refresh( AjaxRequestTarget target ) {
        addEntityFields();
        adjustFields();
        addGoals();
        // todo - remove
        target.add( partDescription );
        // todo - remove
        target.add( partIssuesPanel );
        target.add( attachmentsPanel );
        target.add( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        refresh( target );
    }

    @Override
    public void changed( Change change ) {
        if ( change.isUpdated() && change.isForInstanceOf( Part.class ) ) {
            partUpdated = true;
        }
        super.changed( change );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( !change.isNone() ) {
            if ( change.isUpdated() ) {
                String property = change.getProperty();
                addSummaryPanel();
                target.add( summaryPanel );
                for ( EntityReferencePanel entityReferencePanel : entityFields ) {
                    entityReferencePanel.updateIssues();
                    target.add( entityReferencePanel );
                }
                if ( change.getSubject( getCommunityService() ).equals( getPart() ) ) {
                    if ( Arrays.asList( EntityProps ).contains( property ) )
                        updateEntityLink( target, change );
                }
                addLocationLink();
                target.add( locationLink );
                if ( property.equals( "goals" ) ) {
                    addGoals();
                    target.add( taskGoalsPanel );
                }
                target.add( initiatedEventField );
            }
            if ( !change.isDisplay() && !change.isCopied() ) {
                makeVisible( target,
                        partIssuesPanel,
                        getCommunityService().getDoctor().hasIssues( getCommunityService(), getPart(), false ) );
                target.add( partIssuesPanel );
            }
        }
        change.addQualifier( "updated", partUpdated );
        super.updateWith( target, change, updated );
    }

    private void updateEntityLink( AjaxRequestTarget target, Change change ) {
        ModelObjectLink moLink = addEntityLink( change.getProperty() );
        if ( moLink != null ) {
            target.add( moLink );
        }
    }
}
