package com.mindalliance.channels.pages.components.community;

import com.mindalliance.channels.core.community.AssetBinding;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/7/14
 * Time: 1:56 PM
 */
public class AssetBindingsPanel extends AbstractCommandablePanel {
    private WebMarkupContainer bindingsContainer;

    private List<AssetBinding> tempBindings;

    public AssetBindingsPanel( String id, List<AssetBinding> assetBindings ) {
        super( id );
        tempBindings = assetBindings;
        init();
    }

    private void init() {
        bindingsContainer = new WebMarkupContainer( "bindingsContainer" );
        bindingsContainer.setOutputMarkupId( true );
        add( bindingsContainer );
        addBindingsList();
    }


    private void addBindingsList() {

        ListView<AssetBindingWrapper> bindingsList = new ListView<AssetBindingWrapper>(
                "bindings",
                new PropertyModel<List<AssetBindingWrapper>>( this, "bindings" )
        ) {
            @Override
            protected void populateItem( ListItem<AssetBindingWrapper> item ) {
                AssetBindingWrapper assetBindingWrapper = item.getModelObject();
                String label = assetBindingWrapper.getPlaceholder().getName();
                item.add( new Label( "placeholder", label ) );
                item.add( makeBindingField( assetBindingWrapper ) );
            }
        };
        bindingsList.setOutputMarkupId( true );
        bindingsContainer.addOrReplace( bindingsList );
    }

    @SuppressWarnings( "unchecked" )
    private Component makeBindingField( AssetBindingWrapper assetBindingWrapper ) {
        final List<MaterialAsset> choices = (List<MaterialAsset>) CollectionUtils.select(
                getQueryService().listActualEntities( MaterialAsset.class, true ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (MaterialAsset) object ).isPlaceholder();
                    }
                }
        );
        AutoCompleteTextField<String> assetField = new AutoCompleteTextField<String>(
                "actual",
                new PropertyModel<String>( assetBindingWrapper, "assetName" ) ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                if ( choices != null ) {
                    for ( MaterialAsset asset : choices ) {
                        String choice = asset.getName();
                        if ( getQueryService().likelyRelated( s, choice ) )
                            candidates.add( choice );
                    }
                    Collections.sort( candidates );
                }
                return candidates.iterator();
            }
        };
        assetField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        assetField.setEnabled( isPlanner() );
        addInputHint( assetField, "Enter the name of a known asset" );
        return assetField;
    }

    public List<AssetBindingWrapper> getBindings() {
        List<AssetBindingWrapper> bindings =
                new ArrayList<AssetBindingWrapper>();
        for ( AssetBinding assetBinding : tempBindings ) {
            bindings.add( new AssetBindingWrapper( assetBinding ) );
        }
        return bindings;
    }



    public class AssetBindingWrapper implements Serializable {

        private AssetBinding assetBinding;

        public AssetBindingWrapper( AssetBinding assetBinding ) {
            this.assetBinding = assetBinding;
        }

        public AssetBindingWrapper( MaterialAsset placeholder ) {
            assetBinding = new AssetBinding( placeholder );
        }

        public MaterialAsset getAsset() {
            return assetBinding.getAsset();
        }

        public MaterialAsset getPlaceholder() {
            return assetBinding.getPlaceholder();
        }

        public void setAsset( MaterialAsset asset ) {
            assetBinding.setAsset( asset );
        }

        public boolean isBound() {
            return assetBinding.getAsset() != null;
        }

        public String getAssetName() {
            MaterialAsset asset = assetBinding.getAsset();
            return asset != null
                    ? asset.getName()
                    : "";
        }

        public void setAssetName( String val ) {
            if ( assetBinding.isBound() ) {
                if ( val == null || val.isEmpty() ) {
                    assetBinding.setAsset( null );
                } else {
                    MaterialAsset asset = getCommunityService().safeFindOrCreate( MaterialAsset.class, val );
                    assert asset.isActual();
                    assetBinding.setAsset( asset );
                }
            } else {
                if ( val != null && !val.isEmpty() ) {
                    MaterialAsset asset = getCommunityService().safeFindOrCreate( MaterialAsset.class, val );
                    assert asset.isActual();
                    assetBinding.setAsset( asset );
                }
            }
        }
    }}
