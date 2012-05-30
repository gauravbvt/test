package com.mindalliance.channels.pages.components.guide;

import com.google.code.jqwicket.ui.accordion.AccordionOptions;
import com.google.code.jqwicket.ui.accordion.AccordionWebMarkupContainer;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.guide.Activity;
import com.mindalliance.channels.guide.ActivityChange;
import com.mindalliance.channels.guide.ActivityGroup;
import com.mindalliance.channels.guide.ActivityRef;
import com.mindalliance.channels.guide.ActivityScript;
import com.mindalliance.channels.guide.ActivityStep;
import com.mindalliance.channels.guide.Guide;
import com.mindalliance.channels.guide.GuideReader;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import info.bliki.wiki.model.WikiModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

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
    private AccordionWebMarkupContainer accordion;
    private WebMarkupContainer docContainer;

    public PlanningGuidePanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        guide = guideReader.getGuide();
        addGuideName();
        addHideImage();
        addGuideAccordion();
        addDoc();
    }

    private void addGuideName() {
        add( new Label( "guideName", guide.getName() ) );
    }


    private void addHideImage() {
        AjaxFallbackLink hideSocialLink = new AjaxFallbackLink( "hideGuide" ) {
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Collapsed, Channels.GUIDE_ID );
                change.setMessage( "To re-open, select \"Guide\" in the top \"Show\" menu." );
                update( target, change );
            }
        };
        add( hideSocialLink );
    }

    private void addGuideAccordion() {
        AccordionOptions options = new AccordionOptions();
        // options.setCssResourceReferences( new CssResourceReference( getClass(), "res/guide.css" ) );
        accordion = new AccordionWebMarkupContainer( "accordion", options );
        accordion.add( new ListView<ActivityGroup>(
                "groups",
                guide.getActivityGroups() ) {
            protected void populateItem( ListItem<ActivityGroup> groupItem ) {
                final ActivityGroup group = groupItem.getModelObject();
                groupItem.add( new Label( "title", group.getName() ) );
                ListView<Activity> activityList = new ListView<Activity>(
                        "activities",
                        group.getActivities()
                ) {
                    @Override
                    protected void populateItem( ListItem<Activity> activityItem ) {
                        final Activity activity = activityItem.getModelObject();
                        AjaxLink<String> activityLink = new AjaxLink<String>( "activityLink" ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                Change change = new Change( Change.Type.Selected, Channels.GUIDE_ID );
                                change.setProperty( "activity" );
                                change.addQualifier( "activity", activity );
                                change.addQualifier( "group", group );
                                update( target, change );
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
        add( accordion );
    }

    private void addDoc() {
        docContainer = new WebMarkupContainer( "doc" );
        docContainer.setOutputMarkupId( true );
        docContainer.add( getActivityDoc() );
        docContainer.add( getGuideDoc() );
        addOrReplace( docContainer );
    }

    private WebMarkupContainer getActivityDoc() {
        WebMarkupContainer activityDoc = new WebMarkupContainer( "activityDoc" );
        activityDoc.add( new Label( "activityInGroup",
                selectedActivity == null
                        ? ""
                        : ( selectedGroup.getName() + " - " + selectedActivity.getName() ) ) );
        activityDoc.add( getStepsList() );
        activityDoc.add( getDoNextContainer() );
        activityDoc.setVisible( selectedActivity != null );
        return activityDoc;
    }

    private Label getGuideDoc() {
        Label label = new Label( "guideDoc", wikimediaToHtml(
                selectedActivity == null
                        ? guide.getDescription()
                        : "" ) );
        label.setEscapeModelStrings( false );
        label.setVisible( selectedActivity == null);
        return label;
    }

    private ListView<ActivityStep> getStepsList() {
        List<ActivityStep> activitySteps = getActivitySteps();
        ListView<ActivityStep> stepsListView = new ListView<ActivityStep>(
                "steps",
                activitySteps
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
            return getPlanPage();
        } else {
            return (Updatable) ChannelsUtils.getProperty( getPlanPage(), updatableTargetPath, null );
        }
    }

    private List<ActivityStep> getActivitySteps() {
        return selectedActivity == null
                ? new ArrayList<ActivityStep>()
                : selectedActivity.getActivitySteps();
    }

    private Change makeChange( ActivityChange activityChange ) {
        if ( activityChange == null ) return null;
        Change change;
        Change.Type type = Change.Type.valueOf( activityChange.getChangeType() );
        if ( activityChange.getSubjectId() != null ) {
            change = new Change( type, activityChange.getSubjectId() );
        } else {
            Identifiable identifiable = (Identifiable) ChannelsUtils.getProperty(
                    this,
                    activityChange.getSubjectPath(),
                    null );
            if ( identifiable == null )
                return null;
            else
                change = new Change( type, identifiable );
        }
        change.setProperty( activityChange.getProperty() );
        return change;
    }

    private Label getDescriptionLabel( ActivityStep step ) {
        Label label = new Label( "description", wikimediaToHtml( step.getDescription() ) );
        label.setEscapeModelStrings( false );
        return label;
    }

    private String wikimediaToHtml( String wikimedia ) {
        String serverUrl = guideReader.getServerUrl();
        String helpUrl = serverUrl
                + ( serverUrl.endsWith( "/" ) ? "" : "/" )
                + "doc/channels_user_guide/";
        WikiModel wikiModel = new WikiModel( helpUrl + "${image}", helpUrl + "${title}" );
        String html = wikiModel.render( wikimedia.trim() );
        html = html.replaceAll( "<a ", "<a target='_blank' " );
        return html;
    }

    private WebMarkupContainer getDoNextContainer() {
        List<ActivityRef> activityRefs = getNextActivityRefs();
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
                        selectedActivity = nextActivity;
                        addDoc();
                        target.add( docContainer );
                    }
                };
                String labelString = ( nextGroup == null ? "???" : nextGroup.getName() )
                        + " - "
                        + ( nextActivity == null ? "???" : nextActivity.getName() );
                Label doNextLabel = new Label( "doNextText", labelString );
                nextLink.add( doNextLabel );
                item.add( nextLink );
            }
        };
        doNextContainer.add( doNextListView );
        return doNextContainer;
    }

    private List<ActivityRef> getNextActivityRefs() {
        if ( selectedActivity != null ) {
            return selectedActivity.getNextActivities();
        } else {
            return new ArrayList<ActivityRef>();
        }
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change ) {
        addDoc();
        target.add( docContainer );
    }

    @Override
    public void changed( Change change ) {
        if ( change.isSelected() && change.getId() == Channels.GUIDE_ID ) {
            selectedGroup = (ActivityGroup) change.getQualifier( "group" );
            selectedActivity = (Activity) change.getQualifier( "activity" );
        } else {
            super.changed( change );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected() && change.getId() == Channels.GUIDE_ID ) {
            addDoc();
            target.add( docContainer );
        } else {
            super.updateWith( target, change, updated );
        }
    }

    // Script support

    public PlanPage getPlanPage() {
        return findParent( PlanPage.class );
    }

}
