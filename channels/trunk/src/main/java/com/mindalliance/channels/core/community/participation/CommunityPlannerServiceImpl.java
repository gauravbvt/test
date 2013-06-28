package com.mindalliance.channels.core.community.participation;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.orm.service.impl.GenericSqlServiceImpl;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Community planner service implementation.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/19/13
 * Time: 10:33 AM
 */
public class CommunityPlannerServiceImpl
        extends GenericSqlServiceImpl<CommunityPlanner, Long>
        implements CommunityPlannerService {

    public CommunityPlannerServiceImpl() {
    }

    @Autowired
    private ChannelsUserDao userDao;

    @Override
    @Transactional(readOnly = true)
    public boolean isPlanner( ChannelsUser user, CommunityService communityService ) {
      //  return findCommunityPlanner( user.getUserInfo(), communityService ) != null;
        return false;
    }

    @Override
    @Transactional
    public CommunityPlanner authorizePlanner( String username, ChannelsUser authorizedUser, CommunityService communityService ) {
        ChannelsUser authorizingUser = userDao.getUserNamed( username );
        if ( authorizingUser != null && authorizedUser != null
                && communityService.isCommunityPlanner( authorizingUser )
                && !isPlanner( authorizedUser, communityService ) ) {
            CommunityPlanner communityPlanner = new CommunityPlanner( username, authorizedUser, communityService.getPlanCommunity() );
            save( communityPlanner );
            communityService.clearCache();
            return communityPlanner;
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public boolean resignAsPlanner( String username, ChannelsUser planner, CommunityService communityService ) {
        ChannelsUser user = userDao.getUserNamed( username );
        if ( ( user != null && ( user.isAdmin() || user.equals( planner ) ) )
                && listPlanners( communityService ).size() > 1 ) {
            CommunityPlanner communityPlanner = null;
                    // findCommunityPlanner( planner.getUserInfo(), communityService );
            if ( communityPlanner != null ) {
                delete( communityPlanner );
                communityService.clearCache();
                return true;
            }
        }
        return false;
    }

    private CommunityPlanner findCommunityPlanner( ChannelsUserInfo userInfo, CommunityService communityService ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", communityService.getPlanCommunity().getUri() ) );
        criteria.add( Restrictions.eq( "userInfo", userInfo ) );
        return (CommunityPlanner) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<CommunityPlanner> listPlanners( CommunityService communityService ) {
        return listPlanners( communityService.getPlanCommunity() );
    }

    @SuppressWarnings("unchecked")
    private List<CommunityPlanner> listPlanners( PlanCommunity planCommunity ) {
        Session session = getSession();
        Criteria criteria = session.createCriteria( getPersistentClass() );
        criteria.add( Restrictions.eq( "communityUri", planCommunity.getUri() ) );
        return (List<CommunityPlanner>) criteria.list();
    }


    @Override
    @Transactional(readOnly = true)
    public boolean wasNotified( ChannelsUser planner, CommunityService communityService ) {
        CommunityPlanner communityPlanner = null;
                // findCommunityPlanner( planner.getUserInfo(), communityService );
        return communityPlanner != null && communityPlanner.isUserNotified();
    }

    @Override
    @Transactional
    public void setNotified( ChannelsUser planner, CommunityService communityService ) {
        CommunityPlanner communityPlanner = null;
             //   findCommunityPlanner( planner.getUserInfo(), communityService );
        if ( communityPlanner != null ) {
            communityPlanner.setUserNotified( true );
            save( communityPlanner );
        }
    }

    @Override
    @Transactional
    public void addFounder( ChannelsUser founder, PlanCommunity planCommunity ) {
        List<CommunityPlanner> planners = listPlanners( planCommunity );
        assert planners.isEmpty(); // Make sure founder is first planner
        CommunityPlanner communityPlanner = new CommunityPlanner( founder.getUsername(), founder, planCommunity );
        communityPlanner.setUserNotified( true );
        save( communityPlanner );
    }
}
