package com.mindalliance.channels.social.services;

import com.mindalliance.channels.core.orm.service.GenericSqlService;
import com.mindalliance.channels.social.model.rfi.AnswerSet;
import com.mindalliance.channels.social.model.rfi.Question;
import com.mindalliance.channels.social.model.rfi.RFI;

import java.util.List;

/**
 * AnswerSet service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/11/12
 * Time: 10:41 AM
 */
public interface AnswerSetService extends GenericSqlService<AnswerSet, Long> {
    /**
     * A count of how many have answered this question.
     *
     * @param question a question
     * @return an int
     */
    int getAnswerCount( Question question );

    /**
     * Find the answer set in an RFI to a question.
     *
     * @param rfi      an RFI
     * @param question a question
     * @return an answer set
     */
    AnswerSet findAnswerSet( RFI rfi, Question question );

    /**
     * Find all answer sets for an RFI.
     *
     * @param rfi an RFI
     * @return a list of answer sets
     */
    List<AnswerSet> select( RFI rfi );

}
