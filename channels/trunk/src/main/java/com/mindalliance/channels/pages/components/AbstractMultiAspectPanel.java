/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.LockManager;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.Releaseable;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Multi-aspect panel.
 */
public abstract class AbstractMultiAspectPanel extends FloatingCommandablePanel implements Releaseable {

    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 300;

    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;

    /**
     * Details aspect.
     */
    public static final String DETAILS = "details";

    /**
     * Identifiables locked after initialization.
     */
    private Set<Identifiable> lockedIdentifiables = new HashSet<Identifiable>();

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

    public AbstractMultiAspectPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        this( id, model, expansions, DETAILS );
    }

    protected AbstractMultiAspectPanel( String id,
                                        IModel<? extends Identifiable> model,
                                        Set<Long> expansions,
                                        String aspect ) {
        this( id, model, expansions, aspect, null );
    }

    protected AbstractMultiAspectPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions,
                                        String aspect, Change change ) {
        super( id, model, expansions );
        aspectShown = aspect;
        setChange( change );
        init( );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        releaseAspectShown();
        Change change = new Change( Change.Type.Collapsed, getObject() );
        update( target, change );
    }

    @Override
    protected int getPadTop() {
        return PAD_TOP;
    }

    @Override
    protected int getPadLeft() {
        return PAD_LEFT;
    }

    @Override
    protected int getPadBottom() {
        return PAD_BOTTOM;
    }

    @Override
    protected int getPadRight() {
        return PAD_RIGHT;
    }

    @Override
    protected int getMinWidth() {
        return MIN_WIDTH;
    }

    @Override
    protected int getMinHeight() {
        return MIN_HEIGHT;
    }

    /**
     * Panel initialization.
     */
    protected void init() {
        moContainer = new WebMarkupContainer( "mo" );
        getContentContainer().add( moContainer );
        banner = new WebMarkupContainer( "banner" );
        banner.setOutputMarkupId( true );
        addHeaderTitle();

        String css = getCssClass();
        moContainer.add( new AttributeModifier( "class", true, new Model<String>( css ) ) );
        moContainer.add( banner );

        if ( aspectShown == null )
            aspectShown = getDefaultAspect();
        showAspect( aspectShown, getChange() );
        addShowMenu();
        addActionsMenu();
        addDoneButton();
        adjustComponents();
    }

    protected void addHeaderTitle() {
        headerTitle = new Label( "header-title", new PropertyModel<String>( this, "headerTitle" ) );
        headerTitle.setOutputMarkupId( true );
        banner.addOrReplace( headerTitle );
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

    private void addDoneButton() {
        AjaxFallbackLink doneLink = new AjaxFallbackLink( "done" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                close( target );
            }
        };
        banner.add( doneLink );
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
            banner.add( new AttributeModifier( "class", true, new Model<String>( objectClassName ) ) );
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
        if ( !isAspectShownEditable() ) {
            menu = makeActionMenu( menuId );
        } else {
            LockManager lockManager = getLockManager();
            if ( lockManager.isLockedByUser( User.current().getUsername(), getObject().getId() ) ) {
                menu = makeActionMenu( menuId );
            } else if ( getCommander().isTimedOut( User.current().getUsername() )
                        || getLockOwner( getObject() ) == null )
            {
                menu = timeOutLabel( menuId );
            } else if ( getObject().isImmutable() ) {
                menu = new Label( menuId, new Model<String>( "Immutable" ) );
            } else {
                menu = editedByLabel( menuId, getObject(), lockManager.getLockUser( getObject().getId() ) );
            }
        }
        return menu;
    }

    protected abstract boolean isAspectShownEditable();

    /**
     * Make action menu.
     *
     * @param menuId the menu's id
     * @return a MenuPanel or some Component
     */
    protected abstract MenuPanel makeActionMenu( String menuId );

    protected void annotateHeaderTitle( ModelObject object, Analyst analyst ) {

        String summary =
                analyst.getIssuesSummary( getQueryService(), object, Analyst.INCLUDE_PROPERTY_SPECIFIC );

        String classString;
        String titleString;
        if ( !summary.isEmpty() ) {
            classString = "error";
            titleString = summary;
        } else if ( analyst.hasIssues( getQueryService(), object, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
            classString = "waived";
            titleString = "All issues waived";
        } else {
            classString = "no-error";
            titleString = "No known issue";
        }

        headerTitle.add( new AttributeModifier( "class", true, new Model<String>( classString ) ) );   // NON-NLS
        headerTitle.add( new AttributeModifier( "title", true, new Model<String>( titleString ) ) );   // NON-NLS
    }

    public void showAspect( String aspect, Change change, AjaxRequestTarget target ) {
        if ( change.isCollapsed() ) {
            redisplay( target );
        } else {
            showAspect( aspect, change );
        }
        target.addComponent( aspectPanel );
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        if ( aspectPanel instanceof AbstractUpdatablePanel ) {
            ( (AbstractUpdatablePanel) aspectPanel ).redisplay( target );
        }
    }

    private void showAspect( String aspect, Change change ) {
        String aspectToShow = aspect == null ? getDefaultAspect() : aspect;
        addHeaderTitle();
        aspectPanel = makeAspectPanel( aspectToShow, change );
        aspectPanel.setOutputMarkupId( true );
        moContainer.addOrReplace( aspectPanel );
    }

    private void releaseAspectShown() {
        if ( aspectPanel != null && aspectPanel instanceof AbstractCommandablePanel ) {
            ( (AbstractCommandablePanel) aspectPanel ).release();
        }
    }

    /**
     * Make the aspect sub-panel.
     *
     * @param aspect a string
     * @param change a change
     * @return a component
     */
    protected abstract Component makeAspectPanel( String aspect, Change change );

    @Override
    protected String getTitle() {
        return "About " + getObject().getKindLabel().toLowerCase() + ": " + getObject().getName();
    }

    /**
     * Get entity name plus aspect.
     *
     * @return a string
     */
    public String getHeaderTitle() {
        return getAspectShown();
    }

    /**
     * Maximum length of displayed name in title.
     *
     * @return an int
     */
    protected abstract int getMaxTitleNameLength();

    /**
     * Get the model object  that's viewed.
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
        /*
                aspectShown = aspect;
                showAspect( aspect );
                target.addComponent( headerTitle );
                target.addComponent( aspectPanel );
        */
        releaseAspectShown();
        aspectShown = aspect;
        update( target, new Change( Change.Type.AspectReplaced, getObject(), aspect ) );
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
    /*
        public void refresh( AjaxRequestTarget target ) {
            refreshMenus( target );
            adjustComponents();
            target.addComponent( banner );
            setAspectShown( target, aspectShown );
            target.addComponent( aspectPanel );
        }
    */

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

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        target.addComponent( actionsMenu );
        adjustComponents();
        annotateHeaderTitle( getObject(), ( (Channels) getApplication() ).getAnalyst() );
        target.addComponent( banner );
        super.updateWith( target, change, updated );
    }

    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        refreshTitle( target );
        refreshMenus( target );
        adjustComponents();
        target.addComponent( banner );
        if ( change.isUnknown() || change.isDisplay() || change.isModified() || change.isSelected() ) {
            showAspect( aspect, change, target );
        }
    }

    /**
     * Release locks acquired after initialization.
     */
    @Override
    public void release() {
        for ( Identifiable identifiable : lockedIdentifiables ) {
            getCommander().releaseAnyLockOn( User.current().getUsername(), identifiable );
        }
        lockedIdentifiables = new HashSet<Identifiable>();
    }

    /**
     * Release any lock on an identifiable.
     *
     * @param identifiable an identifiable
     */
    @Override
    public void releaseAnyLockOn( Identifiable identifiable ) {
        getCommander().releaseAnyLockOn( User.current().getUsername(), identifiable );
        lockedIdentifiables.remove( identifiable );
    }

    /**
     * Release any lock on an identifiable.
     *
     * @param identifiable an identifiable
     */
    @Override
    public void requestLockOn( Identifiable identifiable ) {
        getCommander().requestLockOn( User.current().getUsername(), identifiable );
        lockedIdentifiables.add( identifiable );
    }
}
