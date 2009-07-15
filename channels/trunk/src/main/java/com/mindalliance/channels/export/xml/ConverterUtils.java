package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.export.PartSpecification;
import com.mindalliance.channels.export.ScenarioSpecification;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.util.Matcher;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * XML conversion utils.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 8, 2009
 * Time: 2:15:15 PM
 */
public class ConverterUtils {

    public static void writePartSpecification( Part part, HierarchicalStreamWriter writer ) {
        writer.startNode( "part-id" );
        writer.setValue( "" + part.getId() );
        writer.endNode();
        if ( part.getRole() != null ) {
            writer.startNode( "part-role" );
            writer.addAttribute( "name", part.getRole().getName() );
            writer.endNode();
        }
        if ( part.getTask() != null ) {
            writer.startNode( "part-task" );
            writer.addAttribute( "name", part.getTask() );
            writer.setValue( part.getDescription() );
            writer.endNode();
        }
        if ( part.getOrganization() != null ) {
            writer.startNode( "part-organization" );
            writer.addAttribute( "name", part.getOrganization().getName() );
            writer.endNode();
        }
    }

    public static List<Scenario> findMatchingScenarios( ScenarioSpecification scSpec, QueryService queryService ) {
         return findMatchingScenarios(scSpec.getName(), scSpec.getDescription(), queryService);
    }

    // TODO do semantic match on scenario name and description
    public static List<Scenario> findMatchingScenarios( String scenarioName,
                                                        String scenarioDescription,
                                                        QueryService queryService) {
        List<Scenario> scenarios = new ArrayList<Scenario>();
        try {
            scenarios.add( queryService.findScenario( scenarioName ) );
        } catch ( NotFoundException e ) {
            LoggerFactory.getLogger( ConverterUtils.class ).info(
                    "No scenario found matching name ["
                            + scenarioName
                            + "] and description ["
                            + scenarioDescription
                            + "]" );
        }
        return scenarios;
    }

    public static List<Part> findMatchingParts( Scenario scenario, PartSpecification partSpec) {
        return findMatchingParts(
                scenario,
                partSpec.getRoleName(),
                partSpec.getOrganizationName(),
                partSpec.getTask(),
                partSpec.getTaskDescription()
        );
}

    @SuppressWarnings( "unchecked" )
    public static List<Part> findMatchingParts( Scenario scenario,
                                                final String roleName,
                                                final String organizationName,
                                                final String task,
                                                final String taskDescription ) {
        List<Part> externalParts = new ArrayList<Part>();
        Iterator<Part> iterator =
                (Iterator<Part>) new FilterIterator( scenario.parts(), new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Part part = (Part) obj;
                        return partMatches( part, roleName, organizationName, task, taskDescription );
                    }
                }
                );
        while ( iterator.hasNext() ) externalParts.add( iterator.next() );
        return externalParts;
    }

    public static boolean partMatches( Part part, PartSpecification partSpec ) {
        return partMatches(
                part,
                partSpec.getRoleName(),
                partSpec.getOrganizationName(),
                partSpec.getTask(),
                partSpec.getTaskDescription()
        );
    }

    public static boolean partMatches( Part part,
                                       String roleName,
                                       String organizationName,
                                       String task,
                                       String taskDescription ) {
        if ( roleName != null ) {
            if ( part.getRole() == null
                    || !Matcher.same( part.getRole().getName(), roleName ) ) return false;
        }
        if ( organizationName != null ) {
            if ( part.getOrganization() == null
                    || !Matcher.same( part.getOrganization().getName(), organizationName ) )
                return false;
        }
        if ( task != null ) {
            if ( part.getTask() == null || !Matcher.same( part.getTask(), task ) )
                return false;
        }
        if ( taskDescription != null ) {
            if ( part.getDescription() == null || !Matcher.same( part.getDescription(), taskDescription ) )
                return false;
        }
        return true;
    }

}
