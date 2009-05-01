package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.diagrams.EntityNetworkDiagramPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.RequestCycle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Organization network panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2009
 * Time: 3:33:41 PM
 */
public class EntityNetworkPanel<T extends ModelObject> extends AbstractUpdatablePanel {

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

    private void addFlowsPanel() {
        NetworkFlowsPanel flowsPanel = new NetworkFlowsPanel(
                "flows",
                new PropertyModel<ArrayList<Flow>>( this, "flows" ),
                PAGE_SIZE,
                getExpansions()
        );
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

    /**
     * Get flows.
     *
     * @return a list of flows
     */
    public List<Flow> getFlows() {
        if ( selectedEntityRel != null ) {
            return selectedEntityRel.getFlows();
        } else {
            List<Flow> flows = new ArrayList<Flow>();
            for ( T other : getEntities() ) {
                if ( getEntity() != other ) {
                    EntityRelationship<T> sendRel = getQueryService().findEntityRelationship( getEntity(), other );
                    if ( sendRel != null ) flows.addAll( sendRel.getFlows() );
                    EntityRelationship<T> receiveRel = getQueryService().findEntityRelationship( other, getEntity() );
                    if ( receiveRel != null ) flows.addAll( receiveRel.getFlows() );
                }
            }
            return flows;
        }
    }

    public T getEntity() {
        return entityModel.getObject();
    }

    private List<T> getEntities() {
        return (List<T>) getQueryService().list( getEntity().getClass() );
    }

    public void refresh( AjaxRequestTarget target ) {
        addEntityNetworkDiagramPanel();
        addFlowsPanel();
        target.addComponent( this );
    }

    /**
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        if ( change.isSelected() ) {
            Identifiable changed = change.getSubject();
            if ( changed instanceof Channels ) {
                selectedEntityRel = null;
            } else if ( changed instanceof ModelObject
                    && ((ModelObject)changed).isEntity()) {
                if (changed == getEntity()) {
                    selectedEntityRel = null;
                } else {
                    // other entity selected; make it an expanded change
                    change.setType(  Change.Type.Expanded );
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
            // Don't percolate update on selection unless a part was selected.
            if ( change.getSubject() instanceof Part ) {
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


}
