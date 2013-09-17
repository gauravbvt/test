package com.mindalliance.channels.pages.components.entities.agreements;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Agreement;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.entities.AbstractFilterableTablePanel;
import com.mindalliance.channels.pages.components.segment.CommitmentsTablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Agreements panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 19, 2009
 * Time: 2:16:21 PM
 */
public class AgreementsPanel extends AbstractCommandablePanel {
    /**
     * Agreements table.
     */
    private AgreementsTable agreementsTable;
    /**
     * Selected agreement.
     */
    private AgreementWrapper selectedAgreement;
    /**
     * Commitments panel.
     */
    private WebMarkupContainer commitmentsContainer;

    public AgreementsPanel( String id, IModel<Organization> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        addAgreementsTable();
        addCommitmentsContainer();
    }

    private void addAgreementsTable() {
        agreementsTable = new AgreementsTable(
                "agreementsTable",
                new PropertyModel<List<AgreementWrapper>>( this, "agreements" ) );
        agreementsTable.setOutputMarkupId( true );
        addOrReplace( agreementsTable );
    }

    /**
     * Get all implied and confirmed agreements by the organization.
     *
     * @return a list of agreement wrappers
     */
    public List<AgreementWrapper> getAgreements() {
        QueryService queryService = getQueryService();
        List<AgreementWrapper> wrappers = new ArrayList<AgreementWrapper>();
        for ( Agreement agreement : getOrganization().getAgreements() ) {
            wrappers.add( new AgreementWrapper( agreement, getOrganization(), true ) );
        }
        for ( Agreement agreement : getQueryService().findAllImpliedAgreementsOf(
                getOrganization(),
                queryService.getAssignments( false ),
                queryService.findAllFlows() ) ) {
            AgreementWrapper wrapper = new AgreementWrapper( agreement, getOrganization(), false );
            if ( !wrappers.contains( wrapper ) ) wrappers.add( wrapper );
        }
        for ( Organization org : queryService.listActualEntities( Organization.class ) ) {
            if ( !org.equals( getOrganization() ) ) {
                for ( Agreement agreement : org.getAgreements() ) {
                    if ( agreement.getBeneficiary().equals( getOrganization() ) ) {
                        wrappers.add( new AgreementWrapper( agreement, org, true ) );
                    }
                }
                for ( Agreement agreement : queryService.findAllImpliedAgreementsOf(
                        org,
                        queryService.getAssignments( false ),
                        queryService.findAllFlows()
                ) ) {
                    if ( agreement.getBeneficiary().equals( getOrganization() ) ) {
                        wrappers.add( new AgreementWrapper( agreement, org, false ) );
                    }
                }
            }
        }
        return wrappers;
    }

    private void addCommitmentsContainer() {
        commitmentsContainer = new WebMarkupContainer( "commitmentsDiv" );
        commitmentsContainer.setOutputMarkupId( true );
        addCommitments();
        makeVisible( commitmentsContainer, selectedAgreement != null );
        addOrReplace( commitmentsContainer );
    }

    private void addCommitments() {
        Label agreementLabel;
        Label coverageQualifierLabel;
        Component commitmentsPanel;
        Component attachmentPanel;
        if ( selectedAgreement == null ) {
            agreementLabel = new Label( "agreement", "" );
            coverageQualifierLabel = new Label( "coverageQualifier", "" );
            commitmentsPanel = new Label( "commitments", "" );
        } else {
            agreementLabel = new Label(
                    "agreement",
                    new Model<String>( summarizeAgreement() ) );
            coverageQualifierLabel = new Label(
                    "coverageQualifier",
                    isImplied( selectedAgreement )
                            ? "that would be covered by an"
                            : "covered by the" );
            commitmentsPanel = new CommitmentsTablePanel(
                    "commitments",
                    new PropertyModel<List<Commitment>>( this, "commitments" ) );

        }
        int index = getSelectedAgreementIndex();
        if ( index < 0 ) {
            attachmentPanel = new Label( "attachments", "" );
        } else {
            attachmentPanel = new AttachmentPanel(
                    "attachments",
                    new Model<Organization>( selectedAgreement.getOrganization() ),
                    "agreements[" + index + "]" );
        }
        agreementLabel.setOutputMarkupId( true );
        coverageQualifierLabel.setOutputMarkupId( true );
        attachmentPanel.setOutputMarkupId( true );
        commitmentsPanel.setOutputMarkupId( true );
        commitmentsContainer.add( agreementLabel );
        commitmentsContainer.add( coverageQualifierLabel );
        commitmentsContainer.add( attachmentPanel );
        commitmentsContainer.add( commitmentsPanel );
    }

    private int getSelectedAgreementIndex() {
        if ( selectedAgreement == null || isImplied( selectedAgreement ) ) {
            return -1;
        } else {
            return selectedAgreement.getOrganization().getAgreements()
                    .indexOf( selectedAgreement.getAgreement() );
        }
    }

    private boolean isImplied( AgreementWrapper agreementWrapper ) {
        return !agreementWrapper.getOrganization().getAgreements()
                .contains( agreementWrapper.getAgreement() );
    }

    private String summarizeAgreement() {
        if ( selectedAgreement == null ) return "";
        StringBuilder sb = new StringBuilder();
        sb.append( "agreement to share \"" );
        sb.append( selectedAgreement.getInformation() );
        sb.append( "\" with " );
        sb.append( selectedAgreement.getBeneficiary().getName() );
        if ( !selectedAgreement.getUsage().isEmpty() ) {
            sb.append( " for \"" );
            sb.append( selectedAgreement.getUsage() );
            sb.append( "\"" );
        }
        return sb.toString();
    }

    public List<Commitment> getCommitments() {
        if ( selectedAgreement == null )
            return new ArrayList<Commitment>();
        else {
            QueryService queryService = getQueryService();
            return queryService.findAllCommitmentsCoveredBy(
                    selectedAgreement.getAgreement(),
                    selectedAgreement.getOrganization(),
                    queryService.getAssignments( false ),
                    queryService.findAllFlows()
            );
        }
    }

    public Organization getOrganization() {
        return (Organization) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof AgreementWrapper ) {
            AgreementWrapper wrapper = (AgreementWrapper) object;
            if ( action.equals( "select" ) ) {
                if ( selectedAgreement == null || !selectedAgreement.equals( object ) ) {
                    selectedAgreement = (AgreementWrapper) object;
                    addCommitmentsContainer();
                    target.add( commitmentsContainer );
                }
            } else if ( action.equals( "confirmed" ) ) {
                addAgreementsTable();
                target.add( agreementsTable );
                addCommitmentsContainer();
                target.add( commitmentsContainer );
                update( target,
                        new Change( Change.Type.Updated, wrapper.getOrganization(), "agreements" ) );
            }
        }
    }


    /**
     * Agreement wrapper.
     */
    public class AgreementWrapper implements Serializable {
        /**
         * Agreement.
         */
        private Agreement agreement;
        /**
         * Organization agreeing.
         */
        private Organization organization;
        /**
         * Whether confirmed vs implied.
         */
        private boolean confirmed;

        public AgreementWrapper( Agreement agreement, Organization organization, boolean confirmed ) {
            this.agreement = agreement;
            this.organization = organization;
            this.confirmed = confirmed;
        }

        public Agreement getAgreement() {
            return agreement;
        }

        public Organization getOrganization() {
            return organization;
        }

        public boolean isConfirmable() {
            return isLockedByUserIfNeeded( getOrganization() );
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public void setConfirmed( boolean confirmed ) {
            if ( confirmed ) {
                doCommand( new UpdatePlanObject( getUser().getUsername(), organization,
                        "agreements",
                        agreement,
                        UpdateObject.Action.AddUnique ) );
            } else {
                doCommand( new UpdatePlanObject( getUser().getUsername(), organization,
                        "agreements",
                        agreement,
                        UpdateObject.Action.Remove ) );
            }
            this.confirmed = confirmed;
        }

        public Organization getBeneficiary() {
            return agreement.getBeneficiary();
        }

        public String getInformation() {
            return agreement.getInformation();
        }

        public String getEois() {
            return agreement.getEOIsString();
        }

        public String getUsage() {
            return agreement.getUsage();
        }

        public String getMouStatus() {
            if ( isConfirmed() )
                return agreement.hasMOU() ? "Yes" : "No";
            else
                return "-";
        }

        public String getConfirmedIndex() {
            if ( isConfirmed() )
                return ( organization.getAgreements().indexOf( agreement ) + 1 ) + "";
            else
                return "-";
        }

        /**
         * {@inheritDoc}
         */
        public boolean equals( Object object ) {
            return object instanceof AgreementWrapper
                    && ( (AgreementWrapper) object ).getAgreement().equals( agreement );
        }

        /**
         * {@inheritDoc}
         */
        public int hashCode() {
            return agreement.hashCode();
        }

        public long getId() {
            return organization.getId();
        }

        public String getDescription() {
            return agreement.toString();
        }

        public String getName() {
            return agreement.getInformation();
        }

        public String getTypeName() {
            return "Agreement";
        }
    }

    /**
     * Agreements table.
     */
    private class AgreementsTable extends AbstractFilterableTablePanel {
        /**
         * Agreements model.
         */
        private IModel<List<AgreementWrapper>> agreementsModel;

        public AgreementsTable( String id, IModel<List<AgreementWrapper>> model ) {
            super( id );
            agreementsModel = model;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // Columns
            columns.add( makeCheckBoxColumn(
                    "Confirmed",
                    "confirmed",
                    "confirmable",
                    AgreementsPanel.this
            ) );
            columns.add( makeColumn(
                    "#",
                    "confirmedIndex",
                    ""
            ) );
            columns.add( makeFilterableLinkColumn(
                    "Agreeing",
                    "organization",
                    "organization.name",
                    EMPTY,
                    AgreementsTable.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Beneficiary",
                    "beneficiary",
                    "beneficiary.name",
                    EMPTY,
                    AgreementsTable.this ) );
            columns.add( makeColumn(
                    "Information",
                    "information",
                    ""
            ) );
            columns.add( makeColumn(
                    "Elements",
                    "eois",
                    ""
            ) );
            columns.add( makeColumn(
                    "For use",
                    "usage",
                    ""
            ) );
            columns.add( makeColumn(
                    "Has MOU",
                    "mouStatus",
                    ""
            ) );
            columns.add( makeActionLinkColumn(
                    "",
                    "more",
                    "select",
                    AgreementsPanel.this
            ) );
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "agreements",
                    columns,
                    new SortableBeanProvider<AgreementWrapper>(
                            getFilteredAgreements(),
                            "beneficiary.name" ),
                    getPageSize() ) );
        }

        /**
         * Find all agreements implied or confirmed by this organization.
         *
         * @return a list of agreement wrappers.
         */
        @SuppressWarnings( "unchecked" )
        public List<AgreementWrapper> getFilteredAgreements() {
            return (List<AgreementWrapper>) CollectionUtils.select(
                    agreementsModel.getObject(),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            return !isFilteredOut( obj );
                        }
                    }
            );
        }


        /**
         * {@inheritDoc}
         */
        protected void resetTable( AjaxRequestTarget target ) {
            initialize();
            target.add( this );
        }

    }

}
