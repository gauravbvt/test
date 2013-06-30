package com.mindalliance.sb;

import com.mindalliance.sb.model.Respondent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * The actual integrator of a raw survey response.
 */
public class Integrator {

    private static final Logger LOG = LoggerFactory.getLogger( Integrator.class );
    
    /**
     * Convert a SurveyGizmo response into a respondent object.
     * @param response a completed response to the survey
     */
    @Transactional
    public Respondent integrate( SurveyResponse response ) {
        LOG.debug( "Converting survey response #{}", response.getId() );

        if ( Respondent.findRespondent( response.getId() ) == null )
            return new ResponseAdapter( response ).getRespondent();

        else
            throw new RuntimeException( "Respondent #" + response.getId() + " was already integrated" );
    }
}
