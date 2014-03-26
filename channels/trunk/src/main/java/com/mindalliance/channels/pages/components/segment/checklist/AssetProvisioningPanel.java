package com.mindalliance.channels.pages.components.segment.checklist;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.model.checklist.ActionStep;
import com.mindalliance.channels.core.model.checklist.AssetProvisioning;
import com.mindalliance.channels.core.model.checklist.Checklist;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Asset provisioning panel for action steps.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/20/14
 * Time: 5:14 PM
 */
public class AssetProvisioningPanel extends AbstractCommandablePanel {

    private static final int MAX_TASK_NAME_LENGTH = 80;
    private static final int MAX_ASSET_NAME_LENGTH = 30;
    private Checklist checklist;
    private ActionStep actionStep;

    private boolean providesAsset = false;
    private boolean showOnlyAvailable = true;
    private boolean showOnlyNeeding = true;

    private AssetProvisioning assetProvisioning;

    private AjaxCheckBox onlyNeedingCheckBox;
    private DropDownChoice<MaterialAsset> assetsChoice;
    private DropDownChoice<Flow> assetDemandsChoice;
    private WebMarkupContainer assetProvisionContainer;

    public AssetProvisioningPanel( String id, Checklist checklist, ActionStep actionStep ) {
        super( id );
        this.checklist = checklist;
        this.actionStep = actionStep;
        init();
    }

    private void init() {
        reset();
        addProvidesAsset();
        addAssetProvision();
    }

    private void addProvidesAsset() {
        AjaxCheckBox providesAssetCheckBox = new AjaxCheckBox(
                "providesAsset",
                new PropertyModel<Boolean>( this, "providesAsset" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addAssetProvision();
                target.add( assetProvisionContainer );
                if ( !isProvidesAsset() )
                    update( target, new Change( Change.Type.Updated, getPart(), "checklist" ) );
            }
        };
        add( providesAssetCheckBox );
    }

    private void addAssetProvision() {
        assetProvisionContainer = new WebMarkupContainer( "assetProvision" );
        assetProvisionContainer.setOutputMarkupId( true );
        makeVisible( assetProvisionContainer, isProvidesAsset() );
        addOrReplace( assetProvisionContainer );
        addShowOnlyAvailable();
        addShowOnlyNeeding();
        addAssetChoice();
        addProvisionedTaskChoice();
    }

    private void reset() {
        if ( actionStep.getAssetProvisioning() == null ) {
            assetProvisioning = new AssetProvisioning();
            providesAsset = false;
        } else {
            assetProvisioning = new AssetProvisioning( actionStep.getAssetProvisioning() );
            providesAsset = true;
        }
    }

    private void addShowOnlyAvailable() {
        AjaxCheckBox onlyAvailableCheckBox = new AjaxCheckBox(
                "onlyAvailable",
                new PropertyModel<Boolean>( this, "showOnlyAvailable" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addAssetChoice();
                target.add( assetsChoice );
            }
        };
        assetProvisionContainer.add( onlyAvailableCheckBox );
    }

    private void addShowOnlyNeeding() {
        onlyNeedingCheckBox = new AjaxCheckBox(
                "onlyNeeding",
                new PropertyModel<Boolean>( this, "showOnlyNeeding" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addProvisionedTaskChoice();
                target.add( assetDemandsChoice );
            }
        };
        onlyNeedingCheckBox.setOutputMarkupId( true );
        assetProvisionContainer.addOrReplace( onlyNeedingCheckBox );
    }

    private void addAssetChoice() {
        assetsChoice = new DropDownChoice<MaterialAsset>(
                "asset",
                new PropertyModel<MaterialAsset>( this, "provisionedAsset" ),
                new PropertyModel<List<? extends MaterialAsset>>( this, "candidateAssets" ),
                new IChoiceRenderer<MaterialAsset>() {
                    @Override
                    public Object getDisplayValue( MaterialAsset assetOption ) {
                        return StringUtils.abbreviate( assetOption.getName(), MAX_ASSET_NAME_LENGTH );
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
                if ( showOnlyNeeding ) {
                    addProvisionedTaskChoice();
                    target.add( assetDemandsChoice );
                }
                if ( assetProvisioning.isDefined() ) {
                    update( target, new Change( Change.Type.Updated, getPart(), "checklist" ) );
                }
            }
        } );
        assetsChoice.setOutputMarkupId( true );
        assetProvisionContainer.addOrReplace( assetsChoice );
    }

    public List<MaterialAsset> getCandidateAssets() {
        List<MaterialAsset> candidateAssets = new ArrayList<MaterialAsset>();
        if ( showOnlyAvailable ) {
            candidateAssets = new ArrayList<MaterialAsset>( getQueryService().findAllAssetsAvailableTo( getPart() ) );
        } else {
            candidateAssets = new ArrayList<MaterialAsset>( getQueryService()
                    .listKnownEntities( MaterialAsset.class, true, false ) );
        }
        MaterialAsset provisionedAsset = getProvisionedAsset();
        if ( provisionedAsset != null && !candidateAssets.contains( provisionedAsset ) ) {
            candidateAssets.add( provisionedAsset );
        }
        Collections.sort( candidateAssets, new Comparator<MaterialAsset>() {
            @Override
            public int compare( MaterialAsset ma1, MaterialAsset ma2 ) {
                return ma1.getName().compareTo( ma2.getName() );
            }
        } );
        return candidateAssets;
    }

    private void addProvisionedTaskChoice() {
        assetDemandsChoice = new DropDownChoice<Flow>(
                "demand",
                new PropertyModel<Flow>( this, "assetDemand" ),
                new PropertyModel<List<Flow>>( this, "candidateAssetDemands" ),
                new IChoiceRenderer<Flow>() {
                    @Override
                    public Object getDisplayValue( Flow demandFlow ) {
                        return StringUtils.abbreviate( makeDemandLabel( demandFlow ), MAX_TASK_NAME_LENGTH );
                    }

                    @Override
                    public String getIdValue( Flow object, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        assetDemandsChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                if ( assetProvisioning.isDefined() ) {
                    update( target, new Change( Change.Type.Updated, getPart(), "checklist" ) );
                }
            }
        } );
        assetDemandsChoice.setOutputMarkupId( true );
        assetProvisionContainer.addOrReplace( assetDemandsChoice );
    }

    @SuppressWarnings("unchecked")
    public List<Flow> getCandidateAssetDemands() {
        List<Flow> candidates = new ArrayList<Flow>();
        List<Flow> sharingFlows = getIncomingCommunications();
        Flow assetDemand = getAssetDemand();
        if ( showOnlyNeeding ) {
            final MaterialAsset provisionedAsset = getProvisionedAsset();
            if ( provisionedAsset != null ) {
                candidates.addAll( (List<Flow>) CollectionUtils.select(
                        sharingFlows,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (Flow) object ).isDemandsAsset( provisionedAsset );
                            }
                        }
                ) );
            }
        } else {
            candidates.addAll( sharingFlows );
        }
        if ( assetDemand != null && !candidates.contains( assetDemand ) ) {
            candidates.add( assetDemand );
        }
        Collections.sort(
                candidates,
                new Comparator<Flow>() {
                    @Override
                    public int compare( Flow flow1, Flow flow2 ) {
                        return makeDemandLabel( flow1 ).compareTo( makeDemandLabel( flow2 ) );
                    }
                }
        );
        return candidates;
    }

    @SuppressWarnings( "unchecked" )
    private List<Flow> getIncomingCommunications() {
        List<Flow> incoming = new ArrayList<Flow>();
        incoming.addAll( (List<Flow>) CollectionUtils.select(
                getPart().getAllSharingReceives(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Flow) object ).isNotification();
                    }
                }
        ) );
        incoming.addAll( (List<Flow>) CollectionUtils.select(
                getPart().getAllSharingSends(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Flow) object ).isAskedFor();
                    }
                }
        ) );
        return incoming;
    }

    private String makeDemandLabel( Flow sharing ) {
        Part provisionedPart = sharing.isNotification()
                ? (Part) sharing.getSource() // where the incoming notification came from
                : (Part) sharing.getTarget(); // where the incoming request came from
        StringBuilder sb = new StringBuilder();
        sb.append( provisionedPart.getTitle() );
        if ( !sharing.getRestrictions().isEmpty() ) {
            sb.append( " (" )
                    .append( sharing.getRestrictionString( true ) )
                    .append( ")" );
        }
        return sb.toString();
    }

    public boolean isProvidesAsset() {
        return providesAsset;
    }

    public void setProvidesAsset( boolean val ) {
        this.providesAsset = val;
        assetProvisioning = new AssetProvisioning();
        if ( !providesAsset ) {
            doCommand(
                    new UpdateSegmentObject(
                            getUsername(),
                            getPart(),
                            "checklist.actionSteps[" + getStepIndex() + "].assetProvisioning",
                            null,
                            UpdateObject.Action.Set
                    )
            );

        }
    }

    public boolean isShowOnlyAvailable() {
        return showOnlyAvailable;
    }

    public void setShowOnlyAvailable( boolean showOnlyAvailable ) {
        this.showOnlyAvailable = showOnlyAvailable;
    }

    public boolean isShowOnlyNeeding() {
        return showOnlyNeeding;
    }

    public void setShowOnlyNeeding( boolean showOnlyNeeding ) {
        this.showOnlyNeeding = showOnlyNeeding;
    }

    public MaterialAsset getProvisionedAsset() {
        return assetProvisioning.getAssetId() > 0
                ? assetProvisioning.getAsset( getCommunityService() )
                : null;
    }

    public void setProvisionedAsset( MaterialAsset provisionedAsset ) {
        assetProvisioning.setAssetId( provisionedAsset.getId() );
        updateAssetProvisioning();
    }

    private Part getPart() {
        return checklist.getPart();
    }

    public Flow getAssetDemand() {
        return assetProvisioning.getFlow( checklist );
    }

    public void setAssetDemand( Flow assetDemand ) {
        assetProvisioning.setFlowId( assetDemand.getId() );
        updateAssetProvisioning();
    }

    private int getStepIndex() {
        return checklist.getActionSteps().indexOf( actionStep );
    }

    private void updateAssetProvisioning() {
        if ( assetProvisioning.isDefined() && assetProvisioning.isValid( checklist, getCommunityService() ) ) {
            doCommand(
                    new UpdateSegmentObject(
                            getUsername(),
                            getPart(),
                            "checklist.actionSteps[" + getStepIndex() + "].assetProvisioning",
                            assetProvisioning,
                            UpdateObject.Action.Set
                    )
            );
        }
    }


}
