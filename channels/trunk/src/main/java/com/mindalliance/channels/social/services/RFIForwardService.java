package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFIForward;

import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/13/12
 * Time: 2:10 PM
 */
public interface RFIForwardService extends GenericSqlService<RFIForward, Long> {
    /**
     * Find usernames of who forwarded the RFI.
     *
     * @param rfi an RFI
     * @return a string
     */
    List<String> findForwarderUsernames( RFI rfi );
}
