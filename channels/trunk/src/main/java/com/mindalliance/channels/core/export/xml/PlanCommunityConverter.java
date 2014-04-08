package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.IssueDetectionWaiver;
import com.mindalliance.channels.core.community.AssetBinding;
import com.mindalliance.channels.core.community.CommunityDao;
import com.mindalliance.channels.core.community.LocationBinding;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.UserIssue;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
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
public class PlanCommunityConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( PlanCommunityConverter.class );


    public PlanCommunityConverter( XmlStreamer.Context context ) {
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
        writer.addAttribute( "planUri", planCommunity.getModelUri() );
        writer.addAttribute( "planVersion", Integer.toString( planCommunity.getModelVersion() ) );
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
        // Locale - todo - obsolete
        /*Place locale = planCommunity.getCommunityLocale();
        if ( locale != null && !locale.getName().trim().isEmpty() ) {
            writer.startNode( "locale" );
            writer.addAttribute( "id", Long.toString( locale.getId() ) );
            writer.addAttribute( "kind", locale.getKind().name() );
            writer.setValue( locale.getName() );
            writer.endNode();
        }*/
        for ( LocationBinding locationBinding : planCommunity.getLocationBindings() ) {
            if ( locationBinding.isBound() ) {
                writer.startNode( "locationBinding" );
                // placeholder
                writer.startNode( "placeholder" );
                Place placeholder = locationBinding.getPlaceholder();
                writer.addAttribute( "id", Long.toString( placeholder.getId() ) );
                writer.addAttribute( "kind", placeholder.getKind().name() );
                writer.setValue( placeholder.getName() );
                writer.endNode();
                // bound location
                Place location = locationBinding.getLocation();
                writer.startNode( "location" );
                writer.addAttribute( "id", Long.toString( location.getId() ) );
                writer.addAttribute( "kind", location.getKind().name() );
                writer.setValue( location.getName() );
                writer.endNode();
                writer.endNode();
            }
        }
        for ( AssetBinding assetBinding : planCommunity.getAssetBindings() ) {
            if ( assetBinding.isBound() ) {
                writer.startNode( "assetBinding" );
                // placeholder
                writer.startNode( "placeholder" );
                MaterialAsset placeholder = assetBinding.getPlaceholder();
                writer.addAttribute( "id", Long.toString( placeholder.getId() ) );
                writer.addAttribute( "kind", placeholder.getKind().name() );
                writer.setValue( placeholder.getName() );
                writer.endNode();
                // bound asset
                MaterialAsset asset = assetBinding.getAsset();
                writer.startNode( "asset" );
                writer.addAttribute( "id", Long.toString( asset.getId() ) );
                writer.addAttribute( "kind", asset.getKind().name() );
                writer.setValue( asset.getName() );
                writer.endNode();
                writer.endNode();
            }
        }

        // Export issue detection waivers (for non-modelObjects)
        for ( IssueDetectionWaiver waiver : planCommunity.getIssueDetectionWaivers() ) {
            writer.startNode( "issueDetectionWaiver" );
            context.convertAnother( waiver );
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        PlanCommunity planCommunity = getContext().getPlanCommunity();
        String uri = reader.getAttribute( "uri" );
        planCommunity.setUri( uri );
        String planUri = reader.getAttribute( "planUri" );
        planCommunity.setModelUri( planUri );
        int planVersion = Integer.parseInt( reader.getAttribute( "planVersion" ) );
        planCommunity.setModelVersion( planVersion );
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
           /* } else if ( nodeName.equals( "locale" ) ) { // OBSOLETE
                String placeId = reader.getAttribute( "id" );
                String kindName = reader.getAttribute( "kind" );
                String name = reader.getValue();
                Place locale = this.getEntity( Place.class,
                        name,
                        Long.getLong( placeId ),
                        ModelEntity.Kind.valueOf( kindName ),
                        context );
                planCommunity.setCommunityLocale( locale );
            }*/
            } else if ( nodeName.equals( "locationBinding" ) ) {
                String placeId;
                String kindName;
                String name;
                reader.moveDown();
                assert reader.getNodeName().equals( "placeholder" );
                placeId = reader.getAttribute( "id" );
                kindName = reader.getAttribute( "kind" );
                name = reader.getValue();
                Place placeholder = this.getEntity( Place.class,
                        name,
                        Long.getLong( placeId ),
                        ModelEntity.Kind.valueOf( kindName ),
                        context );
                reader.moveUp();
                reader.moveDown();
                assert reader.getNodeName().equals( "location" );
                placeId = reader.getAttribute( "id" );
                kindName = reader.getAttribute( "kind" );
                name = reader.getValue();
                Place location = this.getEntity( Place.class,
                        name,
                        Long.getLong( placeId ),
                        ModelEntity.Kind.valueOf( kindName ),
                        context );
                reader.moveUp();
                planCommunity.addLocationBinding( placeholder, location );
            } else if ( nodeName.equals( "assetBinding" ) ) {
                String assetId;
                String kindName;
                String name;
                reader.moveDown();
                assert reader.getNodeName().equals( "placeholder" );
                assetId = reader.getAttribute( "id" );
                kindName = reader.getAttribute( "kind" );
                name = reader.getValue();
                MaterialAsset placeholder = this.getEntity( MaterialAsset.class,
                        name,
                        Long.getLong( assetId ),
                        ModelEntity.Kind.valueOf( kindName ),
                        context );
                reader.moveUp();
                reader.moveDown();
                assert reader.getNodeName().equals( "asset" );
                assetId = reader.getAttribute( "id" );
                kindName = reader.getAttribute( "kind" );
                name = reader.getValue();
                MaterialAsset asset = this.getEntity( MaterialAsset.class,
                        name,
                        Long.getLong( assetId ),
                        ModelEntity.Kind.valueOf( kindName ),
                        context );
                reader.moveUp();
                planCommunity.addAssetBinding( placeholder, asset );
            } else if ( nodeName.equals( "requirement" ) ) {
                context.convertAnother(
                        planCommunity,
                        Requirement.class );
            } else if ( nodeName.equals( "issueDetectionWaiver" ) ) {
                planCommunity.addIssueDetectionWaiver( (IssueDetectionWaiver) context.convertAnother(
                        planCommunity,
                        IssueDetectionWaiver.class ) );
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
