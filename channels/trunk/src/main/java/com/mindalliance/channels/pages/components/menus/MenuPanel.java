package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

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
     * A model which object the menu is about..
     */
    private IModel<? extends Identifiable> model;

    public MenuPanel( String s, IModel<? extends Identifiable> model ) {
        super( s, model );
        this.model = model;
    }

    public IModel<? extends Identifiable> getModel() {
        return model;
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

    protected Commander getCommander() {
        return Project.getProject().getCommander();
    }

    /**
     * Make menu items linking to model object pages.
     * @param id  id of the menu item
     * @param moWrappers model object wrappers (model object + link text)
     * @return a list of components
     */
    protected List<Component> getModelObjectMenuItems(
            String id,
            List<ModelObjectWrapper> moWrappers ) {
        List<Component> menuItems = new ArrayList<Component>();
        for ( ModelObjectWrapper moWrapper : moWrappers ) {
            ModelObjectLink link = makeLink(
                    "link",
                    new Model<ModelObject>( moWrapper.getModelObject() ) );
            menuItems.add( new LinkMenuItem(
                    id,
                    new PropertyModel<String>( moWrapper, "title" ),
                    link ) );
        }
        return menuItems;
    }

    private ModelObjectLink makeLink( String id,
                                      final IModel<ModelObject> model ) {
        ModelObjectLink link = new ModelObjectLink(
                id,
                new Model<ModelObject>( model.getObject() ) );
        link.setOutputMarkupId( true );
        return link;
    }

    /**
     * Make menu items from commands.
     *
     * @param id              id of the menu item
     * @param commandWrappers a list of wrapped commands
     * @return a list of menu item components
     */
    protected List<Component> getCommandMenuItems(
            String id,
            List<CommandWrapper> commandWrappers ) {
        List<Component> menuItems = new ArrayList<Component>();
        for ( final CommandWrapper commandWrapper : commandWrappers ) {
            final Command command = commandWrapper.getCommand();
            if ( getCommander().canDo( command ) ) {
                Link link = new AjaxFallbackLink( "link" ) {
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
                        new PropertyModel<String>( command, "title" ),
                        link ) );
            } else {
                Label label = new Label( id, new PropertyModel<String>( command, "title" ) );
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
    protected static class ModelObjectWrapper implements Serializable {
        /**
         * Model object.
         */
        private ModelObject modelObject;
        /**
         * Title.
         */
        private String title;

        protected ModelObjectWrapper( String title, ModelObject modelObject ) {
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
