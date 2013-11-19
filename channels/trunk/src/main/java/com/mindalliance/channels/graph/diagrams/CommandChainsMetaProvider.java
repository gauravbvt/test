package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.UserUploadService;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.ContactInfo;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.graph.CommandRelationship;
import com.mindalliance.channels.engine.analysis.graph.Contact;
import com.mindalliance.channels.graph.AbstractMetaProvider;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.DOTAttributeProvider;
import com.mindalliance.channels.graph.URLProvider;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/13/13
 * Time: 12:57 PM
 */
public class CommandChainsMetaProvider extends AbstractMetaProvider {

    /**
     * Font for node labels.
     */
    private static final String CONTACT_FONT = "DejaVuSans";

    /**
     * Font size for node labels.
     */
    private static final String CONTACT_FONT_SIZE = "10";

    /**
     * Message format as URL template with {1} = graph id and {2} = vertex id.
     */
    private static final String CONTACT_URL_FORMAT = "?graph={0,number,0}&vertex={1}";

    private static final int MAX_LABEL_LINE_LENGTH = 20;


    private ChannelsUser user;
    private Agent agent;
    private Resource userIconDirectory;
    private CommunityService communityService;

    public CommandChainsMetaProvider( ChannelsUser user,
                                      String outputFormat,
                                      Resource imageDirectory,
                                      Resource userIconDirectory, Analyst analyst,
                                      CommunityService communityService ) {
        super( outputFormat, imageDirectory, analyst, communityService.getPlanService() );
        this.user = user;
        this.userIconDirectory = userIconDirectory;
        this.communityService = communityService;
    }

    public CommandChainsMetaProvider( Agent agent,
                                      String outputFormat,
                                      Resource imageDirectory,
                                      Resource userIconDirectory, Analyst analyst,
                                      CommunityService communityService ) {
        super( outputFormat, imageDirectory, analyst, communityService.getPlanService() );
        this.agent = agent;
        this.userIconDirectory = userIconDirectory;
        this.communityService = communityService;
    }


    @Override
    public Object getContext() {
        return communityService;
    }

    @Override
    public URLProvider getURLProvider() {
        return new URLProvider<Contact, CommandRelationship>() {

            @Override
            public String getGraphURL( Contact vertex ) {
                return null;
            }

            @Override
            public String getVertexURL( Contact contact ) {
                Object[] args = {0, contact.getUid()};
                return MessageFormat.format( CONTACT_URL_FORMAT, args );
            }

            @Override
            public String getEdgeURL( CommandRelationship commandRelationship ) {
                return null;
            }
        };
    }

    @Override
    public EdgeNameProvider getEdgeLabelProvider() {
        return new EdgeNameProvider<CommandRelationship>() {
            @Override
            public String getEdgeName( CommandRelationship commandRelationship ) {
                return "";
            }
        };
    }


    @Override
    public VertexNameProvider getVertexLabelProvider() {
        return new VertexNameProvider<Contact>() {
            @Override
            public String getVertexName( Contact contact ) {
                String label = getContactLabel( contact ).replaceAll( "\\|", "\\\\n" );
                return sanitize( label );
            }
        };
    }

    @Override
    public VertexNameProvider getVertexIDProvider() {
        return new VertexNameProvider<Contact>() {
            @Override
            public String getVertexName( Contact contact ) {
                return contact.getUid();
            }
        };
    }

    @Override
    public String getGraphOrientation() {
        // Command chains always displayed top down.
        return "TB";
    }

    private String getContactLabel( Contact contact ) {
        return ChannelsUtils.split( contact.getName(), "|", 5, MAX_LABEL_LINE_LENGTH );
    }

    private String getSquaredUserPhotoSrc( Contact contact ) throws IOException {
        String src = null;
        ChannelsUser user = contact.getUser();
        if ( user != null ) {
            if ( user.hasPhoto() ) {
                src = userIconDirectory.getFile().getAbsolutePath()
                        + "/" + user.getPhoto()
                        + UserUploadService.SQUARED
                        + UserUploadService.ICON
                        +".png";
            }
        }
        return src;
    }


    @Override
    public DOTAttributeProvider getDOTAttributeProvider() {
        return new UserCommandChainsDOTAttributeProvider();
    }

    private class UserCommandChainsDOTAttributeProvider implements DOTAttributeProvider<Contact, CommandRelationship> {

        @Override
        public List<DOTAttribute> getGraphAttributes() {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "rankdir", getGraphOrientation() ) );
            if ( getGraphSize() != null ) {
                list.add( new DOTAttribute( "size", getGraphSizeString() ) );
                list.add( new DOTAttribute( "ratio", "compress" ) );
                list.add( new DOTAttribute( "overlap", "false" ) );
            }
            return list;
        }

        @Override
        public List<DOTAttribute> getSubgraphAttributes( boolean highlighted ) {
            return DOTAttribute.emptyList();
        }

        @Override
        public List<DOTAttribute> getVertexAttributes( CommunityService communityService,
                                                       Contact contact,
                                                       boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "image", getIcon( contact ) ) );
            list.add( new DOTAttribute( "labelloc", "b" ) );
            if ( isSelected( contact ) ) {
                list.add( new DOTAttribute( "shape", "ellipse" ) );
                list.add( new DOTAttribute( "style", "solid" ) );
                list.add( new DOTAttribute( "color", "gray" ) );
            } else {
                list.add( new DOTAttribute( "shape", "none" ) );
            }
            list.add( new DOTAttribute( "fontsize", CONTACT_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontname", CONTACT_FONT ) );
            list.add( new DOTAttribute( "tooltip", getContactToolTip( contact, communityService ) ) );
            return list;
        }

        @Override
        public List<DOTAttribute> getEdgeAttributes( CommunityService communityService,
                                                     CommandRelationship edge,
                                                     boolean highlighted ) {
            List<DOTAttribute> list = DOTAttribute.emptyList();
            list.add( new DOTAttribute( "arrowhead", "none" ) );
            list.add( new DOTAttribute( "arrowtail", "open" ) );
            list.add( new DOTAttribute( "arrowsize", "0.5" ) );
            list.add( new DOTAttribute( "color", "gray" ) );
            list.add( new DOTAttribute( "fontname", EDGE_FONT ) );
            list.add( new DOTAttribute( "fontsize", EDGE_FONT_SIZE ) );
            list.add( new DOTAttribute( "fontcolor", "darkslategray" ) );
            list.add( new DOTAttribute( "len", "1.5" ) );
            list.add( new DOTAttribute( "weight", "2.0" ) );
            return list;
        }

        private String getIcon( Contact contact ) {
            try {
                String label = getContactLabel( contact );
                String[] lines = label.split( "\\|" );
                int numLines = lines.length;
                String iconSrc = getSquaredUserPhotoSrc( contact );
                if ( iconSrc == null ) {
                    String dirPath = getImageDirectory().getFile().getAbsolutePath();
                    iconSrc = dirPath
                            + "/"
                            + ( contact.getAgent().getActor().isSystem() ? "system.png" : "person.png" );
                }
                // insert numlines
                if ( numLines > 0 ) {
                    int dotIndex = iconSrc.lastIndexOf( '.' );
                    if ( dotIndex > 0 )
                    iconSrc = iconSrc.substring( 0, dotIndex )
                            + numLines
                            + "."
                            + iconSrc.substring( dotIndex + 1 );
                }
                return iconSrc;
            } catch ( IOException e ) {
                throw new RuntimeException( e );
            }
        }

    }

    private boolean isSelected( Contact contact ) {
        return user != null && contact.isForUser( user )
                || agent != null && contact.getAgent().equals( agent );
    }

    private String getContactToolTip( Contact contact, CommunityService communityService ) {
        ChannelsUser user = contact.getUser();
        if ( user != null ) {
            StringBuilder sb = new StringBuilder(  );
            for ( ContactInfo contactInfo : user.getUserRecord().getContactInfoList() ) {
                Channel channel = contactInfo.asChannel( communityService );
                if ( sb.length() > 0 ) {
                    sb.append( " - " );
                }
                sb.append( channel.getLabel() );
            }
            return sb.toString();
        } else {
            return "No user assigned yet";
        }
    }


}
