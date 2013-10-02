package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.community.CommunityDefinitionManager;
import com.mindalliance.channels.core.dao.PlanDefinitionManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO - remove - obsolete
 * Default page for administrators.
 * Allows defining users and plans.
 */
public class AdminPage extends AbstractChannelsWebPage {

    /**
     * Wicket sometimes serializes pages...
     */
    private static final long serialVersionUID = -7349549537563793567L;

    /**
     * Ye olde logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( AdminPage.class );

/*    @SpringBean
    private SurveyService surveyService;*/

    /**
     * The plan definition manager.
     */
    @SpringBean
    private PlanDefinitionManager planDefinitionManager;

    @SpringBean
    private CommunityDefinitionManager communityDefinitionManager;

    /**
     * The user service.
     */
    @SpringBean
    private UserRecordService userInfoService;

    @SpringBean
    private UserParticipationService userParticipationService;

    private ListView<ChannelsUser> userList;

    private List<ChannelsUser> toDelete = new ArrayList<ChannelsUser>();

    private String newPlanUri;

    private String newPlanClient;
    private FeedbackPanel validationFeedbackPanel;

    /**
     * Constructor. Having this constructor public means that your page is 'bookmarkable' and hence
     * can be called/ created from anywhere.
     */
    public AdminPage() {
        this( new PageParameters() );
    }

    public AdminPage( PageParameters parameters ) {
        super( parameters );
        init();
    }

    private void init() {
        setStatelessHint( true );
        userList = new ListView<ChannelsUser>( "item",
                new PropertyModel<List<ChannelsUser>>( userInfoService, "users" ) ) {
            private static final long serialVersionUID = 2266583072592123487L;

            @Override
            protected void populateItem( ListItem<ChannelsUser> item ) {
                item.add(
                        createUserRow(
                                new PropertyModel<String>( AdminPage.this, "plan.uri" ), item ) );
            }
        };

        Form<Void> form = new Form<Void>( "users" ) {
            private static final long serialVersionUID = -8235938747896846652L;

            @Override
            protected void onSubmit() {
                super.onSubmit();
                submit();
            }
        };

        boolean invalid = CollectionUtils.exists(
                getAnalyst().findAllUnwaivedIssues( getQueryService() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Issue) object ).isValidity();
                    }
                }
        );
        ConfirmedAjaxFallbackLink productizeLink = new ConfirmedAjaxFallbackLink(
                "productize",
                invalid
                        ? "Productize the current version even though validity issues are unresolved?"
                        : "Productize the current version?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                getPlanManager().productize( getPlan() );
               // setResponsePageWithPlan();
            }


        };
        productizeLink.setVisible( getPlan().isDevelopment() );
        // productizeLink.setEnabled( !invalid );
        if ( invalid ) {
            addTipTitle( productizeLink, "This version has unresolved validity issues. It should not be put into production." );
        }
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "deletePlan",
                "Delete the selected plan?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                List<Plan> plans = getPlanManager().getPlans();
                if ( plans.size() > 1 ) {
                    getPlanManager().delete( getPlan() );
                    setPlan( plans.get( 0 ) );
                    //setResponsePageWithPlan();
                }
            }
        };
        deleteLink.setVisible( canBeDeletedPlan( getPlan() ) );
        WebMarkupContainer managePlanSubmit = new WebMarkupContainer( "managePlanSubmit" );
        managePlanSubmit.setVisible( getPlan().isDevelopment() );
        Label homeLink = new Label( "homeLink", "Home" );
        homeLink.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                // Do nothing
            }
        } );
        validationFeedbackPanel = new FeedbackPanel( "feedback" ) {
            @Override
            protected String getCSSClass( final FeedbackMessage message ) {
                return "issues settings";  // customize here
            }
        };
        validationFeedbackPanel.setOutputMarkupId( true );
        // makeVisible( validationFeedbackPanel, !validationFeedbackPanel.getFeedbackMessages().isEmpty() );
        Component newPlanUriField = new TextField<String>( "newPlanUri",
                new PropertyModel<String>( this, "newPlanUri" ) )
                .add( new AbstractValidator<String>() {
                    @Override
                    protected void onValidate( IValidatable<String> validatable ) {
                        if ( !planDefinitionManager.isNewPlanUriValid( validatable.getValue() ) )
                            error( validatable, "NonUniqueUri" );
                    }
                } );
        newPlanUriField.add( new ValidationStyler() );
        addTipTitle( newPlanUriField, "Example: com_company_plans_example" );
        add(
                form.add(
                        homeLink,

                        validationFeedbackPanel
                                /*.setVisible( !validationFeedbackPanel.getFeedbackMessages().isEmpty() )*/,

                        productizeLink,

                        deleteLink,

                        managePlanSubmit,

                        new Label( "planUri", getPlan().getUri() ),
                        new TextField<String>( "planClient",
                                new PropertyModel<String>( this, "planClient" ) )
                                .setEnabled( getPlan().isDevelopment() ),
                        new Label(
                                "communitiesCount",
                                Integer.toString( communityDefinitionManager.countCommunitiesFor( getPlan().getUri() ) ) ),
                        new TextField<String>( "plannerSupportCommunity",
                                new PropertyModel<String>( this, "plannerSupportCommunity" ) ),
                        new TextField<String>( "userSupportCommunity",
                                new PropertyModel<String>( this, "userSupportCommunity" ) ),
                        /*
                        new TextField<String>( "communityCalendarHost",
                                new PropertyModel<String>( this, "communityCalendarHost" ) ),
                        new TextField<String>( "communityCalendar",
                                new PropertyModel<String>( this, "communityCalendar" ) ),
                        new TextField<String>( "communityCalendarPrivateTicket",
                                new PropertyModel<String>( this, "communityCalendarPrivateTicket" ) ),
*/
                        newPlanUriField,

                        new TextField<String>( "newPlanClient",
                                new PropertyModel<String>( this, "newPlanClient" ) ),

 /*                       new TextField<String>( "surveyApiKey",
                                new PropertyModel<String>( this, "surveyApiKey" ) ),
                        new TextField<String>( "surveyUserKey",
                                new PropertyModel<String>( this, "surveyUserKey" ) ),
                        new TextField<String>( "surveyTemplate",
                                new PropertyModel<String>( this, "surveyTemplate" ) ),
                        new TextField<String>( "surveyDefaultEmailAddress",
                                new PropertyModel<String>( this, "surveyDefaultEmailAddress" ) ),
*/
                        new DropDownChoice<Plan>( "plan-sel",
                                new PropertyModel<Plan>( this, "plan" ),
                                new PropertyModel<List<? extends Plan>>( this, "activePlans" ) )
                                .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                                    private static final long serialVersionUID = -5466916152047216396L;

                                    @Override
                                    protected void onUpdate( AjaxRequestTarget target ) {
                                        //setResponsePageWithPlan();
                                    }
                                } ),

                        userList.setReuseItems( true ),

                        new TextField<String>( "new", new Model<String>( null ) ) {
                            private static final long serialVersionUID = -4399667115289497468L;

                            @Override
                            protected void onModelChanged() {
                                super.onModelChanged();
                                String object = getModelObject();
                                if ( object != null ) {
                                    // userInfoService.createUser( object );
                                    setModelObject( null );
                                }
                                userList.removeAll();
                            }
                        }
                                .add( new AbstractValidator<String>() {
                                    @Override
                                    protected void onValidate( IValidatable<String> validatable ) {
                                        String name = validatable.getValue();
                                        if ( userInfoService.getUserWithIdentity( name ) != null ) {
                                            Map<String, Object> map = new HashMap<String, Object>();
                                            map.put( "name", name );
                                            error( validatable, "Duplicate", map );
                                        }
                                    }
                                } )
                                .add( new ValidationStyler() ),
                        new Label( "thisPlan", getPlan().getUri() )
                ) );
    }

    private boolean canBeDeletedPlan( Plan plan ) {
        return planDefinitionManager.getSize() >= 1
                && communityDefinitionManager.countCommunitiesFor( plan.getUri() ) == 0;
    }

    public String getPlannerSupportCommunity() {
        String s = getPlan().getPlannerSupportCommunity();
        return s.isEmpty() ? getPlanManager().getDefaultSupportCommunity() : s;
    }

    public void setPlannerSupportCommunity( String val ) {
        String defaultCommunity = getPlanManager().getDefaultSupportCommunity();
        if ( val != null && !val.isEmpty() && !val.equals( defaultCommunity ) )
            getPlan().setPlannerSupportCommunity( val );
    }

    public String getUserSupportCommunity() {
        String s = getPlan().getUserSupportCommunity();
        return s.isEmpty() ? getPlanManager().getDefaultSupportCommunity() : s;
    }

    public void setUserSupportCommunity( String val ) {
        String defaultCommunity = getPlanManager().getDefaultSupportCommunity();
        if ( val != null && !val.isEmpty() && !val.equals( defaultCommunity ) )
            getPlan().setUserSupportCommunity( val );
    }

    public String getCommunityCalendarHost() {
        String s = getPlan().getCommunityCalendarHost();
        return s.isEmpty() ? getPlanManager().getDefaultCommunityCalendarHost() : s;
    }

    public void setCommunityCalendarHost( String val ) {
        String defaultCalendarHost = getPlanManager().getDefaultCommunityCalendarHost();
        if ( val != null && !val.isEmpty() && !val.equals( defaultCalendarHost ) )
            getPlan().setCommunityCalendarHost( val );
    }

    public String getCommunityCalendar() {
        String s = getPlan().getCommunityCalendar();
        return s.isEmpty() ? getPlanManager().getDefaultCommunityCalendar() : s;
    }

    public void setCommunityCalendar( String val ) {
        String defaultCalendar = getPlanManager().getDefaultCommunityCalendar();
        if ( val != null && !val.isEmpty() && !val.equals( defaultCalendar ) )
            getPlan().setCommunityCalendar( val );
    }

    public String getCommunityCalendarPrivateTicket() {
        String s = getPlan().getCommunityCalendarPrivateTicket();
        return s.isEmpty() ? getPlanManager().getDefaultCommunityCalendarPrivateTicket() : s;
    }

    public void setCommunityCalendarPrivateTicket( String val ) {
        String defaultCalendarPrivateTicket = getPlanManager().getDefaultCommunityCalendarPrivateTicket();
        if ( val != null && !val.isEmpty() && !val.equals( defaultCalendarPrivateTicket ) )
            getPlan().setCommunityCalendarPrivateTicket( val );
    }


    private void submit() {
        for ( ChannelsUser u : toDelete ) {
            // getPlanManager().setAuthorities( u, null, null );
            userInfoService.deleteUser( ChannelsUser.current().getUsername(), u, getCommunityService() );
            userParticipationService.deleteAllParticipations( u, ChannelsUser.current().getUsername() );
        }
        if ( !toDelete.isEmpty() ) {
            toDelete.clear();
            userList.removeAll();
        }

        if ( newPlanUri != null && newPlanClient != null ) {
            try {
                String newPlanName =
                        !newPlanClient.isEmpty()
                                ? ( newPlanClient + ( newPlanClient.endsWith( "s" ) ? "'" : "'s" ) + " New Plan" )
                                : "New Plan";
                planDefinitionManager.getOrCreate( newPlanUri, newPlanName, newPlanClient );
                getPlanManager().assignPlans();
            } catch ( IOException e ) {
                LOG.error( "Unable to create plan", e );
                throw new RuntimeException( "Unable to create plan", e );
            }
        }

        getPlanManager().allDevelopersInFavorToPutInProduction( getPlan() );
        getPlanManager().save( getPlan() );
/*
        try {
            userDao.save();
        } catch ( IOException e ) {
            LOG.error( "Unable to save user definitions", e );
        }
*/
        //setResponsePageWithPlan();
    }


    public List<Plan> getActivePlans() {
        List<Plan> answer = new ArrayList<Plan>();
        for ( Plan plan : getPlanManager().getPlans() )
            if ( plan.isDevelopment() || plan.isProduction() )
                answer.add( plan );

        return answer;
    }


    public String getPlanClient() {
        return getPlan().getClient();
    }

    public void setPlanClient( String val ) {
        getPlan().setClient( val );
    }

    public String getNewPlanClient() {
        return newPlanClient;
    }

    public void setNewPlanClient( String newPlanClient ) {
        this.newPlanClient = newPlanClient;
    }

    public String getNewPlanUri() {
        return newPlanUri;
    }

    public void setNewPlanUri( String newPlanUri ) {
        this.newPlanUri = ChannelsUtils.sanitize( newPlanUri );
    }

    private MarkupContainer createUserRow( IModel<String> uriModel, final ListItem<ChannelsUser> item ) {
        IModel<ChannelsUser> userModel = item.getModel();
        boolean notMe = !userModel.getObject().equals( getUser() );
        return new RadioGroup<Access>( "group", new RadioModel( userModel, uriModel ) ).add(

                new Radio<Access>( "admin", new Model<Access>( Access.Admin ) ).setEnabled( notMe ),
                new Radio<Access>( "planner", new Model<Access>( Access.Planner ) ).setEnabled( notMe ),
                new Radio<Access>( "user", new Model<Access>( Access.User ) ).setEnabled( notMe ),
                new Radio<Access>( "disabled", new Model<Access>( Access.Disabled ) ).setEnabled( notMe ),
                new Radio<Access>( "localPlanner", new Model<Access>( Access.LPlanner ) ).setEnabled( notMe ),
                new Radio<Access>( "localUser", new Model<Access>( Access.LUser ) ).setEnabled( notMe ),
                new Radio<Access>( "localDisabled", new Model<Access>( Access.LDisabled ) ).setEnabled( notMe ),

                new Label( "username", new PropertyModel<String>( userModel, "username" ) ),
                new TextField<String>( "fullName",
                        new PropertyModel<String>( userModel, "userInfo.fullName" ) ) {
                    @Override
                    protected void onModelChanged() {
                        super.onModelChanged();
                        userInfoService.save( item.getModelObject().getUserRecord() );
                    }
                },
                new TextField<String>( "email",
                        new PropertyModel<String>( userModel, "userInfo.email" ) ) {
                    @Override
                    protected void onModelChanged() {
                        super.onModelChanged();
                        userInfoService.save( item.getModelObject().getUserRecord() );
                    }
                }
                        .add( EmailAddressValidator.getInstance() )
                        .add( new ValidationStyler() ),

                new PasswordTextField( "password", new Model<String>( null ) ) {
                    private static final long serialVersionUID = 2037327143613490877L;

                    @Override
                    protected void onModelChanged() {
                        String pwd = getModelObject();
                        if ( pwd != null && !pwd.trim().isEmpty() ) {
                            item.getModelObject().getUserRecord().setPassword( pwd );
                            userInfoService.save( item.getModelObject().getUserRecord() );
                        }
                    }
                }
                        .setRequired( false ),

                new CheckBox( "delete", new Model<Boolean>( false ) ) {
                    private static final long serialVersionUID = 7493342739960682828L;

                    @Override
                    protected void onModelChanged() {
                        toDelete.add( item.getModelObject() );
                    }
                }.setVisible( notMe ) );
    }

    //==================================================================

    /**
     * Utility styler for components with errors.
     */
    public static class ValidationStyler extends AbstractBehavior {

        private static final long serialVersionUID = -4547320874627670444L;

        public ValidationStyler() {
        }

        @Override
        public void onComponentTag( Component component, ComponentTag tag ) {
            FormComponent<?> comp = (FormComponent<?>) component;
            if ( !comp.isValid() )
                tag.put( "class", "error" );

            super.onComponentTag( component, tag );
        }
    }

    //==================================================================
    enum Access {
        Admin, Planner, User, Disabled, LPlanner, LUser, LDisabled
    }

    //==================================================================

    /**
     * Wrapper class for access rights modifications.
     */
    private class RadioModel implements IModel<Access> {

        private static final long serialVersionUID = -3774236040672398817L;

        private final IModel<String> uriModel;

        private final IModel<ChannelsUser> userModel;


        private RadioModel( IModel<ChannelsUser> userModel, IModel<String> uriModel ) {
            this.userModel = userModel;
            this.uriModel = uriModel;
        }

        public void detach() {
        }

        private String getUri() {
            return uriModel.getObject();
        }

        public Access getObject() {
            return getObject( userModel.getObject().getUserRecord() );
        }

        private Access getObject( UserRecord info ) {
         /*   return info.isAdmin() ? Access.Admin
                    : info.isPlanner() ? Access.Planner
                    : info.isUser() ? Access.User
                    : !info.isEnabled() ? Access.Disabled
                    : info.isPlanner( getUri() ) ? Access.LPlanner
                    : info.isUser( getUri() ) ? Access.LUser
                    : Access.LDisabled;*/
            return null;
        }

        public void setObject( Access object ) {
            if ( object != null ) {
                ChannelsUser rowUser = userModel.getObject();
    /*            switch ( object ) {
                    case Admin:
                        getPlanManager().setAuthorities( rowUser, UserRecord.ROLE_ADMIN, null );
                        break;
                    case Planner:
                        getPlanManager().setAuthorities( rowUser, UserRecord.ROLE_PLANNER, null );
                        break;
                    case User:
                        getPlanManager().setAuthorities( rowUser, UserRecord.ROLE_USER, null );
                        break;
                    case LPlanner:
                        getPlanManager().setAuthorities( rowUser, UserRecord.ROLE_PLANNER, getUri() );
                        break;
                    case LUser:
                        getPlanManager().setAuthorities( rowUser, UserRecord.ROLE_USER, getUri() );
                        break;
                    case LDisabled:
                        getPlanManager().setAuthorities( rowUser, null, getUri() );
                        break;
                    case Disabled:
                    default:
                        getPlanManager().setAuthorities( rowUser, null, null );
                        break;
                }
*/                userInfoService.save( rowUser.getUserRecord() );
            }
        }
    }

}
