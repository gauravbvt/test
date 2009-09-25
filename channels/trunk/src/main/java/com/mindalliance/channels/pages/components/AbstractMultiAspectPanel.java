package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.LockManager;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * Multi-aspect panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 6, 2009
 * Time: 7:56:27 PM
 */
public abstract class AbstractMultiAspectPanel extends AbstractCommandablePanel {

    /**
     * Details aspect.
     */
    public static final String DETAILS = "details";

    /**
     * Outermost container.
     */
    private WebMarkupContainer moContainer;
    /**
     * Banner.
     */
    private WebMarkupContainer banner;
    /**
     * Name plus aspect.
     */
    private Label headerTitle;
    /**
     * Entity actions menu.
     */
    private Component actionsMenu;
    /**
     * Entity aspect panel (or label).
     */
    private Component aspectPanel;
    /**
     * Name of aspect shown.
     */
    private String aspectShown;
    /**
     * Show menu.
     */
    private MenuPanel showMenu;

    public AbstractMultiAspectPanel(
            String id,
            IModel<? extends Identifiable> model,
            Set<Long> expansions ) {
        this( id, model, expansions, DETAILS );
    }


    protected AbstractMultiAspectPanel(
            String id,
            IModel<? extends Identifiable> model,
            Set<Long> expansions,
            String aspect ) {
        super( id, model, expansions );
        aspectShown = aspect;
        init();
    }

    /**
     * Panel initialization.
     */
    protected void init() {
        setOutputMarkupId( true );

        headerTitle = new Label( "header-title", new PropertyModel<String>( this, "headerTitle" ) );
        headerTitle.setOutputMarkupId( true );

        AjaxFallbackLink<?> closeLink = new AjaxFallbackLink( "close" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Collapsed, getObject() );
                update( target, change );
            }
        };

        banner = new WebMarkupContainer( "banner" );
        banner.setOutputMarkupId( true );
        banner.add( closeLink );
        banner.add( headerTitle );

        addShowMenu();
        addActionsMenu();

        String css = getCssClass();
        moContainer = new WebMarkupContainer( "mo" );
        moContainer.add( new AttributeModifier( "class", true, new Model<String>( css ) ) );
        moContainer.add( banner );
        add( moContainer );

        if ( aspectShown == null ) aspectShown = getDefaultAspect();
        showAspect( aspectShown );
        adjustComponents();
    }

    protected void addShowMenu() {
        showMenu = makeShowMenu( "showMenu" );
        showMenu.setOutputMarkupId( true );
        banner.addOrReplace( showMenu );
    }

    private void addActionsMenu() {
        actionsMenu = makeActionMenuOrLabel( "actionMenu" );
        actionsMenu.setOutputMarkupId( true );
        banner.addOrReplace( actionsMenu );
    }

    /**
     * Get default aspect.
     *
     * @return a string
     */
    protected abstract String getDefaultAspect();

    /**
     * Get the css class for the panel.
     *
     * @return a string
     */
    protected abstract String getCssClass();

    protected void adjustComponents() {
        annotateHeaderTitle( getObject(), ( (Channels) getApplication() ).getAnalyst() );
        String objectClassName = getObjectClass();
        if ( objectClassName != null && !objectClassName.isEmpty() ) {
            banner.add( new AttributeModifier(
                    "class",
                    true,
                    new Model<String>( objectClassName ) ) );
        }
    }

    /**
     * Make show menu.
     *
     * @param menuId the menu's id
     * @return a MenuPanel
     */
    protected abstract MenuPanel makeShowMenu( String menuId );

    private Component makeActionMenuOrLabel( String menuId ) {
        Component menu;
        LockManager lockManager = getLockManager();
        if ( !objectNeedsLocking() || lockManager.isLockedByUser( getObject() ) ) {
            menu = makeActionMenu( menuId );
        } else if ( getCommander().isTimedOut() || getLockOwner( getObject() ) == null ) {
            menu = new Label(
                    menuId, new Model<String>( "Timed out" ) );
        } else {
            String otherUser = lockManager.getLockOwner( getObject().getId() );
            menu = new Label(
                    menuId, new Model<String>( "Edited by " + otherUser ) );
            menu.add(
                    new AttributeModifier( "class", true, new Model<String>( "locked" ) ) );
        }
        return menu;
    }

    /**
     * Model object needs locking before it can be acted upon.
     *
     * @return a boolean
     */
    protected abstract boolean objectNeedsLocking();

    /**
     * Make action menu.
     *
     * @param menuId the menu's id
     * @return a MenuPanel or some Component
     */
    protected abstract MenuPanel makeActionMenu( String menuId );

    private void annotateHeaderTitle( ModelObject object, Analyst analyst ) {

        String summary = analyst.getIssuesSummary( object, Analyst.INCLUDE_PROPERTY_SPECIFIC );

        String classString;
        String titleString;
        if ( !summary.isEmpty() ) {
            classString = "error";
            titleString = summary;

        } else if ( analyst.hasIssues( object, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
            classString = "waived";
            titleString = "All issues waived";

        } else {
            classString = "no-error";
            titleString = "No known issue";
        }

        headerTitle.add(
            new AttributeModifier( "class", true, new Model<String>( classString ) ) );   // NON-NLS
        headerTitle.add(
            new AttributeModifier( "title", true, new Model<String>( titleString ) ) );   // NON-NLS
    }

    private void showAspect( String aspect ) {
        aspectPanel = makeAspectPanel( aspect );
        aspectPanel.setOutputMarkupId( true );
        moContainer.addOrReplace( aspectPanel );
    }

    /**
     * Make the aspect sub-panel.
     *
     * @param aspect a string
     * @return a component
     */
    protected abstract Component makeAspectPanel( String aspect );


    /**
     * Get entity name plus aspect.
     *
     * @return a string
     */
    public String getHeaderTitle() {
        String abbreviatedName = StringUtils.abbreviate( getObject().getName(), getMaxTitleNameLength() );
        return abbreviatedName + " - " + getObject().getTypeName() + " " + getAspectShown();
    }

    /**
     * Maximum length of displayed name in title.
     *
     * @return an int
     */
    protected abstract int getMaxTitleNameLength();

    /**
     * Get the entity that's viewed.
     *
     * @return a model object
     */
    public ModelObject getObject() {
        return (ModelObject) getModel().getObject();
    }

    public String getAspectShown() {
        return aspectShown;
    }

    /**
     * Change aspect shown.
     *
     * @param target an ajax request target
     * @param aspect the name of the aspect
     */
    public void setAspectShown( AjaxRequestTarget target, String aspect ) {
        aspectShown = aspect;
        showAspect( aspect );
        target.addComponent( headerTitle );
        target.addComponent( aspectPanel );
    }

    /**
     * Get the name of the object's class, to be used as a CSS class.
     *
     * @return a string
     */
    public String getObjectClass() {
        return getObjectClassName();
    }

    /**
     * Get the object's class name, to be used as a CSS class.
     *
     * @return a string
     */
    protected abstract String getObjectClassName();

    /**
     * Refresh everything that could have changed.
     *
     * @param target an ajax request target
     */
    public void refresh( AjaxRequestTarget target ) {
        refreshMenus( target );
        adjustComponents();
        target.addComponent( banner );
        setAspectShown( target, aspectShown );
        target.addComponent( aspectPanel );
    }

    /**
     * Refresh menus.
     *
     * @param target ajax request target
     */
    public void refreshMenus( AjaxRequestTarget target ) {
        addActionsMenu();
        target.addComponent( actionsMenu );
        target.addComponent( showMenu );
    }

    public MenuPanel getShowMenu() {
        return showMenu;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWith( AjaxRequestTarget target, Change change ) {
        target.addComponent( actionsMenu );
        adjustComponents();
        annotateHeaderTitle( getObject(), ( (Channels) getApplication() ).getAnalyst() );
        target.addComponent( banner );
        super.updateWith( target, change );
    }


}
