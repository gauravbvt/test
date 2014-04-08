package com.mindalliance.channels.pages.components.community;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.community.AssetBinding;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.LocationBinding;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Community details panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/12/13
 * Time: 10:15 AM
 */
public class CollaborationCommunityDetailsPanel extends AbstractCommandablePanel {

    private String name;
    private String description;
    private AjaxLink<String> cancelButton;
    private AjaxLink<String> acceptButton;
    private List<LocationBinding> locationBindings;
    private List<AssetBinding> assetBindings;


    public CollaborationCommunityDetailsPanel( String id, IModel<? extends Identifiable> iModel ) {
        super( id, iModel );
        init();
    }

    private void init() {
        name = getPlanCommunity().getName();
        description = getPlanCommunity().getDescription();
        initLocationBindings();
        initAssetBindings();
        addUri();
        addName();
        addDescription();
        addLocationBindingsPanel();
        addAssetBindingsPanel();
        addButtons();
    }

    public void initLocationBindings() {
        List<LocationBinding> bindings = new ArrayList<LocationBinding>(  );
        for ( LocationBinding locationBinding : getPlanCommunity().getLocationBindings() ) {
            bindings.add( new LocationBinding( locationBinding ) );
        }
        Collections.sort( bindings, new Comparator<LocationBinding>() {
            @Override
            public int compare( LocationBinding binding1, LocationBinding binding2 ) {
                return binding1.getPlaceholder().getName().compareTo( binding2.getPlaceholder().getName() );
            }
        } );
        List<Place> unboundPlaceholders = findUnboundLocationPlaceholders();
        Collections.sort( unboundPlaceholders, new Comparator<Place>() {
            @Override
            public int compare( Place place1, Place place2 ) {
                return place1.getName().compareTo( place2.getName() );
            }
        } );
        for ( Place unbound : unboundPlaceholders ) {
            bindings.add( new LocationBinding( unbound ) );
        }
        locationBindings = bindings;
    }

    public void initAssetBindings() {
        List<AssetBinding> bindings = new ArrayList<AssetBinding>(  );
        for ( AssetBinding assetBinding : getPlanCommunity().getAssetBindings() ) {
            bindings.add( new AssetBinding( assetBinding ) );
        }
        Collections.sort( bindings, new Comparator<AssetBinding>() {
            @Override
            public int compare( AssetBinding binding1, AssetBinding binding2 ) {
                return binding1.getPlaceholder().getName().compareTo( binding2.getPlaceholder().getName() );
            }
        } );
        List<MaterialAsset> unboundPlaceholders = findUnboundAssetPlaceholders();
        Collections.sort( unboundPlaceholders, new Comparator<MaterialAsset>() {
            @Override
            public int compare( MaterialAsset asset1, MaterialAsset asset2 ) {
                return asset1.getName().compareTo( asset2.getName() );
            }
        } );
        for ( MaterialAsset unbound : unboundPlaceholders ) {
            bindings.add( new AssetBinding( unbound ) );
        }
        assetBindings = bindings;
    }
    private List<Place> findUnboundLocationPlaceholders() {
        List<Place> boundPlaceholders = getBoundLocationPlaceholders();
        List<Place> unboundPlaceholders = new ArrayList<Place>(  );
        for ( Place place : getCommunityService().listActualEntities( Place.class, true ) ) {
            if ( place.isPlaceholder() && !boundPlaceholders.contains( place ) ) {
                unboundPlaceholders.add( place );
            }
        }
        return unboundPlaceholders;
    }

    private List<Place> getBoundLocationPlaceholders() {
        List<Place> boundPlaceholders = new ArrayList<Place>(  );
        for ( LocationBinding locationBinding : getPlanCommunity().getLocationBindings() ) {
            if ( locationBinding.isBound() )
                boundPlaceholders.add( locationBinding.getPlaceholder() );
        }
        return boundPlaceholders;
    }

    @SuppressWarnings( "unchecked" )
    private List<LocationBinding> getBoundLocationBindings() {
        return (List<LocationBinding>)CollectionUtils.select(
                locationBindings,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((LocationBinding)object).isBound();
                    }
                }
        );
    }


    private List<MaterialAsset> findUnboundAssetPlaceholders() {
        List<MaterialAsset> boundPlaceholders = getBoundAssetPlaceholders();
        List<MaterialAsset> unboundPlaceholders = new ArrayList<MaterialAsset>(  );
        for ( MaterialAsset asset : getCommunityService().listActualEntities( MaterialAsset.class, true ) ) {
            if ( asset.isPlaceholder() && !boundPlaceholders.contains( asset ) ) {
                unboundPlaceholders.add( asset );
            }
        }
        return unboundPlaceholders;
    }

    private List<MaterialAsset> getBoundAssetPlaceholders() {
        List<MaterialAsset> boundPlaceholders = new ArrayList<MaterialAsset>(  );
        for ( AssetBinding assetBinding : getPlanCommunity().getAssetBindings() ) {
            if ( assetBinding.isBound() )
                boundPlaceholders.add( assetBinding.getPlaceholder() );
        }
        return boundPlaceholders;
    }

    @SuppressWarnings( "unchecked" )
    private List<AssetBinding> getBoundAssetBindings() {
        return (List<AssetBinding>)CollectionUtils.select(
                assetBindings,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((AssetBinding)object).isBound();
                    }
                }
        );
    }



    private void addUri() {
        add( new Label( "uri", getCollaborationModel().getUri() ) );
    }

    private void addName() {
        add( new TextField<String>( "name", new PropertyModel<String>( this, "name" ) )
                .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        // do nothing
                    }
                } ) );
    }

    private void addDescription() {
        add( new TextArea<String>( "description", new PropertyModel<String>( this, "description" ) )
                .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        // do nothing
                    }
                } ) );
    }


    private void addButtons() {
        cancelButton = new AjaxLink<String>( "cancel" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Collapsed, getCommunity() ) );
            }
        };
        cancelButton.setOutputMarkupId( true );
        add( cancelButton );

        acceptButton = new AjaxLink<String>( "accept" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                acceptChanges();
                update( target, new Change( Change.Type.Updated, getCommunity() ) );
            }
        };
        acceptButton.setOutputMarkupId( true );
        add( acceptButton );
    }

    private void addLocationBindingsPanel() {
        LocationBindingsPanel locationBindingsPanel = new LocationBindingsPanel( "locationBindings", locationBindings );
        addOrReplace( locationBindingsPanel );
    }

    private void addAssetBindingsPanel() {
        AssetBindingsPanel assetBindingsPanel = new AssetBindingsPanel( "assetBindings", assetBindings );
        addOrReplace( assetBindingsPanel );
    }


    private List<Place> getPlaceCandidates() {
        return getCommunityService().listActualEntities( Place.class, true );
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    private void acceptChanges() {
        if ( hasChanged() ) {
            PlanCommunity planCommunity = getCommunity();
            MultiCommand multiCommand = new MultiCommand( getUsername(), "Update community details" );
            multiCommand.makeUndoable( false );
            multiCommand.setChange( new Change( Change.Type.Updated, getCommunity() ) );
            if ( name != null && !name.equals( planCommunity.getName() ) ) {
                multiCommand.addCommand( new UpdateModelObject(
                        getUsername(),
                        getCommunity(),
                        "name",
                        name
                ) );
            }
            if ( description != null && !description.equals( planCommunity.getDescription()  ) ) {
                multiCommand.addCommand( new UpdateModelObject(
                        getUsername(),
                        getCommunity(),
                        "description",
                        description
                ) );
            }
            if ( !isLocationBindingsUnchanged() ) {
                CommunityService communityService = getCommunityService();
                List<LocationBinding> locationBindingsUpdate = new ArrayList<LocationBinding>();
                // Just to be safe, refresh the updated location bindings
                for ( LocationBinding locationBinding :getBoundLocationBindings() ) {
                    Place location = communityService.safeFindOrCreate( Place.class, locationBinding.getLocation().getName() );
                    if ( location.isActual() ) {
                        locationBindingsUpdate.add( new LocationBinding( locationBinding.getPlaceholder(), location ));
                    }
                }
                multiCommand.addCommand( new UpdateModelObject(
                        getUsername(),
                        getCommunity(),
                        "locationBindings",
                        locationBindingsUpdate
                ) );
            }
            if ( !isAssetBindingsUnchanged() ) {
                CommunityService communityService = getCommunityService();
                List<AssetBinding> assetBindingsUpdate = new ArrayList<AssetBinding>();
                // Just to be safe, refresh the updated location bindings
                for ( AssetBinding assetBinding :getBoundAssetBindings() ) {
                    MaterialAsset asset = communityService.safeFindOrCreate( MaterialAsset.class, assetBinding.getAsset().getName() );
                    if ( asset.isActual() ) {
                        assetBindingsUpdate.add( new AssetBinding( assetBinding.getPlaceholder(), asset ) );
                    }
                }
                multiCommand.addCommand( new UpdateModelObject(
                        getUsername(),
                        getCommunity(),
                        "assetBindings",
                        assetBindingsUpdate
                ) );
            }

            doCommand( multiCommand );
        }
    }

    private boolean hasChanged() {
        PlanCommunity planCommunity = getCommunity();
        return !( name != null && name.equals( planCommunity.getName() )
                && ( description != null && description.equals( planCommunity.getDescription() ) )
                && isLocationBindingsUnchanged()
                && isAssetBindingsUnchanged()
        );
    }

    private boolean isLocationBindingsUnchanged() {
        return CollectionUtils.isEqualCollection(
                getPlanCommunity().getLocationBindings(),
                getBoundLocationBindings() );
    }

    private boolean isAssetBindingsUnchanged() {
        return CollectionUtils.isEqualCollection(
                getPlanCommunity().getAssetBindings(),
                getBoundAssetBindings() );
    }


    private PlanCommunity getCommunity() {
        return (PlanCommunity) getModel().getObject();
    }

}
