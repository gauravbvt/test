package com.mindalliance.sb;

import com.mindalliance.sb.model.Respondent;
import com.mindalliance.sb.surveygizmo.SurveyGizmo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @TODO comment this
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations= "classpath:applicationContext-test.xml" )
public class ResponseAdapterTest {
    
    @Autowired
    SurveyGizmo gizmo;

    @Test
    public void testConvert() throws Exception {
        List<SurveyResponse> responses = gizmo.getResponses( ResponseAdapter.SURVEY, new Date( 0L ) );

        ResponseAdapter responseAdapter = new ResponseAdapter( responses.get( 0 ) );
        Respondent respondent = responseAdapter.getRespondent();
        assertEquals( 1, (int) respondent.getId() );
    }
}
