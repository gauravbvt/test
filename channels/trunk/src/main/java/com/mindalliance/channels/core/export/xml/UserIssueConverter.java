package com.mindalliance.channels.core.export.xml;

import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.UserIssue;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * An XStream converter for UserIssue
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 25, 2009
 * Time: 9:37:40 AM
 */
public class UserIssueConverter extends AbstractChannelsConverter {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( UserIssueConverter.class );

    public UserIssueConverter( XmlStreamer.Context context ) {
        super( context );
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return UserIssue.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    public void marshal( Object object, HierarchicalStreamWriter writer, MarshallingContext context ) {
        UserIssue issue = (UserIssue) object;
        writer.addAttribute( "id", Long.toString( issue.getId() ) );
        writer.addAttribute( "about", Long.toString( issue.getAbout().getId() ) );
        writeTags( writer, issue );
        writer.startNode( "description" );
        writer.setValue( issue.getDescription() );
        writer.endNode();
        writer.startNode( "type" );
        writer.setValue( issue.getType() );
        writer.endNode();
        writer.startNode( "severity" );
        writer.setValue( issue.getSeverity().name() );
        writer.endNode();
        writer.startNode( "remediation" );
        writer.setValue( issue.getRemediation() );
        writer.endNode();
        writer.startNode( "reportedBy" );
        writer.setValue( issue.getReportedBy() );
        writer.endNode();
        exportAttachments( issue, writer );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Map<Long, Long> idMap = getIdMap( context );
        boolean importingPlan = isImportingPlan( context );
        boolean loadingCommunity = isCommunityContext();
        UserIssue issue = null;
        try {
            Long issueId = Long.parseLong( reader.getAttribute( "id" ) );
            Long aboutId = Long.parseLong( reader.getAttribute( "about" ) );
            // When importing a segment (vs reloading a plan), ids may be re-assigned
            Long id = idMap.get( aboutId );
            aboutId = ( id == null ) ? aboutId : id;
            if ( aboutId != null ) {
                ModelObject about = find( ModelObject.class, aboutId );
                issue = new UserIssue( about );
                while ( reader.hasMoreChildren() ) {
                    reader.moveDown();
                    String nodeName = reader.getNodeName();
                    if ( nodeName.equals( "description" ) ) {
                        issue.setDescription( reader.getValue() );
                    } else if ( nodeName.equals( "tags" ) ) {
                        issue.addTags( reader.getValue() );
                    } else if ( nodeName.equals( "type" ) ) {
                        issue.setType( reader.getValue() );
                    } else if ( nodeName.equals( "severity" ) ) {
                        issue.setSeverity( Level.valueOf( reader.getValue() ) );
                    } else if ( nodeName.equals( "remediation" ) ) {
                        issue.setRemediation( reader.getValue() );
                    } else if ( nodeName.equals( "reportedBy" ) ) {
                        issue.setReportedBy( reader.getValue() );
                    } else if ( nodeName.equals( "detection-waivers" ) ) {
                        importDetectionWaivers( issue, reader );
                    } else if ( nodeName.equals( "attachments" ) ) {
                        importAttachments( issue, reader );
                    } else {
                        LOG.debug( "Unknown element " + nodeName );
                    }
                    reader.moveUp();
                }
                if ( importingPlan || loadingCommunity ) {
                    getDao().add( issue, issueId );
                } else {
                    getDao().add( issue );
                }
                idMap.put( issueId, issue.getId() );
            } else {
                LOG.debug( "Template object issue " + issueId + " is about is not found" );
            }
        } catch ( NotFoundException e ) {
            LOG.debug( "Obsolete issue", e );
            issue = null;
        }
        return issue;
    }

}
