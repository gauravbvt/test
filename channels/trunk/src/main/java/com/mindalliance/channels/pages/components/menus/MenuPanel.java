package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * An abstract base class for menu panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 10, 2009
 * Time: 8:29:34 AM
 */
public abstract class MenuPanel extends AbstractCommandablePanel {
    /**
     * Confirmation requested.
     */
    protected static boolean CONFIRM = true;

    /**
     * Title.
     */
    private String title;

    public MenuPanel(
            String s,
            String title,
            IModel<? extends Identifiable> model ) {
        this( s, title, model, null );
    }

    public MenuPanel(
            String s,
            String title,
            IModel<? extends Identifiable> model,
            Set<Long> expansions ) {
        super( s, model, expansions );
        this.title = title;
        init();
    }

    /**
     * {@inheritDoc}
     */
    protected void init() {
        setOutputMarkupId( true );
        add( new Label( "menu-title", new Model<String>( title ) ) );
        ListView<Component> menuItems = new ListView<Component>(
                "items",
                new PropertyModel<List<Component>>( this, "menuItems" ) ) {
            protected void populateItem( ListItem<Component> item ) {
                item.add( item.getModelObject() );
            }
        };
        add( menuItems );
    }

    /**
     * Get menu items.
     *
     * @return a list of components
     * @throws CommandException if fails to get menu items
     */
    public abstract List<Component> getMenuItems() throws CommandException;

    /**
     * Make menu items linking to model object pages.
     *
     * @param id         id of the menu item
     * @param moWrappers model object wrappers (model object + link text)
     * @return a list of components
     */
    protected List<Component> getModelObjectMenuItems(
            String id,
            List<ModelObjectWrapper> moWrappers ) {
        List<Component> menuItems = new ArrayList<Component>();
        for ( final ModelObjectWrapper moWrapper : moWrappers ) {
            Link link = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    update( target, new Change( Change.Type.Expanded, moWrapper.getModelObject() ) );
                }
            };
            menuItems.add( new LinkMenuItem(
                    id,
                    new PropertyModel<String>( moWrapper, "title" ),
                    link ) );
        }
        return menuItems;
    }

    /**
     * Make an undo menu item.
     *
     * @param id the id of the menu item.
     * @return a menu item component
     */
    protected Component getUndoMenuItem( String id ) {
        Component menuItem;
        if ( getCommander().canUndo() ) {
            Link link = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    try {
                        Change change = getCommander().undo();
                        update( target, change );
                    } catch ( CommandException e ) {
                        throw new WicketRuntimeException( "Failed to undo", e );
                    }
                }
            };
            menuItem = new LinkMenuItem(
                    id,
                    new Model<String>( getCommander().getUndoTitle() ), link );
        } else {
            Label undoLabel = new Label( id, "Undo" );
            undoLabel.add( new AttributeModifier(
                    "class",
                    true,
                    new Model<String>( "disabled" ) ) );
            menuItem = undoLabel;
        }
        return menuItem;
    }

    /**
     * Make a redo menu item.
     *
     * @param id the id of the menu item.
     * @return a menu item component
     */
    protected Component getRedoMenuItem( String id ) {
        Component menuItem;
        if ( getCommander().canRedo() ) {
            Link link = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    try {
                        Change change = getCommander().redo();
                        update( target, change );
                    } catch ( CommandException e ) {
                        throw new WicketRuntimeException( "Failed to redo", e );
                    }
                }
            };
            menuItem = new LinkMenuItem(
                    id,
                    new Model<String>( getCommander().getRedoTitle() ), link );
        } else {
            Label label = new Label( id, "Redo" );
            label.add( new AttributeModifier( "class", true, new Model<String>( "disabled" ) ) );
            menuItem = label;
        }
        return menuItem;
    }

    protected Component getSendMessageMenuItem( String id ) {
        Component menuItem;
        final Identifiable identifiable = getModel().getObject();
        if ( identifiable != null & identifiable instanceof ModelObject ) {
            Link link = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    Change change = new Change( Change.Type.Communicated, identifiable );
                    update( target, change );
                }
            };
            menuItem = new LinkMenuItem(
                    id,
                    new Model<String>( "Send message" ), link );
        } else {
            Label label = new Label( id, "Send message" );
            label.add( new AttributeModifier( "class", true, new Model<String>( "disabled" ) ) );
            menuItem = label;
        }
        return menuItem;
    }

    /**
     * Make menu items from commands.
     *
     * @param id              id of the menu item
     * @param commandWrappers a list of wrapped commands
     * @return a list of menu item components
     * @throws CommandException if can't build command label
     */
    protected List<Component> getCommandMenuItems(
            String id,
            List<CommandWrapper> commandWrappers ) throws CommandException {
        List<Component> menuItems = new ArrayList<Component>();
        for ( final CommandWrapper commandWrapper : commandWrappers ) {
            final Command command = commandWrapper.getCommand();
            if ( getCommander().canDo( command ) ) {
                Link link = new ConfirmedAjaxFallbackLink(
                        "link",
                        commandWrapper.isConfirm() ? "Are you sure?" : null ) {
                    public void onClick( AjaxRequestTarget target ) {
                        try {
                            Change change = getCommander().doCommand( command );
                            commandWrapper.onExecuted( target, change );
                        } catch ( CommandException e ) {
                            throw new WicketRuntimeException(
                                    "Failed to " + command.getTitle(),
                                    e );
                        }
                    }
                };
                menuItems.add( new LinkMenuItem( id,
                        new Model<String>( command.getLabel( getCommander() ) ),
                        link ) );
            } else {
                Label label = new Label( id, new Model<String>( command.getLabel( getCommander() ) ) );
                label.add( new AttributeModifier(
                        "class",
                        true,
                        new Model<String>( "disabled" ) ) );
                menuItems.add( label );
            }
        }
        return menuItems;
    }

    /**
     * A Model object wrapper.
     */
    public static class ModelObjectWrapper implements Serializable {
        /**
         * Model object.
         */
        private ModelObject modelObject;
        /**
         * Title.
         */
        private String title;

        public ModelObjectWrapper( String title, ModelObject modelObject ) {
            this.title = title;
            this.modelObject = modelObject;
        }

        public ModelObject getModelObject() {
            return modelObject;
        }

        public void setModelObject( ModelObject modelObject ) {
            this.modelObject = modelObject;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle( String title ) {
            this.title = title;
        }
    }


}
