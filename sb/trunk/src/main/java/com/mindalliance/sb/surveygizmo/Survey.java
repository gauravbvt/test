package com.mindalliance.sb.surveygizmo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RooJavaBean
@RooJson
public class Survey {

    private int id;
    
    private String _type;

    private String _subtype;

    private int team;

    private String status;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-04:00")
    private Date created_on;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-04:00")
    private Date modified_on;

    private boolean forward_only;

    private List<String> languages;

    private String title;

    private String internal_title;

    private Map<String, String> title_ml;

    private long theme;

    private String blockby;

    private Map<String, URL> links;

    private List<SurveyPage> pages;

    private Object statistics;

    public SurveyQuestion getQuestion( int i ) {
        for ( SurveyPage page : pages )
            for ( SurveyQuestion question : page.getQuestions() )
                if ( question.getId() == i )
                    return question;

        return null;
    }
    
    public boolean isPipeSource( SurveyQuestion question ) {
        for ( SurveyPage page : pages )
            if ( question.equals( page.getPipedFrom( this ) ) )
                return true;

        return false;              
    }
}
