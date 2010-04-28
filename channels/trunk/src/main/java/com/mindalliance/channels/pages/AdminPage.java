package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.reports.SOPsReportPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

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

    /**
     * Constructor. Having this constructor public means that your page is 'bookmarkable' and hence
     * can be called/ created from anywhere.
     */
    public AdminPage() {
        add(
            new Label( "user", user.getUsername() ),
            new BookmarkablePageLink<PlanPage>( "plan", PlanPage.class ),
            // new BookmarkablePageLink<SOPsReportPage>( "playbook", TaskPlaybook.class ),
            new DropDownChoice<Plan>(
                        "plan-sel",
                        new PropertyModel<Plan>( this, "plan" ),
                        new PropertyModel<List<? extends Plan>>( planManager, "plans" ) )

                    .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            // Do nothing
                        }
                    } )
        );
        BookmarkablePageLink SOPsLink = new BookmarkablePageLink<SOPsReportPage>( "report", SOPsReportPage.class );
        SOPsLink.setPopupSettings( new PopupSettings(
               PopupSettings.RESIZABLE |
                        PopupSettings.SCROLLBARS |
                        PopupSettings.MENU_BAR ) );
        add( SOPsLink );


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

}
