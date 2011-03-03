package com.mindalliance.channels.pages;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.components.social.SocialPanel;
import com.mindalliance.channels.pages.components.support.UserFeedbackPanel;
import com.mindalliance.channels.pages.reports.ProcedureMapPage;
import com.mindalliance.channels.pages.reports.ProceduresReportPage;
import com.mindalliance.channels.query.PlanService;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Channels' home page.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/2/11
 * Time: 12:42 PM
 */
public class UserPage extends WebPage {

    public static final String PLAN_PARM = "plan";
    public static final String VERSION_PARM = "v";

    @SpringBean
    private User user;


    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private AttachmentManager attachmentManager;

    @SpringBean
    ImagingService imagingService;
    /**
     * The selected plan.
     */
    private Plan plan;

    transient private QueryService queryService;

    private boolean valid = true;

    /**
     * The big form -- used for attachments and segment imports only.
     */
    private Form form;
    /**
     * Ajax activity spinner.
     */
    private WebMarkupContainer spinner;


    public UserPage() {
        this( new PageParameters() );
    }

    public UserPage( PageParameters parameters ) {
        super( parameters );
        setParameters( parameters );
        init();
    }

    private void init() {
        addPageTitle();
        addForm();
        addWelcome();
        addLoggedIn();
        addFeedback();
        addPlanSelector();
        addPlanImage();
        addPlanName();
        addPlanClient();
        // addPlanMetrics();
        addGotoLinks();
        addSocial();
    }

    private void addPageTitle() {
        add( new Label( "sg-title",
                new Model<String>( "Channels - Information Sharing Planning" ) ) );

    }

    /**
     * Set segment and actor fields given parameters.
     *
     * @param parameters the parameters
     */
    private void setParameters( PageParameters parameters ) {
        setPlan( parameters );
    }

    /**
     * Set plan from uri parameters.
     *
     * @param parameters the parameters
     * @return false if plan was not set
     */
    private boolean setPlan( PageParameters parameters ) {
        String planUri = parameters.getString( PLAN_PARM, user.getPlanUri() );

        int planVersion = parameters.getInt( VERSION_PARM, 0 );

        List<Plan> plans = getPlans();
        if ( plans.isEmpty() )
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_FORBIDDEN );

        for ( Iterator<Plan> it = plans.iterator(); it.hasNext() && plan == null; ) {
            Plan p = it.next();
            if ( planUri.equals( p.getUri() ) ) {
                if ( user.isPlanner( p.getUri() ) ) {
                    if ( planVersion == p.getVersion() || p.isDevelopment() )
                        plan = p;
                } else if ( p.isProduction() )
                    plan = p;
            }
        }

        if ( plan == null ) {
            plan = plans.get( 0 );
            if ( getPlans().size() > 1 )
                setValid( false );
        }

        if ( !isPlanner() && planVersion != 0 )
            setValid( false );

        getQueryService();
        return true;
    }

    public final QueryService getQueryService() {
        if ( queryService == null )
            queryService = getQueryService( plan );

        return queryService;
    }


    private void addForm() {
        form = new Form( "big-form" ) {
            @Override
            protected void onSubmit() {
                // Do nothing - everything is done via Ajax, even file uploads
                // System.out.println( "Form submitted" );
            }
        };
        form.setMultiPart( true );
        add( form );
    }


    private void addWelcome() {
        form.add( new Label( "userName", user.getFullName() ) );
    }

    private void addLoggedIn() {
        form.add( new Label( "user",
                user.getUsername() ) );
    }

    private void addFeedback() {
        form.add( new UserFeedbackPanel( "feedback" ) );
    }

    private void addPlanSelector() {
        WebMarkupContainer planSelectorDiv = new WebMarkupContainer( "switch-plan" );
        form.add( planSelectorDiv );
        planSelectorDiv.add( new DropDownChoice<Plan>(
                "plan-sel",
                new PropertyModel<Plan>( this, "plan" ),
                new PropertyModel<List<? extends Plan>>( this, "plans" ) )
                .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        redirect();
                    }
                } ) );
        planSelectorDiv.setVisible( getPlans().size() > 1 );
    }

    private void addPlanImage() {
        WebMarkupContainer image = new WebMarkupContainer( "planImage" );
        image.add( new AttributeModifier( "src", true, new Model<String>( getPlanImagePath() ) ) );
        form.add( image );
    }

    private void addPlanName() {
        form.add( new Label( "planName", plan.getName() ) );
    }

    private void addPlanClient() {
        form.add( new Label( "planClient", plan.getClient() ) );
    }

    private void addGotoLinks() {
        BookmarkablePageLink<PlanPage> gotoPlan = newTargetedLink(
                "gotoModel",
                "",
                PlanPage.class,
                null
        );
        gotoPlan.setVisible( plan.isTemplate() || user.isPlanner( plan.getUri() ) );
        form.add( gotoPlan );
        BookmarkablePageLink<ProcedureMapPage> gotoMapped = newTargetedLink(
                "gotoMapped",
                "",
                ProcedureMapPage.class,
                null
        );
        gotoMapped.setVisible( plan.isTemplate() || user.isPlanner( plan.getUri() ) );
        form.add( gotoMapped );
        BookmarkablePageLink<ProceduresReportPage> gotoReport = newTargetedLink(
                "gotoReport",
                "",
                ProceduresReportPage.class,
                null
        );
        Participation participation = getQueryService().findParticipation( user.getUsername() );
        gotoReport.setVisible(
                participation != null && participation.getActor() != null );
        form.add( gotoReport );
    }

    private void addSocial() {
        form.add ( new SocialPanel( "social") );
    }

    private String getPlanImagePath() {
        String path = imagingService.getSquareIconUrl( plan );
        if ( path == null ) {
            path = "images/plan.png";
        }
        return path;
    }


    private void redirect() {
        setRedirect( true );
        setResponsePage( getPage().getClass(), getParameters() );
    }

    /**
     * Build a new parameter container for the current selections.
     *
     * @return the parameters
     */
    public PageParameters getParameters() {
        PageParameters result = new PageParameters();

        if ( getPlans().size() > 1 )
            result.put( PLAN_PARM, plan.getUri() );
        if ( isPlanner() && plan.isProduction() )
            result.put( VERSION_PARM, Integer.toString( plan.getVersion() ) );

        return result;
    }

    /**
     * Get all plans that the current can read.
     *
     * @return a list of plans
     */
    public final List<Plan> getPlans() {
        List<Plan> result = new ArrayList<Plan>();
        for ( Plan p : planManager.getReadablePlans( user ) ) {
            String uri = p.getUri();
            if ( user.isPlanner( uri ) )
                result.add( p );

            else if ( user.isParticipant( uri ) ) {
                Participation participation =
                        getQueryService( p ).findParticipation( user.getUsername() );
                if ( participation != null && participation.getActor() != null )
                    result.add( p );
            }
        }

        return result;
    }


    private PlanService getQueryService( Plan plan ) {
        return new PlanService( planManager, attachmentManager, plan );
    }

    public boolean isPlanner() {
        return user.isPlanner( plan.getUri() );
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid( boolean valid ) {
        this.valid = valid;
    }

    private static <T extends WebPage> BookmarkablePageLink<T> newTargetedLink(
            String id,
            String target,
            Class<T> pageClass,
            PopupSettings popupSettings ) {

        BookmarkablePageLink<T> link = new BookmarkablePageLink<T>( id, pageClass );
        link.add( new AttributeModifier( "target", true, new Model<String>( target ) ) );
        if ( popupSettings != null )
            link.setPopupSettings( popupSettings );

        return link;
    }

}
