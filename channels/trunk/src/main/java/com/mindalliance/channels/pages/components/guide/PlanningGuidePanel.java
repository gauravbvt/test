package com.mindalliance.channels.pages.components.guide;

import com.google.code.jqwicket.ui.accordion.AccordionWebMarkupContainer;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.guide.Activity;
import com.mindalliance.channels.guide.ActivityChange;
import com.mindalliance.channels.guide.ActivityGroup;
import com.mindalliance.channels.guide.ActivityRef;
import com.mindalliance.channels.guide.Guide;
import com.mindalliance.channels.guide.GuideReader;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import info.bliki.wiki.model.WikiModel;
import org.apache.wicket.Page;
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
        accordion = new AccordionWebMarkupContainer( "accordion" );
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
        docContainer.add( new Label( "activityInGroup",
                selectedActivity == null
                        ? ""
                        : ( selectedGroup.getName() + " - " + selectedActivity.getName() ) ) );
        docContainer.add( getActionContainer() );
        docContainer.add( getDescriptionLabel() );
        docContainer.add( getDoNextContainer() );
        addOrReplace( docContainer );
    }

    private WebMarkupContainer getActionContainer() {
        List<ActivityChange> activityChanges = getActivityChanges();
        WebMarkupContainer actionContainer = new WebMarkupContainer( "actionContainer" );
        ListView<ActivityChange> changesListView = new ListView<ActivityChange>(
                "actions",
                activityChanges
        ) {
            @Override
            protected void populateItem( ListItem<ActivityChange> item ) {
                final ActivityChange activityChange = item.getModelObject();
                final Change change = makeChange( activityChange );
                AjaxLink<String> doItLink = new AjaxLink<String>( "actionLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        if ( change != null )
                            update( target, change );
                    }
                };
                doItLink.add( new Label(
                        "action",
                        activityChange == null
                                ? ""
                                : activityChange.getAction() ) );
                item.add( doItLink );
                item.setVisible( change != null );
            }
        };
        actionContainer.add( changesListView );
        actionContainer.setVisible( !activityChanges.isEmpty() );
        return actionContainer;
    }

    private List<ActivityChange> getActivityChanges() {
        return selectedActivity == null
                ? new ArrayList<ActivityChange>()
                : selectedActivity.getActivityChanges();
    }

    private Change makeChange( ActivityChange activityChange ) {
        if ( activityChange == null ) return null;
        Change change;
        Change.Type type = Change.Type.valueOf( activityChange.getChangeType() );
        if ( activityChange.getSubjectId() != null ) {
            change = new Change( type, activityChange.getSubjectId() );
        } else {
            Page planPage = findPage();
            Identifiable identifiable = (Identifiable) ChannelsUtils.getProperty(
                    planPage,
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

    private Label getDescriptionLabel() {
        Label label = new Label( "description", getDescriptionHTML() );
        label.setEscapeModelStrings( false );
        return label;
    }

    private String getDescriptionHTML() {
        String serverUrl = guideReader.getServerUrl();
        String helpUrl = serverUrl
                + ( serverUrl.endsWith( "/" ) ? "" : "/" )
                + "doc/channels_user_guide/";
        String wikimedia = selectedActivity != null
                ? selectedActivity.getDescription()
                : guide.getDescription();
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
}
