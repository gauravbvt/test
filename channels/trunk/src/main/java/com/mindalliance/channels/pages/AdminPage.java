package com.mindalliance.channels.pages;

import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.User;
import com.mindalliance.channels.pages.playbook.TaskPlaybook;
import com.mindalliance.channels.pages.reports.PlanReportPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Default page for administrators.
 * Allows defining users and plans.
 */
public class AdminPage extends WebPage {

    @SpringBean
    private User user;

    @SpringBean
    private PlanManager planManager;

    /**
     * Constructor. Having this constructor public means that your page is 'bookmarkable' and hence
     * can be called/ created from anywhere.
     */
    public AdminPage() {
        add( new Label( "user", user.getUsername() ) );
        addPlanSwitcher();
        add( new BookmarkablePageLink<PlanPage>( "plan", PlanPage.class ) );
        add( new BookmarkablePageLink<PlanReportPage>( "report", PlanReportPage.class ) );
        add( new BookmarkablePageLink<PlanReportPage>( "playbook", TaskPlaybook.class ) );
    }

    private void addPlanSwitcher() {
        DropDownChoice planDropDownChoice = new DropDownChoice<Plan>(
                "plan-sel",
                new PropertyModel<Plan>( this, "plan" ),
                new PropertyModel<List<? extends Plan>>( this, "allPlans" ) );
        planDropDownChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // Do nothing
            }
        } );
        add( planDropDownChoice );
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
         user.switchPlan( plan );
     }

    /**
     * Get all plans that the current can modify.
     *
     * @return a list of plans
     */
    public List<Plan> getAllPlans() {
        return planManager.getPlans();
    }


}
