package com.mindalliance.sb.surveygizmo;

import com.mindalliance.sb.ResponseAdapter;
import com.mindalliance.sb.SurveyResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * Temporary test.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations= "/META-INF/spring/applicationContext-test.xml" )
public class SurveyGizmoImplTest {

    @Autowired
    private SurveyGizmo gizmo;

    @Before
    public void setUp() {
    }

    @Test
    public void testGetSurvey() {
        Survey survey = gizmo.getSurvey( ResponseAdapter.SURVEY );
        assertEquals( ResponseAdapter.SURVEY, survey.getId() );

        new SurveyGenerator( gizmo, survey ).output( System.out );
    }

    @Test
    public void testGetResponses() throws ParseException {
        List<SurveyResponse> responses = gizmo.getResponses( ResponseAdapter.SURVEY, new Date( 0L ) );
        assertEquals( 3, responses.size() );
    }
    
    @Test
    public void testGetQuestion() {
        SurveyQuestion question = gizmo.getQuestion( ResponseAdapter.SURVEY, 129 );
        assertNotNull( question );

    }
}
