package com.mindalliance.sb.surveygizmo;

import com.mindalliance.sb.SurveyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service( "surveygizmo" )
@RooJavaBean
public class SurveyGizmoImpl implements SurveyGizmo {

    @Value("${surveygizmo.user}")
    private String user;

    @Value("${surveygizmo.password}")
    private String password;

    private RestTemplate template = new RestTemplate();

    private static final Logger LOG = LoggerFactory.getLogger( SurveyGizmoImpl.class );

    public SurveyGizmoImpl() {
    }

    public SurveyGizmoImpl( String user, String password ) {
        this();
        this.user = user;
        this.password = password;
    }

    @Override
    public Survey getSurvey( int id ) {
        SurveyWrapper wrapper = template.getForObject(
            "https://restapi.surveygizmo.com/v3/survey/{surveyid}.json?user:pass={username}:{password}",
            SurveyWrapper.class,
            id,
            user,
            password );

        if ( !wrapper.result_ok )
            LOG.warn( "SurveyGizmo thinks the survey #{} is not ok...", id );
        return wrapper.data;
    }

    @Override
    public List<SurveyResponse> getResponses( int surveyId, Date since ) {
        String dateString = new SimpleDateFormat( "yyyy-MM-dd+HH:mm:ss", Locale.US ).format( since );
        ResponseWrapper wrapper = template.getForObject(
            "https://restapi.surveygizmo.com/v3/survey/{surveyId}/surveyresponse.json?user:pass={username}:{password}&filter[field][0]=datesubmitted&filter[operator][0]=>=&filter[value][0]={dateString}&filter[field][1]=status&filter[operator][1]==&filter[value][1]=Complete",
            ResponseWrapper.class,
            surveyId,
            user,
            password,
            dateString );

        List<SurveyResponse> result = new ArrayList<SurveyResponse>();
        if ( wrapper.result_ok ) {
            for ( Map<String, Object> rawResponse : wrapper.data ) {
                String id = (String) rawResponse.get( "id" );
                try {
                    result.add( new SurveyResponse( surveyId, rawResponse ) );
                } catch ( ParseException e ) {
                    throw new RuntimeException( "Error processing response #" + id + " of survey #" + surveyId, e );
                }
            }
        } else
            throw new RuntimeException( "SurveyGizmo error: " + wrapper.message );

        return result;
    }

    @Override
    public SurveyQuestion getQuestion( int survey, int question ) {
        QuestionWrapper o = template.getForObject(
            "https://restapi.surveygizmo.com/v2/survey/{surveyid}/surveyquestion/{q}.json?user:pass={username}:{password}",
            QuestionWrapper.class,
            survey,
            question,
            user,
            password );

        if ( !o.result_ok )
            LOG.warn( "SurveyGizmo thinks that question #{} of survey #{} is not ok...", question, survey );
        return o.data;
    }

    public static class QuestionWrapper {
        public boolean result_ok;
        public SurveyQuestion data;
    }
    
    public static class SurveyWrapper {

        public boolean result_ok;

        public Survey data;
    }

    public static class ResponseWrapper {

        public boolean result_ok;

        public int total_count;

        public int page;

        public int total_pages;

        public int results_per_page;

        public List<Map<String, Object>> data;
        
        public int code;
        
        public String message;
    }
}
