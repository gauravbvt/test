/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.entities.issues;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.Doctor;
import com.mindalliance.channels.pages.components.AbstractIssueTablePanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
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

public class EntityIssuesPanel extends AbstractIssueTablePanel implements Guidable {

    private static final int MAX_ROWS = 10;

    /**
     * Whether to show waived issues.
     */
    private boolean includeWaived;

    /**
     * Whether to show issues of "contained" entities.
     */
    private boolean includeContained;

    /**
     * Whether to show relevant issues from segments.
     */
    private boolean includeFromSegments;

    public EntityIssuesPanel( String id, IModel<ModelEntity> model ) {
        super( id, model, MAX_ROWS );
    }

    @Override
    public String getHelpSectionId() {
        return "analyzing";
    }

    @Override
    public String getHelpTopicId() {
        return "entity-issues";
    }

    public ModelEntity getEntity() {
        return (ModelEntity) getModel().getObject();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Issue> getIssues() {
        Set<ModelObject> scope = new HashSet<ModelObject>();
        List<Issue> issues = new ArrayList<Issue>();
        ModelObject about = (ModelObject)getAbout();
        final String issueType = getIssueType();
        Doctor doctor = getCommunityService().getDoctor();
        if ( about == null ) {
            scope.add( getEntity() );
            if ( includeFromSegments )
                scope.addAll( findRelatedSegmentObjects( getEntity() ) );

            if ( includeContained ) {
                if ( getEntity() instanceof Phase ) {
                    for ( ModelObject containedModelObject : getQueryService().findAllModelObjectsIn( (Phase) getEntity() ) )
                        scope.add( containedModelObject );
                } else {
                    for ( ModelEntity containedEntity : findContainedEntities() ) {
                        scope.add( containedEntity );
                        if ( includeFromSegments )
                            scope.addAll( findRelatedSegmentObjects( containedEntity ) );
                    }
                }
            }
        } else
            scope.add( about );

        for ( ModelObject mo : scope )
            issues.addAll( doctor.listIssues( getCommunityService(), mo, true, includeWaived ) );

        return (List<Issue>) CollectionUtils.select( issues, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ( issueType.equals( ALL ) || ( (Issue) object ).getType().equals( issueType ) );
            }
        } );
    }

    @Override
    protected void addFilters() {
        // containment (for non-events only)
        WebMarkupContainer containmentContainer = new WebMarkupContainer( "containmentContainer" );
        containmentContainer.setOutputMarkupId( true );
        makeVisible( containmentContainer, !( getEntity() instanceof Event ) );
        addOrReplace( containmentContainer );
        CheckBox includeContainedCheckBox =
                new CheckBox( "includeContained", new PropertyModel<Boolean>( this, "includeContained" ) );
        includeContainedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        Label containmentLabel = new Label( "containment", getContainmentLabel() );
        containmentContainer.add( containmentLabel );
        containmentContainer.add( includeContainedCheckBox );
        // Including waived
        CheckBox includeWaivedCheckBox =
                new CheckBox( "includeWaived", new PropertyModel<Boolean>( this, "includeWaived" ) );
        includeWaivedCheckBox.setOutputMarkupId( true );
        includeWaivedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        addOrReplace( includeWaivedCheckBox );
        // Include from segment
        WebMarkupContainer inSegmentContainer = new WebMarkupContainer( "inSegmentContainer" );
        inSegmentContainer.setOutputMarkupId( true );
        addOrReplace( inSegmentContainer );
        CheckBox includeFromSegments =
                new CheckBox( "includeFromSegments", new PropertyModel<Boolean>( this, "includeFromSegments" ) );
        includeFromSegments.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateIssuesTable( target );
            }
        } );
        inSegmentContainer.add( includeFromSegments );
        makeVisible( inSegmentContainer, !( getEntity() instanceof Phase ) );
    }

    private String getContainmentLabel() {
        ModelEntity entity = getEntity();
        if ( entity instanceof Actor ) {
            return entity.isActual() ? "roles played by this agent" : "roles played by this type of agent";
        } else if ( entity instanceof Role ) {
            return entity.isActual() ? "agents playing this role" : "agents playing this type of role";
        } else if ( entity instanceof Organization ) {
            return entity.isActual() ?
                   "agents and roles in this organization" :
                   "agents and roles in this type of organization";
        } else if ( entity instanceof Place ) {
            return entity.isActual() ?
                   "anything located within this place" :
                   "anything located within this type of place";
        } else if ( entity instanceof Event ) {
            return "";
        } else if ( entity instanceof Phase ) {
            return "anything within this phase";
        } else if ( entity instanceof TransmissionMedium ) {
            return "any transmission medium delegated to";
        } else if ( entity instanceof InfoProduct ) {
            return "";
        } else if ( entity instanceof InfoFormat ) {
            return "";
        } else if ( entity instanceof Function ) {
            return "";
        } else if ( entity instanceof MaterialAsset ) {
            return "any assets this one depends on";
        } else {
            throw new IllegalStateException( "Can't display issue table for " + entity.getClass().getSimpleName() );
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<? extends ModelEntity> findContainedEntities() {
        ModelEntity entity = getEntity();
        QueryService queryService = getQueryService();
        if ( entity instanceof Actor ) {
            return queryService.findAllRolesOf( (Actor) entity );
        } else if ( entity instanceof Role ) {
            return queryService.findAllActualActors( new ResourceSpec( (Role) entity ) );
        } else if ( entity instanceof Organization ) {
            List<ModelEntity> inOrg = new ArrayList<ModelEntity>();
            Assignments assignments = queryService.getAssignments().with( (Specable) entity );
            inOrg.addAll( assignments.getRoles() );
            inOrg.addAll( assignments.getActualActors() );
            return inOrg;
        } else if ( entity instanceof Place ) {
            return queryService.findAllEntitiesIn( (Place) entity );
        } else if ( entity instanceof Event ) {
            return new ArrayList<ModelEntity>();
        } else if ( entity instanceof Phase ) {
            return queryService.findAllEntitiesIn( (Phase) entity );
        } else if ( entity instanceof TransmissionMedium ) {
            return queryService.findAllEntitiesIn( (TransmissionMedium) entity );
        } else if ( entity instanceof InfoProduct ) {
            return new ArrayList<InfoProduct>();
        } else if ( entity instanceof InfoFormat ) {
            return new ArrayList<InfoFormat>();
        } else if ( entity instanceof Function ) {
            return new ArrayList<Function>();
        } else if ( entity instanceof MaterialAsset ) {
            return queryService.findAllEntitiesIn( (MaterialAsset) entity );
        } else {
            throw new IllegalStateException( "Can't display issue table for " + entity.getClass().getSimpleName() );
        }
    }

    private List<ModelObject> findRelatedSegmentObjects( ModelEntity entity ) {
        return getQueryService().findAllSegmentObjectsInvolving( entity );
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

    public boolean isIncludeFromSegments() {
        return includeFromSegments;
    }

    public void setIncludeFromSegments( boolean includeFromSegments ) {
        this.includeFromSegments = includeFromSegments;
    }
}
