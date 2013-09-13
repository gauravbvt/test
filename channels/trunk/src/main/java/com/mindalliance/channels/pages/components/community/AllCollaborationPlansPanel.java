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
public class AllCollaborationPlansPanel extends AbstractCommandablePanel {

    private static final int MAX_ROWS = 10;

    @SpringBean
    private PlanCommunityManager planCommunityManager;
    @SpringBean
    private UserParticipationService userParticipationManager;

    private AllCollaborationPlansTable collaborationPlansTable;
    private Plan selectedProductionTemplate;
    private AjaxLink<String> newCollaborationPlanButton;

    public AllCollaborationPlansPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addCollaborationPlansTable();
        addStartCollaborationPlan();
    }

    private void addStartCollaborationPlan() {
        addProductionTemplateChoice();
        addCreateButton();
    }

    private void addProductionTemplateChoice() {
        DropDownChoice<Plan> templatesChoice = new DropDownChoice<Plan>(
                "productionPlans",
                new PropertyModel<Plan>( this, "selectedProductionTemplate" ),
                new PropertyModel<List<Plan>>( this, "productionTemplates" ),
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
        templatesChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                newCollaborationPlanButton.setEnabled( selectedProductionTemplate != null );
                target.add( newCollaborationPlanButton );
            }
        } );
        templatesChoice.setOutputMarkupId( true );
        addOrReplace( templatesChoice );
    }

    public List<Plan> getProductionTemplates() {
        List<Plan> productionPlans = getPlanManager().getProductionPlans();
        Collections.sort( productionPlans, new Comparator<Plan>() {
            @Override
            public int compare( Plan p1, Plan p2 ) {
                return p1.getName().compareTo( p2.getName() );
            }
        } );
        return productionPlans;
    }

    public Plan getSelectedProductionTemplate() {
        return selectedProductionTemplate;
    }

    public void setSelectedProductionTemplate( Plan selectedProductionTemplate ) {
        this.selectedProductionTemplate = selectedProductionTemplate;
    }

    private void addCreateButton() {
        newCollaborationPlanButton = new AjaxLink<String>( "newCollaborationPlan" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                startNewCollaborationPlan();
                addCollaborationPlansTable();
                target.add( collaborationPlansTable );
            }
        };
        newCollaborationPlanButton.setOutputMarkupId( true );
        add( newCollaborationPlanButton );
        newCollaborationPlanButton.setEnabled( selectedProductionTemplate != null );
    }

    private void startNewCollaborationPlan() {
        planCommunityManager.createNewCommunityFor( selectedProductionTemplate, getUser(), getCommunityService() );
    }

    public void updateContent( AjaxRequestTarget target ) {
        addCollaborationPlansTable();
        target.add( collaborationPlansTable );
    }

    private void addCollaborationPlansTable() {
        collaborationPlansTable = new AllCollaborationPlansTable(
                "allCollaborationPlansTable",
                new PropertyModel<List<CollaborationPlanWrapper>>( this, "collaborationPlanWrappers" ) );
        addOrReplace( collaborationPlansTable );
    }

    public List<CollaborationPlanWrapper> getCollaborationPlanWrappers() {
        List<CollaborationPlanWrapper> wrappers = new ArrayList<CollaborationPlanWrapper>();
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            if ( !planCommunity.isDomainCommunity()
                    && ( !planCommunity.isClosed() || isCollaborationPlanner( getUser(), planCommunity ) ) ) {
                wrappers.add( new CollaborationPlanWrapper( planCommunity ) );
            }
        }
        Collections.sort( wrappers );
        return wrappers;
    }

    private boolean isCollaborationPlanner( ChannelsUser user, PlanCommunity planCommunity ) {
        return user.isCommunityPlanner( planCommunity.getUri() );
    }

    public class CollaborationPlanWrapper implements Identifiable, Comparable<CollaborationPlanWrapper> {

        private PlanCommunity collaborationPlan;

        public CollaborationPlanWrapper( PlanCommunity CollaborationPlan ) {
            this.collaborationPlan = CollaborationPlan;
        }

        public PlanCommunity getCollaborationPlan() {
            return collaborationPlan;
        }

        public Place getLocale() {
            return collaborationPlan.getCommunityLocale() == null
                    ? getTemplate().getLocale()
                    : collaborationPlan.getCommunityLocale();
        }

        public Plan getTemplate() {
            return getPlanManager().getPlan( collaborationPlan.getPlanUri(), collaborationPlan.getPlanVersion() );
        }

        public String getUserParticipates() {
            return userParticipationManager.getUserParticipations(
                    getUser(),
                    getCommunityService( collaborationPlan ) ).isEmpty()
                    ? "No"
                    : "Yes";
        }

        public String getCollaborationPlanUrl() {
            return makeCommunityPageUrl( collaborationPlan );
        }

        public String getStatus() {
            return collaborationPlan.isClosed()
                    ? "Closed"
                    : "Open";
        }

        @Override
        public String getClassLabel() {
            return getClass().getSimpleName();
        }

        @Override
        public long getId() {
            return collaborationPlan.getId();
        }

        @Override
        public String getDescription() {
            return collaborationPlan.getDescription();
        }

        @Override
        public String getTypeName() {
            return collaborationPlan.getTypeName();
        }

        @Override
        public boolean isModifiableInProduction() {
            return collaborationPlan.isModifiableInProduction();
        }

        @Override
        public String getName() {
            return collaborationPlan.getName();
        }

        @Override
        public int compareTo( CollaborationPlanWrapper other ) {
            return getName().compareTo( other.getName() );
        }
    }

    public class AllCollaborationPlansTable extends AbstractTablePanel<CollaborationPlanWrapper> {
        private IModel<List<CollaborationPlanWrapper>> collaborationPlansModel;

        public AllCollaborationPlansTable( String s, IModel<List<CollaborationPlanWrapper>> collaborationPlansModel ) {
            super( s );
            this.collaborationPlansModel = collaborationPlansModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // Columns
            columns.add( makeColumn( "Name", "name", null, EMPTY, "description" ) );
            columns.add( makeColumn( "Locale", "locale.name", null, EMPTY, "locale.description" ) );
            columns.add( makeColumn( "Collaboration template", "template.name", null, EMPTY, "template.description" ) );
            columns.add( makeColumn( "Template URI", "template.versionUri", null, EMPTY, "template.versionUri" ) );
            columns.add( makeColumn( "I participate", "userParticipates", EMPTY ) );
            columns.add( makeColumn( "Status", "status", EMPTY ) );
            columns.add( makeExternalLinkColumn(
                    "",
                    "collaborationPlanUrl",
                    null,
                    "Go",
                    false   // stay on same page
            ) );
            // Provider and table
            add( new AjaxFallbackDefaultDataTable( "allCollaborationPlans",
                    columns,
                    new SortableBeanProvider<CollaborationPlanWrapper>( collaborationPlansModel.getObject(),
                            "name" ),
                    MAX_ROWS ) );
        }
    }
}
