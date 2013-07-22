package com.mindalliance.sb;

import com.mindalliance.sb.model.ContactInfo;
import com.mindalliance.sb.model.Respondent;
import com.mindalliance.sb.surveygizmo.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.roo.addon.json.RooJson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Raw response to a survey.
 */
@RooJson
public class SurveyResponse {

    private static final Logger LOG = LoggerFactory.getLogger( SurveyResponse.class );
    
    private final int id;
    
    private final int surveyId;

    private final String status;

    private final boolean test;
    
    private final Date dateSubmitted;
    
    private final String comment;

    private final Map<Integer,Map<RawAnswer,String>> questions = new LinkedHashMap<Integer, Map<RawAnswer, String>>();

    private final Map<RawVariable,String> variables = new LinkedHashMap<RawVariable, String>();

    private final Map<Integer,String> comments = new LinkedHashMap<Integer, String>();

    public SurveyResponse( int surveyId, Map<String, Object> rawResponse ) throws ParseException {
        this.surveyId = surveyId;
        id = Integer.parseInt( rawResponse.get( "id" ).toString() );
        status = rawResponse.get( "status" ).toString();
        test = "1".equals( rawResponse.get( "is_test_data" ) );
        dateSubmitted = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", Locale.US )
                                .parse( rawResponse.get( "datesubmitted" ).toString() );
        comment = rawResponse.get( "sResponseComment" ).toString();

        // A little unorthodox, but just to make sure we handle everything...
        for ( String key : new String[]{ 
            "id","status","is_test_data","datesubmitted","contact_id","sResponseComment",
            "sCustom1","sCustom2","sCustom3","sCustom4","sCustom5","sCustom6","sCustom7","sCustom8","sCustom9","sCustom10", } )
                rawResponse.remove( key );

        for ( Entry<String, Object> entry : rawResponse.entrySet() ) {
            String key = entry.getKey();
            try {
                if ( key.startsWith( "[question" ) ) {
                    RawAnswer answer = new RawAnswer( key );
                    Map<RawAnswer, String> answers = questions.get( answer.question );
                    if ( answers == null ) {
                        answers = new LinkedHashMap<RawAnswer, String>();
                        questions.put( answer.question, answers );
                    }
                    answers.put( answer, entry.getValue().toString() );
                }
                else if ( key.startsWith( "[variable" ) )
                    variables.put( new RawVariable( key ), entry.getValue().toString() );
                else if ( key.startsWith( "[comment" ) )
                    comments.put( nextInt( key.substring( 0, key.indexOf( ')' ) ) ), entry.getValue().toString() );
                else if ( key.startsWith( "[url" ) )
                    // TODO implement urls?
                    LOG.warn( "Unprocessed URL: {} -> {}", 
                              nextObject( key.substring( 0, key.indexOf( ')' )+1 ) ),
                              entry.getValue().toString() );
                else
                    throw new ParseException( "Don't know what to do with " + key, 0 );
                
            } catch ( ParseException e ) {
                ParseException e2 = new ParseException( "Error parsing " + key, e.getErrorOffset() );
                e2.initCause( e );
                throw e2;
            }
        }
    }

    private static Object nextObject( String s ) throws ParseException {
        int i = s.indexOf( '"' );
        return i == -1 ? nextInt( s ) : s.substring( i + 1, s.length() - 2 );
    }

    private static int nextInt( String q ) throws ParseException {
        int si = q.indexOf( '(' ) + 1;
        try {
            return Integer.parseInt( q.substring( si, q.length() - 1 ) );
        } catch ( NumberFormatException e ) {
            ParseException e2 = new ParseException( "Error converting integer", si );
            e2.initCause( e );
            throw e2;
        }
    }

    public Respondent newRespondent() {
        Respondent result = new Respondent();
        result.setContactInfo( new ContactInfo() );
        result.setId( id );
        result.setSubmitted( dateSubmitted );
        result.setComments( comment );
        
        // Set an initial survey opinion of "Just right"
        result.setSurveyOpinion( 0 );
        
        return result;
    }

    public String getComment() {
        return comment;
    }

    public Map<Integer, String> getComments() {
        return Collections.unmodifiableMap( comments );
    }

    public Date getDateSubmitted() {
        return (Date) dateSubmitted.clone();
    }

    public int getId() {
        return id;
    }

    public Map<Integer, Map<RawAnswer, String>> getQuestions() {
        return Collections.unmodifiableMap( questions );
    }

    public String getStatus() {
        return status;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public boolean isTest() {
        return test;
    }

    public Map<RawVariable, String> getVariables() {
        return Collections.unmodifiableMap( variables );
    }

    @Override
    public String toString() {
        return "SurveyResponse #" + id + " from " + surveyId;
    }

    public String getRadio( int question ) {
        // TODO deal with "other:"
        return getString( question );
    }

    public String getMenu( int question ) {
        return getString( question );
    }
    
    public List<String> getMultibox( Object pageKey, int optionKey, int question ) {
        return getCheckboxes( pageKey, optionKey, question );
    }
    
    public String getEssay( int question ) {
        return getString( question );
    }

    public String getEssay( Object pageKey, int optionKey, int question ) {
        return getString( pageKey, optionKey, question );
    }

    public List<UploadedFile> getFiles( int question ) {
        List<UploadedFile> result = new ArrayList<UploadedFile>();
        Map<RawAnswer, String> map = questions.get( question );
        if ( map != null )
            for ( String fileSpec : map.values() )
                if ( fileSpec != null && !fileSpec.isEmpty() )
                    result.add( new UploadedFile( fileSpec, surveyId ) );

        return result;
    }

    public Set<Integer> getKeys( int... questions ) {
        HashSet<Integer> set = new HashSet<Integer>();

        for ( int question : questions )
            for ( Entry<RawAnswer, String> rawAnswer : this.questions.get( question ).entrySet() ) {
                Integer questionPipe = rawAnswer.getKey().questionPipe;
                if ( questionPipe != null && !"".equals( rawAnswer.getValue() ) )
                    set.add( questionPipe );
            }
        
        return set;
    }

    public Set<Integer> getSourcedKeys( Object key, int... questions ) {
        HashSet<Integer> set = new HashSet<Integer>();

        for ( int question : questions )
            for ( Entry<RawAnswer, String> rawAnswer : this.questions.get( question ).entrySet() ) {
                Integer questionPipe = rawAnswer.getKey().questionPipe;
                if ( questionPipe != null && key.equals( rawAnswer.getKey().getPagePipe() ) )
                    set.add( questionPipe );
            }

        return set;
    }

    public List<String> getCheckboxes( int question ) {
        List<String> values = new ArrayList<String>();

        Set<Entry<RawAnswer, String>> entries = questions.get( question ).entrySet();
        for ( Entry<RawAnswer, String> answer : entries ) {
            if ( answer.getValue().equals( "Other" ) ) {
                String x = answer.getKey().getOption().toString() + "-other";
                for ( Entry<RawAnswer, String> entry : entries ) {
                    if ( x.equals( entry.getKey().getOption().toString() ) ) {
                        values.add( entry.getValue() );
                        break;
                    }
                }
            }
            else if ( answer.getValue() != null && !answer.getValue().isEmpty() )
                values.add( answer.getValue() );
        }

        return Collections.unmodifiableList( values );
    }

    public List<String> getCheckboxes( Object pageKey, int question ) {
        List<String> values = new ArrayList<String>();

        Map<RawAnswer, String> stringMap = questions.get( question );
        if ( stringMap != null )
            for ( Entry<RawAnswer, String> answer : stringMap.entrySet() )
                if ( !answer.getValue().equals( "Other" ) && pageKey.equals( answer.getKey().getPagePipe() )
                    && answer.getValue() != null && !answer.getValue().isEmpty() )
                    values.add( answer.getValue() );

        return Collections.unmodifiableList( values );
    }

    public List<String> getCheckboxes( int optionKey, int question ) {
        List<String> values = new ArrayList<String>();

        for ( Entry<RawAnswer, String> answer : questions.get( question ).entrySet() )
            if ( !answer.getValue().equals( "Other" ) 
                    && answer.getKey().isQuestionPipe()
                    && answer.getKey().getQuestionPipe() == optionKey )
                        values.add( answer.getValue() );


        return Collections.unmodifiableList( values );
    }

    public List<String> getCheckboxes( Object pageKey, int optionKey, int question ) {
        List<String> values = new ArrayList<String>();

        for ( Entry<RawAnswer, String> answer : questions.get( question ).entrySet() )
            if ( !answer.getValue().equals( "Other" )
                && pageKey.equals( answer.getKey().getPagePipe() )
                && answer.getKey().isQuestionPipe()
                && answer.getKey().getQuestionPipe() == optionKey )
                values.add( answer.getValue() );


        return Collections.unmodifiableList( values );
    }

    public Map<Object, String> getPipeKeys( int question ) {
        Map<Object, String> result = new LinkedHashMap<Object, String>();
        for ( Entry<RawAnswer, String> answer : questions.get( question ).entrySet() ) {
            String value = answer.getValue();
            if ( !"Other".equals( value ) && value != null && !value.isEmpty()
                && answer.getKey().getOption() != null )
                result.put( answer.getKey().getOption(), value );
        }

        return result;
    }

    public String getRadio( Object pageKey, int question ) {
        for ( Entry<RawAnswer, String> entry : questions.get( question ).entrySet() )
            if ( pageKey.equals( entry.getKey().getPagePipe() ) 
                && entry.getValue() != null && !entry.getValue().isEmpty() )
                return entry.getValue();

        return null;
    }

    public String getString( int question ) {
        Map<RawAnswer,String> answers = questions.get( question );
        if ( answers == null || answers.isEmpty() )        // unanswered question
            return null;
        if ( answers.size() > 1 )
            LOG.warn( "Too many answers for question #{}", question );

        for ( String s : answers.values() )
            if ( s != null && !s.isEmpty() )
                return s;

        return null;
    }

    public String getString( int optionKey, int question ) {
        for ( Entry<RawAnswer, String> entry : questions.get( question ).entrySet() )
            if ( entry.getKey().isQuestionPipe() && entry.getKey().getQuestionPipe() == optionKey
                && entry.getValue() != null && !entry.getValue().isEmpty() )
                return entry.getValue();

        return null;
    }

    public String getString( Object pageKey, int optionKey, int question ) {
        for ( Entry<RawAnswer, String> entry : questions.get( question ).entrySet() ) {
            RawAnswer key = entry.getKey();
            if ( pageKey.equals( key.getPagePipe() ) && key.isQuestionPipe() && key.getQuestionPipe() == optionKey
                 && entry.getValue() != null && !entry.getValue().isEmpty() )
                return entry.getValue();
        }

        return null;
    }

    public static class RawAnswer {

        private int question;

        private Object pagePipe;

        private Object option;

        private Integer questionPipe;
        
        public RawAnswer() {
        }

        public RawAnswer( String key ) throws ParseException {
            this();
            StringTokenizer t = new StringTokenizer( key.substring( 1, key.length() - 1 ), "," );
            question = nextInt( t.nextToken() );

            while ( t.hasMoreTokens() ) {
                String s = t.nextToken();
                if ( s.startsWith( " option" ) )
                    option = nextObject( s );
                else if ( s.startsWith( " page_pipe" ) )
                    pagePipe = nextObject( s );
                else if ( s.startsWith( " question_pipe" ) )
                    questionPipe = nextInt( s );
                else
                    throw new ParseException( "Don't know what to do with: " + s, key.indexOf( s ) );
            }
        }

        public Object getOption() {
            return option;
        }

        public void setOption( Object option ) {
            this.option = option;
        }

        public Object getPagePipe() {
            return pagePipe;
        }

        public void setPagePipe( Object pagePipe ) {
            this.pagePipe = pagePipe;
        }

        public Integer getQuestionPipe() {
            return questionPipe;
        }

        public void setQuestionPipe( Integer questionPipe ) {
            this.questionPipe = questionPipe;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o )
                return true;
            if ( o == null || getClass() != o.getClass() )
                return false;

            RawAnswer rawAnswer = (RawAnswer) o;

            if ( question != rawAnswer.question )
                return false;
            
            if ( option == null ) {
                if ( rawAnswer.option != null )
                    return false;
            } else if ( !option.equals( rawAnswer.option ) )
                return false;

            if ( pagePipe == null ) {
                if ( rawAnswer.pagePipe != null )
                    return false;
            } else if ( !pagePipe.equals( rawAnswer.pagePipe ) )
                return false;

            return questionPipe == null ?
                   rawAnswer.questionPipe == null :
                   questionPipe.equals( rawAnswer.questionPipe );
        }

        @Override
        public int hashCode() {
            int result = question;
            result = 31 * result + ( pagePipe != null ? pagePipe.hashCode() : 0 );
            result = 31 * result + ( option != null ? option.hashCode() : 0 );
            result = 31 * result + ( questionPipe != null ? questionPipe.hashCode() : 0 );
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append( "RawAnswer #" ).append( question );
            sb.append( '{' );
            if ( option != null )
                sb.append( " option=" ).append( option );
            if ( pagePipe != null )
                sb.append( " pagePipe=" ).append( pagePipe );
            if ( questionPipe != null ) {
                sb.append( " questionPipe=" ).append( questionPipe );   
            }
            sb.append( " }" );
            return sb.toString();
        }

        private boolean isQuestionPipe() {
            return questionPipe != null;
        }
    }

    @RooJson
    public static class RawVariable {

        private Object variable;

        private Object page_pipe;
        
        private Object question_pipe;
        
        public RawVariable() {
        }

        public RawVariable( String key ) throws ParseException {
            this();
            StringTokenizer t = new StringTokenizer( key.substring( 1, key.length() - 1 ), "," );
            variable = nextObject( t.nextToken() );

            while ( t.hasMoreTokens() ) {
                String s = t.nextToken();
                if ( s.startsWith( " page_pipe" ) )
                    page_pipe = nextObject( s );
                else if ( s.startsWith( " question_pipe" ) )
                    question_pipe = nextObject( s );
                else
                    throw new ParseException( "Don't know what to do with: " + s, key.indexOf( s ) );
            }
        }

        public Object getPage_pipe() {
            return page_pipe;
        }

        public void setPage_pipe( Object page_pipe ) {
            this.page_pipe = page_pipe;
        }

        public Object getVariable() {
            return variable;
        }

        public void setVariable( Object variable ) {
            this.variable = variable;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o )
                return true;
            if ( o == null || getClass() != o.getClass() )
                return false;

            RawVariable that = (RawVariable) o;
            return page_pipe == null ? that.page_pipe == null && variable.equals( that.variable ) 
                                     : page_pipe.equals( that.page_pipe ) && variable.equals( that.variable );
        }

        @Override
        public int hashCode() {
            int result = variable.hashCode();
            result = 31 * result + ( page_pipe != null ? page_pipe.hashCode() : 0 );
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append( variable );
            if ( page_pipe != null )
                sb.append( '{' ).append( page_pipe ).append( '}' );
            return sb.toString();
        }
    }
}
