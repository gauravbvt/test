package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Exporter;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.UserIssue;
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

    public UserIssueConverter( Exporter exporter ) {
        super( exporter );
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
        writer.addAttribute( "id", Long.toString( issue.getId() ));
        writer.addAttribute( "about", Long.toString( issue.getAbout().getId() ) );
        writer.startNode( "description" );
        writer.setValue( issue.getDescription() );
        writer.endNode();
        writer.startNode( "severity" );
        writer.setValue( issue.getSeverity().toString() );
        writer.endNode();
        writer.startNode( "remediation" );
        writer.setValue( issue.getRemediation() );
        writer.endNode();
        writer.startNode( "reportedBy" );
        writer.setValue( issue.getReportedBy() );
        writer.endNode();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context ) {
        Map<String, Long> idMap = (Map<String, Long>) context.get( "idMap" );
        UserIssue issue = null;
        try {
            String issueId = reader.getAttribute( "id" );
            String idString = reader.getAttribute( "about" );
            Long id = idMap.get( idString );
            if ( id != null ) {
                ModelObject about = getQueryService().find( ModelObject.class, id );
                issue = new UserIssue( about );
                while ( reader.hasMoreChildren() ) {
                    reader.moveDown();
                    String nodeName = reader.getNodeName();
                    if ( nodeName.equals( "description" ) ) {
                        issue.setDescription( reader.getValue() );
                    } else if ( nodeName.equals( "severity" ) ) {
                        issue.setSeverity( Issue.Level.valueOf( reader.getValue() ) );
                    } else if ( nodeName.equals( "remediation" ) ) {
                        issue.setRemediation( reader.getValue() );
                    } else if ( nodeName.equals( "reportedBy" ) ) {
                        issue.setReportedBy( reader.getValue() );
                    }
                    reader.moveUp();
                }
                getQueryService().add( issue );
                idMap.put( issueId, issue.getId() );                
            } else {
                LOG.warn( "Issue's model object not found at " + id );
            }
        } catch ( NotFoundException e ) {
            XmlStreamer.LOG.warn( "Obsolete issue", e );
            issue = null;
        }
        return issue;
    }

}
