// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.ui;

import org.zkoss.zul.Box;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Splitter;
import org.zkoss.zul.Vbox;

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

    private ScenarioTimeline whenPane;

    private TreeGraphPane whatPane;

    private TreeGraphPane whoPane;

    /**
     * Default constructor.
     * 
     * @param system
     *            the system
     * @param scenario
     *            the scenario
     * @param user
     *            the user
     */
    public ScenarioViewer(System system, Scenario scenario, User user) {
        super();
        this.system = system;
        this.scenario = scenario;
        this.user = user;

        whenPane = new ScenarioTimeline(scenario);
        whenPane.setWidth("100%");
        whatPane = new TreeGraphPane(system, scenario, user);
        whatPane.setWidth("300px");
        whatPane.setHeight("400px");
        whoPane = new TreeGraphPane(system, scenario, user);
        whoPane.setWidth("300px");
        whoPane.setHeight("400px");
        Vbox vertBox = new Vbox();
        vertBox.appendChild(whenPane);
        Splitter splitter = new Splitter();
        splitter.setCollapse("none");

        Box box = new Hbox();
        box.appendChild(whatPane);
        splitter = new Splitter();
        splitter.setCollapse("none");
        box.appendChild(whoPane);
        vertBox.appendChild(box);
        appendChild(vertBox);
        // appendChild(whatPane);
        // appendChild( new Text( "Scenario Viewer for " + scenario.getName() )
        // );
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
