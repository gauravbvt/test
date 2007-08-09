// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.continuum.model.project.Project;
import org.apache.maven.continuum.rpc.ProjectsReader;
import org.apache.xmlrpc.XmlRpcException;

/**
 * Command-line trigger for continuum build.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class ContinuumTrigger implements Runnable {

    private static final int ERROR_CODE = 1;
    private static final int SUCCESS_CODE = 0;

    private static Log logger = LogFactory.getLog( ContinuumTrigger.class );

    private URL address;
    private String projectName;

    /**
     * Default constructor.
     */
    public ContinuumTrigger() {
    }

    /**
     * Invoke a remote build.
     * @param args continuum URL, project
     */
    public static void main( String[] args ) {
        try {
            ContinuumTrigger trigger = new ContinuumTrigger();

            if ( args.length == 1 )
                trigger.setProjectName( args[0] );
            else {
                trigger.setAddress( new URL( args[0] ) );
                trigger.setProjectName( args[1] );
            }

            trigger.run();

        } catch ( MalformedURLException e ) {
            logger.fatal( e );
            System.exit( ERROR_CODE );
        } catch ( RuntimeException e ) {
            logger.fatal( e );
            System.exit( ERROR_CODE );
        }
    }

    /**
     * Return the value of address.
     */
    public URL getAddress() {
        return this.address;
    }

    /**
     * Set the value of address.
     * @param address The new value of address
     */
    public void setAddress( URL address ) {
        this.address = address;
    }

    /**
     * Return the value of project.
     */
    public String getProjectName() {
        return this.projectName;
    }

    /**
     * Set the value of project.
     * @param project The new value of project
     */
    public void setProjectName( String project ) {
        this.projectName = project;
    }

    /**
     * Trigger the build.
     */
    public void run() {
        ProjectsReader reader = new ProjectsReader( getAddress() );
        try {
            String name = getProjectName();
            Project p = getProject( reader, name );
            if ( p == null ) {
                logger.warn( MessageFormat.format(
                        "Project {0} not defined in continuum", name ) );
                System.exit( SUCCESS_CODE );
            }

            reader.buildProject( p );
            logger.info( MessageFormat.format(
                    "Project {0} scheduled for rebuild", name ) );
            System.exit( SUCCESS_CODE );

        } catch ( XmlRpcException e ) {
            logger.fatal( e );
            System.exit( ERROR_CODE );
        } catch ( IOException e ) {
            logger.fatal( e );
            System.exit( ERROR_CODE );
        }
    }

    /**
     * Return a project object for the given name.
     * @param reader the projects reader
     * @param name the name of the project
     * @throws IOException on IO errors
     * @throws XmlRpcException on server errors
     * @return null if no project by that name was found
     */
    private Project getProject( ProjectsReader reader, String name )
        throws XmlRpcException, IOException {

        for ( Project p : reader.readProjects() ) {
            String artifactId = p.getArtifactId();
            if ( artifactId != null && artifactId.equals( name ) )
                return p;
        }

        logger.warn(
                MessageFormat.format( "Couldn''t find project {0}", name ) );
        return null;
    }
}
