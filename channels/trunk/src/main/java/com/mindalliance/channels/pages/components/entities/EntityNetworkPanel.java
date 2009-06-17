package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.diagrams.EntityNetworkDiagramPanel;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Organization network panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2009
 * Time: 3:33:41 PM
 */
public class EntityNetworkPanel<T extends ModelObject> extends AbstractUpdatablePanel implements Filterable {

    /**
     * Default page size for external flows panel.
     */
    private static final int PAGE_SIZE = 10;
    /**
     * Entity model.
     */
    private IModel<T> entityModel;
    /**
     * Selected entity relationship.
     */
    private EntityRelationship<T> selectedEntityRel;
    /**
     * CSS identifier for dom element showing scrollable diagram.
     */
    private String domIdentifier;
    /**
     * Width, height dimension contraints on the plan map diagram.
     * In inches.
     * None if any is 0.
     */
    private double[] diagramSize = new double[2];
    /**
     * Entity network diagram panel
     */
    private EntityNetworkDiagramPanel<T> entityNetworkDiagramPanel;
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

    public EntityNetworkPanel(
            String id,
            IModel<T> model,
            Set<Long> expansions,
            String domIdentifier ) {
        super( id, model, expansions );
        this.entityModel = model;
        this.domIdentifier = domIdentifier;
        init();
    }

    private void init() {
        addEntityNetworkDiagramPanel();
        addFlowsTitleLabel();
        addDiagramSizing();
        addFilters();
        addFlowsPanel();
    }

    private void addDiagramSizing() {
        WebMarkupContainer reduceToFit = new WebMarkupContainer( "fit" );
        reduceToFit.add( new AbstractDefaultAjaxBehavior() {
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String script = "wicketAjaxGet('"
                        + getCallbackUrl( true )
                        + "&width='+$('" + domIdentifier + "').width()+'"
                        + "&height='+$('" + domIdentifier + "').height()";
                String onclick = ( "{" + generateCallbackScript( script ) + " return false;}" )
                        .replaceAll( "&amp;", "&" );
                tag.put( "onclick", onclick );
            }

            protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                String swidth = requestCycle.getRequest().getParameter( "width" );
                String sheight = requestCycle.getRequest().getParameter( "height" );
                diagramSize[0] = ( Double.parseDouble( swidth ) - 20 ) / 96.0;
                diagramSize[1] = ( Double.parseDouble( sheight ) - 20 ) / 96.0;
                addEntityNetworkDiagramPanel();
                target.addComponent( entityNetworkDiagramPanel );
            }
        } );
        add( reduceToFit );
        WebMarkupContainer fullSize = new WebMarkupContainer( "full" );
        fullSize.add( new AjaxEventBehavior( "onclick" ) {
            protected void onEvent( AjaxRequestTarget target ) {
                diagramSize = new double[2];
                addEntityNetworkDiagramPanel();
                target.addComponent( entityNetworkDiagramPanel );
            }
        } );
        add( fullSize );
    }

    private void addEntityNetworkDiagramPanel() {
        if ( diagramSize[0] <= 0.0 || diagramSize[1] <= 0.0 ) {
            entityNetworkDiagramPanel = new EntityNetworkDiagramPanel<T>(
                    "diagram",
                    entityModel,
                    selectedEntityRel,
                    null,
                    domIdentifier
            );
        } else {
            entityNetworkDiagramPanel = new EntityNetworkDiagramPanel<T>(
                    "diagram",
                    entityModel,
                    selectedEntityRel,
                    diagramSize,
                    domIdentifier
            );
        }
        entityNetworkDiagramPanel.setOutputMarkupId( true );
        addOrReplace( entityNetworkDiagramPanel );
    }

    private void addFlowsTitleLabel() {
        Label flowsTitleLabel = new Label( "flows-title", new PropertyModel<String>( this, "flowsTitle" ) );
        flowsTitleLabel.setOutputMarkupId( true );
        add( flowsTitleLabel );
    }

    private void addFilters() {
        TextField<String> infoFilterField = new TextField<String>(
                "infoFilter", new PropertyModel<String>( this, "informationFilter" ) );
        infoFilterField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addFlowsPanel();
                target.addComponent( flowsPanel );
            }
        } );
        add( infoFilterField );
        TextField<String> taskFilterField = new TextField<String>(
                "taskFilter", new PropertyModel<String>( this, "taskFilter" ) );
        taskFilterField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addFlowsPanel();
                target.addComponent( flowsPanel );
            }
        } );
        add( taskFilterField );
    }

    private void addFlowsPanel() {
        if ( getEntity() instanceof Actor ) {
            flowsPanel = new ActorFlowsPanel(
                    "flows",
                    new PropertyModel<ArrayList<ActorFlow>>( this, "actorFlows" ),
                    PAGE_SIZE,
                    this
            );
        } else {
            flowsPanel = new RoleFlowsPanel(
                    "flows",
                    new PropertyModel<ArrayList<Flow>>( this, "flows" ),
                    PAGE_SIZE,
                    this
            );
        }
        flowsPanel.setOutputMarkupId( true );
        addOrReplace( flowsPanel );
    }

    /**
     * Get flows title.
     *
     * @return a string
     */
    public String getFlowsTitle() {
        if ( selectedEntityRel != null ) {
            T fromEntity = selectedEntityRel.getFromEntity( getQueryService() );
            T toEntity = selectedEntityRel.getToEntity( getQueryService() );
            if ( fromEntity == null || toEntity == null ) {
                return "*** You need to refresh ***";
            } else {
                return "Flows from \""
                        + fromEntity.getName()
                        + "\" to \""
                        + toEntity.getName()
                        + "\"";
            }
        } else {
            return "All flows invoving \""
                    + getEntity().getName()
                    + "\"";
        }
    }

    public String getInformationFilter() {
        return informationFilter;
    }

    public void setInformationFilter( String val ) {
        informationFilter = ( val == null ? "" : val.toLowerCase() );
    }

    public String getTaskFilter() {
        return taskFilter;
    }

    public void setTaskFilter( String val ) {
        taskFilter = ( val == null ? "" : val.toLowerCase() );
    }

    /**
     * {@inheritDoc}
     */
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        assert property != null;
        if ( identifiable == null || isFiltered( identifiable, property ) ) {
            identifiableFilters.remove( property );
        } else {
            identifiableFilters.put( property, identifiable );
        }
        addFlowsPanel();
        target.addComponent( flowsPanel );
    }

    /**
     * {@inheritDoc}
     */
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
        return (List<Flow>) CollectionUtils.select(
                flows,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return !isFilteredOut( (Flow) obj );
                    }
                } );
    }

    @SuppressWarnings( "unchecked" )
    public List<ActorFlow> getActorFlows() {
        List<ActorFlow> actorFlows = new ArrayList<ActorFlow>();
        for ( EntityRelationship entityRel : getEntityRelationships() ) {
            actorFlows.addAll( (List<ActorFlow>) CollectionUtils.select(
                    getActorFlowsInRelationship( entityRel ),
                    new Predicate() {
                        @SuppressWarnings( "unchecked" )
                        public boolean evaluate( Object obj ) {
                            return !isFilteredOut( (ActorFlow) obj  );
                        }
                    } ) );
        }
        return actorFlows;
    }

    private List<EntityRelationship> getEntityRelationships() {
        List<EntityRelationship> entityRels = new ArrayList<EntityRelationship>();
        if ( selectedEntityRel != null ) {
            entityRels.add( selectedEntityRel );
        } else {
            for ( T other : getEntities() ) {
                if ( getEntity() != other ) {
                    EntityRelationship<T> sendRel =
                            getQueryService().findEntityRelationship( getEntity(), other );
                    if ( sendRel != null ) entityRels.add( sendRel );
                    EntityRelationship<T> receiveRel =
                            getQueryService().findEntityRelationship( other, getEntity() );
                    if ( receiveRel != null ) entityRels.add( receiveRel );
                }
            }
        }
        return entityRels;
    }

    @SuppressWarnings( "unchecked" )
    private List<ActorFlow> getActorFlowsInRelationship( final EntityRelationship entityRelationship ) {
        return (List<ActorFlow>) CollectionUtils.collect(
                entityRelationship.getFlows(),
                new Transformer() {
                    public Object transform( Object obj ) {
                        return new ActorFlow(
                                (Actor) entityRelationship.getFromEntity( getQueryService() ),
                                (Actor) entityRelationship.getToEntity( getQueryService() ),
                                (Flow) obj
                        );
                    }
                }
        );
    }

    private boolean isFilteredOut( ActorFlow actorFlow ) {
        if ( !informationFilter.isEmpty()
                && !actorFlow.getFlow().getName().toLowerCase().contains( informationFilter ) ) {
            return true;
        }
        if ( !taskFilter.isEmpty() ) {
            if ( !( (Part) actorFlow.getFlow().getSource() ).getTask().toLowerCase().contains( taskFilter )
                    && !( (Part) actorFlow.getFlow().getTarget() ).getTask().toLowerCase().contains( taskFilter ) ) {
                return true;
            }
        } else {
            for ( String property : identifiableFilters.keySet() ) {
                if ( !ModelObject.areEqualOrNull(
                        (ModelObject) identifiableFilters.get( property ),
                        (ModelObject) CommandUtils.getProperty( actorFlow, property, null ) ) ) {
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
                    && !( (Part) flow.getTarget() ).getTask().toLowerCase().contains( taskFilter ) ) {
                return true;
            }
        } else {
            for ( String property : identifiableFilters.keySet() ) {
                if ( !ModelObject.areEqualOrNull(
                        (ModelObject) identifiableFilters.get( property ),
                        (ModelObject) CommandUtils.getProperty( flow, property, null ) ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public T getEntity() {
        return entityModel.getObject();
    }

    @SuppressWarnings( "unchecked" )
    private List<T> getEntities() {
        /*if ( getEntity() instanceof Actor ) {
            return (List<T>) getQueryService().list( getEntity().getClass() );
        } else {*/
            return (List<T>) getQueryService().listEntitiesWithUnknown( getEntity().getClass() );
     //   }
    }

    public void refresh( AjaxRequestTarget target ) {
        addEntityNetworkDiagramPanel();
        addFlowsPanel();
        target.addComponent( this );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public void changed( Change change ) {
        if ( change.isSelected() ) {
            Identifiable changed = change.getSubject();
            if ( changed instanceof Plan ) {
                selectedEntityRel = null;
            } else if ( changed instanceof ModelObject
                    && ( (ModelObject) changed ).isEntity() ) {
                if ( changed == getEntity() ) {
                    selectedEntityRel = null;
                } else {
                    // other entity selected; make it an expanded change
                    change.setType( Change.Type.Expanded );
                    super.changed( change );
                }
            } else if ( changed instanceof EntityRelationship ) {
                selectedEntityRel = (EntityRelationship<T>) changed;
            }
            // Don't percolate change on selection of app, entity or entity relationship.
            else {
                super.changed( change );
            }
        } else {
            super.changed( change );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        if ( change.isSelected() ) {
            refresh( target );
            // Don't percolate update on selection unless a part or flowwas selected.
            if ( change.getSubject() instanceof Part || change.getSubject() instanceof Flow ) {
                super.updateWith( target, change );
            } else {
                if ( change.getScript() != null ) {
                    target.appendJavascript( change.getScript() );
                }
            }
        } else {
            super.updateWith( target, change );
        }
    }

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

        public RoleFlowsPanel(
                String id,
                IModel<ArrayList<Flow>> flowsModel,
                int pageSize,
                Filterable filterable ) {
            super( id, null, pageSize, null );
            this.flowsModel = flowsModel;
            this.filterable = filterable;
            init();
        }

        private void init() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            columns.add( makeFilterableLinkColumn(
                    "In scenario",
                    "scenario",
                    "scenario.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeFilterableLinkColumn(
                    "Role",
                    "source.role",
                    "source.role.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeColumn(
                    "Doing",
                    "source.task",
                    EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "In",
                    "source.organization",
                    "source.organization.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeLinkColumn(
                    "Sends info",
                    "",
                    "name",
                    "?" ) );
            columns.add( makeFilterableLinkColumn(
                    "To role",
                    "target.role",
                    "target.role.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeColumn(
                    "Doing",
                    "target.task",
                    EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "In",
                    "target.organization",
                    "target.organization.name",
                    EMPTY,
                    filterable ) );
            List<Flow> flows = flowsModel.getObject();
            add( new AjaxFallbackDefaultDataTable(
                    "flows",
                    columns,
                    new SortableBeanProvider<Flow>( flows, "scenario.name" ),
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

        public ActorFlowsPanel(
                String id,
                IModel<ArrayList<ActorFlow>> flowsModel,
                int pageSize,
                Filterable filterable ) {
            super( id, null, pageSize, null );
            this.flowsModel = flowsModel;
            this.filterable = filterable;
            init();
        }

        private void init() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            columns.add( makeFilterableLinkColumn(
                    "In scenario",
                    "flow.scenario",
                    "flow.scenario.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeFilterableLinkColumn(
                    "Actor",
                    "sourceActor",
                    "sourceActor.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeColumn(
                    "Doing",
                    "flow.source.task",
                    EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "In",
                    "flow.source.organization",
                    "flow.source.organization.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeLinkColumn(
                    "Sends info",
                    "flow",
                    "flow.name",
                    "?" ) );
            columns.add( makeFilterableLinkColumn(
                    "To actor",
                    "targetActor",
                    "targetActor.name",
                    EMPTY,
                    filterable ) );
            columns.add( makeColumn(
                    "Doing",
                    "flow.target.task",
                    EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "In",
                    "flow.target.organization",
                    "flow.target.organization.name",
                    EMPTY,
                    filterable ) );

            List<ActorFlow> actorFlows = flowsModel.getObject();
            add( new AjaxFallbackDefaultDataTable(
                    "flows",
                    columns,
                    new SortableBeanProvider<ActorFlow>( actorFlows, "flow.scenario.name" ),
                    getPageSize() ) );

        }
    }
}
