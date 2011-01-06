package com.mindalliance.channels.pages.components.segment.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.commands.AddIntermediate;
import com.mindalliance.channels.command.commands.AddUserIssue;
import com.mindalliance.channels.command.commands.BreakUpFlow;
import com.mindalliance.channels.command.commands.CopyFlow;
import com.mindalliance.channels.command.commands.DisconnectFlow;
import com.mindalliance.channels.command.commands.DuplicateFlow;
import com.mindalliance.channels.command.commands.PasteAttachment;
import com.mindalliance.channels.command.commands.RemoveCapability;
import com.mindalliance.channels.command.commands.RemoveNeed;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.pages.components.menus.CommandWrapper;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 11, 2009
 * Time: 4:35:49 PM
 */
public class FlowActionsMenuPanel extends MenuPanel {

    /** Whether flow viewed as send. */
    private boolean isSend;

    /** Whether flow panel is collapsed. */
    private boolean isCollapsed;

    public FlowActionsMenuPanel( String s, IModel<? extends Flow> model, boolean isSend,
                                 boolean isCollapsed ) {
        super( s, "More", model, null );
        this.isSend = isSend;
        this.isCollapsed = isCollapsed;
        doInit();
    }

    @Override
    protected void init() {
        // do nothing
    }

    private void doInit() {
        super.init();
    }

    /** {@inheritDoc} */
    @Override
    public List<Component> getMenuItems() {

        synchronized ( getCommander() ) {
            final Flow flow = getFlow();
            List<Component> menuItems = new ArrayList<Component>();

            // Show/hide details
            menuItems.add(
                new LinkMenuItem(
                    "menuItem",
                    new Model<String>( isCollapsed ? "Show details" : "Hide details" ),
                    new AjaxFallbackLink( "link" ) {
                        @Override
                        public void onClick( AjaxRequestTarget target ) {
                            update(
                                target, new Change(
                                isCollapsed ? Change.Type.Expanded : Change.Type.Collapsed,
                                flow ) );
                        }
                    } ) );

            // Send message
            menuItems.add( getSendMessageMenuItem( "menuItem" ) );

            // View flow eois
            if ( !isCollapsed )
                menuItems.add(
                    new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Show elements" ),
                        new AjaxFallbackLink( "link" ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                update(
                                    target, new Change(
                                    Change.Type.AspectViewed, flow, "eois" ) );
                            }
                        } ) );

            if ( flow.isSharing() ) {
                menuItems.add(
                    new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Show commitments" ),
                        new AjaxFallbackLink( "link" ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                update(
                                    target, new Change(
                                    Change.Type.AspectViewed, flow, "commitments" ) );
                            }
                        } ) );

                menuItems.add(
                    new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Show failure impacts" ),
                        new AjaxFallbackLink( "link" ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                update(
                                    target,
                                    new Change( Change.Type.AspectViewed, flow, "failure" ) );
                            }
                        } ) );

                if ( !flow.getEois().isEmpty() )
                    menuItems.add(
                        new LinkMenuItem(
                            "menuItem",
                            new Model<String>( "Show dissemination" ),
                            new AjaxFallbackLink( "link" ) {
                                @Override
                                public void onClick( AjaxRequestTarget target ) {
                                    Change change = new Change(
                                        Change.Type.AspectViewed, flow, "dissemination" );
                                    change.addQualifier( "show", isSend ? "targets" : "sources" );
                                    update( target, change );
                                }
                            } ) );
            }

            // Undo and redo
            menuItems.add( getUndoMenuItem( "menuItem" ) );
            menuItems.add( getRedoMenuItem( "menuItem" ) );

            if ( getCommander().isTimedOut() )
                menuItems.add( timeOutLabel( "menuItem" ) );
            else if ( isLockedByUser( getFlow() ) || getLockOwner( flow ) == null )
                menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers( flow ) ) );
            else
                menuItems.add( editedByLabel( "menuItem", flow, getLockOwner( flow ) ) );

            return menuItems;
        }    }

    private List<CommandWrapper> getCommandWrappers( Flow flow ) {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();

        commandWrappers.add( wrap( new CopyFlow( flow, getPart() ), false ) );

        if ( !isCollapsed ) {
            if ( flow.isSharing() )
                commandWrappers.add( wrap( new DuplicateFlow( flow, isSend ), false ) );

            commandWrappers.add( wrap( new AddUserIssue( flow ), false ) );

            commandWrappers.add( wrap( new PasteAttachment( flow ), false ) );

            commandWrappers.add( wrap(
                  flow.isSharing() ? new DisconnectFlow( flow )
                                   : flow.isNeed()    ? new RemoveNeed( flow )
                                                      : new RemoveCapability( flow ),
                  CONFIRM ) );

            if ( flow.isSharing() ) {
                commandWrappers.add( wrap( new AddIntermediate( flow ), false ) );
                commandWrappers.add( wrap( new BreakUpFlow( flow ), CONFIRM ) );
            }
        }

        return commandWrappers;
    }

    private CommandWrapper wrap( final Command command, boolean confirm ) {
        return new CommandWrapper( command, confirm ) {
            @Override
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        };
    }

    private Part getPart() {
        return isSend ? (Part) getFlow().getSource()
                      : (Part) getFlow().getTarget();
    }

    private Flow getFlow() {
        return (Flow) getModel().getObject();
    }
}
