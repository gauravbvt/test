package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Hierarchical;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.diagrams.HierarchyDiagramPanel;
import com.mindalliance.channels.pages.components.entities.EntityReferencesAndMatchesPanel;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/21/11
 * Time: 8:59 PM
 */
public class PlanTypologiesPanel extends AbstractCommandablePanel {

    /**
     * Expected screen resolution.
     */
    static private double DPI = 96.0;

    private String selectedTypeKind;

    /**
     * Whether plan map is reduced to fit.
     */
    private boolean reducedToFit = false;
    /**
     * Sizing toggle label..
     */
    private Label sizingLabel;
    /**
     * Width, height dimension constraints on the plan map diagram.
     * In inches.
     * None if any is 0.
     */
    private double[] diagramSize = new double[2];
    /**
     * Typology diagram panel.
     */
    private HierarchyDiagramPanel typologyDiagramPanel;
    /**
     * Type index container.
     */
    private WebMarkupContainer typeIndexContainer;

    private ModelEntity selectedType;

    private boolean neato = false;


    /**
     * DOM identifier for resizeable element.
     */
    private static final String DOM_IDENTIFIER = ".plan .typology";

    private static final String ORG = "Organization";
    private static final String AGENT = "Agent";
    private static final String EVENT = "Event";
    private static final String PLACE = "Place";
    private static final String ROLE = "Role";
    private static final String MEDIUM = "Transmission medium";

    private static String[] TYPE_KINDS = {AGENT, EVENT, ORG, PLACE, ROLE, MEDIUM};
    private CheckBox neatoCheckBox;
    private CheckBox dotCheckBox;

    public PlanTypologiesPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        selectedTypeKind = "Organization";
        addTypeKindChoice();
        addAlgos();
        addTypologySizing();
        addTypologyDiagram();
        addTypeIndex();
    }

    private void addTypeKindChoice() {
        DropDownChoice<String> typeKindChoice = new DropDownChoice<String>(
                "typeKind",
                new PropertyModel<String>( this, "selectedTypeKind" ),
                Arrays.asList( TYPE_KINDS )
        );
        typeKindChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addTypologyDiagram();
                target.addComponent( typologyDiagramPanel );
                setSelectedType( null );
                addTypeIndex();
                target.addComponent( typeIndexContainer );
            }
        } );
        add( typeKindChoice );
    }

    private void addAlgos() {
        neatoCheckBox = new CheckBox( "neato", new PropertyModel<Boolean>( this, "neato" ) );
        neatoCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addTypologyDiagram();
                target.addComponent( typologyDiagramPanel );
                target.addComponent( dotCheckBox );
            }
        } );
        add( neatoCheckBox );
        dotCheckBox = new CheckBox( "dot", new PropertyModel<Boolean>( this, "dot" ) );
        dotCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addTypologyDiagram();
                target.addComponent( typologyDiagramPanel );
                target.addComponent( neatoCheckBox );
            }
        } );
        add( dotCheckBox );
    }

    private void addTypologySizing() {
        sizingLabel = new Label(
                "fit",
                new Model<String>( reducedToFit ? "Full size" : "Reduce to fit" ) );
        sizingLabel.setOutputMarkupId( true );
        sizingLabel.add( new AbstractDefaultAjaxBehavior() {
            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String script;
                if ( !reducedToFit ) {
                    String domIdentifier = DOM_IDENTIFIER;
                    script = "wicketAjaxGet('"
                            + getCallbackUrl( true )
                            + "&width='+$('" + domIdentifier + "').width()+'"
                            + "&height='+$('" + domIdentifier + "').height()";
                } else {
                    script = "wicketAjaxGet('"
                            + getCallbackUrl( true )
                            + "'";
                }
                String onclick = ( "{" + generateCallbackScript( script ) + " return false;}" )
                        .replaceAll( "&amp;", "&" );
                tag.put( "onclick", onclick );
            }

            @Override
            protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                if ( !reducedToFit ) {
                    String swidth = requestCycle.getRequest().getParameter( "width" );
                    String sheight = requestCycle.getRequest().getParameter( "height" );
                    if ( swidth != null && sheight != null ) {
                        diagramSize[0] = ( Double.parseDouble( swidth ) - 20 ) / DPI;
                        diagramSize[1] = ( Double.parseDouble( sheight ) - 20 ) / DPI;
                    }
                } else {
                    diagramSize = new double[2];
                }
                reducedToFit = !reducedToFit;
                addTypologyDiagram();
                target.addComponent( typologyDiagramPanel );
                addTypologySizing();
                target.addComponent( sizingLabel );
            }
        } );
        addOrReplace( sizingLabel );
    }


    private void addTypologyDiagram() {
        typologyDiagramPanel = new HierarchyDiagramPanel(
                "typology",
                new PropertyModel<Hierarchical>( this, "selectedType" ),
                diagramSize,
                DOM_IDENTIFIER,
                isNeato() ? "neato" : "dot" );
        typologyDiagramPanel.setOutputMarkupId( true );
        addOrReplace( typologyDiagramPanel );
    }

    private ModelEntity getSelectedTypeRoot() {
        Class<? extends ModelEntity> clazz =
                selectedTypeKind.equals( ORG )
                        ? Organization.class
                        : selectedTypeKind.equals( AGENT )
                        ? Actor.class
                        : selectedTypeKind.equals( EVENT )
                        ? Event.class
                        : selectedTypeKind.equals( MEDIUM )
                        ? TransmissionMedium.class
                        : selectedTypeKind.equals( PLACE )
                        ? Place.class
                        : selectedTypeKind.equals( ROLE )
                        ? Role.class
                        : null;
        assert clazz != null;
        return ModelEntity.getUniversalTypeFor( clazz );
    }

    private void addTypeIndex() {
        typeIndexContainer = new WebMarkupContainer( "indexContainer" );
        typeIndexContainer.setOutputMarkupId( true );
        addOrReplace( typeIndexContainer );
        typeIndexContainer.add( new Label( "selectedType", "About " + getSelectedType().getName() ) );
        ModelObjectLink selectedTypeLink = new ModelObjectLink(
                "typeLink",
                new PropertyModel<ModelObject>( this, "selectedType" ),
                new Model<String>( "more" ),
                "Click for details",
                "window" );
        typeIndexContainer.add( selectedTypeLink );
        typeIndexContainer.add( new EntityReferencesAndMatchesPanel(
                "typeIndex",
                new PropertyModel<ModelEntity>( this, "selectedType" ),
                null ) );
        makeVisible( typeIndexContainer, !getSelectedType().isUniversal() );
    }

    public String getSelectedTypeKind() {
        return selectedTypeKind;
    }

    public void setSelectedTypeKind( String val ) {
        selectedTypeKind = val == null ? "Organization" : val;
    }

    public ModelEntity getSelectedType() {
        return selectedType == null
                ? getSelectedTypeRoot()
                : selectedType;
    }

    public void setSelectedType( ModelEntity selectedType ) {
        this.selectedType = selectedType;
    }

    public boolean isNeato() {
        return neato;
    }

    public void setNeato( boolean val ) {
        neato = val;
    }

    public boolean isDot() {
        return !neato;
    }

    public void setDot( boolean val ) {
        neato = !val;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected() && change.isForInstanceOf( ModelEntity.class ) ) {
            setSelectedType( (ModelEntity) change.getSubject( getQueryService() ) );
            addTypologyDiagram();
            target.addComponent( typologyDiagramPanel );
            addTypeIndex();
            target.addComponent( typeIndexContainer );
        } else {
            super.updateWith( target, change, updated );
        }
    }


}
