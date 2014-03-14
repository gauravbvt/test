package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.asset.AssetConnection;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/10/14
 * Time: 11:25 AM
 */
public class AssetConnectionConverter extends AbstractChannelsConverter {

    public AssetConnectionConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    public boolean canConvert( Class aClass ) {
        return AssetConnection.class.isAssignableFrom( aClass );
    }

    @Override
    public void marshal( Object object, HierarchicalStreamWriter writer, MarshallingContext context ) {
        AssetConnection assetConnection = (AssetConnection)object;
        // type
        writer.startNode( "type" );
        writer.setValue( assetConnection.getType().name() );
        writer.endNode();
        // asset
        writer.startNode( "asset" );
        MaterialAsset asset = assetConnection.getAsset();
        writer.addAttribute( "id", Long.toString( asset.getId() ) );
        writer.addAttribute( "kind", asset.getKind().name() );
        writer.setValue( asset.getName() );
        writer.endNode();
        // properties
        Map<String,String> properties = assetConnection.getProperties();
        for ( String key : properties.keySet() ) {
            writer.startNode( "property" );
            writer.addAttribute( "name", key );
            writer.setValue( properties.get( key ) );
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        AssetConnection assetConnection = null;
        String typeName = null;
        MaterialAsset asset = null;
        Map<String, String> properties = new HashMap<String, String>(  );
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "type" ) ) {
                typeName = reader.getValue();
            } else if ( nodeName.equals( "asset" ) ) {
                Long id = Long.parseLong( reader.getAttribute( "id" ) );
                String kind = reader.getAttribute( "kind" );
                asset = getEntity(
                        MaterialAsset.class,
                        reader.getValue(),
                        id,
                        ModelEntity.Kind.valueOf( kind ),
                        context );
            } else if ( nodeName.equals( "property") ) {
                String name = reader.getAttribute( "name" );
                properties.put( name, reader.getValue() );
            }
            reader.moveUp();
        }
        if ( typeName != null && asset != null ) {
            assetConnection = new AssetConnection( AssetConnection.Type.valueOf( typeName ), asset );
            assetConnection.setProperties( properties );
        }
        return assetConnection;
    }

}
