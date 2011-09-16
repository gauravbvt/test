package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.dao.DefinitionManager;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserInfo;
import com.mindalliance.channels.core.dao.UserService;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.surveys.SurveyService;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AbstractBehavior;
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

    @SpringBean
    private SurveyService surveyService;

    /**
     * The plan definition manager.
     */
    @SpringBean
    private DefinitionManager definitionManager;

    /**
     * The user service.
     */
    @SpringBean
    private UserService userService;

    private ListView<User> userList;

    private List<User> toDelete = new ArrayList<User>();

    private String newPlanUri;

    private String newPlanClient;

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
        userList = new ListView<User>( "item",
                new PropertyModel<List<User>>( userService, "users" ) ) {
            private static final long serialVersionUID = 2266583072592123487L;

            @Override
            protected void populateItem( ListItem<User> item ) {
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

        ConfirmedAjaxFallbackLink productizeLink = new ConfirmedAjaxFallbackLink(
                "productize",
                "Productize the current version?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                getPlanManager().productize( getPlan() );
                setResponsePageWithPlan();
            }


        };
        productizeLink.setVisible( getPlan().isDevelopment() );
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "deletePlan",
                "Delete the selected plan?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                List<Plan> plans = getPlanManager().getPlans();
                if ( plans.size() > 1 ) {
                    getPlanManager().delete( getPlan() );
                    setPlan( plans.get( 0 ) );
                    setResponsePageWithPlan();
                }
            }
        };
        deleteLink.setVisible( definitionManager.getSize() >= 1 );
        WebMarkupContainer managePlanSubmit = new WebMarkupContainer( "managePlanSubmit" );
        managePlanSubmit.setVisible( getPlan().isDevelopment() );
        add(
                new Label( "loggedUser", getUser().getUsername() ),
                form.add(

                        new FeedbackPanel( "feedback" ),

                        productizeLink,

                        deleteLink,

                        managePlanSubmit,

                        new Label( "planUri", getPlan().getUri() ),
                        new TextField<String>( "planClient",
                                new PropertyModel<String>( this, "planClient" ) )
                                .setEnabled( getPlan().isDevelopment() ),

                        new TextField<String>( "plannerSupportCommunity",
                                new PropertyModel<String>( this, "plannerSupportCommunity" ) ),
                        new TextField<String>( "userSupportCommunity",
                                new PropertyModel<String>( this, "userSupportCommunity" ) ),
                        new TextField<String>( "communityCalendarHost",
                                new PropertyModel<String>( this, "communityCalendarHost" ) ),
                        new TextField<String>( "communityCalendar",
                                new PropertyModel<String>( this, "communityCalendar" ) ),
                        new TextField<String>( "communityCalendarPrivateTicket",
                                new PropertyModel<String>( this, "communityCalendarPrivateTicket" ) ),

                        new TextField<String>( "newPlanUri",
                                new PropertyModel<String>( this, "newPlanUri" ) )
                                .add( new AbstractValidator<String>() {
                                    @Override
                                    protected void onValidate( IValidatable<String> validatable ) {
                                        if ( !definitionManager.isNewPlanUriValid( validatable.getValue() ) )
                                            error( validatable, "NonUniqueUri" );
                                    }
                                } )
                                .add( new ValidationStyler() ),

                        new TextField<String>( "newPlanClient",
                                new PropertyModel<String>( this, "newPlanClient" ) ),

                        new TextField<String>( "surveyApiKey",
                                new PropertyModel<String>( this, "surveyApiKey" ) ),
                        new TextField<String>( "surveyUserKey",
                                new PropertyModel<String>( this, "surveyUserKey" ) ),
                        new TextField<String>( "surveyTemplate",
                                new PropertyModel<String>( this, "surveyTemplate" ) ),
                        new TextField<String>( "surveyDefaultEmailAddress",
                                new PropertyModel<String>( this, "surveyDefaultEmailAddress" ) ),

                        new DropDownChoice<Plan>( "plan-sel",
                                new PropertyModel<Plan>( this, "plan" ),
                                new PropertyModel<List<? extends Plan>>( this, "activePlans" ) )
                                .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                                    private static final long serialVersionUID = -5466916152047216396L;

                                    @Override
                                    protected void onUpdate( AjaxRequestTarget target ) {
                                        setResponsePageWithPlan();
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
                                    userService.createUser( object );
                                    setModelObject( null );
                                }
                                userList.removeAll();
                            }
                        }
                                .add( new AbstractValidator<String>() {
                                    @Override
                                    protected void onValidate( IValidatable<String> validatable ) {
                                        String name = validatable.getValue();
                                        if ( userService.getUserNamed( name ) != null ) {
                                            Map<String, Object> map = new HashMap<String, Object>();
                                            map.put( "name", name );
                                            error( validatable, "Duplicate", map );
                                        }
                                    }
                                } )
                                .add( new ValidationStyler() )
                ) );
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


    public String getSurveyApiKey() {
        String s = getPlan().getSurveyApiKey();
        return s.isEmpty() ? surveyService.getApiKey( getPlan() ) : s;
    }

    public void setSurveyApiKey( String val ) {
        String defaultVal = surveyService.getApiKey( getPlan() );
        if ( val != null && !val.isEmpty() && !val.equals( defaultVal ) )
            getPlan().setSurveyApiKey( val );
    }

    public String getSurveyUserKey() {
        String s = getPlan().getSurveyUserKey();
        return s.isEmpty() ? surveyService.getUserKey( getPlan() ) : s;
    }

    public void setSurveyUserKey( String val ) {
        String defaultVal = surveyService.getUserKey( getPlan() );
        if ( val != null && !val.isEmpty() && !val.equals( defaultVal ) )
            getPlan().setSurveyUserKey( val );
    }


    public String getSurveyTemplate() {
        String s = getPlan().getSurveyTemplate();
        return s.isEmpty() ? surveyService.getTemplate( getPlan() ) : s;
    }

    public void setSurveyTemplate( String val ) {
        String defaultVal = surveyService.getTemplate( getPlan() );
        if ( val != null && !val.isEmpty() && !val.equals( defaultVal ) ) {
            getPlan().setSurveyTemplate( val );
        }
    }


    public String getSurveyDefaultEmailAddress() {
        String s = getPlan().getSurveyDefaultEmailAddress();
        return s.isEmpty() ? surveyService.getDefaultEmailAddress( getPlan() ) : s;
    }

    public void setSurveyDefaultEmailAddress( String val ) {
        String defaultVal = surveyService.getDefaultEmailAddress( getPlan() );
        if ( val != null && !val.isEmpty() && !val.equals( defaultVal ) ) {
            getPlan().setSurveyDefaultEmailAddress( val );
        }
    }

    private void submit() {
        for ( User u : toDelete ) {
            getPlanManager().setAuthorities( u, null, null );
            userService.deleteUser( u );
        }
        if ( !toDelete.isEmpty() ) {
            toDelete.clear();
            userList.removeAll();
        }

        if ( newPlanUri != null ) {
            try {
                definitionManager.getOrCreate( newPlanUri, "New Plan", newPlanClient );
                getPlanManager().assignPlans();
            } catch ( IOException e ) {
                LOG.error( "Unable to create plan", e );
                throw new RuntimeException( "Unable to create plan", e );
            }
        }

        getPlanManager().revalidateProducers( getPlan() );
        getPlanManager().save( getPlan() );
        try {
            userService.save();
        } catch ( IOException e ) {
            LOG.error( "Unable to save user definitions", e );
        }
        setResponsePageWithPlan();
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
        this.newPlanUri = newPlanUri;
    }

    private MarkupContainer createUserRow( IModel<String> uriModel, final ListItem<User> item ) {
        IModel<User> userModel = item.getModel();
        boolean notMe = !userModel.getObject().equals( User.current() );
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
                        new PropertyModel<String>( userModel, "userInfo.fullName" ) ),
                new TextField<String>( "email",
                        new PropertyModel<String>( userModel, "userInfo.email" ) )
                        .add( EmailAddressValidator.getInstance() )
                        .add( new ValidationStyler() ),

                new PasswordTextField( "password", new Model<String>( null ) ) {
                    private static final long serialVersionUID = 2037327143613490877L;

                    @Override
                    protected void onModelChanged() {
                        String pwd = getModelObject();
                        if ( pwd != null && !pwd.trim().isEmpty() )
                            item.getModelObject().getUserInfo().setPassword( pwd );
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

        private final IModel<User> userModel;


        private RadioModel( IModel<User> userModel, IModel<String> uriModel ) {
            this.userModel = userModel;
            this.uriModel = uriModel;
        }

        public void detach() {
        }

        private String getUri() {
            return uriModel.getObject();
        }

        public Access getObject() {
            return getObject( userModel.getObject().getUserInfo() );
        }

        private Access getObject( UserInfo info ) {
            return info.isAdmin() ? Access.Admin
                    : info.isPlanner() ? Access.Planner
                    : info.isUser() ? Access.User
                    : !info.isEnabled() ? Access.Disabled
                    : info.isPlanner( getUri() ) ? Access.LPlanner
                    : info.isUser( getUri() ) ? Access.LUser
                    : Access.LDisabled;
        }

        public void setObject( Access object ) {
            if ( object != null ) {
                User rowUser = userModel.getObject();
                switch ( object ) {
                    case Admin:
                        getPlanManager().setAuthorities( rowUser, UserInfo.ROLE_ADMIN, null );
                        break;
                    case Planner:
                        getPlanManager().setAuthorities( rowUser, UserInfo.ROLE_PLANNER, null );
                        break;
                    case User:
                        getPlanManager().setAuthorities( rowUser, UserInfo.ROLE_USER, null );
                        break;
                    case LPlanner:
                        getPlanManager().setAuthorities( rowUser, UserInfo.ROLE_PLANNER, getUri() );
                        break;
                    case LUser:
                        getPlanManager().setAuthorities( rowUser, UserInfo.ROLE_USER, getUri() );
                        break;
                    case LDisabled:
                        getPlanManager().setAuthorities( rowUser, null, getUri() );
                        break;
                    case Disabled:
                    default:
                        getPlanManager().setAuthorities( rowUser, null, null );
                        break;
                }
            }
        }
    }

}
