package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.services.RFIService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 2:21 PM
 */
@Repository
public class RFIServiceImpl extends GenericSqlServiceImpl<RFI, Long> implements RFIService {

    public RFIServiceImpl() {
    }

    @Override
    @Transactional( readOnly = true)
    public boolean isCompleted( RFI rfi ) {
        return false;  //Todo
    }

    @Override
    @Transactional( readOnly = true)
    public boolean isIncomplete( RFI rfi ) {
        return false;  //Todo
    }

    @Override
    public int getRFICount( Questionnaire questionnaire ) {
        return 0;  //Todo
    }
}
