// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import org.zkoss.zhtml.Text;
import org.zkoss.zul.Box;

import com.mindalliance.channels.System;
import com.mindalliance.channels.User;
import com.mindalliance.channels.model.Scenario;

/**
 * The world-famous scenario viewer.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision:$
 */
public class ScenarioViewer extends Box {

    private System system;
    private Scenario scenario;
    private User user;

    /**
     * Default constructor.
     * @param system the system
     * @param scenario the scenario
     * @param user the user
     */
    public ScenarioViewer( System system, Scenario scenario, User user ) {
        super();
        this.system = system;
        this.scenario = scenario;
        this.user = user;

        appendChild( new Text( "Scenario Viewer for " + scenario.getName() ) );
    }

    /**
     * Return the value of scenario.
     */
    public Scenario getScenario() {
        return this.scenario;
    }

    /**
     * Return the value of system.
     */
    public System getSystem() {
        return this.system;
    }

    /**
     * Return the value of user.
     */
    public User getUser() {
        return this.user;
    }
}
