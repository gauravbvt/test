package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.dao.UserInfo;
import com.mindalliance.channels.dao.UserService;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.reports.SOPsReportPage;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Default page for administrators.
 * Allows defining users and plans.
 */
public class AdminPage extends WebPage {

    /** Wicket sometimes serializes pages... */
    private static final long serialVersionUID = -7349549537563793567L;

    /** Current user. */
    @SpringBean
    private User user;

    /** The plan manager. */
    @SpringBean
    private PlanManager planManager;

    /** The user service. */
    @SpringBean
    private UserService userService;

    private static final Logger LOG = LoggerFactory.getLogger( AdminPage.class );

    /**
     * Constructor. Having this constructor public means that your page is 'bookmarkable' and hence
     * can be called/ created from anywhere.
     */
    public AdminPage() {
        setStatelessHint( true );
        add(
            new Label( "loggedUser", user.getUsername() ),
            new Form<Void>( "users" ) {
                    private static final long serialVersionUID = -8235938747896846652L;

                    @Override
                    protected void onSubmit() {
                        super.onSubmit();
                        try {
                            userService.save();
                        } catch ( IOException e ) {
                            LOG.error( "Unable to save user definitions", e );
                        }
                    }
                }
                .add(
                new DropDownChoice<Plan>( "plan-sel",
                                  new PropertyModel<Plan>( this, "plan" ),
                                  new PropertyModel<List<? extends Plan>>( planManager, "plans" ) )
                    .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                        private static final long serialVersionUID = -5466916152047216396L;

                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            target.addComponent( get( "users" ) );
                        }
                    } ),

                new BookmarkablePageLink<PlanPage>( "plan", PlanPage.class ),

                new ListView<User>( "item",
                        new PropertyModel<List<User>>( userService, "users" ) ) {
                        private static final long serialVersionUID = 2266583072592123487L;

                        @Override
                        protected void populateItem( ListItem<User> item ) {
                            item.add( createUserRow(
                                    new PropertyModel<String>( AdminPage.this, "plan.uri" ),
                                    item ) );
                        }
                    }
                        .setReuseItems( true ),

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
                        }
                    },

                new BookmarkablePageLink<SOPsReportPage>( "report", SOPsReportPage.class )
                    .setPopupSettings( new PopupSettings(
                        PopupSettings.RESIZABLE | PopupSettings.SCROLLBARS
                                                | PopupSettings.MENU_BAR ) ) ) );
    }

    /**
      * Return current plan.
      *
      * @return a plan
      */
    public Plan getPlan() {
        return user.getPlan();
    }

    /**
     * Switch the user's current plan.
     *
     * @param plan a plan
     */
    public void setPlan( Plan plan ) {
        user.setPlan( plan );
    }

    private MarkupContainer createUserRow( IModel<String> uriModel, final ListItem<User> item ) {
        IModel<User> userModel = item.getModel();
        return new RadioGroup<Access>( "group", new RadioModel( userModel, uriModel ) ).add(

            new Label( "username", new PropertyModel<String>( userModel, "username" ) ),
            new TextField<String>( "fullName",
                                   new PropertyModel<String>( userModel, "userInfo.fullName" ) ),
            new TextField<String>( "email",
                                   new PropertyModel<String>( userModel, "userInfo.email" ) ),

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

            new Radio<Access>( "admin", new Model<Access>( Access.Admin ) ),
            new Radio<Access>( "planner", new Model<Access>( Access.Planner ) ),
            new Radio<Access>( "user", new Model<Access>( Access.User ) ),
            new Radio<Access>( "disabled", new Model<Access>( Access.Disabled ) ),
            new Radio<Access>( "localPlanner", new Model<Access>( Access.LPlanner ) ),
            new Radio<Access>( "localUser", new Model<Access>( Access.LUser ) ),
            new Radio<Access>( "localDisabled", new Model<Access>( Access.LDisabled ) ),

            new CheckBox( "delete", new Model<Boolean>( false ) ) {
                private static final long serialVersionUID = 7493342739960682828L;

                @Override
                protected void onModelChanged() {
                    userService.deleteUser( item.getModelObject() );
                }
            } );
    }

    //==================================================================
    enum Access { Admin,Planner,User,Disabled,LPlanner,LUser,LDisabled };

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
            return info.isAdmin()             ? Access.Admin
                 : info.isPlanner()           ? Access.Planner
                 : info.isUser()              ? Access.User
                 : !info.isEnabled()          ? Access.Disabled
                 : info.isPlanner( getUri() ) ? Access.LPlanner
                 : info.isUser( getUri() )    ? Access.LUser
                                              : Access.LDisabled;
        }

        public void setObject( Access object ) {
            User rowUser = userModel.getObject();
            switch ( object ) {
            case Admin:
                planManager.setAuthorities( rowUser, UserInfo.ROLE_ADMIN, null );
                break;
            case Planner:
                planManager.setAuthorities( rowUser, UserInfo.ROLE_PLANNER, null );
                break;
            case User:
                planManager.setAuthorities( rowUser, UserInfo.ROLE_USER, null );
                break;
            case LPlanner:
                planManager.setAuthorities( rowUser, UserInfo.ROLE_PLANNER, getUri() );
                break;
            case LUser:
                planManager.setAuthorities( rowUser, UserInfo.ROLE_USER, getUri() );
                break;
            case LDisabled:
                planManager.setAuthorities( rowUser, null, getUri() );
                break;
            case Disabled:
            default:
                planManager.setAuthorities( rowUser, null, null );
                break;
            }
        }
    }

}
