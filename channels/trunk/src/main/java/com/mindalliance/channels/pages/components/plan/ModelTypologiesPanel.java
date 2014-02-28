package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramAjaxBehavior;
import com.mindalliance.channels.pages.components.diagrams.HierarchyDiagramPanel;
import com.mindalliance.channels.pages.components.entities.EntityReferencesAndMatchesPanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/21/11
 * Time: 8:59 PM
 */
public class ModelTypologiesPanel extends AbstractCommandablePanel implements Guidable {

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
    private static final String DOM_IDENTIFIER = ".searching .typology";

    private static final String ORG = "Organization";
    private static final String AGENT = "Agent";
    private static final String EVENT = "Event";
    private static final String PLACE = "Place";
    private static final String ROLE = "Role";
    private static final String MEDIUM = "Transmission medium";
    private static final String INFO_PRODUCT = "Info product";
    private static final String INFO_FORMAT = "Info format";
    private static final String FUNCTION = "Function";

    private static String[] TYPE_KINDS = {AGENT, EVENT, FUNCTION, INFO_FORMAT, INFO_PRODUCT, ORG, PLACE, ROLE, MEDIUM};
    private CheckBox neatoCheckBox;
    private CheckBox dotCheckBox;

    public ModelTypologiesPanel( String id ) {
        super( id );
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }

    @Override
    public String getHelpSectionId() {
        return "searching";
    }

    @Override
    public String getHelpTopicId() {
        return "taxonomies";
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
        typeKindChoice.setOutputMarkupId( true );
        typeKindChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addTypologyDiagram();
                target.add( typologyDiagramPanel );
                setSelectedType( null );
                addTypeIndex();
                target.add( typeIndexContainer );
            }
        } );
        addOrReplace( typeKindChoice );
    }

    private void addAlgos() {
        neatoCheckBox = new CheckBox( "neato", new PropertyModel<Boolean>( this, "neato" ) );
        neatoCheckBox.setOutputMarkupId( true );
        neatoCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addTypologyDiagram();
                target.add( typologyDiagramPanel );
                target.add( dotCheckBox );
            }
        } );
        addOrReplace( neatoCheckBox );
        dotCheckBox = new CheckBox( "dot", new PropertyModel<Boolean>( this, "dot" ) );
        dotCheckBox.setOutputMarkupId( true );
        dotCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addTypologyDiagram();
                target.add( typologyDiagramPanel );
                target.add( neatoCheckBox );
            }
        } );
        addOrReplace( dotCheckBox );
    }

    private void addTypologySizing() {
        sizingLabel = new Label(
                "fit",
                new Model<String>( reducedToFit ? "Full size" : "Reduce to fit" ) );
        sizingLabel.setOutputMarkupId( true );
        sizingLabel.add( new AbstractDiagramAjaxBehavior( DOM_IDENTIFIER, reducedToFit ) {
            @Override
            protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                if ( !reducedToFit ) {
                    String swidth = requestCycle.getRequest().getQueryParameters().getParameterValue( "width" ).toString();
                    String sheight = requestCycle.getRequest().getQueryParameters().getParameterValue( "height" ).toString();
                    if ( swidth != null && sheight != null ) {
                        diagramSize[0] = ( Double.parseDouble( swidth ) - 20 ) / DPI;
                        diagramSize[1] = ( Double.parseDouble( sheight ) - 20 ) / DPI;
                    }
                } else {
                    diagramSize = new double[2];
                }
                reducedToFit = !reducedToFit;
                addTypologyDiagram();
                target.add( typologyDiagramPanel );
                addTypologySizing();
                target.add( sizingLabel );
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
                        : selectedTypeKind.equals( INFO_PRODUCT )
                        ? InfoProduct.class
                        :selectedTypeKind.equals( INFO_FORMAT )
                        ? InfoFormat.class
                        :selectedTypeKind.equals( FUNCTION )
                        ? Function.class
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
                new Model<String>( "profile" ),
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
            setSelectedType( (ModelEntity) change.getSubject( getCommunityService() ) );
            addTypologyDiagram();
            target.add( typologyDiagramPanel );
            addTypeIndex();
            target.add( typeIndexContainer );
        } else {
            super.updateWith( target, change, updated );
        }
    }


}
