/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FilterableEntityFlowsPanel<T extends ModelEntity> extends AbstractUpdatablePanel implements Filterable {

    /**
     * Default page size for external flows panel.
     */
    private static final int PAGE_SIZE = 10;

    /**
     * Selected entity relationship.
     */
    private EntityRelationship<T> selectedEntityRel;

    private Class<T> entityClass;

    private Segment segment;

    /**
     * Selected entity.
     */
    private T selectedEntity;

    /**
     * Filter on flow information.
     */
    private String informationFilter = "";

    /**
     * Filter on flow information.
     */
    private String taskFilter = "";

    /**
     * Filters on flow attributes that are identifiable.
     */
    private Map<String, Identifiable> identifiableFilters = new HashMap<String, Identifiable>();

    /**
     * Network flows panel.
     */
    private Component flowsPanel;

    public FilterableEntityFlowsPanel( String id, Class<T> entityClass, Segment segment, Set<Long> expansions,
                                       T selectedEntity, EntityRelationship<T> selectedEntityRel ) {
        super( id, null, expansions );
        this.entityClass = entityClass;
        this.segment = segment;
        this.selectedEntity = selectedEntity;
        this.selectedEntityRel = selectedEntityRel;
        init();
    }

    private void init() {
        addFlowsTitleLabel();
        addFilters();
        addFlowsPanel();
    }

    private void addFlowsTitleLabel() {
        Label flowsTitleLabel = new Label( "flows-title", new PropertyModel<String>( this, "flowsTitle" ) );
        flowsTitleLabel.setOutputMarkupId( true );
        add( flowsTitleLabel );
    }

    private void addFilters() {
        TextField<String> infoFilterField =
                new TextField<String>( "infoFilter", new PropertyModel<String>( this, "informationFilter" ) );
        infoFilterField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addFlowsPanel();
                target.add( flowsPanel );
            }
        } );
        add( infoFilterField );
        TextField<String> taskFilterField =
                new TextField<String>( "taskFilter", new PropertyModel<String>( this, "taskFilter" ) );
        taskFilterField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addFlowsPanel();
                target.add( flowsPanel );
            }
        } );
        add( taskFilterField );
    }

    private void addFlowsPanel() {
        if ( getEntity() instanceof Actor ) {
            flowsPanel = new ActorFlowsPanel( "flows",
                                              new PropertyModel<ArrayList<ActorFlow>>( this, "actorFlows" ),
                                              PAGE_SIZE,
                                              this );
        } else {
            flowsPanel =
                    new RoleFlowsPanel( "flows", new PropertyModel<ArrayList<Flow>>( this, "flows" ), PAGE_SIZE, this );
        }
        flowsPanel.setOutputMarkupId( true );
        addOrReplace( flowsPanel );
    }

    /**
     * Get flows title.
     *
     * @return a string
     */
    @SuppressWarnings( "unchecked" )
    public String getFlowsTitle() {
        if ( selectedEntityRel != null ) {
            T fromEntity = (T) selectedEntityRel.getFromIdentifiable( getQueryService() );
            T toEntity = (T) selectedEntityRel.getToIdentifiable( getQueryService() );
            if ( fromEntity == null || toEntity == null ) {
                return "*** You need to refresh ***";
            } else {
                return "Flows from \"" + fromEntity.getName() + "\" to \"" + toEntity.getName() + "\"";
            }
        } else if ( getEntity() != null ) {
            return "All flows invoving \"" + getEntity().getName() + "\"";
        } else {
            return "All network flows";
        }
    }

    public String getInformationFilter() {
        return informationFilter;
    }

    public void setInformationFilter( String val ) {
        informationFilter = val == null ? "" : val.toLowerCase();
    }

    public String getTaskFilter() {
        return taskFilter;
    }

    public void setTaskFilter( String val ) {
        taskFilter = val == null ? "" : val.toLowerCase();
    }

    @Override
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        assert property != null;
        if ( identifiable == null || isFiltered( identifiable, property ) ) {
            identifiableFilters.remove( property );
        } else {
            identifiableFilters.put( property, identifiable );
        }
        addFlowsPanel();
        target.add( flowsPanel );
    }

    @Override
    public boolean isFiltered( Identifiable identifiable, String property ) {
        ModelObject mo = (ModelObject) identifiableFilters.get( property );
        return mo != null && mo.equals( identifiable );
    }

    /**
     * Get flows.
     *
     * @return a list of flows
     */
    @SuppressWarnings( "unchecked" )
    public List<Flow> getFlows() {
        List<Flow> flows = new ArrayList<Flow>();
        for ( EntityRelationship entityRel : getEntityRelationships() ) {
            flows.addAll( entityRel.getFlows() );
        }
        return (List<Flow>) CollectionUtils.select( flows, new Predicate() {
            @Override
            public boolean evaluate( Object obj ) {
                return !isFilteredOut( (Flow) obj );
            }
        } );
    }

    @SuppressWarnings( "unchecked" )
    public List<ActorFlow> getActorFlows() {
        List<ActorFlow> actorFlows = new ArrayList<ActorFlow>();
        for ( EntityRelationship entityRel : getEntityRelationships() ) {
            actorFlows.addAll( (List<ActorFlow>) CollectionUtils.select( getActorFlowsInRelationship( entityRel ),
                                                                         new Predicate() {
                                                                             @Override
                                                                             @SuppressWarnings( "unchecked" )
                                                                             public boolean evaluate( Object obj ) {
                                                                                 return !isFilteredOut( (ActorFlow) obj );
                                                                             }
                                                                         } ) );
        }
        return actorFlows;
    }

    private List<EntityRelationship> getEntityRelationships() {
        List<EntityRelationship> rels = new ArrayList<EntityRelationship>();
        if ( selectedEntityRel != null ) {
            rels.add( selectedEntityRel );
        } else {
            ModelEntity entity = getEntity();
            if ( entity != null ) {
                // relationships with a given entity
                rels.addAll( getAnalyst().findEntityRelationships( segment, entity, getQueryService() ) );
            } else {
                // relationships between all actual entities of given class in a segment or entire plan if segment is null
                rels.addAll( getAnalyst().findEntityRelationships( getQueryService(),
                                                                   segment,
                                                                   entityClass,
                                                                   ModelEntity.Kind.Actual ) );
            }
        }
        return rels;
    }

    @SuppressWarnings( "unchecked" )
    private List<ActorFlow> getActorFlowsInRelationship( final EntityRelationship entityRelationship ) {
        return (List<ActorFlow>) CollectionUtils.collect( entityRelationship.getFlows(), new Transformer() {
            @Override
            public Object transform( Object obj ) {
                return new ActorFlow( (Actor) entityRelationship.getFromIdentifiable( getQueryService() ),
                                      (Actor) entityRelationship.getToIdentifiable( getQueryService() ),
                                      (Flow) obj );
            }
        } );
    }

    private boolean isFilteredOut( ActorFlow actorFlow ) {
        if ( !informationFilter.isEmpty()
             && !actorFlow.getFlow().getName().toLowerCase().contains( informationFilter ) )
        {
            return true;
        }
        if ( !taskFilter.isEmpty() ) {
            if ( !( (Part) actorFlow.getFlow().getSource() ).getTask().toLowerCase().contains( taskFilter )
                 && !( (Part) actorFlow.getFlow().getTarget() ).getTask().toLowerCase().contains( taskFilter ) )
            {
                return true;
            }
        } else {
            for ( String property : identifiableFilters.keySet() ) {
                if ( !ModelObject.areEqualOrNull( (ModelObject) identifiableFilters.get( property ),
                                                  (ModelObject) ChannelsUtils.getProperty( actorFlow,
                                                                                           property,
                                                                                           null ) ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isFilteredOut( Flow flow ) {
        if ( !informationFilter.isEmpty() && !flow.getName().toLowerCase().contains( informationFilter ) ) {
            return true;
        }
        if ( !taskFilter.isEmpty() ) {
            if ( !( (Part) flow.getSource() ).getTask().toLowerCase().contains( taskFilter )
                 && !( (Part) flow.getTarget() ).getTask().toLowerCase().contains( taskFilter ) )
            {
                return true;
            }
        } else {
            for ( String property : identifiableFilters.keySet() ) {
                if ( !ModelObject.areEqualOrNull( (ModelObject) identifiableFilters.get( property ),
                                                  (ModelObject) ChannelsUtils.getProperty( flow, property, null ) ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public T getEntity() {
        return selectedEntity;
    }

    /*    @SuppressWarnings( "unchecked" )
    private List<T> getEntities() {
        if ( segment != null ) {
            return getQueryService().listEntitiesTaskedInSegment( entityClass, segment );
        } else {
            return (List<T>) CollectionUtils.select(
                    getQueryService().listActualEntities( entityClass ),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return !( (ModelEntity) object ).isUnknown();
                        }
                    }
            );
        }
    }*/

    public class ActorFlow implements Serializable {

        private Actor sourceActor;

        private Actor targetActor;

        private Flow flow;

        public ActorFlow( Actor sourceActor, Actor targetActor, Flow flow ) {
            this.sourceActor = sourceActor;
            this.targetActor = targetActor;
            this.flow = flow;
        }

        public Actor getSourceActor() {
            return sourceActor;
        }

        public Actor getTargetActor() {
            return targetActor;
        }

        public Flow getFlow() {
            return flow;
        }

        /**
         * Get name of actor flow.
         *
         * @return a string
         */
        public String getName() {
            return sourceActor.getName() + ( flow.isAskedFor() ? " answers with " : " sends notification of " )
                   + flow.getName() + " to " + targetActor.getName();
        }
    }

    public class RoleFlowsPanel extends AbstractTablePanel<Flow> {

        /**
         * Flows model.
         */
        private IModel<ArrayList<Flow>> flowsModel;

        /**
         * Filterable.
         */
        private Filterable filterable;

        public RoleFlowsPanel( String id, IModel<ArrayList<Flow>> flowsModel, int pageSize, Filterable filterable ) {
            super( id, null, pageSize, null );
            this.flowsModel = flowsModel;
            this.filterable = filterable;
            init();
        }

        @SuppressWarnings( "unchecked" )
        private void init() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            columns.add( makeFilterableLinkColumn( "In segment", "segment", "segment.name", EMPTY, filterable ) );
            columns.add( makeFilterableLinkColumn( "Role", "source.role", "source.role.name", EMPTY, filterable ) );
            columns.add( makeLinkColumn( "Doing", "source", "source.task", EMPTY ) );
            columns.add( makeFilterableLinkColumn( "In",
                                                   "source.organization",
                                                   "source.organization.name",
                                                   EMPTY,
                                                   filterable ) );
            columns.add( makeLinkColumn( "Sends info", "", "name", EMPTY ) );
            columns.add( makeFilterableLinkColumn( "To role", "target.role", "target.role.name", EMPTY, filterable ) );
            columns.add( makeLinkColumn( "Doing", "target", "target.task", EMPTY ) );
            columns.add( makeFilterableLinkColumn( "In",
                                                   "target.organization",
                                                   "target.organization.name",
                                                   EMPTY,
                                                   filterable ) );
            columns.add( makeGeomapLinkColumn( "",
                                               "name",
                                               Arrays.asList( "source", "target" ),
                                               new Model<String>( "Show both tasks in map" ) ) );
            List<Flow> flows = flowsModel.getObject();
            add( new AjaxFallbackDefaultDataTable( "flows",
                                                   columns,
                                                   new SortableBeanProvider<Flow>( flows, "segment.name" ),
                                                   getPageSize() ) );
        }
    }

    public class ActorFlowsPanel extends AbstractTablePanel<ActorFlow> {

        /**
         * Flows model.
         */
        private IModel<ArrayList<ActorFlow>> flowsModel;

        /**
         * Filterable.
         */
        private Filterable filterable;

        public ActorFlowsPanel( String id, IModel<ArrayList<ActorFlow>> flowsModel, int pageSize,
                                Filterable filterable ) {
            super( id, null, pageSize, null );
            this.flowsModel = flowsModel;
            this.filterable = filterable;
            init();
        }

        @SuppressWarnings( "unchecked" )
        private void init() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            columns.add( makeFilterableLinkColumn( "In segment",
                                                   "flow.segment",
                                                   "flow.segment.name",
                                                   EMPTY,
                                                   filterable ) );
            columns.add( makeFilterableLinkColumn( "Agent", "sourceActor", "sourceActor.name", EMPTY, filterable ) );
            columns.add( makeLinkColumn( "Doing", "flow.source", "flow.source.task", EMPTY ) );
            columns.add( makeFilterableLinkColumn( "In",
                                                   "flow.source.organization",
                                                   "flow.source.organization.name",
                                                   EMPTY,
                                                   filterable ) );
            columns.add( makeLinkColumn( "Sends info", "flow", "flow.name", EMPTY ) );
            columns.add( makeColumn( "With intent", "flow.intent.label", EMPTY ) );
            columns.add( makeFilterableLinkColumn( "To agent", "targetActor", "targetActor.name", EMPTY, filterable ) );
            columns.add( makeLinkColumn( "Doing", "flow.target", "flow.target.task", EMPTY ) );
            columns.add( makeFilterableLinkColumn( "In",
                                                   "flow.target.organization",
                                                   "flow.target.organization.name",
                                                   EMPTY,
                                                   filterable ) );
            columns.add( makeGeomapLinkColumn( "",
                                               "name",
                                               Arrays.asList( "flow.source", "flow.target" ),
                                               new Model<String>( "Show both tasks in map" ) ) );
            List<ActorFlow> actorFlows = flowsModel.getObject();
            add( new AjaxFallbackDefaultDataTable( "flows",
                                                   columns,
                                                   new SortableBeanProvider<ActorFlow>( actorFlows,
                                                                                        "flow.segment.name" ),
                                                   getPageSize() ) );
        }
    }
}
