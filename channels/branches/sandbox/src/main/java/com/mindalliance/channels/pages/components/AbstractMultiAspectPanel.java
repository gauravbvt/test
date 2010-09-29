package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.Releaseable;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Multi-aspect panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 6, 2009
 * Time: 7:56:27 PM
 */
public abstract class AbstractMultiAspectPanel extends FloatingCommandablePanel implements Releaseable {

    @SpringBean
    private QueryService queryService;

    /**
     * Pad top on move.
     */
    private static final int PAD_TOP = 68;
    /**
     * Pad left on move.
     */
    private static final int PAD_LEFT = 5;
    /**
     * Pad bottom on move and resize.
     */
    private static final int PAD_BOTTOM = 5;
    /**
     * Pad right on move and resize.
     */
    private static final int PAD_RIGHT = 6;
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
     * {@inheritDoc}
     */
    protected void close( AjaxRequestTarget target ) {
        releaseAspectShown();
        Change change = new Change( Change.Type.Collapsed, getObject() );
        update( target, change );
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadTop() {
        return PAD_TOP;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadLeft() {
        return PAD_LEFT;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadBottom() {
        return PAD_BOTTOM;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadRight() {
        return PAD_RIGHT;
    }

    /**
     * {@inheritDoc}
     */
    protected int getMinWidth() {
        return MIN_WIDTH;
    }

    /**
     * {@inheritDoc}
     */
    protected int getMinHeight() {
        return MIN_HEIGHT;
    }

    /**
     * Panel initialization.
     */
    protected void init() {
        moContainer = new WebMarkupContainer( "mo" );
        add( moContainer );
        headerTitle = new Label( "header-title", new PropertyModel<String>( this, "headerTitle" ) );
        headerTitle.setOutputMarkupId( true );

        banner = new WebMarkupContainer( "banner" );
        banner.setOutputMarkupId( true );
        banner.add( headerTitle );

        addShowMenu();
        addActionsMenu();
        addDoneButton();

        String css = getCssClass();
        moContainer.add( new AttributeModifier( "class", true, new Model<String>( css ) ) );
        moContainer.add( banner );

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

    private void addDoneButton() {
        AjaxFallbackLink doneLink = new AjaxFallbackLink( "done" ) {
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
            menu = timeOutLabel( menuId ) ;
        } else if ( getObject().isImmutable() ) {
            menu = new Label(
                    menuId, new Model<String>( "Immutable" ) );
        } else {
            menu = editedByLabel( menuId, getObject(), lockManager.getLockOwner( getObject().getId() ) );
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

    protected void annotateHeaderTitle( ModelObject object, Analyst analyst ) {

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
        releaseAspectShown();
        aspectPanel = makeAspectPanel( aspect == null ? getDefaultAspect() : aspect );
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
     * @return a component
     */
    protected abstract Component makeAspectPanel( String aspect );

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        target.addComponent( actionsMenu );
        adjustComponents();
        annotateHeaderTitle( getObject(), ( (Channels) getApplication() ).getAnalyst() );
        target.addComponent( banner );
        super.updateWith( target, change, updated );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        refreshTitle( target );
        refreshMenus( target );
        adjustComponents();
        target.addComponent( banner );
        if ( change.isModified() ) {
            showAspect( aspect );
            target.addComponent( aspectPanel );
        }
    }

    /**
     * Release locks acquired after initialization.
     */
    public void release() {
        for ( Identifiable identifiable : lockedIdentifiables ) {
            getCommander().releaseAnyLockOn( identifiable );
        }
        lockedIdentifiables = new HashSet<Identifiable>();
    }

    /**
     * Release any lock on an identifiable.
     *
     * @param identifiable an identifiable
     */
    public void releaseAnyLockOn( Identifiable identifiable ) {
        getCommander().releaseAnyLockOn( identifiable );
        lockedIdentifiables.remove( identifiable );
    }

    /**
     * Release any lock on an identifiable.
     *
     * @param identifiable an identifiable
     */
    public void requestLockOn( Identifiable identifiable ) {
        getCommander().requestLockOn( identifiable );
        lockedIdentifiables.add( identifiable );
    }

}
