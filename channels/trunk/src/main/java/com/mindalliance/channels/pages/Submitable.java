package com.mindalliance.channels.pages;

import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 9:03:52 PM
 */
public interface Submitable {
    /**
     * React to submit event
     * @param expansions expansion parameters
     */
    void onSubmit( Set<Long> expansions );

}
