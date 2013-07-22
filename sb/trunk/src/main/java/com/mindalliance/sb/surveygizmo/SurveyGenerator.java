package com.mindalliance.sb.surveygizmo;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* Big method generator for hooking to a survey.
*/
public class SurveyGenerator {
    // TODO find how to differentiate Add-as-needed questions
    private static final Set<Integer> asNeeded = new HashSet<Integer>(
        Arrays.asList( 44, 135, 236, 208, 179, 241, 111, 251, 127 ) );

    private final Survey survey;
    private final SurveyGizmo gizmo;

    public SurveyGenerator( SurveyGizmo gizmo, Survey survey ) {
        this.gizmo = gizmo;
        this.survey = survey;
    }
    
    public void output( PrintStream out ) {
        for ( SurveyPage page : survey.getPages() ) {
            out.println();
            out.println( "//------------ Page " + page.getId() + ": " + page.getTitle().get( "English" ) );
            SurveyQuestion pipedFrom = page.getPipedFrom( survey );
            if ( pipedFrom != null )
                outputPipedQuestion( out, page, pipedFrom );

            else {
                for ( SurveyQuestion question : page.getQuestions() ) {
                    if ( "SurveyDecorative".equals( question.get_type() ) )
                        continue;
                    int questionId = question.getId();
                    if ( survey.isPipeSource( question ) ) {
                        out.println( "// " + question.getEnglishTitle() );
                        out.println( "Map<Object,Object> pipeObjects" + questionId + " = new LinkedHashMap<Object, Object>();" );
                        out.println( "for ( Entry<Object, String> entry : response.getPipeKeys( " + questionId + " ).entrySet() )" );
                        out.println( "    pipeObjects" + questionId + ".put( entry.getKey(), question" + questionId + "( entry.getValue() ) );" );
                    }
                    else if ( asNeeded.contains( questionId ) )
                        outputAsNeededQuestion( out, question, null );
                    else
                        outputQuestion( out, question, null );
    
                    out.println();
                }
            }
        }
    }

    private void outputPipedQuestion( PrintStream out, SurveyPage page, SurveyQuestion source ) {
        out.println( "for( Entry<Object,Object> source : pipeObjects" + source.getId() + ".entrySet() ) {" );
        for ( SurveyQuestion question : page.getQuestions() ) {
            if ( "SurveyDecorative".equals( question.get_type() ) )
                continue;
            int questionId = question.getId();
            if ( survey.isPipeSource( question ) ) {
                out.println( "// " + question.getEnglishTitle() );
                out.println( "// TODO piped pipe??" );
            }
            else if ( asNeeded.contains( questionId ) )
                outputAsNeededQuestion( out, question, source );
            else
                outputQuestion( out, question, source );

            out.println();
        }
        out.println( "}" );
    }

    private void outputAsNeededQuestion( PrintStream out, SurveyQuestion question, SurveyQuestion source ) {
        int questionId = question.getId();
        out.println( "// " + question.getEnglishTitle() );
        out.print( "for ( int key : " );
        if ( source != null )
            out.print( "response.getSourcedKeys( source.getKey(), " );
        else
            out.print( "response.getKeys( " );

        List<Integer> skus = question.getSub_question_skus();
        boolean first = true;
        if ( skus != null )
            for ( Integer sku : skus ) {
                if ( !first )
                    out.print( ", " );
                out.print( sku );
                first = false;
            }
        else
            out.print( questionId );

        out.println( " ) )" );

        if ( "group".equals( question.get_subtype() ) ) {
            out.print( "    question" + questionId + '(' );
            out.println( source == null ? "" : " source.getValue(),");
            if ( skus != null ) {
                first = true;
                for ( int sku : skus )  {
                    SurveyQuestion q = gizmo.getQuestion( survey.getId(), sku );
                    if ( !first )
                        out.println( "," );
                    out.print( MessageFormat.format( "        response.get{0}( {3}key, {1} /* {2} */ )",
                                                     q.getFunction(),
                                                     sku,
                                                     q.getEnglishTitle(),
                                                     source == null ? "" : "source.getKey(), " ) );
                    first = false;
                }
            }
            out.println( " );" );
        }
        else
            out.println( MessageFormat.format( "    question{0}( {3}response.get{1}( {4}key, {2} ) );",
                                               questionId,
                                               question.getFunction(),
                                               questionId,
                                               source == null ? "" : "source.getValue(), ",
                                               source == null ? "" : "source.getKey(), " ) );
    }

    private void outputQuestion( PrintStream out, SurveyQuestion question, SurveyQuestion source ) {
        int questionId = question.getId();
        out.println( "// " + question.getEnglishTitle() );
        if ( "group".equals( question.get_subtype() ) ) {
            out.print( "question" + questionId + '(' );
            out.println( source == null ? "" : " source.getValue(),");
            List<Integer> skus = question.getSub_question_skus();
            if ( skus != null ) {
                boolean first = true;
                for ( int sku : skus )  {
                    SurveyQuestion q = gizmo.getQuestion( survey.getId(), sku );
                    if ( !first )
                        out.println( "," );
                    out.print( MessageFormat.format( "    response.get{0}( {3}{1} /* {2} */ )",
                                                     q.getFunction(),
                                                     sku,
                                                     q.getEnglishTitle(),
                                                     source == null ? "" : "source.getKey(), " ) );
                    first = false;
                }
            }
            out.println( " );" );
        }
        else {
            if ( "radio".equals( question.get_subtype() ) ) {
                List<SurveyOption> options = question.getOptions();
                out.print( "//              | " );
                for ( SurveyOption option : options ) {
                    out.print( option.getTitle().get( "English" ) + " | " );
                }
                out.println();
            }
            out.println( MessageFormat.format( "question{0}( {3}response.get{1}( {4}{2} ) );",
                                               questionId,
                                               question.getFunction(),
                                               questionId,
                                               source == null ? "" : "source.getValue(), ",
                                               source == null ? "" : "source.getKey(), " ) );
        }
    }
}
