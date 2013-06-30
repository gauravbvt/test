package com.mindalliance.sb;

import org.springframework.integration.annotation.Header;

import java.util.Date;

/**
 * Manually trigger lookup of new SurveyGizmo responses since a given date.
 */
public interface TriggerService {
    
    void trigger( @Header( "date" ) Date date, Integer surveyId );
}
