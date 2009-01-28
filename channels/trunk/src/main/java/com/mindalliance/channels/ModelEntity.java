package com.mindalliance.channels;


/**
 * Something that exists independently of scenarios and describes resources.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 12, 2009
 * Time: 7:56:21 PM
 */
public interface ModelEntity extends Identifiable {
    /**
     * Get name
     *
     * @return a String
     */
    String getName();

    /**
     * Set name
     *
     * @param name a String
     */
    void setName( String name );

    /**
     * Get description
     *
     * @return a String
     */
    String getDescription();

    /**
     * Set description
     *
     * @param description a String
     */
    void setDescription( String description );

}
