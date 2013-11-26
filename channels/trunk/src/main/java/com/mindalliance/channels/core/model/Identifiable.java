package com.mindalliance.channels.core.model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 8:01:24 PM
 */
public interface Identifiable extends Nameable {
    /**
     * Get id
     *
     * @return an id
     */
    long getId();

    /**
     * A short description
     *
     * @return a String
     */
    String getDescription();

    /**
     * Get type of object.
     *
     * @return a string
     */
    String getTypeName();

    /**
     * Whether it can be modified in production.
     *
     * @return a boolean
     */
    boolean isModifiableInProduction();

    /**
     * Get a presentable name for the identifiable's class.
     * @return a string
     */
    String getClassLabel();

    /**
     * Get UI-friendly type of object.
     *
     * @return a string
     */
    String getKindLabel();

}
