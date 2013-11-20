package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.LockManager;
import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.pages.GeoMapPage;
import com.mindalliance.channels.pages.Releaseable;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.guide.Guidable;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract floating multi-aspect panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/26/12
 * Time: 1:58 PM
 */
public abstract class AbstractFloatingMultiAspectPanel extends AbstractFloatingTabbedCommandablePanel implements Releaseable {

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
     * Entity aspect panel (or label).
     */
    private Component aspectPanel;

    /**
     * Name of aspect shown.
     */
    private String aspectShown;


    public AbstractFloatingMultiAspectPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        this( id, model, expansions, DETAILS );
    }

    protected AbstractFloatingMultiAspectPanel( String id,
                                                IModel<? extends Identifiable> model,
                                                Set<Long> expansions,
                                                String aspect ) {
        this( id, model, expansions, aspect, null );
    }

    protected AbstractFloatingMultiAspectPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions,
                                                String aspect, Change change ) {
        super( id, model, expansions );
        setSelectedTabName( aspect );
        aspectShown = aspect;
        setChange( change );
        init();
    }

    /**
     * Get the css class for the panel.
     *
     * @return a string
     */
    protected abstract String getCssClass();

    /**
     * Make action menu.
     *
     * @param menuId the menu's id
     * @return a MenuPanel or some Component
     */
    protected abstract MenuPanel makeActionMenu( String menuId );

    /**
     * Make the aspect sub-panel.
     *
     * @param aspect a string
     * @param change a change
     * @return a component
     */
    protected abstract Component makeAspectPanel( String aspect, Change change );

    protected abstract List<String> getAllAspects();

    protected abstract List<String> getActionableAspects();

    protected abstract String getMapTitle();

    protected abstract List<? extends GeoLocatable> getGeoLocatables();


    @Override
    protected void doClose( AjaxRequestTarget target ) {
        releaseAspectShown();
        update( target, getClosingChange() );
    }

    // DEFAULT
    protected Change getClosingChange() {
        return new Change( Change.Type.Collapsed, getObject() );
    }

    protected void minimizeNormalize( AjaxRequestTarget target ) {
        super.minimizeNormalize( target );
        if ( !isMinimized() ) {
            refresh( target, new Change( Change.Type.Refresh ), getAspectShown() );
        }
    }

    /**
     * Panel initialization.
     */
    protected void init() {
        moContainer = new WebMarkupContainer( "mo" );
        getContentContainer().add( moContainer );
        String css = getCssClass();
        moContainer.add( new AttributeModifier( "class", new Model<String>( css ) ) );
        if ( aspectShown == null )
            aspectShown = getDefaultAspect();
        showAspect( aspectShown, getChange() );
    }

    @Override
    public String getUserRoleId() {
        return null;  // DEFAULT
    }

    public String getHelpSectionId() {
        return aspectPanel instanceof Guidable
                ? ( (Guidable) aspectPanel ).getHelpSectionId()
                : null;
    }

    public String getHelpTopicId() {
        return aspectPanel instanceof Guidable
                ? ( (Guidable) aspectPanel ).getHelpTopicId()
                : null;
    }

    @Override
    protected Component makeActionMenuOrLabel( String menuId ) {
        Component menu = makeActionMenu( menuId );
        if ( menu != null && getPlan().isDevelopment() && isAspectShownEditable() ) {
            LockManager lockManager = getLockManager();
            Identifiable identifiable = getObject();
            if ( !lockManager.isLockedByUser( getUser().getUsername(), identifiable.getId() ) ) {
                if ( getCommander().isTimedOut( getUser().getUsername() )
                        || getLockOwner( identifiable ) == null ) {
                    menu = timeOutLabel( menuId );
                } else if ( identifiable instanceof ModelObject && ( (ModelObject) identifiable ).isImmutable() ) {
                    menu = new Label( menuId, new Model<String>( "Immutable" ) );
                } else {
                    menu = editedByLabel( menuId, identifiable, lockManager.getLockUser( getObject().getId() ) );
                }
            }
        }
        return menu;
    }


    public void showAspect( String aspect, Change change, AjaxRequestTarget target ) {
        if ( change.isCollapsed() ) {
            redisplay( target );
        } else {
            showAspect( aspect, change );
        }
        target.add( aspectPanel );
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        if ( aspectPanel instanceof AbstractUpdatablePanel ) {
            ( (AbstractUpdatablePanel) aspectPanel ).redisplay( target );
        }
    }

    private void showAspect( String aspect, Change change ) {
        setSelectedTabName( aspect );
        String aspectToShow = aspect == null ? getDefaultAspect() : aspect;
        aspectPanel = makeAspectPanel( aspectToShow, change );
        aspectPanel.setOutputMarkupId( true );
        moContainer.addOrReplace( aspectPanel );
    }

    private void releaseAspectShown() {
        if ( aspectPanel != null && aspectPanel instanceof AbstractCommandablePanel ) {
            ( (AbstractCommandablePanel) aspectPanel ).release();
        }
    }


    @Override
    protected String getTitle() {
        Identifiable identifiable = getObject();
        if ( identifiable instanceof ModelObject ) {
            ModelObject mo = (ModelObject) identifiable;
            return "About " + mo.getKindLabel().toLowerCase() + ": " + mo.getName();
        } else {
            return "About" + identifiable.getClassLabel();
        }
    }

    /**
     * Get the identifiable that's viewed.
     *
     * @return an identifiable
     */
    public Identifiable getObject() {
        return getModel().getObject();
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
        setAspectShown( aspect );
        update( target, new Change( Change.Type.AspectReplaced, getObject(), aspect ) );
    }

    protected void setAspectShown( String aspect ) {
        releaseAspectShown();
        aspectShown = aspect;
        if ( getActionableAspects().contains( aspectShown ) ) {
            requestLockOn( getObject() );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( !isMinimized() ) {
            refreshMenus( target );
            if ( change.isAspectReplaced() ) {
                refresh( target, change, change.getProperty() );
            }
        }
        super.updateWith( target, change, updated );
    }

    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        if ( !isMinimized() ) {
            super.refresh( target, change, aspect );
            refreshTitle( target );
            refreshMenus( target );
            if ( change.isUnknown()
                    || change.isRefresh()
                    || change.isDisplay()
                    || change.isModified()
                    || change.isSelected() ) {
                if ( /*!isMinimized() && */isAspect( aspect ) ) {
                    showAspect( aspect, change, target );
                }
            }
        }
    }

    protected boolean isAspect( String aspect ) {
        return aspect != null
                && !aspect.isEmpty()
                && getAllAspects().contains( aspect );
    }

    /**
     * Release locks acquired after initialization.
     */
    @Override
    public void release() {
        for ( Identifiable identifiable : lockedIdentifiables ) {
            getCommander().releaseAnyLockOn( getUser().getUsername(), identifiable );
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
        if ( getCommander().releaseAnyLockOn( getUser().getUsername(), identifiable ) )
            lockedIdentifiables.remove( identifiable );
    }

    /**
     * Release any lock on an identifiable.
     *
     * @param identifiable an identifiable
     */
    @Override
    public void requestLockOn( Identifiable identifiable ) {
        if ( getCommander().requestLockOn( getUser().getUsername(), identifiable ) )
            lockedIdentifiables.add( identifiable );
    }

    @Override
    protected List<PathIcon> getPathIcons( String id ) {
        List<PathIcon> pathIcons = new ArrayList<PathIcon>();
        // issues
        PathIcon issuesPathIcon = getIssuesPathIcon( id );
        if ( issuesPathIcon != null ) pathIcons.add( issuesPathIcon );
        // Map
        PathIcon mapPathIcon = getMapPathIcon( id );
        if ( mapPathIcon != null ) pathIcons.add( mapPathIcon );
        // surveys
        PathIcon surveysPathIcon = getSurveysPathIcon( id );
        if ( surveysPathIcon != null ) pathIcons.add( surveysPathIcon );
        return pathIcons;
    }

    protected PathIcon getIssuesPathIcon( String id ) {
        String titleString;
        Identifiable identifiable = getObject();
        if ( identifiable instanceof ModelObject ) {
            ModelObject mo = (ModelObject) identifiable;
            boolean waived = false;
            String summary =
                    getAnalyst().getIssuesSummary( getQueryService(), mo, Analyst.INCLUDE_PROPERTY_SPECIFIC );
            if ( !summary.isEmpty() ) {
                titleString = summary;
            } else if ( getAnalyst().hasIssues( getQueryService(), mo, Analyst.INCLUDE_PROPERTY_SPECIFIC ) ) {
                waived = true;
                titleString = "All issues waived";
            } else {
                titleString = "";
            }
            AjaxLink issuesLink = new AjaxLink( id ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    // Do nothing for now

                }
            };
            if ( !titleString.isEmpty() ) {
                addTipTitle( issuesLink, titleString );
            }
            String src = waived ? "images/waived2.png" : "images/warning.png";
            PathIcon issuesPathIcon = new PathIcon( "Issues", src, issuesLink );
            issuesPathIcon.setVisible( !titleString.isEmpty() || waived );
            issuesPathIcon.setAlt( titleString );
            return issuesPathIcon;
        } else {
            return null;
        }
    }

    protected PathIcon getSurveysPathIcon( String id ) {
        final Identifiable identifiable = getObject();
        if ( identifiable instanceof ModelObject && !( (ModelObject) identifiable ).isUnknown() ) {
            AjaxLink surveysLink = new AjaxLink( id ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    Change change = new Change( Change.Type.AspectViewed, identifiable, "surveys" );
                    update( target, change );
                }
            };
            addTipTitle( surveysLink, "Surveys launched or that could be launched" );
            return new PathIcon( "Surveys", "images/surveys.png", surveysLink );
        } else {
            return null;
        }
    }

    protected PathIcon getMapPathIcon( String id ) {
        List<? extends GeoLocatable> geoLocatables = getGeoLocatables();
        if ( !geoLocatables.isEmpty() ) {
            BookmarkablePageLink<GeoMapPage> geomapLink = GeoMapPage.makeLink(
                    id,
                    new Model<String>( getMapTitle() ),
                    geoLocatables,
                    getQueryService() );
            PathIcon mapPathIcon = new PathIcon( "Map", "images/location.png", geomapLink );
            addTipTitle( geomapLink, getMapTitle() );
            mapPathIcon.setVisible( !geoLocatables.isEmpty() );
            return mapPathIcon;
        } else {
            return null;
        }

    }

    @Override
    protected List<Tab> makeTabs() {
        List<Tab> tabs = new ArrayList<Tab>();
        for ( String aspect : getAllAspects() ) {
            Change change = new Change( Change.Type.AspectViewed );
            change.setSubject( getTabChangeDefaultSubject() );
            change.setId( getTabIdentifiableId() );
            change.setProperty( aspect );
            tabs.add( new Tab( aspect, change ) );
        }
        return tabs;
    }

    protected Identifiable getTabChangeDefaultSubject() {
        return getObject();       // DEFAULT
    }

    protected long getTabIdentifiableId() {
        return getModel().getObject().getId();
    }


    protected boolean isAspectShownEditable() {
        return getAllAspects().contains( getAspectShown() );
    }

    protected String getDefaultAspect() {
        return getAllAspects().get( 0 );
    }


}
