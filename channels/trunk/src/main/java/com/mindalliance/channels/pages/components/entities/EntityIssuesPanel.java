package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.pages.components.AbstractIssueTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 25, 2009
 * Time: 5:39:31 PM
 */
public class EntityIssuesPanel extends AbstractIssueTablePanel {

    private static final int MAX_ROWS = 12;

    /**
     * Whether to show waived issues.
     */
    private boolean includeWaived = false;
    /**
     * Whether to show issues of "contained" entities.
     */
    private boolean includeContained = false;
    /**
     * Whether to show relevant issues from scenarios.
     */
    private boolean includeFromScenarios = false;

    public EntityIssuesPanel( String id, IModel<ModelObject> model ) {
        super( id, model, MAX_ROWS );
    }

    public ModelObject getEntity() {
        return (ModelObject) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Issue> getIssues() {
        Set<ModelObject> scope = new HashSet<ModelObject>();
        List<Issue> issues = new ArrayList<Issue>();
        ModelObject about = getAbout();
        final String issueType = getIssueType();
        Analyst analyst = getAnalyst();
        if ( about != null ) {
            scope.add( about );
        } else {
            scope.add( getEntity() );
            if ( includeFromScenarios ) {
                scope.addAll( findRelatedScenarioObjects( getEntity() ) );
            }
            if ( includeContained ) {
                for ( ModelObject containedEntity : findContainedEntities() ) {
                    scope.add( containedEntity );
                    if ( includeFromScenarios ) {
                        scope.addAll( findRelatedScenarioObjects( containedEntity ) );
                    }
                }
            }
        }
        for ( ModelObject mo : scope ) {
            issues.addAll( analyst.listIssues( mo, true, includeWaived ) );
        }
        return (List<Issue>) CollectionUtils.select(
                issues,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( issueType.equals( ALL )
                                || ( (Issue) obj ).getType().equals( issueType ) );
                    }
                }
        );
    }

    protected void addIncluded() {
        CheckBox includeWaivedCheckBox = new CheckBox(
                "includeWaived",
                new PropertyModel<Boolean>( this, "includeWaived" ) );
        includeWaivedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        add( includeWaivedCheckBox );
        CheckBox includeContainedCheckBox = new CheckBox(
                "includeContained",
                new PropertyModel<Boolean>( this, "includeContained" ) );
        includeContainedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        Label containmentLabel = new Label( "containment", getContainmentLabel() );
        add( containmentLabel );
        add( includeContainedCheckBox );
        CheckBox includeFromScenario = new CheckBox(
                "includeFromScenarios",
                new PropertyModel<Boolean>( this, "includeFromScenarios" ) );
        includeFromScenario.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        add( includeFromScenario );
    }

    private String getContainmentLabel() {
        ModelObject entity = getEntity();
        if ( entity instanceof Actor ) {
            return "roles played by this actor";
        } else if ( entity instanceof Role ) {
            return "actors playing this role";
        } else if ( entity instanceof Organization ) {
            return "actors and roles in this organization";
        } else {
            throw new IllegalStateException( "Can't diplay issue table for " + entity.getClass().getSimpleName() );
        }
    }

    private List<? extends ModelObject> findContainedEntities() {
        ModelObject entity = getEntity();
        QueryService queryService = getQueryService();
        if ( entity instanceof Actor ) {
            return queryService.findAllRolesOf( (Actor) entity );
        } else if ( entity instanceof Role ) {
            return queryService.findAllActors( ResourceSpec.with( entity ) );
        } else if ( entity instanceof Organization ) {
            List<ModelObject> inOrg = new ArrayList<ModelObject>();
            inOrg.addAll( queryService.findRolesIn( (Organization) entity ) );
            inOrg.addAll( queryService.findAllActorsInOrganization( (Organization) entity ) );
            return inOrg;
        } else {
            throw new IllegalStateException( "Can't diplay issue table for " + entity.getClass().getSimpleName() );
        }
    }

    private List<ModelObject> findRelatedScenarioObjects( ModelObject entity ) {
        return getQueryService().findAllScenarioObjectsInvolving( entity );
    }


    public boolean isIncludeWaived() {
        return includeWaived;
    }

    public void setIncludeWaived( boolean includeWaived ) {
        this.includeWaived = includeWaived;
    }

    public boolean isIncludeContained() {
        return includeContained;
    }

    public void setIncludeContained( boolean includeContained ) {
        this.includeContained = includeContained;
    }

    public boolean isIncludeFromScenarios() {
        return includeFromScenarios;
    }

    public void setIncludeFromScenarios( boolean includeFromScenarios ) {
        this.includeFromScenarios = includeFromScenarios;
    }
}
