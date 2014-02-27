package com.mindalliance.channels.pages.components.segment.menus;

import com.mindalliance.channels.core.command.commands.AddIntermediate;
import com.mindalliance.channels.core.command.commands.AddUserIssue;
import com.mindalliance.channels.core.command.commands.BreakUpFlow;
import com.mindalliance.channels.core.command.commands.CopyFlow;
import com.mindalliance.channels.core.command.commands.DisconnectFlow;
import com.mindalliance.channels.core.command.commands.DuplicateFlow;
import com.mindalliance.channels.core.command.commands.PasteAttachment;
import com.mindalliance.channels.core.command.commands.RemoveCapability;
import com.mindalliance.channels.core.command.commands.RemoveNeed;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.components.menus.CommandWrapper;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.model.IModel;

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

    /**
     * Whether flow viewed as send.
     */
    private boolean isSend;

    public FlowActionsMenuPanel( String s, IModel<? extends Flow> model, boolean isSend ) {
        super( s, "Actions", model );
        this.isSend = isSend;
        doInit();
    }

    @Override
    public String getHelpTopicId() {
        return "actions-flow";
    }

    @Override
    protected void init() {
        // do nothing
    }

    private void doInit() {
        super.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LinkMenuItem> getMenuItems() {

        synchronized ( getCommander() ) {
            final Flow flow = getFlow();
            List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();

            // Undo and redo
                menuItems.add( getUndoMenuItem( "menuItem" ) );
                menuItems.add( getRedoMenuItem( "menuItem" ) );

            if ( getCommander().isTimedOut( getUser().getUsername() ) ) {
                menuItems.add( timeOutLinkMenuItem( "menuItem" ) );
            } else if ( isLockedByUser( getFlow() ) || getLockOwner( flow ) == null )
                menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers( flow ) ) );
            else {
                menuItems.add( editedByLinkMenuItem( "menuItem", flow, getLockOwner( flow ) ) );
            }

            return menuItems;
        }
    }

    private Segment getSegment() {
        return getPart().getSegment();
    }

    private List<CommandWrapper> getCommandWrappers( Flow flow ) {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        if ( getPart() != null )
            commandWrappers.add( wrap( new CopyFlow( getUser().getUsername(), flow, getPart() ), false ) );

        if ( flow.isSharing() )
            commandWrappers.add( wrap( new DuplicateFlow( getUser().getUsername(), flow, isSend ), false ) );

        commandWrappers.add( wrap(
                flow.isSharing() ? new DisconnectFlow( getUser().getUsername(), flow )
                        : flow.isNeed() ? new RemoveNeed( getUser().getUsername(), flow )
                        : new RemoveCapability( getUser().getUsername(), flow ),
                CONFIRM ) );

        commandWrappers.add( wrap( new AddUserIssue( getUser().getUsername(), flow ), false ) );

        commandWrappers.add( wrap( new PasteAttachment( getUser().getUsername(), flow ), false ) );

        if ( flow.isSharing() ) {
            commandWrappers.add( wrap( new AddIntermediate( getUser().getUsername(), flow ), false ) );
            commandWrappers.add( wrap( new BreakUpFlow( getUser().getUsername(), flow ), CONFIRM ) );
        }

        return commandWrappers;
    }

    private Part getPart() {
        return isSend ? (Part) getFlow().getSource()
                : (Part) getFlow().getTarget();
    }

    private Flow getFlow() {
        return (Flow) getModel().getObject();
    }
}
