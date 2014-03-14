package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelEntity;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Info product XML converter.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/30/12
 * Time: 1:49 PM
 */
public class InfoProductConverter extends EntityConverter {

    public InfoProductConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    public boolean canConvert( Class type ) {
        return InfoProduct.class.isAssignableFrom( type );
    }

    @Override
    protected Class<? extends ModelEntity> getEntityClass() {
        return InfoProduct.class;
    }

    @Override
    protected void writeSpecifics( ModelEntity entity, HierarchicalStreamWriter writer, MarshallingContext context ) {
        InfoProduct infoProduct = (InfoProduct) entity;
        // eois
        for ( ElementOfInformation eoi : infoProduct.getLocalEois() ) {
            writer.startNode( "eoi" );
            context.convertAnother( eoi );
            writer.endNode();
        }
        writer.startNode( "classificationsLinked" );
        writer.setValue( String.valueOf( infoProduct.isClassificationsLinked() ) );
        writer.endNode();

    }

    @Override
    protected void setSpecific( ModelEntity entity, String nodeName, HierarchicalStreamReader reader, UnmarshallingContext context ) {
        InfoProduct infoProduct = (InfoProduct) entity;
        // eois
        if ( nodeName.equals( "eoi" ) ) {
            ElementOfInformation eoi = (ElementOfInformation) context.convertAnother(
                    getModel(),
                    ElementOfInformation.class );
            infoProduct.addLocalEoi( eoi );
            // classifications linked
        } else if ( nodeName.equals( "classificationsLinked" ) ) {
            boolean classificationsLinked = reader.getValue().equals( "true" );
            infoProduct.setClassificationsLinked( classificationsLinked );
        }
    }
}
