package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.CommandException;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * An abstract base class for menu panel.
 */
public abstract class MenuPanel extends AbstractCommandablePanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( MenuPanel.class );
    /**
     * Confirmation requested.
     */
    protected static final boolean CONFIRM = true;

    /**
     * Title.
     */
    private String title;
    /**
     * Cached menu items.
     */
    private List<Component> menuItems;

    protected MenuPanel( String s, String title, IModel<? extends Identifiable> model ) {
        this( s, title, model, null );
    }

    protected MenuPanel( String s, String title, IModel<? extends Identifiable> model,
                         Set<Long> expansions ) {
        super( s, model, expansions );
        this.title = title;
        init();
    }

    /**
     * Initialize menu components.
     */
    protected void init() {
        menuItems = null;
        setOutputMarkupId( true );
        add(
                new Label( "menu-title", new Model<String>( title ) ),
                new ListView<Component>(
                        "items", allMenuItems() ) {
                    @Override
                    protected void populateItem( ListItem<Component> item ) {
                        item.add( item.getModelObject() );
                    }
                } );
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        adjustMenuItems();
    }

    private void adjustMenuItems() {
        try {
            int maxItemLength = maxItemLength();
            for ( Component menuItem : allMenuItems() ) {
                if ( menuItem instanceof LinkMenuItem ) {
                    ( (LinkMenuItem) menuItem ).padName( maxItemLength );
                }
            }
        } catch ( CommandException e ) {
            LOG.warn( "Fail to adjust menu items", e );
        }

    }


    private int maxItemLength() throws CommandException {
        int maxLength = 0;
        for ( Component menuItem : allMenuItems() ) {
            if ( menuItem instanceof LinkMenuItem ) {
                LinkMenuItem linkMenuItem = (LinkMenuItem) menuItem;
                String name = linkMenuItem.getName();
                maxLength = Math.max( maxLength, name.length() );
            }
        }
        return maxLength;
    }

    protected List<Component> allMenuItems() {
        if ( menuItems == null ) {
            try {
                menuItems = getMenuItems();
            } catch ( CommandException e ) {
                LoggerFactory.getLogger( getClass() ).warn( "Failed to get menu items", e );
                return new ArrayList<Component>(  );
            }
        }
        return menuItems;
    }

    /**
     * Get menu items.
     *
     * @return a list of components
     * @throws CommandException if fails to get menu items
     */
    public abstract List<Component> getMenuItems() throws CommandException;

    /**
     * Whether the menu is empty.
     *
     * @return a boolean
     */
    public boolean isEmpty() {
        List<Component> items = null;
        items = allMenuItems();
        return items != null && items.isEmpty();
    }

    /**
     * Make menu items linking to model object pages.
     *
     * @param id         id of the menu item
     * @param moWrappers model object wrappers (model object + link text)
     * @return a list of components
     */
    protected List<Component> getModelObjectMenuItems( String id,
                                                       List<ModelObjectWrapper> moWrappers ) {
        List<Component> menuItems = new ArrayList<Component>();
        for ( final ModelObjectWrapper moWrapper : moWrappers )
            menuItems.add(
                    new LinkMenuItem(
                            id,
                            new PropertyModel<String>( moWrapper, "title" ),
                            new AjaxFallbackLink( "link" ) {
                                @Override
                                public void onClick( AjaxRequestTarget target ) {
                                    update(
                                            target,
                                            new Change( Change.Type.Expanded, moWrapper.getModelObject() ) );
                                }
                            } ) );

        return menuItems;
    }

    /**
     * Make an undo menu item.
     *
     * @param id the id of the menu item.
     * @return a menu item component
     */
    protected Component getUndoMenuItem( String id ) {
        return getCommander().canUndo() ?
                new LinkMenuItem(
                        id,
                        new Model<String>( getCommander().getUndoTitle() ),
                        new AjaxFallbackLink( "link" ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                update( target, getCommander().undo() );
                            }
                        } )

                : new Label( id, "Undo" )
                .add( new AttributeModifier( "class", true, new Model<String>( "disabled" ) ) );
    }

    /**
     * Make a redo menu item.
     *
     * @param id the id of the menu item.
     * @return a menu item component
     */
    protected Component getRedoMenuItem( String id ) {
        return getCommander().canRedo() ?
                new LinkMenuItem(
                        id,
                        new Model<String>( getCommander().getRedoTitle() ),
                        new AjaxFallbackLink( "link" ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                update(
                                        target, getCommander().redo() );
                            }
                        } )

                : new Label( id, "Redo" )
                .add( new AttributeModifier( "class", true, new Model<String>( "disabled" ) ) );
    }

    /**
     * Create a send message menu item.
     *
     * @param id the id
     * @return the component
     */
    protected Component getSendMessageMenuItem( String id ) {
        final Identifiable identifiable = getModel().getObject();
        return identifiable != null && identifiable instanceof ModelObject ?
                new LinkMenuItem(
                        id,
                        new Model<String>( "Send message" ),
                        new AjaxFallbackLink( "link" ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                update( target,
                                        new Change( Change.Type.Communicated, identifiable ) );
                            }
                        } )

                : new Label( id, "Send message" )
                .add( new AttributeModifier( "class", true, new Model<String>( "disabled" ) ) );
    }

    /**
     * Make menu items from commands.
     *
     * @param id       id of the menu item
     * @param commands a list of wrapped commands
     * @return a list of menu item components
     */
    protected List<Component> getCommandMenuItems( String id, List<CommandWrapper> commands ) {

        List<Component> menuItems = new ArrayList<Component>();

        Commander commander = getCommander();

        for ( final CommandWrapper commandWrapper : commands )
            try {
                final Command command = commandWrapper.getCommand();
                String label = command.getLabel( commander );
                menuItems.add(
                        commander.canDo( command ) ? new LinkMenuItem(
                                id,
                                new Model<String>( label ),
                                new ConfirmedAjaxFallbackLink(
                                        "link", commandWrapper.isConfirm() ? "Are you sure?" : null ) {
                                    @Override
                                    public void onClick( AjaxRequestTarget target ) {
                                        commandWrapper.onExecuted(
                                                target, getCommander().doCommand( command ) );
                                    }
                                } )

                                : new Label( id, new Model<String>( label ) )
                                .add( new AttributeModifier(
                                        "class", true, new Model<String>( "disabled" ) ) ) );

            } catch ( CommandException e ) {
                LoggerFactory.getLogger( getClass() ).warn( "Unable to get command label", e );
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
