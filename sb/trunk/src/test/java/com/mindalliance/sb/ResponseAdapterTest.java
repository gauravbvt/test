package com.mindalliance.sb;

import com.mindalliance.sb.model.CoreCapability;
import com.mindalliance.sb.model.Expertise;
import com.mindalliance.sb.model.OrgType;
import com.mindalliance.sb.model.Respondent;
import com.mindalliance.sb.model.Subcommittee;
import com.mindalliance.sb.surveygizmo.Survey;
import com.mindalliance.sb.surveygizmo.SurveyGizmo;
import com.mindalliance.sb.surveygizmo.SurveyOption;
import org.junit.Before;
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
@ContextConfiguration( locations= "/META-INF/spring/applicationContext-test.xml" )
public class ResponseAdapterTest {
    
    @Autowired
    SurveyGizmo gizmo;
    
    Survey survey;

    @Before
    public void init() {
        if ( survey == null )
            survey = gizmo.getSurvey( ResponseAdapter.SURVEY );
    }

    //@Test
    public void testConvert() throws Exception {
        List<SurveyResponse> responses = gizmo.getResponses( survey.getId(), new Date( 0L ) );
        //assertEquals( 3, responses.size() );

        int id = 2;
        ResponseAdapter responseAdapter = new ResponseAdapter( responses.get( id ) );
        Respondent respondent = responseAdapter.getRespondent();
        assertEquals( id+1, (int) respondent.getId() );
    }

    @Test
    /** Make sure we have defined all the org types from the survey. */
    public void testOrgTypes() {
        for ( SurveyOption option : survey.getQuestion( 258 ).getOptions() ) {
            String value = option.getValue();
            List<OrgType> list = OrgType.findOrgTypesByNameEquals( value ).getResultList();
            assertEquals( "Can't find OrgType '" + value + '\'', 1, list.size() );
        }
    }

    @Test
    /** Make sure we have defined all the core capabilities from the survey. */
    public void testCoreCaps() {
        int q = 138;
        for ( SurveyOption option : survey.getQuestion( q ).getOptions() ) {
            String value = option.getValue();
            List<CoreCapability> list = CoreCapability.findCoreCapabilitysByNameEquals( value ).getResultList();
            assertEquals( "Can't find CoreCapability '" + value + "' in question " + q, 1, list.size() );
        }
    }

    @Test
    /** Make sure we have defined all the expertises from the survey. */
    public void testExpertise() {
        for ( SurveyOption option : survey.getQuestion( 29 ).getOptions() ) {
            String value = option.getValue();
            List<Expertise> list = Expertise.findExpertisesByNameEquals( value ).getResultList();
            assertEquals( "Can't find Expertise '" + value + '\'', 1, list.size() );
        }
    }

    @Test
    /** Make sure we have defined all the expertises from the survey. */
    public void testSubcommittees() {
        for ( SurveyOption option : survey.getQuestion( 42 ).getOptions() ) {
            String value = option.getValue();
            List<Subcommittee> list = Subcommittee.findSubcommitteesByNameEquals( value ).getResultList();
            assertEquals( "Can't find Subcommittee '" + value + '\'', 1, list.size() );
        }
    }
}
