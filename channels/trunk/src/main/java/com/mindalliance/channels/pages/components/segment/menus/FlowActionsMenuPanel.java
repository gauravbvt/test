package com.mindalliance.channels.pages.components.segment.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.commands.AddIntermediate;
import com.mindalliance.channels.command.commands.AddUserIssue;
import com.mindalliance.channels.command.commands.BreakUpFlow;
import com.mindalliance.channels.command.commands.CopyFlow;
import com.mindalliance.channels.command.commands.DisconnectFlow;
import com.mindalliance.channels.command.commands.DuplicateFlow;
import com.mindalliance.channels.command.commands.PasteAttachment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.pages.components.menus.CommandWrapper;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
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
    /**
     * Whether flow viewed as send.
     */
    private boolean isSend;
    /**
     * Whether flow panel is collapsed.
     */
    private boolean isCollapsed;

    public FlowActionsMenuPanel(
            String s,
            IModel<? extends Flow> model,
            boolean isSend,
            boolean isCollapsed ) {
        super( s, "More", model, null );
        this.isSend = isSend;
        this.isCollapsed = isCollapsed;
        doInit();
    }

    protected void init() {
        // do nothing
    }

    private void doInit() {
        super.init();
    }

    /**
     * {@inheritDoc}
     */
    public List<Component> getMenuItems() throws CommandException {
        List<Component> menuItems = new ArrayList<Component>();
        // Show/hide details
        if ( isCollapsed ) {
            AjaxFallbackLink showLink = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Expanded, getFlow() ) );
                }
            };
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Show details" ), showLink ) );
        } else {
            AjaxFallbackLink hideLink = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Collapsed, getFlow() ) );
                }
            };
            menuItems.add( new LinkMenuItem( "menuItem", new Model<String>( "Hide details" ), hideLink ) );
        }
        // View flow eois
        if ( !isCollapsed ) {
            AjaxFallbackLink eoisLink = new AjaxFallbackLink( "link" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.AspectViewed, getFlow(), "eois" ) );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Show elements" ),
                    eoisLink ) );
        }
        // View flow commitments
        if ( getFlow().isSharing() ) {
            AjaxFallbackLink commitmentsLink = new AjaxFallbackLink( "link" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.AspectViewed, getFlow(), "commitments" ) );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Show commitments" ),
                    commitmentsLink ) );
        }
        // View flow failure impacts
        if ( getFlow().isSharing() ) {
            AjaxFallbackLink failureImpactsLink = new AjaxFallbackLink( "link" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.AspectViewed, getFlow(), "failure" ) );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Show failure impacts" ),
                    failureImpactsLink ) );
        }
        // Undo and redo
        menuItems.add( this.getUndoMenuItem( "menuItem" ) );
        menuItems.add( this.getRedoMenuItem( "menuItem" ) );
        String disablement =
                ( isLockedByUser( getFlow() ) || isCollapsed && getLockOwner( getFlow() ) == null )
                        ? null
                        : ( getCommander().isTimedOut() || !isCollapsed && getLockOwner( getFlow() ) == null )
                        ? "Timed out"
                        : ( "(Edited by " + getLockOwner( getFlow() ) + ")" );
        if ( disablement == null ) {
            // Commands
            menuItems.addAll( getCommandMenuItems( "menuItem", getCommandWrappers() ) );
        } else {
            // Commands disabled
            Label label = new Label( "menuItem", disablement );
            label.add( new AttributeModifier( "class", true, new Model<String>( "disabled" ) ) );
            menuItems.add( label );
        }
        return menuItems;
    }

    private List<CommandWrapper> getCommandWrappers() {
        List<CommandWrapper> commandWrappers = new ArrayList<CommandWrapper>();
        final Flow flow = getFlow();
        commandWrappers.add( new CommandWrapper( new CopyFlow( getFlow(), getPart() ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        if ( ( isSend && getFlow().getTarget().isPart() )
                || ( !isSend && getFlow().getSource().isPart() ) ) {
            commandWrappers.add( new CommandWrapper( new DuplicateFlow( flow, isSend ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
        }
        commandWrappers.add( new CommandWrapper( new AddUserIssue( flow ) ) {
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        } );
        if ( !isCollapsed )
            commandWrappers.add( new CommandWrapper( new PasteAttachment( getFlow() ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
        if ( !isCollapsed )
            commandWrappers.add( new CommandWrapper( new DisconnectFlow( flow ), CONFIRM ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
        if ( !isCollapsed && flow.isSharing() )
            commandWrappers.add( new CommandWrapper( new AddIntermediate( flow ) ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
        if ( !isCollapsed && flow.isSharing() )
            commandWrappers.add( new CommandWrapper( new BreakUpFlow( flow ), CONFIRM ) {
                public void onExecuted( AjaxRequestTarget target, Change change ) {
                    update( target, change );
                }
            } );
        return commandWrappers;
    }

    private Part getPart() {
        if ( isSend ) {
            return (Part) getFlow().getSource();
        } else {
            return (Part) getFlow().getTarget();
        }
    }

    private Flow getFlow() {
        return (Flow) getModel().getObject();
    }
}
