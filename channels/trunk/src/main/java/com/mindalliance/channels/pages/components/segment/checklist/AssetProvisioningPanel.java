package com.mindalliance.channels.pages.components.segment.checklist;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
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

    private static final int MAX_TASK_NAME_LENGTH = 60;
    private static final int MAX_ASSET_NAME_LENGTH = 40;
    private Checklist checklist;
    private ActionStep actionStep;

    private boolean showOnlyAvailable = true;
    private boolean showOnlyNeeding = true;

    private AssetProvisioning assetProvisioning;

    private AjaxCheckBox onlyNeedingCheckBox;
    private DropDownChoice<MaterialAsset> assetsChoice;
    private DropDownChoice<Part> tasksChoice;

    public AssetProvisioningPanel( String id, Checklist checklist, ActionStep actionStep ) {
        super( id );
        this.checklist = checklist;
        this.actionStep = actionStep;
        init();
    }

    private void init() {
        reset();
        addShowOnlyAvailable();
        addShowOnlyNeeding();
        addAssetChoice();
        addProvisionedTaskChoice();
    }

    private void reset() {
        if ( actionStep.getAssetProvisioning() == null ) {
            assetProvisioning = new AssetProvisioning(  );
        } else {
            assetProvisioning = new AssetProvisioning( actionStep.getAssetProvisioning() );
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
        add( onlyAvailableCheckBox );
    }

    private void addShowOnlyNeeding() {
        onlyNeedingCheckBox = new AjaxCheckBox(
                "onlyNeeding",
                new PropertyModel<Boolean>( this, "showOnlyNeeding" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addProvisionedTaskChoice();
                target.add( tasksChoice );
            }
        };
        onlyNeedingCheckBox.setOutputMarkupId( true );
        addOrReplace( onlyNeedingCheckBox );
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
                    target.add( tasksChoice );
                }
                if ( assetProvisioning.isDefined() ) {
                    update( target, new Change( Change.Type.Updated, getPart(), "checklist" ) );
                }
            }
        } );
        assetsChoice.setOutputMarkupId( true );
        addOrReplace( assetsChoice );
    }

    public List<MaterialAsset> getCandidateAssets() {
        List<MaterialAsset> candidateAssets = new ArrayList<MaterialAsset>(  );
        if ( showOnlyAvailable ) {
            candidateAssets = new ArrayList<MaterialAsset>( getQueryService().findAllAssetsAvailableTo( getPart() ) );
        } else {
            candidateAssets = new ArrayList<MaterialAsset>( getQueryService()
                    .listKnownEntities( MaterialAsset.class, true, false ) );
        }
        if ( getProvisionedAsset() != null && ! candidateAssets.contains( getProvisionedAsset() ) ) {
            candidateAssets.add( getProvisionedAsset() );
        }
        Collections.sort( candidateAssets, new Comparator<MaterialAsset>() {
            @Override
            public int compare( MaterialAsset ma1, MaterialAsset ma2 ) {
                return ma1.getName().compareTo( ma2.getName() );
            }
        });
        return candidateAssets;
    }

    private void addProvisionedTaskChoice() {
        tasksChoice = new DropDownChoice<Part>(
                "task",
                new PropertyModel<Part>( this, "provisionedPart" ),
                new PropertyModel<List<Part>>( this, "candidateParts" ),
                new IChoiceRenderer<Part>() {
                    @Override
                    public Object getDisplayValue( Part taskOption ) {
                        return StringUtils.abbreviate( taskOption.getTitle(), MAX_TASK_NAME_LENGTH );
                    }

                    @Override
                    public String getIdValue( Part object, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        tasksChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                if ( assetProvisioning.isDefined( ) ) {
                    update( target, new Change( Change.Type.Updated, getPart(), "checklist" ) );
                }
            }
        } );
        tasksChoice.setOutputMarkupId( true );
        addOrReplace( tasksChoice );
    }

    @SuppressWarnings( "unchecked" )
    public List<Part> getCandidateParts() {
        List<Part> candidates = new ArrayList<Part>(  );
        List<Part> visibleParts = getQueryService().findAllPartsVisibleTo( getPart() );
        Part provisionedPart = getProvisionedPart();
        if ( showOnlyNeeding ) {
            if ( getProvisionedAsset() != null ) {
                candidates.addAll( (List<Part>) CollectionUtils.select(
                        visibleParts,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (Part) object ).isAssetNeeded( getProvisionedAsset(), getCommunityService() );
                            }
                        }
                ) );
            }
        } else {
            candidates.addAll( visibleParts );
        }
        if ( provisionedPart != null && !candidates.contains( provisionedPart ) ) {
            candidates.add( provisionedPart );
        }
        Collections.sort(
                candidates,
                new Comparator<Part>() {
            @Override
            public int compare( Part part1, Part part2 ) {
                return part1.getTitle().compareTo( part2.getTitle() );
            }
        });
        return candidates;
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
        return assetProvisioning.getAsset( getCommunityService() );
    }

    public void setProvisionedAsset( MaterialAsset provisionedAsset ) {
        assetProvisioning.setAssetId( provisionedAsset.getId() );
        updateAssetProvisioning();
    }

    private Part getPart() {
        return checklist.getPart();
    }

    public Part getProvisionedPart() {
        return assetProvisioning.getPart( checklist );
    }

    public void setProvisionedPart( Part provisionedPart ) {
        assetProvisioning.setPartId( provisionedPart.getId() );
        updateAssetProvisioning(  );
    }

    private int getStepIndex() {
        return checklist.getActionSteps().indexOf( actionStep );
    }

    private void updateAssetProvisioning(  ) {
        if ( assetProvisioning.isDefined( ) && assetProvisioning.isValid( checklist, getCommunityService() ) ) {
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
