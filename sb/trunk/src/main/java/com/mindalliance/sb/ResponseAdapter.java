package com.mindalliance.sb;

import com.mindalliance.sb.model.ContactInfo;
import com.mindalliance.sb.model.CoreCapability;
import com.mindalliance.sb.model.Expertise;
import com.mindalliance.sb.model.Organization;
import com.mindalliance.sb.model.Respondent;
import com.mindalliance.sb.model.RespondentSubcommittee;
import com.mindalliance.sb.model.Subcommittee;
import com.mindalliance.sb.model.SubcommitteeCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.persistence.NoResultException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Converter from a specific survey response to a respondent with all the trimings.
 */
public class ResponseAdapter {

    public static final int SURVEY = 1272741;

    private final SurveyResponse response;
    
    private Respondent respondent = new Respondent();
    
    private Set<Expertise> expertises = new HashSet<Expertise>();

    private Organization organization = new Organization();
    
    private static final Logger LOG = LoggerFactory.getLogger( ResponseAdapter.class );

    public ResponseAdapter( SurveyResponse response ) {
        int surveyId = response.getSurveyId();
        int id = response.getId();

        if ( surveyId != SURVEY )
            LOG.warn( MessageFormat.format( "This converter was built for survey #{0}, not {1}", SURVEY, surveyId ) );

        if ( Respondent.findRespondent( id ) != null )
            throw new IllegalArgumentException( MessageFormat.format( "Response #{0} already converted", id ) );

        this.response = response;
        respondent.setContactInfo( new ContactInfo() );
        respondent.getContactInfo().setOrganization( organization );
        convert();
    }

    /**
     *  Ugly and long just to make sure we know what to do with all answers
     *  and detect new questions if survey changes.
     *  @see com.mindalliance.sb.surveygizmo.SurveyGenerator#output
     */
    private void convert() {
        respondent.setId( response.getId() );
        respondent.setSubmitted( response.getDateSubmitted() );
        respondent.setComments( response.getComment() );
        
        // TODO question comments
        // TODO multiple files
        //------------ Page 1: Title

        //------------ Page 5: Respondent identity
        // Name
        question25(
            response.getMenu( 142 /* Select one */ ),
            response.getString( 26 /* First */ ),
            response.getString( 27 /* Last */ ) );

        // What organization do you work for?
        question231( response.getString( 231 ) );

        // How would you categorize you organizations?
        //              | Federal Government | State Government | Local Government | Tribal Government | Private Sector/Non-Governmental | Community | 
        question258( response.getRadio( 258 ) );

        // Please provide your contact information
        question37(
            response.getString( 38 /* Email */ ),
            response.getString( 40 /* Mobile */ ),
            response.getString( 39 /* Landline */ ) );

        // Please provide the following information
        question69(
            response.getString( 70 /* Your title */ ),
            response.getString( 71 /* Your department or unit */ ),
            response.getString( 72 /* Your supervisor's name */ ),
            response.getString( 73 /* Supervisor's title */ ),
            response.getString( 74 /* Supervisor's email */ ) );

        // Indicate your personal domains of expertise
        question29( response.getCheckboxes( 29 ) );


        //------------ Page 4: Respondent subcommittees
        // Are you member of one or more planning subcommittees?
        //              | Yes (you will be asked to provide further details) | No | 
        question67( response.getRadio( 67 ) );

        // What subcommittees are you member of?
        Map<Object,Object> pipeObjects42 = new LinkedHashMap<Object, Object>();
        for ( Entry<Object, String> entry : response.getPipeKeys( 42 ).entrySet() )
            pipeObjects42.put( entry.getKey(), question42( entry.getValue() ) );


        //------------ Page 3: Subcommittee
        for( Entry<Object,Object> source : pipeObjects42.entrySet() ) {
            // Are you in charge of this subcommitee?
            //              | Yes (you will be asked to provide further details) | No | TBD | 
            question46( source.getValue(), response.getRadio( source.getKey(), 46 ) );

            // What FEMA Core Capabilitities are within the scope of the <strong><span style="text-decoration:underline;">[page("piped title")]</span></strong> subcommittee?
            question138( source.getValue(), response.getCheckboxes( source.getKey(), 138 ) );

            // List the organizations in the <span style="text-decoration:underline;"><strong>[page("piped title")]</strong></span> subcommittee
            for ( int key : response.getKeys( 45 ) )
                question44( source.getValue(),
                            response.getString( source.getKey(), key, 45 /* Name */ ) );

            // Are there organizations that are <strong>not</strong> in the <span style="text-decoration:underline;"><strong>[page("piped title")]</strong></span> subcommittee but should be?
            //              | Yes (you will be asked to provide further details) | No | 
            question134( source.getValue(), response.getRadio( source.getKey(), 134 ) );

            // List the organizations that should be added to the <span style="text-decoration:underline;"><strong>[page("piped title")]</strong></span> subcommittee.
            for ( int key : response.getKeys( 136 ) )
                question135( source.getValue(),
                             response.getString( source.getKey(), key, 136 /* Name */ ) );

        }

        //------------ Page 10: Your organization
        // Please select your organization type
        question141( response.getMenu( 141 ) );

        // What are the FEMA Core Capabilities of your organization?
        question137( response.getCheckboxes( 137 ) );


        //------------ Page 16: Super Bowl Plan
        // Does your organization have a security plan specifically designed for the Super Bowl?
        //              | Yes | No | 
        question202( response.getRadio( 202 ) );

        // Have you shared that plan with the Super Bowl planning committee?
        //              | Yes | No | 
        question205( response.getRadio( 205 ) );

        // If possible, please make available the relevant planning documents (maximum 10 files, no more than 25M each)
        question233( response.getFile( 233 ) );

        // Who are the planners in your agency?
        for ( int key : response.getKeys( 209, 210, 211 ) )
            question208(
                response.getString( key, 209 /* Name */ ),
                response.getString( key, 210 /* Title */ ),
                response.getString( key, 211 /* Email */ ) );

        // Who is the primary point of contact for determining the information and intelligence needs of your agency?
        question212(
            response.getString( 213 /* Name */ ),
            response.getString( 214 /* Title */ ),
            response.getString( 215 /* Email */ ) );

        // Who will be the ultimate decision maker about releasing information from your organization to other organizations engaged in Super Bowl Security?
        question216(
            response.getString( 217 /* Name */ ),
            response.getString( 218 /* Title */ ),
            response.getString( 219 /* Email */ ) );


        //------------ Page 17: Situation reporting
        // Has your organization defined the scope and format of a hourly/daily situation report or common operating picture report during the Super Bowl?
        //              | Yes | No | 
        question220( response.getRadio( 220 ) );

        // In the context of producing the situation report...
        question221(
            response.getEssay( 222 /* Briefly describe the process by which information is captured, analyzed, reviewed, approved, and included */ ),
            response.getRadio( 226 /* At what time(s) is the situation report distributed? */ ),
            response.getEssay( 224 /* To what audience? */ ) );


        //------------ Page 18: EOC
        // Does your organization have an Emergency Operations Center of some kind?
        //              | Yes | No | 
        question225( response.getRadio( 225 ) );

        // What is the name of your Emergency Operations Center?
        question235( response.getString( 235 ) );

        // What incident management software does your EOC use?
        for ( int key : response.getKeys( 237 ) )
            question236(
                response.getString( key, 237 /* Name */ ) );

        // Will your EOC be activated during the Super Bowl?
        //              | Yes | No | 
        question227( response.getRadio( 227 ) );


        //------------ Page 15: Incidents
        // List the types of incidents that your organization needs to plan for.
        Map<Object,Object> pipeObjects139 = new LinkedHashMap<Object, Object>();
        for ( Entry<Object, String> entry : response.getPipeKeys( 139 ).entrySet() )
            pipeObjects139.put( entry.getKey(), question139( entry.getValue() ) );


        //------------ Page 14: Incident planning
        for( Entry<Object,Object> source : pipeObjects139.entrySet() ) {
            // Does your organization have a plan for addressing an incident of type <span style="text-decoration:underline;"><strong>[page("piped title")]</strong></span>?
            //              | Yes | No | 
            question200( source.getValue(), response.getRadio( source.getKey(), 200 ) );

            // Has your organization conducted or participated in training and exercises relevant to type of incident <span style="text-decoration:underline;"><strong>[page("piped title")]</strong></span> in the last 24 months?
            //              | Yes | No | 
            question201( source.getValue(), response.getRadio( source.getKey(), 201 ) );

        }

        //------------ Page 8: Incident management
        for( Entry<Object,Object> source : pipeObjects139.entrySet() ) {
            // List the mission areas where your organization is active in regards to an incident of type <span style="text-decoration:underline;"><strong>[page("piped title")]</strong></span>
            question84( source.getValue(), response.getCheckboxes( source.getKey(), 84 ) );

            // Select the FEMA Core Capabilities that would be required of your organization to address an incident of type <span style="text-decoration:underline;"><strong>[page("piped title")]</strong></span>.
            question143( source.getValue(), response.getCheckboxes( source.getKey(), 143 ) );

            // What <strong>Planning</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question144( source.getValue(), response.getCheckboxes( source.getKey(), 144 ) );

            // What <strong>Public Information and Warning</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question148( source.getValue(), response.getCheckboxes( source.getKey(), 148 ) );

            // What <strong>Operational Coordination</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question149( source.getValue(), response.getCheckboxes( source.getKey(), 149 ) );

            // What <strong>Forensics and Attribution</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question150( source.getValue(), response.getCheckboxes( source.getKey(), 150 ) );

            // What <strong>Intelligence and Information Sharing</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question151( source.getValue(), response.getCheckboxes( source.getKey(), 151 ) );

            // What <strong>Interdiction and Disruption</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question152( source.getValue(), response.getCheckboxes( source.getKey(), 152 ) );

            // What <strong>Screening, Search, and Detection</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question153( source.getValue(), response.getCheckboxes( source.getKey(), 153 ) );

            // What <strong>Access Control and Identity Verification</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question154( source.getValue(), response.getCheckboxes( source.getKey(), 154 ) );

            // What <strong>Cybersecurity</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question155( source.getValue(), response.getCheckboxes( source.getKey(), 155 ) );

            // What <strong>Physical Protective Measures</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question156( source.getValue(), response.getCheckboxes( source.getKey(), 156 ) );

            // What <strong>Risk Management for Protection Programs and Activities</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question157( source.getValue(), response.getCheckboxes( source.getKey(), 157 ) );

            // What <strong>Supply Chain Integrity and Security</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question158( source.getValue(), response.getCheckboxes( source.getKey(), 158 ) );

            // What <strong>Community Resilience</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question159( source.getValue(), response.getCheckboxes( source.getKey(), 159 ) );

            // What <strong>Long-term Vulnerability Reduction</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question160( source.getValue(), response.getCheckboxes( source.getKey(), 160 ) );

            // What <strong>Risk and Disaster Resilience Assessment</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question161( source.getValue(), response.getCheckboxes( source.getKey(), 161 ) );

            // What <strong>Threats and Hazard Identification</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question162( source.getValue(), response.getCheckboxes( source.getKey(), 162 ) );

            // What <strong>Critical Transportation</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question163( source.getValue(), response.getCheckboxes( source.getKey(), 163 ) );

            // What <strong>Environmental Response/Health and Safety</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question164( source.getValue(), response.getCheckboxes( source.getKey(), 164 ) );

            // What <strong>Fatality Management Services</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question165( source.getValue(), response.getCheckboxes( source.getKey(), 165 ) );

            // What <strong>Infrastructure Systems</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question166( source.getValue(), response.getCheckboxes( source.getKey(), 166 ) );

            // What <strong>Mass Care Services</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question167( source.getValue(), response.getCheckboxes( source.getKey(), 167 ) );

            // What <strong>Mass Search and Rescue Operations</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question168( source.getValue(), response.getCheckboxes( source.getKey(), 168 ) );

            // What <strong>On-scene Security and Protection</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question169( source.getValue(), response.getCheckboxes( source.getKey(), 169 ) );

            // What <strong>Operational Communications</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question170( source.getValue(), response.getCheckboxes( source.getKey(), 170 ) );

            // What <strong>Public and Private Services and Resources</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question171( source.getValue(), response.getCheckboxes( source.getKey(), 171 ) );

            // What <strong>Public Health and Medical Services</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question172( source.getValue(), response.getCheckboxes( source.getKey(), 172 ) );

            // What <strong>Situational Assessment</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question173( source.getValue(), response.getCheckboxes( source.getKey(), 173 ) );

            // What <strong>Economic Recovery</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question174( source.getValue(), response.getCheckboxes( source.getKey(), 174 ) );

            // What <strong>Health and Social Services</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question175( source.getValue(), response.getCheckboxes( source.getKey(), 175 ) );

            // What <strong>Housing</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question176( source.getValue(), response.getCheckboxes( source.getKey(), 176 ) );

            // What <strong>Natural and Cultural Resources</strong> FEMA Critical Tasks would your organization be responsible for when addressing an incident of type [page("piped title")]?
            question177( source.getValue(), response.getCheckboxes( source.getKey(), 177 ) );

        }

        //------------ Page 9: Info sharing for incident
        for( Entry<Object,Object> source : pipeObjects139.entrySet() ) {
            // Does your organization need to <strong><span style="text-decoration:underline;">receive information</span></strong> from other organizations to address incidents of type <span style="text-decoration:underline;"><strong>[page("piped title")]</strong></span>?
            //              | Yes (you will be asked to provide further details) | No | 
            question109( source.getValue(), response.getRadio( source.getKey(), 109 ) );

            // What information would your organization need to <span style="text-decoration:underline;"><strong>receive</strong></span> in order to address incidents of type<span style="text-decoration:underline;"><strong> [page("piped title")]</strong></span>?
            for ( int key : response.getKeys( 186, 187, 183, 240, 184, 188, 189, 190 ) )
                question179( source.getValue(),
                             response.getString( source.getKey(), key, 186 /* From your point of contact */ ),
                             response.getString( source.getKey(), key, 187 /* with title */ ),
                             response.getString( source.getKey(), key, 183 /* at organization */ ),
                             response.getMultibox( source.getKey(), key, 240 /* you need to receive this information/these reports */ ),
                             response.getCheckboxes( source.getKey(), key, 184 /* via */ ),
                             response.getMultibox( source.getKey(), key, 188 /* Your source's contact information is */ ),
                             response.getCheckboxes( source.getKey(), key, 189 /* Are there issues? If so, please select them */ ),
                             response.getEssay( source.getKey(), key, 190 /* Additional comments */ ) );

            // Does your organization<span style="text-decoration:underline;"><strong> share information</strong></span> with other organizations when addressing incidents of type <span style="text-decoration:underline;"><strong>[page("piped title")]</strong></span>?
            //              | Yes (you will be asked to provide further details) | No | 
            question178( source.getValue(), response.getRadio( source.getKey(), 178 ) );

            // What information would your organization <span style="text-decoration:underline;"><strong>share</strong></span> when addressing incidents of type <span style="text-decoration:underline;"><strong> [page("piped title")]</strong></span>?
            for ( int key : response.getKeys( 242, 243, 244, 245, 246, 247, 248, 249 ) )
                question241( source.getValue(),
                             response.getString( source.getKey(), key, 242 /* Your point of contact */ ),
                             response.getString( source.getKey(), key, 243 /* with title */ ),
                             response.getString( source.getKey(), key, 244 /* at organization */ ),
                             response.getMultibox( source.getKey(), key, 245 /* would receive, from your organization, these information/these reports */ ),
                             response.getCheckboxes( source.getKey(), key, 246 /* via */ ),
                             response.getMultibox( source.getKey(), key, 247 /* The recipient's contact information is */ ),
                             response.getCheckboxes( source.getKey(), key, 248 /* Are there issues? If so, please select them */ ),
                             response.getEssay( source.getKey(), key, 249 /* Additional comments */ ) );

        }

        //------------ Page 11: Info systems for incident
        for( Entry<Object,Object> source : pipeObjects139.entrySet() ) {
            // Does your organization need to <span style="text-decoration:underline;"><strong>access</strong></span> information systems to address an incident of type <strong>[page("piped title")]</strong>?
            //              | Yes (you will be asked to provide further details) | No | 
            question110( source.getValue(), response.getRadio( source.getKey(), 110 ) );

            // What information would your organization need to <strong>access</strong> from an information system in order to address an incident of type <strong>[page("piped title")]</strong>?
            for ( int key : response.getKeys( 113, 116, 117, 250, 120, 121 ) )
                question111( source.getValue(),
                             response.getString( source.getKey(), key, 113 /* From system */ ),
                             response.getString( source.getKey(), key, 116 /* also known as (acronym) */ ),
                             response.getString( source.getKey(), key, 117 /* and administered by organization */ ),
                             response.getMultibox( source.getKey(), key, 250 /* your organization needs to access this information/these reports */ ),
                             response.getCheckboxes( source.getKey(), key, 120 /* Are there issues? If so, please select them. */ ),
                             response.getEssay( source.getKey(), key, 121 /* Additional comments */ ) );

            // Does your organization need to publish to information systems to address an incident of type <strong>[page("piped title")]</strong>?
            //              | Yes (you will be asked to provide further details) | No | 
            question191( source.getValue(), response.getRadio( source.getKey(), 191 ) );

            // What information does your organization <span style="text-decoration:underline;"><strong>upload or store</strong></span> in the context of incidents of type <span style="text-decoration:underline;"><strong>[page("piped title")]</strong></span>?
            for ( int key : response.getKeys( 252, 253, 254, 255, 256, 257 ) )
                question251( source.getValue(),
                             response.getString( source.getKey(), key, 252 /* Your organization uploads to/stores in system */ ),
                             response.getString( source.getKey(), key, 253 /* also known as (acronym) */ ),
                             response.getString( source.getKey(), key, 254 /* and administered by organization */ ),
                             response.getMultibox( source.getKey(), key, 255 /*  this information/these reports */ ),
                             response.getCheckboxes( source.getKey(), key, 256 /* Are there issues? If so, please select them. */ ),
                             response.getEssay( source.getKey(), key, 257 /* Additional comments */ ) );

        }

        //------------ Page 13: Reference documentation
        // Does your organization have other documents that the Super Bowl planning committee should be aware of?
        //              | Yes (you will be asked to provide further details) | No | 
        question133( response.getRadio( 133 ) );

        // Identify an important reference document
        for ( int key : response.getKeys( 128, 129, 130 ) )
            question127(
                response.getString( key, 128 /* Name */ ),
                response.getCheckboxes( key, 129 /* What kind of document is it? */ ),
                response.getString( key, 130 /* If available online, please provide a URL. */ ) );

        // If documents are not available online, please upload them here.
        question131( response.getFile( 131 ) );


        //------------ Page 12: About this survey
        // Did you find this survey
        //              | Too short | Too long | Just right | 
        question125( response.getRadio( 125 ) );

        // Did you have find it difficult or problematic to complete this survey?
        //              | Yes (you will be asked to provide further details) | No | 
        question123( response.getRadio( 123 ) );

        // Please describe the problems or shortcomings you encountered.
        question124( response.getEssay( 124 ) );


        //------------ Page 2: Thank You!
    }

    private void question179( Object source, String string, String string1, String string2, List<String> multibox,
                              List<String> checkboxes, List<String> multibox1, List<String> checkboxes1, String essay ) {
        // TODO implement this

    }

    private void question251( Object source, String string, String string1, String string2, List<String> multibox,
                              List<String> checkboxes, String essay ) {
        // TODO implement this

    }

    private void question111( Object source, String string, String string1, String string2, List<String> multibox,
                              List<String> checkboxes, String essay ) {
        // TODO implement this

    }

    private void question241( Object source, String string, String string1, String string2, List<String> multibox,
                              List<String> checkboxes, List<String> multibox1, List<String> checkboxes1, String essay ) {
        // TODO implement this

    }

    private void question191( Object source, String radio ) {
        // TODO implement this

    }

    private void question110( Object source, String radio ) {
        // TODO implement this

    }

    private void question178( Object source, String radio ) {
        // TODO implement this

    }

    private void question109( Object source, String radio ) {
        // TODO implement this

    }

    private void question177( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question176( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question175( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question174( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question173( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question172( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question171( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question170( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question169( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question168( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question167( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question166( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question165( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question164( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question163( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question162( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question161( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question160( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question159( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question158( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question157( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question156( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question155( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question154( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question153( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question152( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question151( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question150( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question149( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question148( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question144( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question143( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question84( Object source, List<String> checkboxes ) {
        // TODO implement this

    }

    private void question201( Object source, String radio ) {
        // TODO implement this

    }

    private void question200( Object source, String radio ) {
        // TODO implement this

    }

    private void question135( Object object, String orgName ) {
        RespondentSubcommittee sc = (RespondentSubcommittee) object;
        // TODO

    }

    private void question134( Object source, String radio ) {
        // TODO implement this

    }

    private void question44( Object source, String inCharge ) {
        RespondentSubcommittee rs = (RespondentSubcommittee) source;
        if ( inCharge != null && !"TBD".equals( inCharge ) )
            rs.setInCharge( !"No".equals( inCharge ) );
    }

    private void question138( Object source, List<String> checkboxes ) {
        RespondentSubcommittee rs = (RespondentSubcommittee) source;
        for ( String capName : checkboxes ) {
            SubcommitteeCapability sc = new SubcommitteeCapability();
            sc.setRespondentSubcommittee( rs );
            sc.setCoreCapability( CoreCapability.findCoreCapabilitysByNameEquals( capName ).getSingleResult() );
        }
    }

    private void question46( Object source, String radio ) {
        // TODO implement this
    }

    private void question258( String orgType ) {
        // TODO implement this

    }

    private void question124( String essay ) {
        // TODO implement this

    }

    private void question131( byte[] file ) {
        // TODO implement this

    }

    private void question233( byte[] file ) {
        // TODO implement this
    }

    private Object question139( String incidentName ) {
        // TODO implement this
        return null;
    }

    private void question123( String radio ) {
        // TODO implement this

    }

    private void question125( String radio ) {
        // TODO implement this

    }

    private void question133( String radio ) {
        // TODO implement this

    }

    private void question227( String radio ) {
        // TODO implement this

    }

    private void question225( String radio ) {
        // TODO implement this

    }

    private void question220( String radio ) {
        // TODO implement this

    }

    private void question205( String radio ) {
        // TODO implement this

    }

    private void question202( String radio ) {
        // TODO implement this

    }

    private void question141( String menu ) {
        // TODO implement this

    }

    private void question67( String radio ) {
        // TODO implement this

    }

    private void question235( String string ) {
        // TODO implement this

    }

    private void question221( String string, String string1, String string2 ) {
        // TODO implement this

    }

    private void question216( String string, String string1, String string2 ) {
        // TODO implement this

    }

    private void question212( String string, String string1, String string2 ) {
        // TODO implement this

    }

    private void question137( List<String> checkboxes ) {
        // TODO implement this

    }

    private Object question42( String subcommitteeName ) {
        RespondentSubcommittee subcommittee = new RespondentSubcommittee();
        subcommittee.setSubcommittee( Subcommittee.findSubcommitteesByNameEquals( subcommitteeName ).getSingleResult() );
        subcommittee.setRespondent( respondent );

        return subcommittee;
    }

    private void question208( String string, String string1, String string2 ) {
        // TODO implement this

    }

    private void question29( List<String> checkboxes ) {
        for ( String expertiseName : checkboxes )
            try {
                expertises.add( Expertise.findExpertisesByNameEquals( expertiseName ).getSingleResult() );
            } catch ( NoResultException e ) {
                LOG.warn( "Can't resolve expertise " + expertiseName, e );
            } catch ( EmptyResultDataAccessException e ) {
                LOG.warn( "Can't resolve expertise " + expertiseName, e );
            }
}

    private void question69( String title, String department, String supName, String supTitle, String supEmail ) {
        ContactInfo contactInfo = respondent.getContactInfo();
        contactInfo.setTitle( title );
        contactInfo.setDepartment( department );
        
        if ( supName != null || supTitle != null || supEmail != null ) {
            ContactInfo supervisor = new ContactInfo();
            supervisor.setLastName( supName );
            supervisor.setTitle( supTitle );
            supervisor.setEmail( supEmail );
            contactInfo.setSupervisor( supervisor );
        }
    }

    private void question37( String email, String mobile, String landLine ) {
        ContactInfo contactInfo = respondent.getContactInfo();
        contactInfo.setEmail( email );
        contactInfo.setLandline( landLine );
        contactInfo.setMobile( mobile );

    }

    private void question236( String string ) {
        // TODO implement this

    }

    private void question127( String string, List<String> string1, String string2 ) {
        // TODO implement this

    }

    private void question231( String string ) {
        organization.setName( string );
    }

    private void question25( String prefix, String firstName, String lastName ) {
        ContactInfo contactInfo = respondent.getContactInfo();
        contactInfo.setPrefix( prefix );
        contactInfo.setFirstName( firstName );
        contactInfo.setLastName( lastName );
    }

    public Organization getOrganization() {
        return organization;
    }

    public Respondent getRespondent() {
        return respondent;
    }

    public Set<Expertise> getExpertises() {
        return Collections.unmodifiableSet( expertises );
    }
}
