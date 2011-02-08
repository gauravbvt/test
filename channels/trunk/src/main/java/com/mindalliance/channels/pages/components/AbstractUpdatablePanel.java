package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract base class of updatable panels.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 27, 2009
 * Time: 7:30:31 PM
 */
public class AbstractUpdatablePanel extends Panel implements Updatable {

    @SpringBean
    private QueryService queryService;

    /**
     * String comparator for equality tests.
     */
    private static final Collator COMPARATOR = Collator.getInstance();
    /**
     * Model on an identifiable.
     */
    private IModel<? extends Identifiable> model = null;
    /**
     * Ids of expanded model objects.
     */
    private Set<Long> expansions;

    /**
     * Name pattern.
     */
    private Pattern namePattern = Pattern.compile( "^.*?(\\(\\d+\\))?$" );

    public AbstractUpdatablePanel( String id ) {
        super( id );
        setOutputMarkupId( true );
    }

    public AbstractUpdatablePanel(
            String id,
            IModel<? extends Identifiable> model ) {
        this( id, model, null );
    }

    public AbstractUpdatablePanel(
            String id,
            IModel<? extends Identifiable> model,
            Set<Long> expansions ) {
        super( id, model );
        setOutputMarkupId( true );
        this.model = model;
        this.expansions = expansions;
    }

    protected IModel<? extends Identifiable> getModel() {
        return model;
    }

    /**
     * Get the query service.
     *
     * @return a query service
     */
    protected QueryService getQueryService() {
        return queryService;
    }

    /**
     * Get an analyst.
     *
     * @return an analyst
     */
    protected Analyst getAnalyst() {
        return getChannels().getAnalyst();
    }

    /**
     * Get diagram factory.
     *
     * @return diagram factory
     */
    protected DiagramFactory getDiagramFactory() {
        return getChannels().getDiagramFactory();
    }

    /**
     * Get the active plan's commander.
     *
     * @return a commander
     */
    protected Commander getCommander() {
        return getChannels().getCommander( User.plan() );
    }

    /**
     * Get plan manager.
     *
     * @return the plan manager
     */
    protected PlanManager getPlanManager() {
        return queryService.getPlanManager();
    }

    /**
     * Get the lock manager.
     *
     * @return a lock manager
     */
    protected LockManager getLockManager() {
        return getChannels().getLockManager();
    }

    private Channels getChannels() {
        return (Channels) getApplication();
    }

    /**
     * Set and update a component's visibility.
     *
     * @param target    an ajax request target
     * @param component a component
     * @param visible   a boolean
     */
    protected static void makeVisible( AjaxRequestTarget target, Component component,
                                       boolean visible ) {
        makeVisible( component, visible );
        target.addComponent( component );
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
     */
    protected static void makeVisible( Component component, boolean visible ) {
        component.add(
                new AttributeModifier(
                        "style",
                        true,
                        new Model<String>( visible ? "display:inline" : "display:none" ) ) );
    }

    /**
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        Updatable updatableParent = findParent( Updatable.class );
        if ( updatableParent != null )
            updatableParent.changed( change );
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Updatable updatableParent = findParent( Updatable.class );
        if ( updatableParent != null ) {
            updated.add( this );
            updatableParent.updateWith( target, change, updated );
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public void refresh( AjaxRequestTarget target, Change change ) {
        refresh( target, change, new ArrayList<Updatable>() );
    }

    /**
     * {@inheritDoc}
     */
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        refresh( target, change, updated, null );
    }

    /**
     * {@inheritDoc}
     */
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated, String aspect ) {
        if ( !updated.contains( this ) && !change.isNone() ) {
            refresh( target, change, aspect );
            //   target.addComponent( this );
        }
    }

    /**
     * Refresh given change
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
        for ( String taken : queryService.findAllEntityNames( entity.getClass() ) ) {
            if ( taken.equals( entity.getName() ) )
                choices.add( taken );
            else {
                Matcher matcher = namePattern.matcher( taken );
                int count = matcher.groupCount();
                if ( count > 1 ) {
                    String group = matcher.group( 0 );
                    int index = Integer.valueOf( group.substring( 1, group.length() - 2 ) );
                    choices.add(
                        taken.substring( 0, taken.lastIndexOf( '(' ) - 1 )
                        + '(' + ( index + 1 ) + ')'
                    );
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
        Analyst analyst = getAnalyst();
        String summary = property == null ?
                analyst.getIssuesSummary( object, false ) :
                analyst.getIssuesSummary( object, property );

        boolean hasIssues = property == null ?
                analyst.hasIssues( object, Analyst.INCLUDE_PROPERTY_SPECIFIC ) :
                analyst.hasIssues( object, property );

        if ( summary.isEmpty() )
            component.add(
                new AttributeModifier(
                    "class", true,
                    new Model<String>( hasIssues ? "waived" : "no-error" ) ),
                new AttributeModifier(
                    "title", true,
                     new Model<String>( hasIssues ? "All issues waived" : "" ) ) );
        else
            component.add(
                new AttributeModifier( "class", true, new Model<String>( "error" ) ),
                new AttributeModifier( "title", true, new Model<String>( summary ) ) );
    }

    /**
     * Get current plan.
     *
     * @return a plan
     */
    protected Plan getPlan() {
        return User.plan();
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
     * @param id       a string
     * @param identifiable an identifiable
     * @param username a string
     * @return a label
     */
    protected Label editedByLabel( String id, final Identifiable identifiable, final String username ) {
        Label label = new Label(
                id, "(Edited by " + queryService.findUserFullName( username ) + ")" );
        label.add(
                new AttributeModifier( "class", true, new Model<String>( "disabled pointer" ) ) );
        label.add(
                new AttributeModifier( "title", true, new Model<String>( "Click to send a message" ) ) );
        label.add( new AjaxEventBehavior( "onclick" ) {
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Communicated, identifiable, username ) );
            }
        } );
        return label;
    }

    /**
     * Return a label indicating a time out.
     *
     * @param id a string
     * @return a label
     */
    protected Label timeOutLabel( String id ) {
        Label label = new Label(
                id, new Model<String>( getPlan().isDevelopment() ? "Timed out" : "") );
        label.add( new AttributeModifier( "class", true, new Model<String>( "disabled timed-out" ) ) );
        return label;
    }

}
