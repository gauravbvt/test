package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Commander;
import com.mindalliance.channels.DiagramFactory;
import com.mindalliance.channels.LockManager;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.Updatable;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

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
        return getChannels().getQueryService();
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
        return getChannels().getCommander();
    }

    /**
     * Get plan manager.
     *
     * @return the plan manager
     */
    protected PlanManager getPlanManager() {
        return getQueryService().getPlanManager();
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
        return Channels.instance();
    }

    /**
     * Set and update a component's visibility.
     *
     * @param target    an ajax request target
     * @param component a component
     * @param visible   a boolean
     */
    protected static void makeVisible(
            AjaxRequestTarget target,
            Component component,
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
    public void updateWith( AjaxRequestTarget target, Change change ) {
        Updatable updatableParent = findParent( Updatable.class );
        if ( updatableParent != null ) updatableParent.updateWith( target, change );
    }

    /**
     * {@inheritDoc}
     */
    public void update( AjaxRequestTarget target, Object object, String action ) {
        // Do nothing
    }

    /**
     * Send changed event and then the updateWith event.
     *
     * @param target an ajax request target
     * @param change the nature of the change
     */
    protected void update( AjaxRequestTarget target, Change change ) {
        changed( change );
        updateWith( target, change );
    }

    /**
     * Test if strings are equivalent.
     *
     * @param name   the new name
     * @param target the original name
     * @return true if strings are equivalent
     */
    protected static boolean isSame( String name, String target ) {
        return COMPARATOR.compare( name, target ) == 0;
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
        List<String> namesTaken = getQueryService().findAllEntityNames( entity.getClass() );
        for ( String taken : namesTaken ) {
            if ( taken.equals( entity.getName() ) ) {
                choices.add( taken );
            } else {
                Matcher matcher = namePattern.matcher( taken );
                int count = matcher.groupCount();
                if ( count > 1 ) {
                    String group = matcher.group( 0 );
                    int index = Integer.valueOf( group.substring( 1, group.length() - 2 ) );
                    String newTaken = taken.substring( 0, taken.lastIndexOf( '(' ) - 1 ) + "(" + ( index + 1 ) + ")";
                    choices.add( newTaken );
                } else {
                    choices.add( taken + "(2)" );
                }
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
        Analyst analyst = ( (Channels) getApplication() ).getAnalyst();
        String summary = property == null ?
                analyst.getIssuesSummary( object, false ) :
                analyst.getIssuesSummary( object, property );
        boolean hasIssues = property == null ?
                analyst.hasIssues( object, Analyst.INCLUDE_PROPERTY_SPECIFIC ) :
                analyst.hasIssues( object, property );
        if ( !summary.isEmpty() ) {
            component.add(
                    new AttributeModifier(
                            "class", true, new Model<String>( "error" ) ) );
            component.add(
                    new AttributeModifier(
                            "title", true, new Model<String>( summary ) ) );                // NON-NLS
        } else {
            if ( hasIssues ) {
                // All waived issues
                component.add(
                        new AttributeModifier( "class", true, new Model<String>( "waived" ) ) );
                component.add(
                        new AttributeModifier( "title", true, new Model<String>( "All issues waived" ) ) );
            } else {
                component.add(
                        new AttributeModifier(
                                "class", true, new Model<String>( "no-error" ) ) );
                component.add(
                        new AttributeModifier(
                                "title", true, new Model<String>( "" ) ) );
            }
        }
    }

    /**
     * Get current plan.
     *
     * @return a plan
     */
    protected Plan getPlan() {
        return PlanManager.plan();
    }
}
