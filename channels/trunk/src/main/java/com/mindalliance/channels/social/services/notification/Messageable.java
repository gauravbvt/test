package com.mindalliance.channels.social.services.notification;


import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.social.services.SurveysDAO;

import java.text.SimpleDateFormat;

/**
 * Something that can be the subject of a message.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/8/12
 * Time: 1:53 PM
 */
public interface Messageable {

    enum Format {
        TEXT,
        HTML
    }

    /**
     * Simple date format.
     */
    static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "M/d/yyyy HH:mm" );

    String getToUsername( String topic );

    String getFromUsername( String topic );

    String getContent(
            String topic,
            Format format,
            PlanService planService,
            SurveysDAO surveysDAO );

    String getSubject(
            String topic,
            Format format,
            PlanService planService,
            SurveysDAO surveysDAO );

    String getPlanUri();

    int getPlanVersion();

    String getTypeName();


}
