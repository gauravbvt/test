package com.mindalliance.channels.core.model;

import java.io.Serializable;

/**
 * An identifiable with a required cardinality.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/3/11
 * Time: 3:31 PM
 */
public interface Countable extends Serializable {

    Requirement.Cardinality getCardinality();

}
