package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.asset.AssetField;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/14
 * Time: 1:13 PM
 */
public class MaterialAssetConverter extends EntityConverter {

    public MaterialAssetConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    protected Class<? extends ModelEntity> getEntityClass() {
        return MaterialAsset.class;
    }

    @Override
    public boolean canConvert( Class type ) {
        return MaterialAsset.class.isAssignableFrom( type );
    }

    @Override
    protected void writeSpecifics( ModelEntity entity, HierarchicalStreamWriter writer, MarshallingContext context ) {
        MaterialAsset asset = (MaterialAsset)entity;
        // dependencies
        for ( MaterialAsset dependency : asset.getDependencies() ) {
            writer.startNode( "dependency");
            writer.addAttribute( "id", Long.toString( dependency.getId() ) );
            writer.addAttribute( "kind", dependency.isType() ? "Type" : "Actual" );
            writer.setValue( dependency.getName() );
            writer.endNode();
        }
        // placeholder
        if ( asset.isPlaceholder() ) {
            writer.startNode( "placeholder" );
            writer.setValue( Boolean.toString( true ) );
            writer.endNode();
        }
        // fields
        for ( AssetField assetField : asset.getFields() ) {
            writer.startNode( "field" );
            context.convertAnother( assetField );
            writer.endNode();
        }
    }


    @Override
    protected void setSpecific( ModelEntity entity, String nodeName, HierarchicalStreamReader reader, UnmarshallingContext context ) {
        MaterialAsset asset = (MaterialAsset)entity;
        if ( nodeName.equals( "dependency" ) ) {
            String idString = reader.getAttribute( "id" );
            asset.addDependency( getEntity(
                    MaterialAsset.class,
                    reader.getValue(),
                    Long.parseLong( idString ),
                    ModelEntity.Kind.Type,
                    context ) );
        } else if ( nodeName.equals( "placeholder" ) ) {
            asset.setPlaceholder( Boolean.parseBoolean( reader.getValue() ) );
        } else if ( nodeName.equals( "field" ) ) {
            AssetField field = (AssetField)context.convertAnother( getModel(), AssetField.class );
            asset.addField( field );
        }
    }

}
