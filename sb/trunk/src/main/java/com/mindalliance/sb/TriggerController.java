package com.mindalliance.sb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RequestMapping( "/trigger" )
@Controller
public class TriggerController {
        
    @Autowired
    private TriggerService triggerService;

    private static final Logger LOG = LoggerFactory.getLogger( TriggerController.class );

    @RequestMapping( method = RequestMethod.POST )
    public void post( @RequestParam( "survey" ) Integer survey, HttpServletResponse response ) {
        Date date = new Date();
        
        try {
            LOG.debug( "Received trigger for survey #{} on {}", survey, date );
            triggerService.trigger( date, survey );
            response.setStatus( HttpServletResponse.SC_ACCEPTED );            
            
        } catch ( Exception e ) {
            LOG.error( "Error while checking " + survey, e );
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        }
    }
}
