package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Deletable;
import com.mindalliance.channels.Issue;

/**
 * An issue panel with a deletable checkbox
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 7:45:02 PM
 */
public interface DeletableIssue extends Deletable {
    /**
     *
     * @return the underlying issue
     */
    Issue getIssue();
}
