package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.CommandEvent;
import com.mindalliance.channels.social.PlanningEventService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    private boolean othersOnly = true;
    private boolean allShown;
    private WebMarkupContainer planningEventsContainer;

    private WebMarkupContainer noCommandContainer;
    private AjaxFallbackLink showAFew;
    private AjaxFallbackLink showMore;
    private Updatable updatable;

    public CommandEventListPanel( String id, Updatable updatable ) {
        super( id );
        this.updatable = updatable;
        init();
    }

    private void init() {
        addOthersOnly();
        planningEventsContainer = new WebMarkupContainer( "planningEventsContainer" );
        planningEventsContainer.setOutputMarkupId( true );
        add( planningEventsContainer );
        addCommandEvents();
        addShowMore();
        addShowAFew();
        adjustComponents();
    }

    private void addOthersOnly() {
        CheckBox othersOnlyCheckBox = new CheckBox(
                "othersOnly",
                new PropertyModel<Boolean>( this, "othersOnly" ) );
        othersOnlyCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addCommandEvents();
                adjustComponents( target );
            }
        } );
        add( othersOnlyCheckBox );
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
        Iterator<CommandEvent> iterator = planningEventService.getCommandEvents();
        while ( iterator.hasNext() && commandEvents.size() < numberToShow ) {
            CommandEvent commandEvent = iterator.next();
            if ( commandEvent != null ) {
                if ( !( isOthersOnly() && commandEvent.getCommand().getUserName().equals( getUsername() ) )
                        && !isAnotherUpdate( commandEvent, commandEvents ) )
                    commandEvents.add( commandEvent );
            }
        }
        allShown = !iterator.hasNext();
        Collections.reverse( commandEvents );
        return commandEvents;
    }

    private boolean isAnotherUpdate( CommandEvent commandEvent, List<CommandEvent> commandEvents ) {
        if ( commandEvents.isEmpty() ) return false;
        if ( !commandEvent.isDone() ) return false;
        if ( !( commandEvent.getCommand() instanceof UpdateObject ) ) return false;
        CommandEvent priorCommandEvent = commandEvents.get( commandEvents.size() - 1 );
        if ( !priorCommandEvent.isDone() ) return false;
        if ( !( priorCommandEvent.getCommand() instanceof UpdateObject ) ) return false;
        if ( priorCommandEvent.getChange().getId() != commandEvent.getChange().getId() ) return false;
        return true;
    }

    public void refresh( AjaxRequestTarget target, Change change ) {
        addCommandEvents();
        adjustComponents( target );
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
