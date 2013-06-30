package com.mindalliance.sb.surveygizmo;

import com.mindalliance.sb.SurveyResponse;

import java.util.Date;
import java.util.List;

/**
 * External interface to SurveyGizmo.
 */
public interface SurveyGizmo {

    /**
     * Get a handle on a survey.
     * @param id the survey id
     * @return the survey
     */
    Survey getSurvey( int id );
    
    List<SurveyResponse> getResponses( int surveyId, Date since );
    
    SurveyQuestion getQuestion( int survey, int question );

}
