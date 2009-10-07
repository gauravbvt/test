package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.pages.components.AbstractIssueTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
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

    private static final int MAX_ROWS = 10;

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

    public EntityIssuesPanel( String id, IModel<ModelEntity> model ) {
        super( id, model, MAX_ROWS );
    }

    public ModelEntity getEntity() {
        return (ModelEntity) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Issue> getIssues() {
        Set<ModelObject> scope = new HashSet<ModelObject>();
        List<Issue> issues = new ArrayList<Issue>();
        ModelEntity about = (ModelEntity)getAbout();
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
                if ( getEntity() instanceof Phase ) {
                    for ( ModelObject containedModelObject :
                            getQueryService().findAllModelObjectsIn( (Phase)getEntity()) ) {
                        scope.add( containedModelObject );
                    }
                } else {
                    for ( ModelEntity containedEntity : findContainedEntities() ) {
                        scope.add( containedEntity );
                        if ( includeFromScenarios ) {
                            scope.addAll( findRelatedScenarioObjects( containedEntity ) );
                        }
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

    /**
     * {@inheritDoc}
     */
    protected void addIncluded() {
        // containment (for non-events only)
        WebMarkupContainer containmentContainer = new WebMarkupContainer( "containmentContainer" );
        makeVisible( containmentContainer, !( getEntity() instanceof Event ) );
        add( containmentContainer );
        CheckBox includeContainedCheckBox = new CheckBox(
                "includeContained",
                new PropertyModel<Boolean>( this, "includeContained" ) );
        includeContainedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        Label containmentLabel = new Label( "containment", getContainmentLabel() );
        containmentContainer.add( containmentLabel );
        containmentContainer.add( includeContainedCheckBox );
        // Including waived
        CheckBox includeWaivedCheckBox = new CheckBox(
                "includeWaived",
                new PropertyModel<Boolean>( this, "includeWaived" ) );
        includeWaivedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        add( includeWaivedCheckBox );
        // Include from scenario
        WebMarkupContainer inScenarioContainer = new WebMarkupContainer( "inScenarioContainer" );
        add( inScenarioContainer );
        CheckBox includeFromScenario = new CheckBox(
                "includeFromScenarios",
                new PropertyModel<Boolean>( this, "includeFromScenarios" ) );
        includeFromScenario.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        inScenarioContainer.add( includeFromScenario );
        makeVisible( inScenarioContainer, !( getEntity() instanceof Phase ) );
    }

    private String getContainmentLabel() {
        ModelEntity entity = getEntity();
        if ( entity instanceof Actor ) {
            return "roles played by this actor";
        } else if ( entity instanceof Role ) {
            return "actors playing this role";
        } else if ( entity instanceof Organization ) {
            return "actors and roles in this organization";
        } else if ( entity instanceof Place ) {
            return "anything located within this place";
        } else if ( entity instanceof Event ) {
            return "";
        } else if ( entity instanceof Phase ) {
            return "anything within this phase";
        } else {
            throw new IllegalStateException( "Can't diplay issue table for " + entity.getClass().getSimpleName() );
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<? extends ModelEntity> findContainedEntities() {
        ModelEntity entity = getEntity();
        QueryService queryService = getQueryService();
        if ( entity instanceof Actor ) {
            return queryService.findAllRolesOf( (Actor) entity );
        } else if ( entity instanceof Role ) {
            return queryService.findAllActors( ResourceSpec.with( entity ) );
        } else if ( entity instanceof Organization ) {
            List<ModelEntity> inOrg = new ArrayList<ModelEntity>();
            inOrg.addAll( queryService.findRolesIn( (Organization) entity ) );
            inOrg.addAll( queryService.findAllActorsInOrganization( (Organization) entity ) );
            return inOrg;
        } else if ( entity instanceof Place ) {
            return queryService.findAllEntitiesIn( (Place) entity );
        } else if ( entity instanceof Event ) {
            return new ArrayList<ModelEntity>();
        } else if ( entity instanceof Phase ) {
            return queryService.findAllEntitiesIn( (Phase) entity );
        } else {
            throw new IllegalStateException( "Can't diplay issue table for " + entity.getClass().getSimpleName() );
        }
    }

    private List<ModelObject> findRelatedScenarioObjects( ModelEntity entity ) {
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
