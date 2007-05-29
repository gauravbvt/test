// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.elements.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.scenario.Environment;
import com.mindalliance.channels.util.CollectionType;
import com.mindalliance.channels.util.GUID;

/**
 * The model.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 */
public class Model extends AbstractElement {

    private Project project;
    private Set<Scenario> scenarios = new TreeSet<Scenario>();
    private List<Environment> environments = new ArrayList<Environment>();

    /**
     * Default constructor.
     */
    public Model() {
        super();
    }

    /**
     * Default constructor.
     * @param guid the GUID
     */
    public Model( GUID guid ) {
        super( guid );
    }

    /**
     * Return the environments where this model applies.
     */
    @CollectionType(type=Environment.class)
    public List<Environment> getEnvironments() {
        return environments;
    }

    /**
     * Set the environments where this model applies.
     * @param environments the environments to set
     */
    public void setEnvironments( List<Environment> environments ) {
        this.environments = environments;
    }

    /**
     * Add an environment.
     * @param environment the environment
     */
    public void addEnvironment( Environment environment ) {
        environments.add( environment );
    }

    /**
     * Remove an environment.
     * @param environment the environment
     */
    public void removeEnvironment( Environment environment ) {
        environments.remove( environment );
    }

    /**
     * Return the scenarios in this model.
     */
    @CollectionType(type=Scenario.class)
    public Set<Scenario> getScenarios() {
        return scenarios;
    }

    /**
     * Sets the scenarios in this model.
     * @param scenarios the scenarios to set
     */
    public void setScenarios( Set<Scenario> scenarios ) {
        this.scenarios = new TreeSet<Scenario>( scenarios );
    }

    /**
     * Add a scenario.
     * @param scenario the scenario
     */
    public void addScenario( Scenario scenario ) {
        scenarios.add( scenario );
    }

    /**
     * Remove a scenario.
     * @param scenario the scenario
     */
    public void removeScenario( Scenario scenario ) {
        scenarios.remove( scenario );
    }

    /**
     * Return the project.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set the project.
     * @param project the project to set
     */
    public void setProject( Project project ) {
        this.project = project;
    }

}
