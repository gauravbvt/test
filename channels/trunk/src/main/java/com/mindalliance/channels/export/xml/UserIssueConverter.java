package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.pages.Project;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.Map;

/**
 * An XStream converter for UserIssue
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 25, 2009
 * Time: 9:37:40 AM
 */
public class UserIssueConverter implements Converter {

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
        writer.addAttribute( "about", Long.toString( issue.getAbout().getId() ) );
        writer.startNode( "description" );
        writer.setValue( issue.getDescription() );
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
        UserIssue issue;
        try {
            String idString = reader.getAttribute( "about" );
            long id = idMap.get( idString );
            ModelObject about = Project.dao().find( id );
            issue = new UserIssue( about );
            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                String nodeName = reader.getNodeName();
                if ( nodeName.equals( "description" ) ) {
                    issue.setDescription( reader.getValue() );
                }
                if ( nodeName.equals( "remediation" ) ) {
                    issue.setRemediation( reader.getValue() );
                }
                if ( nodeName.equals( "reportedBy" ) ) {
                    issue.setReportedBy( reader.getValue() );
                }
                reader.moveUp();
            }
            Project.dao().addUserIssue( issue );
        } catch ( NotFoundException e ) {
            XmlStreamer.LOG.warn( "Obsolete issue", e );
            issue = null;
        }
        return issue;
    }

}
