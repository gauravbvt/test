package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.util.PeekAheadIterator;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.social.model.ExecutedCommand;
import com.mindalliance.channels.social.services.ExecutedCommandService;
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
public class ExecutedCommandsListPanel extends AbstractSocialListPanel {

    @SpringBean
    private ExecutedCommandService executedCommandService;

    private static final int A_FEW = 7;
    private static final int MORE = 7;
    private int numberToShow = A_FEW;
    boolean allShown = false;
    private boolean othersOnly = false;
    private WebMarkupContainer planningEventsContainer;
    private AjaxFallbackLink showHideLink;
    private Label showHideLabel;
    private WebMarkupContainer noCommandContainer;
    private AjaxFallbackLink showAFew;
    private AjaxFallbackLink showMore;
    private Updatable updatable;
    private boolean showProfile;
    private Date whenLastRefreshed;

    public ExecutedCommandsListPanel( String id, Updatable updatable, boolean collapsible, boolean showProfile ) {
        super( id, collapsible );
        this.updatable = updatable;
        this.showProfile = showProfile;
        init();
    }

    protected void init() {
        super.init();
        addShowHideLink();
        addShowHideLabel();
        planningEventsContainer = new WebMarkupContainer( "executedCommandsContainer" );
        planningEventsContainer.setOutputMarkupId( true );
        add( planningEventsContainer );
        addExecutedCommands();
        addShowMore();
        addShowAFew();
        adjustComponents();
        whenLastRefreshed = new Date();
    }

    private void addShowHideLink() {
        showHideLink = new AjaxFallbackLink( "hideShowLink" ) {
            public void onClick( AjaxRequestTarget target ) {
                othersOnly = !othersOnly;
                addShowHideLabel();
                target.add( showHideLabel );
                addExecutedCommands();
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
        target.add( planningEventsContainer );
        target.add( showAFew );
        target.add( showMore );
        target.add( noCommandContainer );
    }

    private void adjustComponents() {
        List<ExecutedCommand> executedCommands = getExecutedCommands();
        makeVisible( noCommandContainer, executedCommands.isEmpty() );
        makeVisible( planningEventsContainer, !executedCommands.isEmpty() );
        makeVisible( showMore, !allShown );
        makeVisible( showAFew, executedCommands.size() > A_FEW );
    }

    private void addExecutedCommands() {
        List<ExecutedCommand> executedCommands = getExecutedCommands();
        ListView<ExecutedCommand> executedCommandListView = new ListView<ExecutedCommand>(
                "executedCommands",
                executedCommands ) {
            protected void populateItem( ListItem<ExecutedCommand> item ) {
                ExecutedCommand executedCommand = item.getModelObject();
                ExecutedCommandPanel executedCommandPanel = new ExecutedCommandPanel(
                        "executedCommand",
                        new Model<ExecutedCommand>( executedCommand ),
                        item.getIndex(),
                        showProfile,
                        updatable );
                item.add( executedCommandPanel );
            }
        };
        planningEventsContainer.addOrReplace( executedCommandListView );
        noCommandContainer = new WebMarkupContainer( "noCommands" );
        noCommandContainer.setOutputMarkupId( true );
        addOrReplace( noCommandContainer );
    }

    private void addShowMore() {
        showMore = new AjaxFallbackLink( "showMore" ) {
            public void onClick( AjaxRequestTarget target ) {
                numberToShow += MORE;
                addExecutedCommands();
                adjustComponents( target );
            }
        };
        showMore.setOutputMarkupId( true );
        planningEventsContainer.add( showMore );
    }

    private void addShowAFew() {
        showAFew = new AjaxFallbackLink( "showFew" ) {
            public void onClick( AjaxRequestTarget target ) {
                numberToShow = A_FEW;
                addExecutedCommands();
                adjustComponents( target );
            }
        };
        showAFew.setOutputMarkupId( true );
        planningEventsContainer.add( showAFew );
    }

    public List<ExecutedCommand> getExecutedCommands() {
        Plan plan = getPlan();
        List<ExecutedCommand> executedCommands = new ArrayList<ExecutedCommand>();
        PeekAheadIterator<ExecutedCommand> iterator = new PeekAheadIterator<ExecutedCommand>(
                executedCommandService.getExecutedCommands( getPlanCommunity() ) );
        while ( iterator.hasNext() && executedCommands.size() < numberToShow ) {
            ExecutedCommand executedCommand = iterator.next();
            if ( executedCommand != null ) {
                ExecutedCommand nextExecutedCommand = iterator.peek();
                if ( !( isOthersOnly() && executedCommand.getUsername().equals( getUsername() ) )
                        && !isFollwedByAnotherUpdate( executedCommand, nextExecutedCommand ) )
                    executedCommands.add( executedCommand );
            }
        }
        allShown = !iterator.hasNext();
        return executedCommands;
    }

    private boolean isFollwedByAnotherUpdate( ExecutedCommand executedCommand, ExecutedCommand nextExecutedCommand ) {
        if ( !executedCommand.isDone() ) return false;
        if ( !( executedCommand.isUpdate() ) ) return false;
        if ( nextExecutedCommand == null ) return false;
        if ( !nextExecutedCommand.isDone() ) return false;
        if ( !( nextExecutedCommand.isUpdate() ) ) return false;
        if ( executedCommand.getChangeId() != nextExecutedCommand.getChangeId() ) return false;
        return true;
    }

    public void refresh( AjaxRequestTarget target, Change change ) {
        Date whenLastChanged = executedCommandService.getWhenLastChanged( getPlanCommunity().getUri() );
        if ( whenLastChanged != null && whenLastChanged.after( whenLastRefreshed ) ) {
            addExecutedCommands();
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

}
