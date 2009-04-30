package com.mindalliance.channels.model;

/**
 * A model object in the scope of a scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 4, 2009
 * Time: 3:56:52 PM
 */
public interface ScenarioObject extends Identifiable {
    /**
     * Get the model object's containing scenario.
     *
     * @return a scenario
     */
    Scenario getScenario();
}
