package com.mindalliance.channels.pages.components.guide;

import com.google.code.jqwicket.ui.accordion.AccordionOptions;
import com.google.code.jqwicket.ui.accordion.AccordionWebMarkupContainer;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.guide.Activity;
import com.mindalliance.channels.guide.ActivityChange;
import com.mindalliance.channels.guide.ActivityGroup;
import com.mindalliance.channels.guide.ActivityRef;
import com.mindalliance.channels.guide.ActivityScript;
import com.mindalliance.channels.guide.ActivityStep;
import com.mindalliance.channels.guide.ChangeQualifier;
import com.mindalliance.channels.guide.Guide;
import com.mindalliance.channels.guide.GuideReader;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import info.bliki.wiki.model.WikiModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Planning guide panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/24/12
 * Time: 3:26 PM
 */
public class PlanningGuidePanel extends AbstractUpdatablePanel {

    @SpringBean
    private GuideReader guideReader;

    private Guide guide;
    private ActivityGroup selectedGroup;
    private Activity selectedActivity;
    private Activity openedActivity;
    private AccordionWebMarkupContainer accordion;
    private Map<Activity, WebMarkupContainer> activityDocs = new HashMap<Activity, WebMarkupContainer>();
    private Map<Activity, Component> groupDivs = new HashMap<Activity, Component>();

    public PlanningGuidePanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        guide = guideReader.getGuide();
        addGuideName();
        addHideImage();
        addGuideAccordion();
 //       addGuideDoc();
    }

    private void addGuideName() {
        add( new Label( "guideName", guide.getName() ) );
    }


    private void addHideImage() {
        AjaxFallbackLink hideSocialLink = new AjaxFallbackLink( "hideGuide" ) {
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Collapsed, Channels.GUIDE_ID );
                change.setMessage( "To re-open, select Guide in the top Show menu." );
                update( target, change );
            }
        };
        add( hideSocialLink );
    }

    private void addGuideAccordion() {
        groupDivs = new HashMap<Activity, Component>();
        AccordionOptions options = new AccordionOptions();
        options.active( false );
        // options.addCssResourceReferences( new CssResourceReference( getClass(), "res/guide.css" ) );
        accordion = new AccordionWebMarkupContainer( "accordion", options );
        accordion.setOutputMarkupId( true );
        accordion.add( new ListView<ActivityGroup>(
                "groups",
                guide.getActivityGroups() ) {
            protected void populateItem( final ListItem<ActivityGroup> groupItem ) {
                final ActivityGroup group = groupItem.getModelObject();
                Label groupLabel = new Label( "title", group.getName() );
                groupItem.add( groupLabel );
                groupLabel.add( new AjaxEventBehavior( "onclick" ) {
                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        if ( selectedGroup != null && selectedGroup.equals( group ) ) {
                            selectedGroup = null;
                            selectedActivity = null;
                            accordion.activate( target, false );
                            target.add( accordion );
                        } else {
                            selectedGroup = group;
                            selectActivity( selectedGroup, null, target );
                        }
                        String js = "setTimeout('" + accordion.resize().toString( true ) + "',500);";
                        target.appendJavaScript( js );
                    }
                } );
                ListView<Activity> activityList = new ListView<Activity>(
                        "activities",
                        group.getActivities()
                ) {
                    @Override
                    protected void populateItem( ListItem<Activity> activityItem ) {
                        final Activity activity = activityItem.getModelObject();
                        groupDivs.put( activity, groupItem );
                        WebMarkupContainer doc = getDoc( group, activity );
                        makeVisible( doc, false );
                        activityItem.add( doc );
                        AjaxLink<String> activityLink = new AjaxLink<String>( "activityLink" ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                selectActivity( group, activity, target );
                            }
                        };
                        Label activityNameLabel = new Label( "activityName", activity.getName() );
                        activityLink.add( activityNameLabel );
                        activityItem.add( activityLink );
                    }
                };
                groupItem.add( activityList );
            }
        } );
        addOrReplace( accordion );
    }

    private WebMarkupContainer getDoc( ActivityGroup group, Activity activity ) {
        WebMarkupContainer docContainer = activityDocs.get( activity );
        if ( docContainer == null ) {
            docContainer = new WebMarkupContainer( "activityDoc" );
            docContainer.setOutputMarkupId( true );
            docContainer.add( getStepsList( activity ) );
            docContainer.add( getDoNextContainer( group, activity ) );
            activityDocs.put( activity, docContainer );
        }
        return docContainer;
    }

    private void addGuideDoc() {
        add( getGuideDoc() );
    }

    private Label getGuideDoc() {
        Label label = new Label( "guideDoc", wikimediaToHtml(
                selectedActivity == null
                        ? guide.getDescription()
                        : "" ) );
        label.setEscapeModelStrings( false );
        return label;
    }

    private ListView<ActivityStep> getStepsList( Activity activity ) {
        ListView<ActivityStep> stepsListView = new ListView<ActivityStep>(
                "steps",
                new PropertyModel<List<ActivityStep>>( activity, "activitySteps" )
        ) {
            @Override
            protected void populateItem( ListItem<ActivityStep> item ) {
                final ActivityStep activityStep = item.getModelObject();
                final ActivityScript activityScript = activityStep.getActivityScript();
                AjaxLink<String> actionLink = new AjaxLink<String>( "actionLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        if ( activityScript != null )
                            runActivityScript( activityScript, target );
                    }
                };
                actionLink.add( new Label(
                        "action",
                        activityScript == null
                                ? ""
                                : activityScript.getAction() ) );
                item.add( actionLink );
                actionLink.setVisible( canRunScript( activityScript ) );
                item.add( getDescriptionLabel( activityStep )
                        .setVisible( !activityStep.getDescription().isEmpty() ) );
            }
        };
        return stepsListView;
    }

    private boolean canRunScript( ActivityScript activityScript ) {
        return activityScript != null && !CollectionUtils.exists(
                activityScript.getActivityChanges(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return makeChange( (ActivityChange) object ) == null;
                    }
                }
        );
    }

    private void runActivityScript( ActivityScript activityScript, AjaxRequestTarget target ) {
        if ( activityScript != null ) {
            for ( ActivityChange activityChange : activityScript.getActivityChanges() ) {
                Change change = makeChange( activityChange );
                if ( change != null ) {
                    Updatable updatable = getUpdatable( activityChange );
                    if ( updatable != null ) {
                        updatable.changed( change );
                        updatable.updateWith( target, change, new ArrayList<Updatable>() );
                    } else {
                        break;
                    }
                } else {
                    break; // stop running the script
                }
            }
        }
    }

    private Updatable getUpdatable( ActivityChange activityChange ) {
        String updatableTargetPath = activityChange.getUpdateTargetPath();
        if ( updatableTargetPath == null ) {
            return planPage();
        } else {
            if ( updatableTargetPath.equals( "planPage" ) ) {
                return planPage();
            } else {
                return (Updatable) ChannelsUtils.getProperty( planPage(), updatableTargetPath, null );
            }
        }
    }

    private Change makeChange( ActivityChange activityChange ) {
        if ( activityChange == null ) return null;
        Change change;
        Change.Type type = Change.Type.valueOf( activityChange.getChangeType() );
        if ( activityChange.getSubjectId() != null ) {
            change = new Change( type, activityChange.getSubjectId() );
        } else {
            Identifiable identifiable = getSubject( activityChange );
            if ( identifiable == null )
                return null;
            else
                change = new Change( type, identifiable );
        }
        change.setProperty( activityChange.getProperty() );
        for ( ChangeQualifier qualifier : activityChange.getQualifiers() ) {
            change.addQualifier( qualifier.getName(), qualifier.getValue() );
        }
        return change;
    }

    private Identifiable getSubject( ActivityChange activityChange ) {
        return (Identifiable) ChannelsUtils.getProperty(
                getGuide(),
                activityChange.getSubjectPath(),
                null );
    }

    private Label getDescriptionLabel( ActivityStep step ) {
        Label label = new Label( "description", wikimediaToHtml( step.getDescription() ) );
        label.setEscapeModelStrings( false );
        return label;
    }

    private String wikimediaToHtml( String wikimedia ) {
        // First substitute template variable
        GuideTemplateContext guideTemplateContext = new GuideTemplateContext();
        String convertedWikimedia = ChannelsUtils.convertTemplate( wikimedia.trim(), guideTemplateContext );
        String helpUrl = guideTemplateContext.getHelpUrl();
        WikiModel wikiModel = new WikiModel( helpUrl + "/${image}", helpUrl + "/${title}" );
        String html = wikiModel.render( convertedWikimedia );
        html = html.replaceAll( "<a ", "<a target='_blank' " );
        return html;
    }


    private WebMarkupContainer getDoNextContainer( final ActivityGroup group, Activity activity ) {
        List<ActivityRef> activityRefs = activity.getNextActivities();
        WebMarkupContainer doNextContainer = new WebMarkupContainer( "doNextContainer" );
        doNextContainer.setVisible( !activityRefs.isEmpty() );
        ListView<ActivityRef> doNextListView = new ListView<ActivityRef>(
                "doNexts",
                activityRefs
        ) {
            @Override
            protected void populateItem( ListItem<ActivityRef> item ) {
                ActivityRef activityRef = item.getModelObject();
                final ActivityGroup nextGroup = guide.derefGroup( activityRef.getGroupId() );
                final Activity nextActivity = nextGroup == null
                        ? null
                        : nextGroup.derefActivity( activityRef.getActivityId() );
                AjaxLink<String> nextLink = new AjaxLink<String>( "doNextLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        accordion.activate( target, guide.findGroupIndex( nextGroup ) );
                        selectActivity( group, nextActivity, target );
                        //String js = "setTimeout(\"$('#guide').scrollTop(0)\",500);";
                        String js = "$('#guide').scrollTop(0);";
                        target.appendJavaScript( js );
                    }
                };
                String groupPrefix = ( nextGroup == null || nextGroup.equals( selectedGroup )
                        ? ""
                        : "(" + nextGroup.getName() + ") " );
                String labelString = groupPrefix
                        + ( nextActivity == null ? "???" : nextActivity.getName() );
                Label doNextLabel = new Label( "doNextText", labelString );
                nextLink.add( doNextLabel );
                item.add( nextLink );
            }
        };
        doNextContainer.add( doNextListView );
        return doNextContainer;
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change ) {
        if ( selectedActivity != null ) {
            target.add( getDoc( selectedGroup, selectedActivity ) );
        }
    }

    private void selectActivity( ActivityGroup group, Activity activity, AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Selected, Channels.GUIDE_ID );
        change.setProperty( "activity" );
        change.addQualifier( "activity", activity );
        change.addQualifier( "group", group );
        update( target, change );
    }

    @Override
    public void changed( Change change ) {
        if ( change.isSelected() && change.getId() == Channels.GUIDE_ID ) {
            Activity activity = (Activity) change.getQualifier( "activity" );
            ActivityGroup group = (ActivityGroup) change.getQualifier( "group" );
            openedActivity = selectedActivity;
            if ( activity == null || selectedActivity != null && activity.equals( selectedActivity ) ) {
                selectedActivity = null;
            } else {
                selectedGroup = group;
                selectedActivity = activity;
            }
        } else {
            super.changed( change );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected() && change.getId() == Channels.GUIDE_ID ) {
            if ( openedActivity != null ) {
                makeVisible( getDoc( selectedGroup, openedActivity ), false );
                target.add( getDoc( selectedGroup, openedActivity ) );
            }
            if ( selectedActivity != null ) {
                makeVisible( getDoc( selectedGroup, selectedActivity ), true );
                target.add( getDoc( selectedGroup, selectedActivity ) );
            }
            accordion.resize( target );
        } else {
            super.updateWith( target, change, updated );
        }
    }


    // Script support: the bean against which all subject paths are evaluated

    public GuideInfo getGuide() {
        return new GuideInfo();
    }

    public class GuideTemplateContext implements Serializable {

        public GuideTemplateContext() {
        }

        public String getHelpUrl() {
            String serverUrl = guideReader.getServerUrl();
            return serverUrl
                    + ( serverUrl.endsWith( "/" ) ? "" : "/" )
                    + "doc/channels_user_guide";
        }

    }

    public class GuideInfo implements Serializable {

        public GuideInfo() {
        }

        public GuideInfo getGuide() {
            return this;
        }

        public Plan getPlan() {
            return PlanningGuidePanel.this.getPlan();
        }

        public PlanPage getPlanPage() {
            try {
                return PlanningGuidePanel.this.planPage();
            } catch( Exception e ) {
                return null;
            }
        }

        public Flow getAnyFlow() {
            List<Flow> flows = getPlanPage().getPart().getAllFlows();
            return flows.isEmpty()
                    ? null
                    : flows.get( new Random( 13 ).nextInt( flows.size() ) );
        }

        public Flow getAnySharingFlow() {
            List<Flow> flows = new ArrayList<Flow>();
            flows.addAll( getPlanPage().getPart().getAllSharingSends() );
            flows.addAll( getPlanPage().getPart().getAllSharingReceives() );
            return flows.isEmpty()
                    ? null
                    : flows.get( new Random( 13 ).nextInt( flows.size() ) );
        }


        public Actor getAnyActualAgent() {
            List<Actor> actualActors = getQueryService().listActualEntities( Actor.class );
            return actualActors.isEmpty()
                    ? null
                    : actualActors.get( new Random( 13 ).nextInt( actualActors.size() ) );
        }

        public Organization getAnyActualOrganization() {
            List<Organization> actualOrgs = getQueryService().listActualEntities( Organization.class );
            return actualOrgs.isEmpty()
                    ? null
                    : actualOrgs.get( new Random( 13 ).nextInt( actualOrgs.size() ) );
        }

        public Event getAnyEvent() {
            List<Event> events = getQueryService().listKnownEntities( Event.class );
            return events.isEmpty()
                    ? null
                    : events.get( new Random( 13 ).nextInt( events.size() ) );
        }

        public Phase getAnyPhase() {
            List<Phase> phases = getQueryService().listKnownEntities( Phase.class );
            return phases.isEmpty()
                    ? null
                    : phases.get( new Random( 13 ).nextInt( phases.size() ) );
        }

        public Place getAnyActualPlace() {
            List<Place> actualPlaces = getQueryService().listActualEntities( Place.class );
            return actualPlaces.isEmpty()
                    ? null
                    : actualPlaces.get( new Random( 13 ).nextInt( actualPlaces.size() ) );
        }

        public Role getAnyRole() {
            List<Role> roles = getQueryService().listKnownEntities( Role.class );
            return roles.isEmpty()
                    ? null
                    : roles.get( new Random( 13 ).nextInt( roles.size() ) );
        }

        public TransmissionMedium getAnyMedium() {
            List<TransmissionMedium> media = getQueryService().listKnownEntities( TransmissionMedium.class );
            return media.isEmpty()
                    ? null
                    : media.get( new Random( 13 ).nextInt( media.size() ) );
        }

        public InfoProduct getAnyInfoProduct() {
            List<InfoProduct> infoProducts = getQueryService().listKnownEntities( InfoProduct.class );
            return infoProducts.isEmpty()
                    ? null
                    : infoProducts.get( new Random( 13 ).nextInt( infoProducts.size() ) );
        }

        public InfoFormat getAnyInfoFormat() {
            List<InfoFormat> formats = getQueryService().listKnownEntities( InfoFormat.class );
            return formats.isEmpty()
                    ? null
                    : formats.get( new Random( 13 ).nextInt( formats.size() ) );
        }

        public RFISurvey getUnknownRFISurvey() {
            return RFISurvey.UNKNOWN;
        }

        public Feedback getUnknownFeedback() {
            return Feedback.UNKNOWN;
        }

        public Requirement getUnknownRequirement() {
            return Requirement.UNKNOWN;
        }

    }


}
