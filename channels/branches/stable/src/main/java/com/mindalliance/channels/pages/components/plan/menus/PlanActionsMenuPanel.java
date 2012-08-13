package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.commands.AddPart;
import com.mindalliance.channels.core.command.commands.AddSegment;
import com.mindalliance.channels.core.command.commands.AddUserIssue;
import com.mindalliance.channels.core.command.commands.DisconnectAndRemoveSegment;
import com.mindalliance.channels.core.command.commands.PasteAttachment;
import com.mindalliance.channels.core.command.commands.PastePart;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.components.menus.ActionMenuPanel;
import com.mindalliance.channels.pages.components.menus.CommandWrapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Segment menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 10, 2009
 * Time: 8:58:39 AM
 */
public class PlanActionsMenuPanel extends ActionMenuPanel {

    public PlanActionsMenuPanel( String s, IModel<? extends Segment> model, Set<Long> expansions ) {
        super( s, model, expansions );
    }


    @Override
    protected List<CommandWrapper> getCommandWrappers() {
        final Segment segment = getSegment();

        String userName = getUser().getUsername();
        List<CommandWrapper> menuItems = new ArrayList<CommandWrapper>();
        menuItems.addAll( Arrays.asList(
                newWrapper( new PastePart( getUser().getUsername(), segment ) ),
                newWrapper( new PasteAttachment( userName, segment ) ),
                newWrapper( new AddPart( userName, segment ) ),
                newWrapper( new AddUserIssue( userName, segment ) ),
                newWrapper( new AddSegment( userName ) ) ) );
        if ( getLockManager().isLockableByUser( getUser().getUsername(), segment ) ) {
            menuItems.add( new CommandWrapper( new DisconnectAndRemoveSegment( getUser().getUsername(), segment ), CONFIRM ) {
                @Override
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
        }
        return menuItems;
    }

    private CommandWrapper newWrapper( final Command command ) {
        return new CommandWrapper( command ) {
            @Override
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        };
    }

    private Segment getSegment() {
        return (Segment) getModel().getObject();
    }
}
