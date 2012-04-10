package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.model.rfi.RFI;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 2:19 PM
 */
public interface RFIService extends GenericSqlService<RFI, Long> {
    
    boolean isCompleted( RFI rfi );

    boolean isIncomplete( RFI rfi );

    int getRFICount( Questionnaire questionnaire );

}
