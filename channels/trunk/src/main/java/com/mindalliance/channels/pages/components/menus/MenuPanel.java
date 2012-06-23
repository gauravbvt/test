package com.mindalliance.channels.pages.components.menus;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import org.apache.wicket.AttributeModifier;
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
    private List<LinkMenuItem> menuItems;

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
                new ListView<LinkMenuItem>(
                        "items", allMenuItems() ) {
                    @Override
                    protected void populateItem( ListItem<LinkMenuItem> item ) {
                        item.add( item.getModelObject() );
                    }
                } );
    }

    /*
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
    */
    protected List<LinkMenuItem> allMenuItems() {
        if ( menuItems == null ) {
            try {
                menuItems = getMenuItems();
            } catch ( CommandException e ) {
                LoggerFactory.getLogger( getClass() ).warn( "Failed to get menu items", e );
                return new ArrayList<LinkMenuItem>();
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
    public abstract List<LinkMenuItem> getMenuItems() throws CommandException;

    /**
     * Whether the menu is empty.
     *
     * @return a boolean
     */
    public boolean isEmpty() {
        List<LinkMenuItem> items = null;
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
    protected List<LinkMenuItem> getModelObjectMenuItems( String id,
                                                          List<ModelObjectWrapper> moWrappers ) {
        List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();
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
    protected LinkMenuItem getUndoMenuItem( String id ) {
        final boolean enabled = getCommander().canUndo( getUser().getUsername() );
        LinkMenuItem linkMenuItem = new LinkMenuItem(
                id,
                new Model<String>( getCommander().getUndoTitle( getUser().getUsername() ) ),
                new AjaxFallbackLink( "link" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        if ( enabled )
                            update( target, getCommander().undo( getUser().getUsername() ) );
                    }
                } );
        if ( !enabled ) {
            linkMenuItem.add( new AttributeModifier( "class", new Model<String>( "disabled" ) ) );
        }
        return linkMenuItem;
    }

    /**
     * Make a redo menu item.
     *
     * @param id the id of the menu item.
     * @return a menu item component
     */
    protected LinkMenuItem getRedoMenuItem( String id ) {
        final boolean enabled = getCommander().canRedo( getUser().getUsername() );
        LinkMenuItem linkMenuItem = new LinkMenuItem(
                id,
                new Model<String>( getCommander().getRedoTitle( getUser().getUsername() ) ),
                new AjaxFallbackLink( "link" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        if ( enabled ) update( target, getCommander().redo( getUser().getUsername() ) );
                    }
                } );
        if ( !enabled ) {
            linkMenuItem.add( new AttributeModifier( "class", new Model<String>( "disabled" ) ) );
        }
        return linkMenuItem;
    }

    /**
     * Create a send message menu item.
     *
     * @param id the id
     * @return the component
     */
    protected LinkMenuItem getSendMessageMenuItem( String id ) {
        final Identifiable identifiable = getModel().getObject();
        final boolean enabled = identifiable != null && identifiable instanceof ModelObject;
        LinkMenuItem linkMenuItem = new LinkMenuItem(
                id,
                new Model<String>( "Send message" ),
                new AjaxFallbackLink( "link" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        if ( enabled ) update( target,
                                new Change( Change.Type.Communicated, identifiable ) );
                    }
                } );

        if ( !enabled ) {
            linkMenuItem.add( new AttributeModifier( "class", new Model<String>( "disabled" ) ) );
        }
        return linkMenuItem;
    }

    /**
     * Make menu items from commands.
     *
     * @param id              id of the menu item
     * @param commandWrappers a list of wrapped commands
     * @return a list of menu item components
     */
    protected List<LinkMenuItem> getCommandMenuItems( String id, List<CommandWrapper> commandWrappers ) {

        List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();

        Commander commander = getCommander();

        for ( final CommandWrapper commandWrapper : commandWrappers )
            try {
                final Command command = commandWrapper.getCommand();
                String label = command.getLabel( commander );
                final boolean enabled = commander.canDo( command );
                LinkMenuItem linkMenuItem = new LinkMenuItem(
                        id,
                        new Model<String>( label ),
                        enabled
                                ?
                                new ConfirmedAjaxFallbackLink(
                                "link", commandWrapper.isConfirm() ? "Are you sure?" : null ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                commandWrapper.onExecuted(
                                        target, getCommander().doCommand( command ) );
                            }
                        }
                                :
                                new AjaxFallbackLink( "link" ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                // do nothing
                            }
                        }
                );
                if ( !enabled ) {
                    linkMenuItem.add( new AttributeModifier( "class", new Model<String>( "disabled" ) ) );
                }
                menuItems.add( linkMenuItem );
            } catch ( CommandException e ) {
                LoggerFactory.getLogger( getClass() ).warn( "Unable to get command label", e );
            }

        return menuItems;
    }

    protected CommandWrapper wrap( final Command command, boolean confirm ) {
        return new CommandWrapper( command, confirm ) {
            @Override
            public void onExecuted( AjaxRequestTarget target, Change change ) {
                update( target, change );
            }
        };
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

