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
public abstract class AbstractFloatingMultiAspectPanel extends AbstractFloatingCommandablePanel implements Releaseable {

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

    protected abstract List<GeoLocatable> getGeoLocatables();


    @Override
    protected void doClose( AjaxRequestTarget target ) {
        releaseAspectShown();
        Change change = new Change( Change.Type.Collapsed, getObject() );
        update( target, change );
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
    protected Component makeActionMenuOrLabel( String menuId ) {
        Component menu;
        if ( !isAspectShownEditable() ) {
            menu = makeActionMenu( menuId );
        } else {
            LockManager lockManager = getLockManager();
            if ( lockManager.isLockedByUser( getUser().getUsername(), getObject().getId() ) ) {
                menu = makeActionMenu( menuId );
            } else if ( getCommander().isTimedOut( getUser().getUsername() )
                    || getLockOwner( getObject() ) == null ) {
                menu = timeOutLabel( menuId );
            } else if ( getObject().isImmutable() ) {
                menu = new Label( menuId, new Model<String>( "Immutable" ) );
            } else {
                menu = editedByLabel( menuId, getObject(), lockManager.getLockUser( getObject().getId() ) );
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
        return "About " + getObject().getKindLabel().toLowerCase() + ": " + getObject().getName();
    }

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
        releaseAspectShown();
        aspectShown = aspect;
        if ( getActionableAspects().contains( aspectShown ) ) {
            requestLockOn( getObject() );
        }
        update( target, new Change( Change.Type.AspectReplaced, getObject(), aspect ) );
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        refreshMenus( target );
        super.updateWith( target, change, updated );
    }

    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        super.refresh( target, change, aspect );
        refreshTitle( target );
        refreshMenus( target );
        if ( change.isUnknown()
                || change.isDisplay()
                || change.isModified()
                || change.isSelected() ) {
            showAspect( aspect, change, target );
        }
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
        getCommander().releaseAnyLockOn( getUser().getUsername(), identifiable );
        lockedIdentifiables.remove( identifiable );
    }

    /**
     * Release any lock on an identifiable.
     *
     * @param identifiable an identifiable
     */
    @Override
    public void requestLockOn( Identifiable identifiable ) {
        getCommander().requestLockOn( getUser().getUsername(), identifiable );
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

    private PathIcon getIssuesPathIcon( String id ) {
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
                issuesLink.add( new AttributeModifier( "title", titleString ) );
            }
            String src = waived ? "images/waived2.png" : "images/warning2.png";
            PathIcon issuesPathIcon = new PathIcon( src, issuesLink );
            issuesPathIcon.setVisible( !titleString.isEmpty() || waived );
            issuesPathIcon.setAlt( titleString );
            return issuesPathIcon;
        } else {
            return null;
        }
    }

    private PathIcon getSurveysPathIcon( String id ) {
        final Identifiable identifiable = getObject();
        if ( identifiable instanceof ModelObject ) {
            AjaxLink surveysLink = new AjaxLink( id ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    Change change = new Change( Change.Type.AspectViewed, identifiable, "surveys" );
                    update( target, change );
                }
            };
            surveysLink.add(  new AttributeModifier( "title", "Surveys launched or that could be launched" ) );
            return new PathIcon( "images/survey_small2.png", surveysLink );
        } else {
            return null;
        }
    }

    private PathIcon getMapPathIcon( String id ) {
        List<GeoLocatable> geoLocatables = getGeoLocatables();
        if ( !geoLocatables.isEmpty() ) {
            BookmarkablePageLink<GeoMapPage> geomapLink = GeoMapPage.makeLink(
                    id,
                    new Model<String>( getMapTitle() ),
                    geoLocatables,
                    getQueryService() );
            PathIcon mapPathIcon = new PathIcon( "images/map2.png", geomapLink );
            geomapLink
                    .add( new AttributeModifier( "title", getMapTitle() ) );
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
            tabs.add( new Tab( aspect, new Change( Change.Type.AspectViewed, getModel().getObject(), aspect ) ) );
        }
        return tabs;
    }


    protected boolean isAspectShownEditable() {
        return getAllAspects().contains( getAspectShown() );
    }

    protected String getDefaultAspect() {
        return getAllAspects().get( 0 );
    }


}
