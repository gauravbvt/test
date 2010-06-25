package com.mindalliance.channels.model;

import java.io.Serializable;
import java.util.List;

/**
 * Someting with secrecy classifications.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 4, 2009
 * Time: 2:19:34 PM
 */
public interface Classifiable extends Serializable {
    /**
     * Get classifications.
     *
     * @return a list of classifications
     */
    List<Classification> getClassifications();

}
