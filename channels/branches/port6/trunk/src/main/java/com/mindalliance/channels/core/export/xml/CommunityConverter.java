package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.community.CommunityDao;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.UserIssue;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * PlanCommunity XML converter.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/5/13
 * Time: 11:54 AM
 */
public class CommunityConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( CommunityConverter.class );


    public CommunityConverter( XmlStreamer.Context context ) {
        super( context );
    }

    @Override
    public boolean canConvert( Class aClass ) {
        return PlanCommunity.class.isAssignableFrom( aClass );
    }

    @Override
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context ) {
        PlanCommunity planCommunity = (PlanCommunity) source;
        CommunityDao communityDao = getCommunityDao();
        writer.addAttribute( "id", Long.toString( planCommunity.getId() ) );
        writer.addAttribute( "uri", planCommunity.getUri() );
        writer.addAttribute( "planUri", planCommunity.getPlanUri() );
        writer.addAttribute( "planVersion", Integer.toString( planCommunity.getPlanVersion() ) );
        writer.startNode( "lastId" );
        writer.setValue( String.valueOf( communityDao.getIdGenerator().getIdCounter( planCommunity.getUri() ) ) );
        writer.endNode();
        for ( Date date : planCommunity.getIdShifts().keySet() ) {
            writer.startNode( "idShift" );
            writer.addAttribute( "date", getDateFormat().format( date ) );
            writer.addAttribute( "shift", Long.toString( planCommunity.getIdShifts().get( date ) ) );
            writer.endNode();
        }
        writer.startNode( "name" );
        writer.setValue( planCommunity.getName() );
        writer.endNode();
        writer.startNode( "dateCreated" );
        writer.setValue( getDateFormat().format( planCommunity.getDateCreated() ) );
        writer.endNode();
        writer.startNode( "description" );
        writer.setValue( planCommunity.getDescription() );
        writer.endNode();
        writer.startNode( "closed" );
        writer.setValue( Boolean.toString( planCommunity.isClosed() ) );
        writer.endNode();
        writer.startNode( "plannerSupportCommunity" );
        writer.setValue( planCommunity.getPlannerSupportCommunity() );
        writer.endNode();
        writer.startNode( "userSupportCommunity" );
        writer.setValue( planCommunity.getUserSupportCommunity() );
        writer.endNode();
        writer.startNode( "communityCalendarHost" );
        writer.setValue( planCommunity.getCommunityCalendarHost() );
        writer.endNode();
        writer.startNode( "communityCalendar" );
        writer.setValue( planCommunity.getCommunityCalendar() );
        writer.endNode();
        writer.startNode( "communityCalendarPrivateTicket" );
        writer.setValue( planCommunity.getCommunityCalendarPrivateTicket() );
        writer.endNode();
        // Export attachments
        exportAttachments( planCommunity, writer );
        // Export user issues
        exportUserIssues( planCommunity, writer, context );

        // Entities
        // All requirements
        for ( Requirement requirement : communityDao.listLocal( Requirement.class ) ) {
            if ( !requirement.isUnknown() ) {
                writer.startNode( "requirement" );
                context.convertAnother( requirement );
                writer.endNode();
            }
        }
        // Locale
        Place locale = planCommunity.getCommunityLocale();
        if ( locale != null && !locale.getName().trim().isEmpty() ) {
            writer.startNode( "locale" );
            writer.addAttribute( "id", Long.toString( locale.getId() ) );
            writer.addAttribute( "kind", locale.getKind().name() );
            writer.setValue( locale.getName() );
            writer.endNode();
        }
        // Local assignments and commitments
        // TBD
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        PlanCommunity planCommunity = getContext().getPlanCommunity();
        String uri = reader.getAttribute( "uri" );
        planCommunity.setUri( uri );
        String planUri = reader.getAttribute( "planUri" );
        planCommunity.setPlanUri( planUri );
        int planVersion = Integer.parseInt( reader.getAttribute( "planVersion" ) );
        planCommunity.setPlanVersion( planVersion );
        Long id = Long.parseLong( reader.getAttribute( "id" ) );
        planCommunity.setId( id );
        getCommunityDao().loadingModelContextWithId( id ); // can set a shift in all ids to prevent overshadowing of IDs in subDaos
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if ( nodeName.equals( "lastId" ) ) {
                LOG.info( "Plan community last saved with last id " + reader.getValue() );
            } else if ( nodeName.equals( "name" ) ) {
                planCommunity.setName( reader.getValue() );
            } else if ( nodeName.equals( "dateCreated" ) ) {
                try {
                    planCommunity.setDateCreated( getDateFormat().parse( reader.getValue() ) );
                } catch ( ParseException e ) {
                    throw new RuntimeException( e );
                }
            } else if ( nodeName.equals( "idShift" ) ) {
                try {
                    Date date = getDateFormat().parse( reader.getAttribute( "date" ) );
                    Long shift = Long.parseLong( reader.getAttribute( "shift" ) );
                    planCommunity.getIdShifts().put( date, shift );
                } catch ( ParseException e ) {
                    throw new RuntimeException( e );
                }
            } else if ( nodeName.equals( "plannerSupportCommunity" ) ) {
                planCommunity.setPlannerSupportCommunity( reader.getValue() );
            } else if ( nodeName.equals( "userSupportCommunity" ) ) {
                planCommunity.setUserSupportCommunity( reader.getValue() );
            } else if ( nodeName.equals( "communityCalendarHost" ) ) {
                planCommunity.setCommunityCalendarHost( reader.getValue() );
            } else if ( nodeName.equals( "communityCalendar" ) ) {
                planCommunity.setCommunityCalendar( reader.getValue() );
            } else if ( nodeName.equals( "communityCalendarPrivateTicket" ) ) {
                planCommunity.setCommunityCalendarPrivateTicket( reader.getValue() );
            } else if ( nodeName.equals( "closed" ) ) {
                planCommunity.setClosed( Boolean.parseBoolean( reader.getValue() ) );
            } else if ( nodeName.equals( "description" ) ) {
                planCommunity.setDescription( reader.getValue() );
                // Entities
            } else if ( nodeName.equals( "attachments" ) ) {
                importAttachments( planCommunity, reader );
            } else if ( nodeName.equals( "issue" ) ) {
                context.convertAnother( planCommunity, UserIssue.class );
            } else if ( nodeName.equals( "locale" ) ) {
                String placeId = reader.getAttribute( "id" );
                String kindName = reader.getAttribute( "kind" );
                String name = reader.getValue();
                Place locale = this.getEntity( Place.class,
                        name,
                        Long.getLong( placeId ),
                        ModelEntity.Kind.valueOf( kindName ),
                        context );
                planCommunity.setCommunityLocale( locale );
            } else if ( nodeName.equals( "requirement" ) ) {
                context.convertAnother(
                        planCommunity,
                        Requirement.class );
            } else {
                LOG.debug( "Unknown element " + nodeName );
            }
            reader.moveUp();
        }
        Map<String, Object> state = new HashMap<String, Object>();
        state.put( "idMap", context.get( "idMap" ) );
        return state;
    }


}
