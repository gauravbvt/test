package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Risk;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Risk converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2009
 * Time: 11:21:40 AM
 */
public class RiskConverter extends AbstractChannelsConverter {

    public RiskConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Risk.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal(
            Object object,
            HierarchicalStreamWriter writer,
            MarshallingContext context ) {
        Risk risk = (Risk) object;
        if ( risk.getType() != null ) {
            writer.startNode( "type" );
            writer.setValue( risk.getType().name() );
            writer.endNode();
        }
        if ( risk.getOrganization() != null ) {
            Organization organization = risk.getOrganization();
            writer.startNode( "organization" );
            writer.addAttribute( "id", Long.toString( organization.getId() ) );
            writer.addAttribute( "kind", organization.getKind().name() );
            writer.setValue( organization.getName() );
            writer.endNode();
        }
        writer.startNode( "severity" );
        writer.setValue( risk.getSeverity().toString() );
        writer.endNode();
        writer.startNode( "description" );
        writer.setValue( risk.getDescription() );
        writer.endNode();
    }

    /**
     * {@inheritDoc}
     */
    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        Risk risk = new Risk();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "type" ) ) {
                Risk.Type type = Risk.Type.valueOf( reader.getValue() );
                risk.setType( type );
            } else if ( nodeName.equals( "organization" ) ) {
                Long id = Long.parseLong(reader.getAttribute( "id"));
                String kind = reader.getAttribute( "kind" );
                Organization org = getEntity(
                        Organization.class,
                        reader.getValue(),
                        id,
                        ModelEntity.Kind.valueOf( kind ),
                        context); 
                risk.setOrganization( org );
            } else if ( nodeName.equals( "severity" ) ) {
                risk.setSeverity( Issue.Level.valueOf( reader.getValue() ) );
            } else if ( nodeName.equals( "description" ) ) {
                risk.setDescription( reader.getValue() );
            }
            reader.moveUp();
        }
        return risk;
    }
}
