package com.mindalliance.sb;

import com.mindalliance.sb.surveygizmo.SurveyGizmo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Service that checks with SurveyGizmo for new information.
 */
@Service
public class LookupService {

    // TODO persist last check date for lookups to SG
    private Date lastCheck = new Date( 0L );

    @Autowired
    private SurveyGizmo surveyGizmo;
        
    private static final Logger LOG = LoggerFactory.getLogger( LookupService.class );

    public List<SurveyResponse> lookup( @Header( "date" ) Date date, Integer survey ) {

        if ( date.before( lastCheck ) )
            return Collections.emptyList();
        
        LOG.debug( "Checking last changes in survey #{} since {}", survey, lastCheck );
        List<SurveyResponse> responses = surveyGizmo.getResponses( survey, lastCheck );
        for ( SurveyResponse surveyResponse : responses )
            if ( surveyResponse.getDateSubmitted().after( lastCheck ) )
                lastCheck = surveyResponse.getDateSubmitted();

        return responses;
    }
}
