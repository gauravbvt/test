package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.asset.AssetField;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/14
 * Time: 1:51 PM
 */
public class AssetFieldConverter extends AbstractChannelsConverter {

    public AssetFieldConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    public boolean canConvert( Class aClass ) {
        return AssetField.class.isAssignableFrom( aClass );
    }

    @Override
    public void marshal( Object object, HierarchicalStreamWriter writer, MarshallingContext context ) {
        AssetField assetField = (AssetField) object;
        // name
        writer.startNode( "name" );
        writer.setValue( assetField.getName() );
        writer.endNode();
        // required
        if ( assetField.isRequired() ) {
            writer.startNode( "required" );
            writer.setValue( Boolean.toString( true ) );
            writer.endNode();
        }
        // description
        writer.startNode( "description" );
        writer.setValue( assetField.getDescription() );
        writer.endNode();
        // group
        writer.startNode( "group" );
        writer.setValue( assetField.getGroup() );
        writer.endNode();
        // values
        if ( assetField.getValue() != null ) {
            writer.startNode( "value" );
            writer.setValue( assetField.getValue() );
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        AssetField field = new AssetField();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "name" ) ) {
                field.setName( reader.getValue() );
            } else if ( nodeName.equals( "description" ) ) {
                field.setDescription( reader.getValue() );
            } else if ( nodeName.equals( "required" ) ) {
                field.setRequired( Boolean.parseBoolean( reader.getValue() ) );
            } else if ( nodeName.equals( "group" ) ) {
                field.setGroup( reader.getValue() );
            } else if ( nodeName.equals( "value" ) ) {
                field.setValue( reader.getValue() );
            }
            reader.moveUp();
        }
        return field;
    }

}
