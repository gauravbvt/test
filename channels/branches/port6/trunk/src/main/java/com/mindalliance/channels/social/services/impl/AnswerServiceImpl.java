package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.rfi.Answer;
import com.mindalliance.channels.social.services.AnswerService;
import org.springframework.stereotype.Repository;

/** Implementation of the answer service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/1/12
 * Time: 9:08 PM
 */
@Repository
public class AnswerServiceImpl extends GenericSqlServiceImpl<Answer, Long> implements AnswerService {
}
