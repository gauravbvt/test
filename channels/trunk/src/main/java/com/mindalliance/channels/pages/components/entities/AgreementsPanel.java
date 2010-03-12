package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Agreement;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.segment.CommitmentsTablePanel;
import com.mindalliance.channels.util.SortableBeanProvider;
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
import java.util.HashSet;
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
    private Agreement selectedAgreement;
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
        addCommitments();
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
        Set<AgreementWrapper> wrappers = new HashSet<AgreementWrapper>();
        for ( Agreement agreement : getOrganization().getAgreements() ) {
            wrappers.add( new AgreementWrapper( agreement, true ) );
        }
        for ( Agreement agreement : getQueryService().findAllImpliedAgreementsOf( getOrganization() ) ) {
            wrappers.add( new AgreementWrapper( agreement, false ) );
        }
        return new ArrayList<AgreementWrapper>( wrappers );
    }

    private void addCommitmentsContainer() {
        commitmentsContainer = new WebMarkupContainer( "commitmentsDiv" );
        commitmentsContainer.setOutputMarkupId( true );
        makeVisible( commitmentsContainer, false );
        add( commitmentsContainer );
    }

    private void addCommitments() {
        Label agreementLabel;
        Label coverageQualifierLabel;
        Component commitmentsPanel;
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
        agreementLabel.setOutputMarkupId( true );
        coverageQualifierLabel.setOutputMarkupId( true );
        commitmentsPanel.setOutputMarkupId( true );
        commitmentsContainer.addOrReplace( agreementLabel );
        commitmentsContainer.addOrReplace( coverageQualifierLabel );
        commitmentsContainer.addOrReplace( commitmentsPanel );
    }

    private boolean isImplied( Agreement agreement ) {
        return !getOrganization().getAgreements().contains( agreement );
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
        else
            return getQueryService().findAllCommitmentsCoveredBy(
                    selectedAgreement,
                    getOrganization() );
    }

    public Organization getOrganization() {
        return (Organization) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof AgreementWrapper ) {
            if ( action.equals( "select" ) ) {
                selectedAgreement = ( (AgreementWrapper) object ).getAgreement();
                makeVisible( commitmentsContainer, true );
                addCommitments();
                target.addComponent( commitmentsContainer );
            } else if ( action.equals( "confirmed" ) ) {
                addAgreementsTable();
                target.addComponent( agreementsTable );
                addCommitments();
                target.addComponent( commitmentsContainer );
                if ( ( (AgreementWrapper) object ).isConfirmed() ) {
                    update( target, new Change( Change.Type.Updated, getOrganization(), "agreements" ) );
                } else {
                    update( target, new Change( Change.Type.Updated, getOrganization(), "agreements" ) );
                }
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
         * Whether confirmed vs implied.
         */
        private boolean confirmed;

        public AgreementWrapper( Agreement agreement, boolean confirmed ) {
            this.agreement = agreement;
            this.confirmed = confirmed;
        }

        public Agreement getAgreement() {
            return agreement;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public void setConfirmed( boolean confirmed ) {
            if ( confirmed ) {
                doCommand( new UpdatePlanObject(
                        getOrganization(),
                        "agreements",
                        agreement,
                        UpdateObject.Action.Add ) );
            } else {
                doCommand( new UpdatePlanObject(
                        getOrganization(),
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
            return getOrganization().getId();
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
                    isLockedByUser( getOrganization() ),
                    AgreementsPanel.this
            ) );
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
            columns.add( makeActionLinkColumn(
                    "",
                    "More...",
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
            target.addComponent( this );
        }

    }

}
