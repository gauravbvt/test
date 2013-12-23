package com.mindalliance.channels.core.model;

/**
 * Something with an availability.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 20, 2010
 * Time: 3:36:03 PM
 */
public interface Available extends Identifiable {

    WorkTime getAvailability();
    
}
