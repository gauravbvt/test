package com.mindalliance.channels.pages.sops;

import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.LoginPage;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Abstract class for SOP pages.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2010
 * Time: 1:45:21 PM
 */
public abstract class AbstractSOPsPage extends WebPage {

    /**
     * The name of the part id parameter.
     */
    public static final String PARTICIPATION_PARM = "p";

    /**
     * The logged-in user.
     */
    @SpringBean
    private User user;

    /**
     * Data access.
     */
    @SpringBean
    private QueryService queryService;

    protected AbstractSOPsPage() {
    }

    protected AbstractSOPsPage( PageParameters parameters ) {
        super( parameters );
        Participation participation = getActualParticipation();
        if ( participation == null ) {
            setRedirect( true );
            throw new RestartResponseException( LoginPage.class );
        }
    }

    /**
     * Get implied participation for SOPs.
     *
     * @return a participation
     */
    protected Participation getActualParticipation() {
        Participation participation = getParm( PARTICIPATION_PARM, Participation.class );
        if ( participation == null ) {
            participation = queryService.findOrCreate( Participation.class, user.getUsername() );
        } else {
            if ( !user.isPlanner( getPlan().getUri() ) ) {
                setRedirect( true );
                throw new RestartResponseException( LoginPage.class );                
            }
        }
        return participation;
    }

    /**
     * Get model object from page parameter.
     *
     * @param parm      a string
     * @param parmClass a model object class
     * @return a model object
     */
    protected <T extends ModelObject> T getParm( String parm, Class<T> parmClass ) {
        T result = null;
        PageParameters parms = getPageParameters();
        if ( parms.containsKey( parm ) )
            try {
                result = queryService.find( parmClass, Long.valueOf( parms.getString( parm ) ) );

            } catch ( NumberFormatException ignored ) {
                result = null;

            } catch ( NotFoundException ignored ) {
                result = null;
            }

        return result;
    }


    /**
     * Get query service.
     *
     * @return a query service
     */
    protected QueryService getQueryService() {
        return queryService;
    }

    protected final Plan getPlan() {
        return getQueryService().getCurrentPlan();
    }

}

