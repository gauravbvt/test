/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components;

import com.google.code.jqwicket.ui.tiptip.TipTipBehavior;
import com.google.code.jqwicket.ui.tiptip.TipTipOptions;
import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Commander;
import com.mindalliance.channels.core.command.LockManager;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.core.query.PlanServiceFactory;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.pages.AbstractChannelsBasicPage;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.CommunityPage;
import com.mindalliance.channels.pages.Modalable;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.guide.Guidable;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.net.URLEncoder;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract base class of updatable panels.
 */
public class AbstractUpdatablePanel extends Panel implements Updatable {

    @SpringBean
    private CommanderFactory commanderFactory;

    @SpringBean
    private Analyst analyst;

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private DiagramFactory diagramFactory;

    @SpringBean
    private UserParticipationService userParticipationService;

    @SpringBean
    private UserRecordService userInfoService;

    @SpringBean
    private PlanServiceFactory planServiceFactory;

    @SpringBean
    private PlanCommunityManager planCommunityManager;

    /**
     * Simple date format.
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat( "M/d/yyyy HH:mm" );

    /**
     * Short date format.
     */
    private static final SimpleDateFormat shortDateFormat = new SimpleDateFormat( "M/d/yyyy" );


    /**
     * String comparator for equality tests.
     */
    private static final Collator COMPARATOR = Collator.getInstance();

    /**
     * Model on an identifiable.
     */
    private IModel<? extends Identifiable> model;

    /**
     * Ids of expanded model objects.
     */
    private Set<Long> expansions;

    /**
     * The change that caused this panel to open.
     */
    private Change change;
    /**
     * Subsituted update target.
     */
    private Updatable updateTarget;

    /**
     * Name pattern.
     */
    private final Pattern namePattern = Pattern.compile( "^.*?(\\(\\d+\\))?$" );

    @SpringBean
    private CommunityServiceFactory communityServiceFactory;

    public AbstractUpdatablePanel( String id ) {
        super( id );
        setOutputMarkupId( true );
    }

    public AbstractUpdatablePanel( String id, IModel<? extends Identifiable> model ) {
        this( id, model, null );
    }

    public AbstractUpdatablePanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model );
        setOutputMarkupId( true );
        this.model = model;
        this.expansions = expansions;
    }

    public String getUserRoleId() {
        return null;  // DEFAULT
    }


    protected IModel<? extends Identifiable> getModel() {
        return model;
    }

    protected SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    protected SimpleDateFormat getShortDateFormat() {
        return shortDateFormat;
    }


    /**
     * Get the query service from further up updatable parent.
     *
     * @return a query service
     */
    @Override
    public QueryService getQueryService() {
//        return getCommander().getQueryService();
        return getPlanService();
    }

    /**
     * Get an analyst.
     *
     * @return an analyst
     */
    protected Analyst getAnalyst() {
        return analyst;
    }

    /**
     * Get diagram factory.
     *
     * @return diagram factory
     */
    protected DiagramFactory getDiagramFactory() {
        return diagramFactory;
    }

    /**
     * Get the active community's commander.
     *
     * @return a commander
     */
    protected Commander getCommander() {
        return commanderFactory.getCommander( getCommunityService() );
    }

    /**
     * Get plan manager.
     *
     * @return the plan manager
     */
    protected PlanManager getPlanManager() {
        return planManager;
    }

    /**
     * Get the lock manager.
     *
     * @return a lock manager
     */
    protected LockManager getLockManager() {
        return getCommander().getLockManager();
    }

    private Channels getChannels() {
        return (Channels) getApplication();
    }

    /**
     * Get the user's name.
     *
     * @return a string
     */
    protected String getUsername() {
        return getUser().getUsername();
    }

    protected Change getChange() {
        return change;
    }

    protected void setChange( Change change ) {
        this.change = change;
    }

    protected Change getChangeOnce() {
        Change once = getChange();
        setChange( null );
        return once;
    }

    protected void setModel( IModel<? extends ModelObject> aModel ) {
        model = aModel;
    }

    protected void setExpansions( Set<Long> exp ) {
        expansions = exp;
    }


    /**
     * Set and update a component's visibility.
     *
     * @param target    an ajax request target
     * @param component a component
     * @param visible   a boolean
     */
    protected static void makeVisible( AjaxRequestTarget target, Component component, boolean visible ) {
        makeVisible( component, visible );
        target.add( component );
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
     */
    protected static void makeVisible( Component component, boolean visible ) {
        component.add( new AttributeModifier( "style", new Model<String>( visible ? "" : "display:none" ) ) );
    }

    public Updatable getUpdateTarget() {
        return updateTarget;
    }

    public void setUpdateTarget( Updatable updateTarget ) {
        this.updateTarget = updateTarget;
    }

    protected Modalable getModalableParent() {
        return findParent( Modalable.class );
    }

    @Override
    public void changed( Change change ) {
        Updatable updatableParent = findUpdatableParent();
        if ( updatableParent != null )
            updatableParent.changed( change );
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Updatable updatableParent = findUpdatableParent();
        if ( updatableParent != null ) {
            updated.add( this );
            updatableParent.updateWith( target, change, updated );
        }
    }

    protected Updatable findUpdatableParent() {
        return updateTarget != null ? updateTarget : findParent( Updatable.class );
    }

    @Override
    public void update( AjaxRequestTarget target, Object object, String action ) {
        // Do nothing
    }

    /**
     * Update a change.
     *
     * @param target the target
     * @param change the change
     */
    protected void update( AjaxRequestTarget target, Change change ) {
        changed( change );
        updateWith( target, change, new ArrayList<Updatable>() );
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change ) {
        refresh( target, change, new ArrayList<Updatable>() );
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        refresh( target, change, updated, null );
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated, String aspect ) {
        if ( !updated.contains( this ) && !change.isNone() ) {
            refresh( target, change, aspect );
            //   target.add( this );
        }
    }

    /**
     * Refresh given change.
     *
     * @param target an ajax request target
     * @param change the nature of the change
     * @param aspect aspect shown
     */
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        // do nothing
    }

    /**
     * Test if strings are equivalent.
     *
     * @param name   the new name
     * @param target the original name
     * @return true if strings are equivalent
     */
    protected static boolean isSame( String name, String target ) {
        return name != null && target != null && COMPARATOR.compare( name, target ) == 0;
    }

    /**
     * Get the expansions.
     *
     * @return a set of Longs
     */
    protected Set<Long> getExpansions() {
        return expansions;
    }

    protected List<String> getUniqueNameChoices( ModelEntity entity ) {
        List<String> choices = new ArrayList<String>();
        for ( String taken : getQueryService().findAllEntityNames( entity.getClass() ) ) {
            if ( taken.equals( entity.getName() ) )
                choices.add( taken );
            else {
                Matcher matcher = namePattern.matcher( taken );
                int count = matcher.groupCount();
                if ( count > 1 ) {
                    String group = matcher.group( 0 );
                    int index = Integer.valueOf( group.substring( 1, group.length() - 2 ) );
                    choices.add( taken.substring( 0, taken.lastIndexOf( '(' ) - 1 ) + '(' + ( index + 1 ) + ')' );
                } else
                    choices.add( taken + "(2)" );
            }
        }
        return choices;
    }

    /**
     * Add issues annotations to a component.
     *
     * @param component the component
     * @param object    the object of the issues
     * @param property  the property of concern. If null, get issues of object
     */
    protected void addIssues( FormComponent<?> component, ModelObject object, String property ) {
        String summary = property == null ?
                analyst.getIssuesSummary( getQueryService(), object, false ) :
                analyst.getIssuesSummary( getQueryService(), object, property );

        boolean hasIssues = property == null ?
                analyst.hasIssues( getQueryService(), object, Analyst.INCLUDE_PROPERTY_SPECIFIC ) :
                analyst.hasIssues( getQueryService(), object, property );

        if ( summary.isEmpty() ) {
            component.add( new AttributeModifier( "class",
                    new Model<String>( hasIssues ? "waived" : "no-error" ) ) );
            addTipTitle( component, new Model<String>( hasIssues ? "All issues waived" : "" ) );

        } else {
            component.add( new AttributeModifier( "class", new Model<String>( "error" ) ) );
            addTipTitle( component, new Model<String>( summary ) );
        }
    }

    public ChannelsUser getUser() {
        return ChannelsUser.current();
    }

    /**
     * Get current plan.
     *
     * @return a plan
     */
    @Override
    public Plan getPlan() {
        Plan plan = getUser().getPlan();
        if ( plan == null ) {
            return getCommunityService().getPlan();
        } else {
            return plan;
        }
    }

    public String getPlanCommunityUri() {
        String uri = getUser().getPlanCommunityUri();
        return uri == null ? getPlan().getUri() : uri;
    }

    protected String planVersionUri() {
        return getPlan().getVersionUri();
    }

    /**
     * Whether or not the idenfiable is collapsed.
     *
     * @param identifiable an identifiable
     * @return a boolean
     */
    protected boolean isCollapsed( Identifiable identifiable ) {
        return !isExpanded( identifiable );
    }

    /**
     * Whether or not the idenfiable is expanded.
     *
     * @param identifiable an identifiable
     * @return a boolean
     */
    protected boolean isExpanded( Identifiable identifiable ) {
        return isExpanded( identifiable.getId() );
    }

    /**
     * Whether or not the id is expanded.
     *
     * @param id an long
     * @return a boolean
     */
    protected boolean isExpanded( long id ) {
        return getExpansions().contains( id );
    }

    /**
     * Return an actionalble label declaring that another user is editing.
     *
     * @param id           a string
     * @param identifiable an identifiable
     * @param username     a string
     * @return a label
     */
    protected Label editedByLabel( String id, final Identifiable identifiable, final String username ) {
        Label label = new Label( id, "(Edited by " + getQueryService().findUserFullName( username ) + ")" );
        label.add( new AttributeModifier( "class", new Model<String>( "disabled pointer" ) ) );
        addTipTitle( label, new Model<String>( "Click to send a message" ) );
        label.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Communicated, identifiable, username ) );
            }
        } );
        return label;
    }

    protected LinkMenuItem editedByLinkMenuItem( String id, final Identifiable identifiable, final String username ) {
        LinkMenuItem linkMenuItem = new LinkMenuItem(
                "menuItem",
                new Model<String>( "(Edited by " + getQueryService().findUserFullName( username ) + ")" ),
                new AjaxLink<String>( "link" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        update( target, new Change( Change.Type.Communicated, identifiable, username ) );
                    }
                }
        );
        addTipTitle( linkMenuItem, new Model<String>( "Click to send a message" ) );
        return linkMenuItem;
    }

    /**
     * Return a label indicating a time out.
     *
     * @param id a string
     * @return a label
     */
    protected Label timeOutLabel( String id ) {
        Label label = new Label( id, new Model<String>( getPlan().isDevelopment() ? "Timed out" : "" ) );
        label.add( new AttributeModifier( "class", new Model<String>( "disabled timed-out" ) ) );
        return label;
    }

    protected LinkMenuItem timeOutLinkMenuItem( String id ) {
        LinkMenuItem linkMenuItem = new LinkMenuItem(
                "menuItem",
                new Model<String>( "Timed out" ),
                new AjaxLink<String>( "link" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        //Do nothing
                    }
                }
        );
        linkMenuItem.add( new AttributeModifier( "class", "disabled" ) );
        return linkMenuItem;
    }


    public void redisplay( AjaxRequestTarget target ) {
        target.add( this );
    }

    protected PlanPage planPage() {
        Page page = getPage();
        return page instanceof PlanPage ? (PlanPage) page : null;
    }

    public CommanderFactory getCommanderFactory() {
        return commanderFactory;
    }

    /**
     * Add issues annotations to a component.
     *
     * @param component the component
     * @param object    the object of the issues
     * @param property  the property of concern. If null, get issues of object
     */
    protected void addIssuesAnnotation( FormComponent<?> component, ModelObject object, String property ) {
        addIssuesAnnotation( component, object, property, "error" );
    }

    /**
     * Add issues annotations to a component.
     *
     * @param component the component
     * @param object    the object of the issues
     * @param property  the property of concern. If null, get issues of object
     */
    protected void addIssuesAnnotation( FormComponent<?> component, ModelObject object, String property,
                                        String errorClass ) {
        Analyst analyst = ( (Channels) getApplication() ).getAnalyst();
        String summary = property == null ?
                analyst.getIssuesSummary( getQueryService(), object, false ) :
                analyst.getIssuesSummary( getQueryService(), object, property );
        boolean hasIssues = analyst.hasIssues( getQueryService(), object, Analyst.INCLUDE_PROPERTY_SPECIFIC );
        if ( !summary.isEmpty() ) {
            component.add( new AttributeModifier( "class", new Model<String>( errorClass ) ) );
            addTipTitle( component, new Model<String>( summary ) );
        } else {
            if ( property == null && hasIssues ) {
                // All waived issues
                component.add( new AttributeModifier( "class", new Model<String>( "waived" ) ) );
                addTipTitle( component, new Model<String>( "All issues waived" ) );
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Actor findActor( ChannelsUser user ) {
        List<UserParticipation> participations = userParticipationService.getActiveUserParticipations(
                getUser(),
                getCommunityService() );
        List<Actor> actors = (List<Actor>) CollectionUtils.collect(
                participations,
                new Transformer() {
                    @Override
                    public Object transform( Object input ) {
                        return ( (UserParticipation) input ).getAgent( getCommunityService() ).getActor();
                    }
                }
        );
        Collections.sort(
                actors,
                new Comparator<Actor>() {
                    @Override
                    public int compare( Actor a1, Actor a2 ) {
                        if ( a1.isSingularParticipation() ) return -1;
                        if ( a2.isSingularParticipation() ) return 1;
                        if ( a1.isAnonymousParticipation() ) return 1;
                        if ( a2.isAnonymousParticipation() ) return -1;
                        return 0;
                    }
                }
        );
        if ( actors.size() > 0 ) {
            return actors.get( 0 );
        } else {
            return null;
        }
    }

    protected String getUserFullName( String userName ) {
        if ( userName == null ) {
            return "?";
        } else {
            ChannelsUser aUser = userInfoService.getUserWithIdentity( userName );
            return aUser == null ? userName : aUser.getFullName();
        }
    }

    protected AutoCompleteSettings getAutoCompleteSettings() {
        AutoCompleteSettings settings = new AutoCompleteSettings();
        settings.setUseSmartPositioning( true );
        settings.setAdjustInputWidth( true );
        return settings;
    }

    protected PlanCommunity getPlanCommunity() {
        return getUser().getPlan() != null  // domain context else community context
                ? planCommunityManager.getDomainPlanCommunity( getUser().getPlan() )
                : planCommunityManager.getPlanCommunity( getPlanCommunityUri() );
    }

    protected PlanCommunity getDomainPlanCommunity() {
        return planCommunityManager.getPlanCommunity( getPlanCommunity().getPlanUri() );
    }

    protected CommunityService getCommunityService() {
        return getCommunityService( getPlanCommunity() );
    }

    protected CommunityService getCommunityService( PlanCommunity planCommunity ) {
        return communityServiceFactory.getService( planCommunity );
    }

    protected PlanService getPlanService() {
        return getCommunityService().getPlanService();
    }

    protected Place getPlanLocale() {
        return getPlanService().getPlanLocale();
    }

    protected Form getForm() {
        return ( (AbstractChannelsBasicPage) getPage() ).getForm();
    }

    protected Component addTipTitle( Component component, String title ) {
        return addTipTitle( component, new Model<String>( title ) );
    }

    protected Component addTipTitle( Component component, String title, boolean keepAlive ) {
        return addTipTitle( component, new Model<String>( title ), keepAlive );
    }

    protected Component addTipTitle( Component component, IModel<String> titleModel ) {
        return addTipTitle( component, titleModel, false );
    }

    protected Component addTipTitle( Component component, IModel<String> titleModel, boolean keepAlive ) {
        TipTipOptions options = new TipTipOptions().maxWidth( "400px" ).keepAlive( keepAlive );
        component.add( new AttributeModifier( "title", titleModel ) );
        component.add( new TipTipBehavior( options ) );
        return component;
    }

    public String makeCommunityPageUrl( PlanCommunity planCommunity ) {
        try {
            PageParameters parameters = new PageParameters();
            parameters.set(
                    AbstractChannelsBasicPage.COMMUNITY_PARM,
                    URLEncoder.encode( planCommunity.getUri(), "UTF-8" ) );
            return urlFor( CommunityPage.class, parameters ).toString();
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    protected boolean isPlanner() {
        return getUser().isPlannerOrAdmin( getPlanCommunity().getPlanUri() );
    }

    protected Component makeHelpIcon( String id, final Guidable guidable, String iconSrc ) {
        WebMarkupContainer helpIcon = new WebMarkupContainer( id );
        helpIcon.setOutputMarkupId( true );
        helpIcon.add( new AttributeModifier( "src", iconSrc) );
        helpIcon.add( new AttributeModifier( "alt", "Help") );
        addTipTitle( helpIcon, "Quick help" );
        helpIcon.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update(  target, Change.guide( guidable.getHelpSectionId(), guidable.getHelpTopicId() ) );
            }
        } );
        return helpIcon;
    }

    protected Component makeHelpIcon( String id, final Guidable guidable ) {
            return makeHelpIcon( id, guidable, "images/help_guide.png" );
     }


    protected Component makeHelpIcon( String id, final String sectionId, final String topicId, String iconSrc ) {
        WebMarkupContainer helpIcon = new WebMarkupContainer( id );
        helpIcon.setOutputMarkupId( true );
        helpIcon.add( new AttributeModifier( "src", iconSrc) );
        helpIcon.add( new AttributeModifier( "alt", "Help") );
        addTipTitle( helpIcon, "Quick help" );
        helpIcon.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update(  target, Change.guide( sectionId, topicId ) );
            }
        } );
        return helpIcon;
    }

    protected Component makeHelpIcon( String id,
                                      final String userRoleId,
                                      final String sectionId,
                                      final String topicId,
                                      String iconSrc ) {
        WebMarkupContainer helpIcon = new WebMarkupContainer( id );
        helpIcon.setOutputMarkupId( true );
        helpIcon.add( new AttributeModifier( "src", iconSrc) );
        helpIcon.add( new AttributeModifier( "alt", "Help") );
        addTipTitle( helpIcon, "Quick help" );
        helpIcon.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update(  target, Change.guide( userRoleId, sectionId, topicId ) );
            }
        } );
        return helpIcon;
    }

    protected Component makeHelpIcon( String id, final String sectionId, final String topicId ) {
        return makeHelpIcon( id, sectionId, topicId, "images/help_guide.png" );
    }

    protected void addInputHint( AbstractTextComponent textComponent, String hint ) {
        textComponent.add( new AttributeModifier( "placeholder", ChannelsUtils.sanitizeAttribute( hint ) ) );
    }

}
