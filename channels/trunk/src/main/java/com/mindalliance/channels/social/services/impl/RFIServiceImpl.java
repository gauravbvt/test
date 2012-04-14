package com.mindalliance.channels.social.services.impl;

import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.RFIService;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/16/12
 * Time: 2:21 PM
 */
@Repository
public class RFIServiceImpl extends GenericSqlServiceImpl<RFI, Long> implements RFIService {

    public RFIServiceImpl() {
    }

    @Override
    @Transactional( readOnly = true)
    public boolean isCompleted( RFI rfi ) {
        return false;  //Todo
    }

    @Override
    @Transactional( readOnly = true)
    public boolean isIncomplete( RFI rfi ) {
        return false;  //Todo
    }

    @Override
    @Transactional( readOnly = true)
    @SuppressWarnings( "unchecked" )
    public List<RFI> select( RFISurvey rfiSurvey ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "rfiSurvey", rfiSurvey ) );
        criteria.addOrder( Order.desc( "created" ) );
        return (List<RFI>) criteria.list();
    }

    @Override
    @Transactional( readOnly = true)
    public int getRFICount( Questionnaire questionnaire ) {
        return 0;  //Todo
    }

    @Override
    public void makeOrUpdateRFI(
            Plan plan,
            String username,
            RFISurvey rfiSurvey,
            ChannelsUserInfo userInfo,
            Organization organization,
            String title,
            Role role,
            Date deadlineDate ) {
        // todo
    }

    @Override
    public void nag(
            Plan plan,
            String username,
            RFISurvey rfiSurvey,
            ChannelsUserInfo userInfo,
            Organization organization,
            String title,
            Role role ) {
        // todo
    }
}
