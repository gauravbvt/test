/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.elements.project;

import java.util.List;

import com.mindalliance.channels.data.elements.AbstractElement;
import com.mindalliance.channels.data.elements.reference.Environment;
import com.mindalliance.channels.util.GUID;

public class Model extends AbstractElement {

    private Project project; // backpointer
    private List<Scenario> scenarios;
    private List<Environment> environments;

    public Model() {
        super();
    }

    public Model( GUID guid ) {
        super( guid );
    }

    /**
     * @return the environments
     */
    public List<Environment> getEnvironments() {
        return environments;
    }

    /**
     * @param environments the environments to set
     */
    public void setEnvironments( List<Environment> environments ) {
        this.environments = environments;
    }

    /**
     * @return the scenarios
     */
    public List<Scenario> getScenarios() {
        return scenarios;
    }

    /**
     * @param scenarios the scenarios to set
     */
    public void setScenarios( List<Scenario> scenarios ) {
        this.scenarios = scenarios;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject( Project project ) {
        this.project = project;
    }

    /**
     * @param scenario
     */
    public void addScenario( Scenario scenario ) {
        scenarios.add( scenario );
    }

    /**
     * @param scenario
     */
    public void removeScenario( Scenario scenario ) {
        scenarios.remove( scenario );
    }

    /**
     * @param environment
     */
    public void addEnvironment( Environment environment ) {
        environments.add( environment );
    }

    /**
     * @param environment
     */
    public void removeEnvironment( Environment environment ) {
        environments.remove( environment );
    }

}
