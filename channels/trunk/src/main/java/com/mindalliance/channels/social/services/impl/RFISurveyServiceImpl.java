package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.RFIService;
import com.mindalliance.channels.social.services.RFISurveyService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/12
 * Time: 11:18 AM
 */
@Repository
public class RFISurveyServiceImpl extends GenericSqlServiceImpl<RFISurvey, Long> implements RFISurveyService {

    public RFISurveyServiceImpl() {
    }

    @Override
    @SuppressWarnings( "unchecked" )
    @Transactional( readOnly = true)
   public List<RFISurvey> select( Plan plan, boolean onlyOpen, String aboutTypeName ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "planUri", plan.getUri() ) );
        criteria.add( Restrictions.eq( "planVersion", plan.getVersion() ) );
        if ( onlyOpen ) {
            criteria.add( Restrictions.eq( "closed", false ) );
        }
        if ( aboutTypeName != null && !aboutTypeName.isEmpty() ) {
            criteria.add( Restrictions.eq( "moTypeName", aboutTypeName ) );
        }
        criteria.addOrder( Order.desc( "created" ) );
        return (List<RFISurvey>) criteria.list();
   }

    @Override
    @Transactional( readOnly = true)
   public String findResponseMetrics( RFISurvey rfiSurvey, final RFIService rfiService ) {
        List<RFI> surveyedRFIs = rfiSurvey.getRfis();
        int[]counts = new int[3];
        counts[0] = CollectionUtils.countMatches(
                rfiSurvey.getRfis(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return rfiService.isCompleted( (RFI) object );
                    }
                }
        );
        counts[1] = CollectionUtils.countMatches(
                rfiSurvey.getRfis(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (RFI) object ).isDeclined();  //To change body of implemented methods use File | Settings | File Templates.
                    }
                }
        );
        counts[2] = surveyedRFIs.size() - (counts[0] + counts[1] );
        return new MessageFormat( "{0}c {1}d {2}i").format( counts );
    }

    @Override
    public List<String> findParticipants( RFISurvey rfiSurvey ) {
        return new ArrayList<String>(  ); //TODO
    }

    @Override
    public RFI findRFI( String username, RFISurvey rfiSurvey ) {
        return null;  //TODO
    }
}
