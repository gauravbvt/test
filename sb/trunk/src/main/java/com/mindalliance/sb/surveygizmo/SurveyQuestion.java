package com.mindalliance.sb.surveygizmo;

import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@RooJavaBean
@RooEquals
public class SurveyQuestion {

    private int id;

    private String _type;

    private String _subtype;

    private Map<String, String> title;

    private String shortname;

    private String varname;

    private List<Object> description;

    private boolean has_showhide_deps;

    private boolean comment;

    private Map<String, Object> properties;

    private List<SurveyOption> options;

    private List<Integer> sub_question_skus;

    String getFunction() {
        if ( "textbox".equals( _subtype ) )
            return "String";
        else if ( "checkbox".equals( _subtype ) )
            return "Checkboxes";
        else if ( "radio".equals( _subtype ) )
            return "Radio";
        else if ( "menu".equals( _subtype ) )
            return "Menu";
        else if ( "file".equals( _subtype ) )
            return "File";
        else if ( "multi_textbox".equals( _subtype ) )
            return "Multibox";
        else if ( "essay".equals( _subtype ) )
            return "Essay";
        else
            throw new RuntimeException( MessageFormat.format( "Don''t know what to do with {0}", _subtype ) );
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SurveyQuestion");
        if (title != null) sb.append('{').append(title.get("English")).append('}');
        return sb.toString();
    }

    String getEnglishTitle() {
        String english = title.get( "English" );
        if ( english.contains( "\n" ) )
            english = english.substring( 0, english.indexOf( "\n" ) );
        return english;
    }
    
}
