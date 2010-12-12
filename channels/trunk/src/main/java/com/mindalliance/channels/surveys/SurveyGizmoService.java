package com.mindalliance.channels.surveys;

import com.mindalliance.channels.model.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Survey Gizmo Service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 21, 2009
 * Time: 1:50:44 PM
 */
public class SurveyGizmoService extends AbstractSurveyService {
    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( SurveyGizmoService.class );
    /**
     * API key.
     */
    private String apiKey;
    /**
     * User key.
     */
    private String userKey;

    private XPath xpath;

    private static final String SCHEME_DOMAIN = "https://api.sgizmo.com/";

    private String template;

    public SurveyGizmoService() {
        xpath = XPathFactory.newInstance().newXPath();
    }

    public void setApiKey( String val ) {
        this.apiKey = val == null ? "" : val;
    }

    public void setUserKey( String val ) {
        this.userKey = val == null ? "" : val;
    }

    public void setTemplate( String val ) {
        this.template = val == null ? "" : val;
    }

    public String getApiKey() {
        String planValue = getPlan().getSurveyApiKey();
        return planValue.isEmpty() ? apiKey : planValue;
    }

    public String getUserKey() {
        String planValue = getPlan().getSurveyUserKey();
        return planValue.isEmpty() ? userKey : planValue;
    }

    public String getTemplate() {
        String planValue = getPlan().getSurveyTemplate();
        return planValue.isEmpty() ? template : planValue;
    }

    private String getBaseUrl( String command, Map<String, String> query ) {
        StringBuilder sb = new StringBuilder();
        sb.append( SCHEME_DOMAIN );
        sb.append( "?dk=" );
        sb.append( getApiKey() );
        sb.append( "&uk=" );
        sb.append( getUserKey() );
        sb.append( "&cmd=" );
        sb.append( command );
        if ( query != null ) {
            for ( String param : query.keySet() ) {
                sb.append( '&' );
                sb.append( param );
                sb.append( "=" );
                try {
                    sb.append( URLEncoder.encode( query.get( param ), "UTF-8" ) );
                } catch ( UnsupportedEncodingException e ) {
                    throw new RuntimeException( "Failure to encode query parameter", e );
                }
            }
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    protected long registerSurvey( Survey survey ) throws SurveyException {
        String xml = getSurveyXml( survey, findIssue( survey ) );
        Map<String, String> get = new HashMap<String, String>();
        Map<String, String> post = new HashMap<String, String>();
        get.put( "template", getTemplate() );
        post.put( "title", survey.getTitle() );
        post.put( "surveyxml", xml );
        String response = sendRequest( getBaseUrl( "sgCreateSurvey", get ), post );
        boolean succeeded = requestSuccess( response );
        if ( !succeeded ) {
            throw new SurveyException( "Failed to create survey" );
        }
        String id = xpathExtract( response, "/apiResults/survey/@id" );
        if ( id != null ) {
            LOG.info( "Survey " + id + " registered" );
            return Long.parseLong( id );
        } else {
            LOG.error( "Failed to create survey" );
            throw new SurveyException( "Failed to create survey" );
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void unregisterSurvey( Survey survey )  throws SurveyException {
        // todo -- do something when removing a survey is supported
    }


    private boolean requestSuccess( String response ) throws SurveyException {
        return xpathEquals( response, "/apiResults/status/text()", "success" );
    }

    private String getSurveyXml( Survey survey, Issue issue ) throws SurveyException {
        return resolveTemplate( "survey.vm", getSurveyContext( survey, issue ) );
    }

    private String xpathExtract( String xml, String expression ) throws SurveyException {
        try {
            XPathExpression expr = xpath.compile( expression );
            return expr.evaluate( new InputSource( new StringReader( xml ) ) );
        } catch ( XPathExpressionException e ) {
            LOG.error( "XPath extract failed", e );
            throw new RuntimeException( e );
        }
    }

    private boolean xpathEquals( String xml, String expression, String value ) throws SurveyException {
        String extracted = xpathExtract( xml, expression );
        return extracted.equals( value );
    }

    private String sendRequest( String urlString, Map<String, String> post ) throws SurveyException {
        try {
            URL url = new URL( urlString );
            URLConnection connection = url.openConnection();
            connection.setDoOutput( true );
            OutputStreamWriter out = new OutputStreamWriter( connection.getOutputStream() );
            StringWriter writer = new StringWriter();
            String response;
            if ( post != null ) {
                out.write( postData( post ) );
            }
            out.close();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream() ) );
            String line;
            while ( ( line = in.readLine() ) != null ) {
                writer.write( line );
                writer.write( '\n' );
            }
            in.close();
            response = writer.toString();
            return response;
        } catch ( IOException e ) {
            LOG.error( "Request failed: " + e.getMessage(), e );
            throw new SurveyException( "Request failed: " + e.getMessage(), e );
        }
    }

    private String postData( Map<String, String> post ) {
        try {
            StringBuilder sb = new StringBuilder();
            Iterator<String> params = post.keySet().iterator();
            while ( params.hasNext() ) {
                String param = params.next();
                sb.append( URLEncoder.encode( param, "UTF-8" ) );
                sb.append( '=' );
                sb.append( URLEncoder.encode( post.get( param ), "UTF-8" ) );
                if ( params.hasNext() ) sb.append( '&' );
            }
            return sb.toString();
        } catch ( UnsupportedEncodingException e ) {
            // Can't happen
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void doLaunchSurvey( Survey survey ) throws SurveyException {
        changeSurveyStatus( survey, "Launched" );
        LOG.info( "Survey " + survey.getId() + " launched" );
    }

    /**
     * {@inheritDoc}
     */
    protected void doCloseSurvey( Survey survey ) throws SurveyException {
        changeSurveyStatus( survey, "Closed" );
        LOG.info( "Survey " + survey.getId() + " closed" );
    }

    private void changeSurveyStatus( Survey survey, String status ) throws SurveyException {
        assert survey.getId() >= 0;
        Map<String, String> query = new HashMap<String, String>();
        query.put( "sid", "" + survey.getId() );
        query.put( "status", status );
        String response = sendRequest( getBaseUrl( "sgSetSurveyStatus", query ), null );
        boolean succeeded = requestSuccess( response );
        if ( !succeeded ) {
            LOG.error( "Failed to change survey status to " + status );
            throw new SurveyException( "Failed to change survey status to " + status );
        }
        survey.setStatus( surveyStatus( status ) );
        survey.resetData();
        survey.updateSurveyData( this );
    }

    /**
     * {@inheritDoc}
     */
    // TODO - cache results? 
    public SurveyData getSurveyData( Survey survey ) throws SurveyException {
        SurveyData data = new SurveyData();
        Map<String, String> get = new HashMap<String, String>();
        get.put( "status", getStatusValue( survey ) );
        String response = sendRequest( getBaseUrl( "sgGetSurveyList", get ), null );
        String surveyPath = "//survey[id/text()=\"" + survey.getId() + "\"]";
        String surveyNode = xpathExtract( response, surveyPath );
        if ( !surveyNode.isEmpty() ) {
            String value = xpathExtract( response, surveyPath + "/status/text()" );
            data.setStatus( surveyStatus( value ) );
            value = xpathExtract( response, surveyPath + "/count_inprogress/text()" );
            data.setCountInProgress( Integer.parseInt( value.trim() ) );
            value = xpathExtract( response, surveyPath + "/count_complete/text()" );
            data.setCountCompleted( Integer.parseInt( value.trim() ) );
            value = xpathExtract( response, surveyPath + "/count_partial/text()" );
            data.setCountPartial( Integer.parseInt( value.trim() ) );
            value = xpathExtract( response, surveyPath + "/count_abandoned/text()" );
            data.setCountAbandoned( Integer.parseInt( value.trim() ) );
            value = xpathExtract( response, surveyPath + "/link_preview/text()" );
            data.setPreviewLink( value );
            value = xpathExtract( response, surveyPath + "/publish_link/text()" );
            data.setPublishLink( value );
            data.setReportingLink( "http://app.sgizmo.com/survey_list_responses.php?id=" + survey.getId() );
            LOG.info( "Data retrieved for survey " + survey.getId() );
            return data;
        } else {
            LOG.warn( "Failed to find survey " + survey.getId() );
            throw new SurveyException( "Failed to find survey " + survey.getId() );
        }
    }

    private Survey.Status surveyStatus( String value ) {
        if ( value.equalsIgnoreCase( "In Design" ) ) return Survey.Status.In_design;
        else if ( value.equalsIgnoreCase( "Launched" ) ) return Survey.Status.Launched;
        else if ( value.equalsIgnoreCase( "Closed" ) ) return Survey.Status.Closed;
        else throw new RuntimeException( "Unknown status " + value );
    }


    private String getStatusValue( Survey survey ) {
        switch ( survey.getStatus() ) {
            case In_design:
                return "indesign";
            case Launched:
                return "launched";
            case Closed:
                return "closed";
            default:
                throw new RuntimeException( "Unknown survey status " + survey.getStatus().name() );
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return "The SurveyGizmo survey service";
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "SurveyGizmo";
    }
}
