package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.model.Issue;
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

    public RiskConverter( Exporter exporter ) {
        super( exporter );
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
            writer.startNode( "organization" );
            writer.setValue( risk.getOrganization().getName() );
            writer.endNode();
        }
        writer.startNode( "severity" );
        writer.setValue( risk.getSeverity().toString() );
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
                Organization org = getQueryService().findOrCreate( Organization.class, reader.getValue() );
                risk.setOrganization( org );
            } else if ( nodeName.equals( "severity" ) ) {
                risk.setSeverity( Issue.Level.valueOf( reader.getValue() ) );
            }
            reader.moveUp();
        }
        return risk;
    }
}
