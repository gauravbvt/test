package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.CommandEvent;
import com.mindalliance.channels.social.PlanningEventService;
import com.mindalliance.channels.util.PeekAheadIterator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Planner commands panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 5, 2010
 * Time: 1:31:46 PM
 */
public class CommandEventListPanel extends AbstractUpdatablePanel {

    @SpringBean
    private PlanningEventService planningEventService;

    private static final int A_FEW = 7;
    private static final int MORE = 7;
    private int numberToShow = A_FEW;
    boolean allShown = false;
    private boolean othersOnly = true;
    private WebMarkupContainer planningEventsContainer;
    private AjaxFallbackLink showHideLink;
    private Label showHideLabel;
    private WebMarkupContainer noCommandContainer;
    private AjaxFallbackLink showAFew;
    private AjaxFallbackLink showMore;
    private Updatable updatable;
    private Date whenLastRefreshed;

    public CommandEventListPanel( String id, Updatable updatable ) {
        super( id );
        this.updatable = updatable;
        init();
    }

    private void init() {
        addHideSocial();
        addShowHideLink();
        addShowHideLabel();
        planningEventsContainer = new WebMarkupContainer( "planningEventsContainer" );
        planningEventsContainer.setOutputMarkupId( true );
        add( planningEventsContainer );
        addCommandEvents();
        addShowMore();
        addShowAFew();
        adjustComponents();
        whenLastRefreshed = new Date();
    }

    private void addHideSocial() {
        AjaxFallbackLink hideSocialLink = new AjaxFallbackLink( "hideAll" ) {
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Collapsed, Channels.SOCIAL_ID ) );
            }
        };
        add( hideSocialLink );
    }

    private void addShowHideLink() {
        showHideLink = new AjaxFallbackLink( "hideShowLink" ) {
            public void onClick( AjaxRequestTarget target ) {
                othersOnly = !othersOnly;
                addShowHideLabel();
                target.addComponent( showHideLabel );
                addCommandEvents();
                adjustComponents( target );
            }
        };
        add( showHideLink );
    }

    private void addShowHideLabel() {
        showHideLabel = new Label(
                "hideShow",
                othersOnly ? "show all activites" : "hide my activites" );
        showHideLabel.setOutputMarkupId( true );
        showHideLink.addOrReplace( showHideLabel );
    }

    private void adjustComponents( AjaxRequestTarget target ) {
        adjustComponents();
        target.addComponent( planningEventsContainer );
        target.addComponent( showAFew );
        target.addComponent( showMore );
        target.addComponent( noCommandContainer );
    }

    private void adjustComponents() {
        List<CommandEvent> commandEvents = getCommandEvents();
        makeVisible( noCommandContainer, commandEvents.isEmpty() );
        makeVisible( planningEventsContainer, !commandEvents.isEmpty() );
        showMore.setEnabled( !allShown );
        showAFew.setEnabled( commandEvents.size() > A_FEW );
    }

    private void addCommandEvents() {
        List<CommandEvent> commandEvents = getCommandEvents();
        ListView<CommandEvent> commandEventListView = new ListView<CommandEvent>(
                "commandEvents",
                commandEvents ) {
            protected void populateItem( ListItem<CommandEvent> item ) {
                CommandEvent commandEvent = item.getModelObject();
                CommandEventPanel commandEventPanel = new CommandEventPanel(
                        "commandEvent",
                        new Model<CommandEvent>( commandEvent ),
                        item.getIndex(),
                        updatable );
                item.add( commandEventPanel );
            }
        };
        planningEventsContainer.addOrReplace( commandEventListView );
        noCommandContainer = new WebMarkupContainer( "noCommands" );
        noCommandContainer.setOutputMarkupId( true );
        addOrReplace( noCommandContainer );
    }

    private void addShowMore() {
        showMore = new AjaxFallbackLink( "showMore" ) {
            public void onClick( AjaxRequestTarget target ) {
                numberToShow += MORE;
                addCommandEvents();
                adjustComponents( target );
            }
        };
        add( showMore );
    }

    private void addShowAFew() {
        showAFew = new AjaxFallbackLink( "showFew" ) {
            public void onClick( AjaxRequestTarget target ) {
                numberToShow = A_FEW;
                showAFew.setEnabled( false );
                addCommandEvents();
                adjustComponents( target );
            }
        };
        showAFew.setEnabled( false );
        add( showAFew );
    }

    public List<CommandEvent> getCommandEvents() {
        List<CommandEvent> commandEvents = new ArrayList<CommandEvent>();
        PeekAheadIterator<CommandEvent> iterator = new PeekAheadIterator<CommandEvent>(
                planningEventService.getCommandEvents() );
        while ( iterator.hasNext() && commandEvents.size() < numberToShow ) {
            CommandEvent commandEvent = iterator.next();
            if ( commandEvent != null ) {
                CommandEvent nextCommandEvent = iterator.peek();
                if ( !( isOthersOnly() && commandEvent.getCommand().getUserName().equals( getUsername() ) )
                        && !isFollwedByAnotherUpdate( commandEvent, nextCommandEvent ) )
                    commandEvents.add( commandEvent );
            }
        }
        allShown = !iterator.hasNext();
        return commandEvents;
    }

    private boolean isFollwedByAnotherUpdate( CommandEvent commandEvent, CommandEvent nextCommandEvent ) {
        if ( !commandEvent.isDone() ) return false;
        if ( !( commandEvent.getCommand() instanceof UpdateObject ) ) return false;
        if ( nextCommandEvent == null ) return false;
        if ( !nextCommandEvent.isDone() ) return false;
        if ( !( nextCommandEvent.getCommand() instanceof UpdateObject ) ) return false;
        if ( commandEvent.getChange().getId() != nextCommandEvent.getChange().getId() ) return false;
        return true;
    }

    public void refresh( AjaxRequestTarget target, Change change ) {
        if ( planningEventService.getWhenLastChanged().after( whenLastRefreshed ) ) {
            addCommandEvents();
            adjustComponents( target );
            whenLastRefreshed = new Date();
        }
    }

    public boolean isOthersOnly() {
        return othersOnly;
    }

    public void setOthersOnly( boolean othersOnly ) {
        this.othersOnly = othersOnly;
    }

    private String getUsername() {
        return User.current().getUsername();
    }

}
