package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.engine.analysis.graph.AssetSupplyRelationship;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramAjaxBehavior;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import com.mindalliance.channels.pages.components.diagrams.SupplyChainsDiagramPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/3/14
 * Time: 4:09 PM
 */
public class ModelSupplyChainsPanel extends AbstractUpdatablePanel {

    /**
     * Expected screen resolution.
     */
    static private double DPI = 96.0;

    /**
     * DOM identifier for resizeable element.
     */
    private static final String DOM_IDENTIFIER = ".supplyChains";
    private static final String BY_ORG_TYPE = "type of organization";
    private static final String BY_ORG = "organization";
    private static final String BY_ROLE = "organization and role";
    private static final String BY_NONE = "Don't summarize";
    private static final String BY_ORG_TYPE_AND_ROLE = "type of organization and role";
    private static final String[] SUMMARY_CHOICES = {BY_ORG_TYPE, BY_ORG_TYPE_AND_ROLE, BY_ORG, BY_ROLE, BY_NONE};

    private Label sizingLabel;

    private boolean summarizeByOrgType = true;
    private boolean summarizeByOrg = false;

    private boolean summarizeByRole = false;

    /**
     * Width, height dimension constraints on the plan map diagram.
     * In inches.
     * None if any is 0.
     */
    private double[] diagramSize = new double[2];

    private SupplyChainsDiagramPanel supplyChainsDiagramPanel;

    /**
     * Whether plan map is reduced to fit.
     */
    private boolean reducedToFit = false;
    private MaterialAsset assetScope = MaterialAsset.UNKNOWN;
    private List<MaterialAsset> allAssetsSupplied;


    public ModelSupplyChainsPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addAssetScopeChoice();
        addSummarizeChoice();
        addSupplyListsDiagramPanel();
        addSizing();
    }

    private void addAssetScopeChoice() {
        DropDownChoice<MaterialAsset> assetsChoice = new DropDownChoice<MaterialAsset>(
                "assetScope",
                new PropertyModel<MaterialAsset>( this, "assetScope"  ),
                getAllAssetScopes(),
                new IChoiceRenderer<MaterialAsset>() {
                    @Override
                    public Object getDisplayValue( MaterialAsset asset ) {
                        return asset.isUnknown() ? "All material assets" : asset.getName();
                    }

                    @Override
                    public String getIdValue( MaterialAsset object, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        assetsChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addSupplyListsDiagramPanel();
                target.add( supplyChainsDiagramPanel );
            }
        });
        add( assetsChoice );
    }

    private List<MaterialAsset> getAllAssetScopes() {
        List<MaterialAsset> assetsScopes = new ArrayList<MaterialAsset>(  );
        assetsScopes.add( MaterialAsset.UNKNOWN );
        assetsScopes.addAll( findAllSuppliedAssets() );
        return assetsScopes;
    }

    private Collection<? extends MaterialAsset> findAllSuppliedAssets() {
        if ( allAssetsSupplied == null ) {
            Set<MaterialAsset> assetsSupplied = new HashSet<MaterialAsset>(  );
            List<AssetSupplyRelationship<Part>> assetSupplyRelationships =
                    getQueryService().findAllAssetSupplyRelationships();
            for ( AssetSupplyRelationship<Part> assetSupplyRelationship : assetSupplyRelationships ) {
                assetsSupplied.addAll( assetSupplyRelationship.getAssets() );
            }
            allAssetsSupplied = new ArrayList<MaterialAsset>( assetsSupplied );
            Collections.sort(allAssetsSupplied, new Comparator<MaterialAsset>() {
                @Override
                public int compare( MaterialAsset ma1, MaterialAsset ma2 ) {
                    return ma1.getName().compareTo( ma2.getName() );
                }
            });
        }
        return allAssetsSupplied;
    }

    private void addSummarizeChoice() {
        DropDownChoice<String> summarizeChoice = new DropDownChoice<String>(
                "summarizeChoice",
                new PropertyModel<String>( this, "summarizeChoice" ),
                Arrays.asList( SUMMARY_CHOICES )
        );
        summarizeChoice.setOutputMarkupId( true );
        summarizeChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addSupplyListsDiagramPanel();
                target.add( supplyChainsDiagramPanel );
            }
        } );
        addOrReplace( summarizeChoice );
    }


    private void addSupplyListsDiagramPanel() {
        Settings settings = diagramSize[0] <= 0.0 || diagramSize[1] <= 0.0
                ? new Settings( DOM_IDENTIFIER, null, null, true, true )
                : new Settings( DOM_IDENTIFIER, null, diagramSize, true, true );
        settings.setOrientationLeftRight();
        supplyChainsDiagramPanel = new SupplyChainsDiagramPanel(
                "supplyChainsDiagram",
                assetScope.isUnknown() ? null : assetScope,
                isSummarizeByOrgType(),
                isSummarizeByOrg(),
                isSummarizeByRole(),
                settings
        );
        addOrReplace( supplyChainsDiagramPanel );
    }

    public String getSummarizeChoice() {
        return summarizeByOrgType && summarizeByRole
                ? BY_ORG_TYPE_AND_ROLE
                : summarizeByOrgType
                ? BY_ORG_TYPE
                : summarizeByOrg
                ? BY_ORG
                : summarizeByRole
                ? BY_ROLE
                : BY_NONE;
    }

    public void setSummarizeChoice( String val ) {
        if ( val.equals( BY_ORG_TYPE_AND_ROLE ) )
            setSummarizeByOrgTypeAndRole( true );
        else if ( val.equals( BY_ORG_TYPE ) )
            setSummarizeByOrgType( true );
        else if ( val.equals( BY_ORG ) )
            setSummarizeByOrg( true );
        else if ( val.equals( BY_ROLE ) )
            setSummarizeByRole( true );
        else if ( val.equals( BY_NONE ) ) {
            setSummarizeByRole( false );
            setSummarizeByOrg( false );
            setSummarizeByOrgType( false );
        }
    }

    private void setSummarizeByOrgTypeAndRole( boolean val ) {
        if ( val ) {
            summarizeByOrgType = true;
            summarizeByRole = true;
        } else {
            summarizeByOrgType = false;
            summarizeByOrg = false;
            summarizeByRole = false;
        }
    }

    public boolean isSummarizeByOrg() {
        return summarizeByOrg;
    }

    public void setSummarizeByOrg( boolean summarizeByOrg ) {
        this.summarizeByOrg = summarizeByOrg;
        if ( summarizeByOrg ) {
            summarizeByOrgType = false;
            summarizeByRole = false;
        }
    }

    public boolean isSummarizeByOrgType() {
        return summarizeByOrgType;
    }

    public void setSummarizeByOrgType( boolean summarizeByOrgType ) {
        this.summarizeByOrgType = summarizeByOrgType;
        if ( summarizeByOrgType ) {
            summarizeByOrg = false;
            summarizeByRole = false;
        }
    }


    public boolean isSummarizeByRole() {
        return summarizeByRole;
    }

    public void setSummarizeByRole( boolean summarizeByRole ) {
        this.summarizeByRole = summarizeByRole;
        if ( summarizeByRole ) {
            summarizeByOrgType = false;
            summarizeByOrg = false;
        }
    }


    private void addSizing() {
        sizingLabel = new org.apache.wicket.markup.html.basic.Label(
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
                    diagramSize[0] = ( Double.parseDouble( swidth ) - 20 ) / DPI;
                    diagramSize[1] = ( Double.parseDouble( sheight ) - 20 ) / DPI;
                } else {
                    diagramSize = new double[2];
                }
                reducedToFit = !reducedToFit;
                addSupplyListsDiagramPanel();
                target.add( supplyChainsDiagramPanel );
                addSizing();
                target.add( sizingLabel );
            }
        } );
        addOrReplace( sizingLabel );

    }

    public MaterialAsset getAssetScope() {
        return assetScope;
    }

    public void setAssetScope( MaterialAsset assetScope ) {
        this.assetScope = assetScope;
    }
}
