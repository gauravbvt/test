package com.mindalliance.channels.pages.components.community;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * All communities panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/11/13
 * Time: 12:39 PM
 */
public class AllCommunitiesPanel extends AbstractCommandablePanel {

    private static final int MAX_ROWS = 10;

    @SpringBean
    private PlanCommunityManager planCommunityManager;
    @SpringBean
    private UserParticipationService userParticipationManager;

    private AllCommunitiesTable communitiesTable;
    private Plan selectedProductionPlan;
    private AjaxLink<String> newCommunityButton;

    public AllCommunitiesPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addCommunitiesTable();
        addStartCommunity();
    }

    private void addStartCommunity() {
        addProductionPlanChoice();
        addCreateButton();
    }

    private void addProductionPlanChoice() {
        DropDownChoice<Plan> plansChoice = new DropDownChoice<Plan>(
                "productionPlans",
                new PropertyModel<Plan>( this, "selectedProductionPlan" ),
                new PropertyModel<List<Plan>>( this, "productionPlans" ),
                new IChoiceRenderer<Plan>() {
                    @Override
                    public Object getDisplayValue( Plan plan ) {
                        return plan.getVersionedName();
                    }

                    @Override
                    public String getIdValue( Plan plan, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        plansChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                newCommunityButton.setEnabled( selectedProductionPlan != null );
                target.add( newCommunityButton );
            }
        } );
        plansChoice.setOutputMarkupId( true );
        addOrReplace( plansChoice );
    }

    public List<Plan> getProductionPlans() {
        List<Plan> productionPlans = getPlanManager().getProductionPlans();
        Collections.sort( productionPlans, new Comparator<Plan>() {
            @Override
            public int compare( Plan p1, Plan p2 ) {
                return p1.getName().compareTo( p2.getName() );
            }
        } );
        return productionPlans;
    }

    public Plan getSelectedProductionPlan() {
        return selectedProductionPlan;
    }

    public void setSelectedProductionPlan( Plan selectedProductionPlan ) {
        this.selectedProductionPlan = selectedProductionPlan;
    }

    private void addCreateButton() {
        newCommunityButton = new AjaxLink<String>( "newCommunity" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                startNewCommunity();
                addCommunitiesTable();
                target.add( communitiesTable );
            }
        };
        newCommunityButton.setOutputMarkupId( true );
        add( newCommunityButton );
        newCommunityButton.setEnabled( selectedProductionPlan != null );
    }

    private void startNewCommunity() {
        planCommunityManager.createNewCommunityFor( selectedProductionPlan, getUser(), getCommunityService() );
    }

    public void updateContent( AjaxRequestTarget target ) {
        addCommunitiesTable();
        target.add( communitiesTable );
    }

    private void addCommunitiesTable() {
        communitiesTable = new AllCommunitiesTable(
                "allCommunitiesTable",
                new PropertyModel<List<CommunityWrapper>>( this, "communityWrappers" ) );
        addOrReplace( communitiesTable );
    }

    public List<CommunityWrapper> getCommunityWrappers() {
        List<CommunityWrapper> wrappers = new ArrayList<CommunityWrapper>();
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            if ( !planCommunity.isDomainCommunity()
                    && ( !planCommunity.isClosed() || isCommunityPlanner( getUser(), planCommunity ) ) ) {
                wrappers.add( new CommunityWrapper( planCommunity ) );
            }
        }
        Collections.sort( wrappers );
        return wrappers;
    }

    private boolean isCommunityPlanner( ChannelsUser user, PlanCommunity planCommunity ) {
        return user.isCommunityPlanner( planCommunity.getUri() );
    }

    public class CommunityWrapper implements Identifiable, Comparable<CommunityWrapper> {

        private PlanCommunity planCommunity;

        public CommunityWrapper( PlanCommunity planCommunity ) {
            this.planCommunity = planCommunity;
        }

        public PlanCommunity getPlanCommunity() {
            return planCommunity;
        }

        public Place getLocale() {
            return planCommunity.getCommunityLocale() == null
                    ? getPlan().getLocale()
                    : planCommunity.getCommunityLocale();
        }

        public Plan getPlan() {
            return getPlanManager().getPlan( planCommunity.getPlanUri(), planCommunity.getPlanVersion() );
        }

        public String getUserParticipates() {
            return userParticipationManager.getUserParticipations(
                    getUser(),
                    getCommunityService( planCommunity ) ).isEmpty()
                    ? "No"
                    : "Yes";
        }

        public String getCommunityUrl() {
            return makeCommunityPageUrl( planCommunity );
        }

        public String getStatus() {
            return planCommunity.isClosed()
                    ? "Closed"
                    : "Open";
        }

        @Override
        public String getClassLabel() {
            return getClass().getSimpleName();
        }

        @Override
        public long getId() {
            return planCommunity.getId();
        }

        @Override
        public String getDescription() {
            return planCommunity.getDescription();
        }

        @Override
        public String getTypeName() {
            return planCommunity.getTypeName();
        }

        @Override
        public boolean isModifiableInProduction() {
            return planCommunity.isModifiableInProduction();
        }

        @Override
        public String getName() {
            return planCommunity.getName();
        }

        @Override
        public int compareTo( CommunityWrapper other ) {
            return getName().compareTo( other.getName() );
        }
    }

    public class AllCommunitiesTable extends AbstractTablePanel<CommunityWrapper> {
        private IModel<List<CommunityWrapper>> communitiesModel;

        public AllCommunitiesTable( String s, IModel<List<CommunityWrapper>> communitiesModel ) {
            super( s );
            this.communitiesModel = communitiesModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // Columns
            columns.add( makeColumn( "Name", "name", null, EMPTY, "description" ) );
            columns.add( makeColumn( "Locale", "locale.name", null, EMPTY, "locale.description" ) );
            columns.add( makeColumn( "Collaboration model", "plan.name", null, EMPTY, "plan.description" ) );
            columns.add( makeColumn( "Model URI", "plan.versionUri", null, EMPTY, "plan.versionUri" ) );
            columns.add( makeColumn( "I participate", "userParticipates", EMPTY ) );
            columns.add( makeColumn( "Status", "status", EMPTY ) );
            columns.add( makeExternalLinkColumn(
                    "",
                    "communityUrl",
                    null,
                    "Go",
                    false   // stay on same page
            ) );
            // Provider and table
            add( new AjaxFallbackDefaultDataTable( "allCommunities",
                    columns,
                    new SortableBeanProvider<CommunityWrapper>( communitiesModel.getObject(),
                            "name" ),
                    MAX_ROWS ) );
        }
    }
}
