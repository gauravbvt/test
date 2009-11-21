package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.model.Agreement;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Plan;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Agreement XML converter.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2009
 * Time: 1:19:20 PM
 */
public class AgreementConverter extends AbstractChannelsConverter {

    public AgreementConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( AgreementConverter.class );


    public boolean canConvert( Class aClass ) {
        return Agreement.class.isAssignableFrom( aClass );
    }

    public void marshal(
            Object obj,
            HierarchicalStreamWriter writer,
            MarshallingContext context ) {
        Agreement agreement = (Agreement) obj;
        writer.startNode( "beneficiary" );
        writer.addAttribute( "id", Long.toString( agreement.getBeneficiary().getId() ) );
        writer.setValue( agreement.getBeneficiary().getName() );
        writer.endNode();
        writer.startNode( "information" );
        writer.setValue( agreement.getInformation() );
        writer.endNode();
        writer.startNode( "usage" );
        writer.setValue( agreement.getUsage() );
        writer.endNode();
        for ( ElementOfInformation eoi : agreement.getEois() ) {
            writer.startNode( "eoi" );
            context.convertAnother( eoi );
            writer.endNode();
        }
    }

    public Object unmarshal(
            HierarchicalStreamReader reader,
            UnmarshallingContext context ) {
        Map<Long, Long> idMap = getIdMap( context );
        boolean importingPlan = isImportingPlan( context );
        Plan plan = getPlan();
        Agreement agreement = new Agreement();
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "beneficiary" ) ) {
                String id = reader.getAttribute( "id" );
                Organization org = getEntity(
                        Organization.class,
                        reader.getValue(),
                        Long.parseLong( id ),
                        false,
                        importingPlan,
                        idMap );
                agreement.setBeneficiary( org );
            } else if ( nodeName.equals( "information" ) ) {
                agreement.setInformation( reader.getValue() );
            } else if ( nodeName.equals( "usage" ) ) {
                agreement.setUsage( reader.getValue() );
            } else if ( nodeName.equals( "eoi" ) ) {
                ElementOfInformation eoi = (ElementOfInformation) context.convertAnother(
                        plan,
                        ElementOfInformation.class );
                agreement.addEoi( eoi );
            } else {
                LOG.warn( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        return agreement;
    }
}
