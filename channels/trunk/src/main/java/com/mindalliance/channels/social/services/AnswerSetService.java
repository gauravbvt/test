package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.rfi.AnswerSet;
import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.RFI;

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

    /**
     * Are all required questions answered?
     *
     * @param rfi an RFI
     * @return a boolean
     */
    boolean isCompleted( RFI rfi );

    /**
     * Are some required questions unanswered?
     *
     * @param rfi an RFI
     * @return a boolean
     */
    boolean isIncomplete( RFI rfi );

    /**
     * Find the answer set in an RFI to a question.
     * @param rfi an RFI
     * @param question a question
     * @return an answer set
     */
    AnswerSet findAnswers( RFI rfi, Question question );

}
