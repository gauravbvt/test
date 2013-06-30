package com.mindalliance.sb.surveygizmo;

import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;

import java.util.List;
import java.util.Map;

@RooJavaBean
@RooEquals
public class SurveyPage {

    private int id;

    private String _type;

    private String _subtype;

    private Map<String, String> title;

    private Object description;

    private List<SurveyQuestion> questions;

    private Object properties;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SurveyPage{");
        if (title != null) {
            sb.append(title.get("English"));
        }
        sb.append('}');
        return sb.toString();
    }

    public SurveyQuestion getPipedFrom( Survey survey ) {
        if ( properties != null && properties instanceof Map ) {
            Map<String, String> m = (Map<String, String>) properties;
            String from = m.get( "piped_from" );
            if ( from != null )
                return survey.getQuestion( Integer.parseInt( from ) );
        }

        return null;
    }    
}
