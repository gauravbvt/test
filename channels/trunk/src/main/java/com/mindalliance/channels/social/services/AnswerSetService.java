package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.rfi.AnswerSet;
import com.mindalliance.channels.social.model.rfi.Question;

/**
 * Answer service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/11/12
 * Time: 10:41 AM
 */
public interface AnswerSetService extends GenericSqlService<AnswerSet,Long> {
    /**
     * A count of how many have answered this question.
     * @param question a question
     * @return an int
     */
    int getAnswerCount( Question question );
}
