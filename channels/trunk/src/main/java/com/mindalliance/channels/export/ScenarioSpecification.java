package com.mindalliance.channels.export;

import java.io.Serializable;

/**
 * Specification of a scenario.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 21, 2009
 * Time: 7:30:39 PM
 */
public class ScenarioSpecification implements Serializable {
    /**
     * Name.
     */
    private String name;
    /**
     * Description.
     */
    private String description;


    public ScenarioSpecification ( String name, String description  ) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }
}
