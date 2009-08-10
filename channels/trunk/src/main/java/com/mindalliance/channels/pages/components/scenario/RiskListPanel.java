package com.mindalliance.channels.pages.components.scenario;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.RemoveRisk;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Risk;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.util.Matcher;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Risk list panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 8, 2009
 * Time: 3:02:51 PM
 */
public class RiskListPanel extends AbstractCommandablePanel {
    /**
     * Maximum number of rows in mitigations table before paging.
     */
    private static final int MAX_MITIGATION_ROWS = 5;
    /**
     * Collator.
     */
    private static Collator collator = Collator.getInstance();
    /**
     * Risks container.
     */
    private WebMarkupContainer risksContainer;
    /**
     * More... container.
     */
    private WebMarkupContainer moreContainer;
    /**
     * Risk for which mitigations are shown.
     */
    private RiskWrapper selectedRisk;

    public RiskListPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
        init();
    }

    private void init() {
        risksContainer = new WebMarkupContainer( "risksDiv" );
        risksContainer.setOutputMarkupId( true );
        add( risksContainer );
        risksContainer.add( makeRisksTable() );
        moreContainer = new WebMarkupContainer( "moreDiv" );
        moreContainer.setOutputMarkupId( true );
        add( moreContainer );
        initLabel();
        addDescriptionField();
        moreContainer.add( makeMitigationsTable() );
        makeVisible( moreContainer, false );
    }

    private void initLabel() {
        Label riskLabel = new Label( "riskLabel", new PropertyModel<String>( this, "riskLabel" ) );
        riskLabel.setOutputMarkupId( true );
        moreContainer.addOrReplace( riskLabel );
    }


    private ListView<RiskWrapper> makeRisksTable() {
        List<RiskWrapper> riskWrappers = getWrappedRisks();
        return new ListView<RiskWrapper>( "risk", riskWrappers ) {
            /** {@inheritDoc} */
            protected void populateItem( ListItem<RiskWrapper> item ) {
                addSeverityCell( item );
                addTypeCell( item );
                addOrganizationCell( item );
                addDeleteImage( item );
                addShowMoreCell( item );
            }
        };
    }

    private void addSeverityCell( final ListItem<RiskWrapper> item ) {
        final RiskWrapper wrapper = item.getModelObject();
        final List<Issue.Level> candidateLevels = getCandidateLevels();
        DropDownChoice<Issue.Level> severityChoices = new DropDownChoice<Issue.Level>(
                "severity",
                new PropertyModel<Issue.Level>( wrapper, "severity" ),
                candidateLevels,
                new IChoiceRenderer<Issue.Level>() {
                    public Object getDisplayValue( Issue.Level level ) {
                        return level == null ? "Select a severity" : level.getLabel();
                    }

                    public String getIdValue( Issue.Level level, int index ) {
                        return Integer.toString( index );
                    }
                } );
        severityChoices.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        if ( !wrapper.isUndergoingCreation() ) {
                            update( target, new Change( Change.Type.Updated, getScenario(), "risks" ) );
                        }
                    }
                } );

        item.add( severityChoices );
    }

    private void addTypeCell( final ListItem<RiskWrapper> item ) {
        final RiskWrapper wrapper = item.getModelObject();
        final List<Risk.Type> candidateTypes = getCandidateTypes();
        DropDownChoice<Risk.Type> typeChoices = new DropDownChoice<Risk.Type>(
                "type",
                new PropertyModel<Risk.Type>( wrapper, "type" ),
                candidateTypes,
                new IChoiceRenderer<Risk.Type>() {
                    public Object getDisplayValue( Risk.Type type ) {
                        return type == null
                                ? "Select a type"
                                : type.toString();
                    }

                    public String getIdValue( Risk.Type type, int index ) {
                        return Integer.toString( index );
                    }
                } );
        typeChoices.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        if ( !wrapper.isUndergoingCreation() ) {
                            update( target, new Change( Change.Type.Updated, getScenario(), "risks" ) );
                        }

                    }
                } );
        item.add( typeChoices );
    }

    private void addOrganizationCell( final ListItem<RiskWrapper> item ) {
        item.setOutputMarkupId( true );
        final RiskWrapper wrapper = item.getModelObject();
        final List<String> choices = getQueryService().findAllNames( Organization.class );
        TextField<String> orgNameField = new AutoCompleteTextField<String>(
                "organization",
                new PropertyModel<String>( wrapper, "organizationName" ) ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();

            }
        };
        orgNameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                if ( !wrapper.isUndergoingCreation() ) {
                    update( target, new Change( Change.Type.Updated, getScenario(), "risks" ) );
                }
            }
        } );
        item.add( orgNameField );
    }

    private void addDeleteImage( ListItem<RiskWrapper> item ) {
        final RiskWrapper wrapper = item.getModelObject();
        AjaxFallbackLink deleteLink = new AjaxFallbackLink( "delete" ) {
            public void onClick( AjaxRequestTarget target ) {
                wrapper.deleteRisk();
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getScenario(),
                                "risks"
                        ) );
            }
        };
        makeVisible( deleteLink, wrapper.isComplete() );
        item.addOrReplace( deleteLink );
    }

    private void addShowMoreCell( ListItem<RiskWrapper> item ) {
        final RiskWrapper wrapper = item.getModel().getObject();
        AjaxFallbackLink<String> moreLink = new AjaxFallbackLink<String>(
                "more-link",
                new Model<String>( "More..." ) ) {
            public void onClick( AjaxRequestTarget target ) {
                selectedRisk = wrapper;
                moreContainer.addOrReplace( makeMitigationsTable() );
                // target.addComponent( riskLabel );
                // target.addComponent( descriptionField );
                makeVisible( moreContainer, selectedRisk != null );
                target.addComponent( moreContainer );
            }
        };
        makeVisible( moreLink, !wrapper.isMarkedForCreation() );
        item.add( moreLink );
    }

    private void addDescriptionField() {
        TextArea<String> descriptionField = new TextArea<String>(
                "description",
                new PropertyModel<String>( this, "description" ) );
        descriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
/*
                if ( selectedRisk != null && !selectedRisk.isMarkedForCreation() ) {
                    update( target, new Change( Change.Type.Updated, getScenario(), "risks" ) );
                }
*/
            }
        } );
        descriptionField.setOutputMarkupId( true );
        moreContainer.add( descriptionField );
    }

    private Component makeMitigationsTable() {
        if ( selectedRisk == null ) {
            return new Label( "mitigations", new Model<String>( "No risk selected" ) );
        } else {
            return new MitigationsTable(
                    "mitigations",
                    new Model<Scenario>( getScenario() ),
                    MAX_MITIGATION_ROWS,
                    selectedRisk.getRisk()
            );
        }
    }

    private List<Issue.Level> getCandidateLevels() {
        return Arrays.asList( Issue.Level.values() );
    }

    private List<Risk.Type> getCandidateTypes() {
        List<Risk.Type> types = Arrays.asList( Risk.Type.values() );
        Collections.sort( types, new Comparator<Risk.Type>() {
            public int compare( Risk.Type r1, Risk.Type r2 ) {
                return collator.compare( r1.toString(), r2.toString() );
            }
        } );
        return types;
    }

    private List<RiskWrapper> getWrappedRisks() {
        List<RiskWrapper> wrappers = new ArrayList<RiskWrapper>();
        for ( Risk risk : getScenario().getRisks() ) {
            wrappers.add( new RiskWrapper( risk, true ) );
        }
        Collections.sort( wrappers, new Comparator<RiskWrapper>() {
            public int compare( RiskWrapper r1, RiskWrapper r2 ) {
                return collator.compare( r1.getSeverity().getLabel(), r2.getSeverity().getLabel() );
            }
        } );
        RiskWrapper creationWrapper = new RiskWrapper( new Risk(), false );
        creationWrapper.setMarkedForCreation( true );
        wrappers.add( creationWrapper );
        return wrappers;
    }

    /**
     * Get a label for the selected risk, if any.
     *
     * @return a string
     */
    public String getRiskLabel() {
        return selectedRisk != null
                ? selectedRisk.getRisk().getLabel()
                : "no risk is selected";
    }

    /**
     * Get selected risk's description.
     *
     * @return a string
     */
    public String getDescription() {
        return selectedRisk != null
                ? selectedRisk.getDescription()
                : "";
    }

    /**
     * Set selected risk's description.
     *
     * @param value a string
     */
    public void setDescription( String value ) {
        if ( selectedRisk != null ) {
            selectedRisk.setDescription( value != null ? value : "" );
        }
    }

    private Scenario getScenario() {
        return (Scenario) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        risksContainer.addOrReplace( makeRisksTable() );
        initLabel();
        moreContainer.addOrReplace( makeMitigationsTable() );
        makeVisible( moreContainer, selectedRisk != null );
        target.addComponent( risksContainer );
        target.addComponent( moreContainer );
        super.updateWith( target, change );
    }

    public class RiskWrapper implements Serializable {
        /**
         * Risk.
         */
        private Risk risk;
        /**
         * Whether to be created.
         */
        private boolean markedForCreation;
        /**
         * Whether confirmed.
         */
        private boolean confirmed;

        protected RiskWrapper( Risk risk, boolean confirmed ) {
            this.risk = risk;
            markedForCreation = false;
            this.confirmed = confirmed;
        }

        public Risk getRisk() {
            return risk;
        }

        public void setRisk( Risk risk ) {
            this.risk = risk;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public void setMarkedForCreation( boolean markedForCreation ) {
            this.markedForCreation = markedForCreation;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        /**
         * Can risk be deleted?
         *
         * @return a boolean
         */
        public boolean isComplete() {
            return risk.getType() != null && risk.getSeverity() != null && risk.getOrganization() != null;
        }

        public void deleteRisk() {
            selectedRisk = null;
            if ( getScenario().getRisks().contains( risk ) ) {
                doCommand( new RemoveRisk( getScenario(), risk ) );
            }
        }

        public void addIfComplete() {
            assert markedForCreation;
            if ( risk.getType() != null && risk.getSeverity() != null && risk.getOrganization() != null ) {
                if ( !getScenario().getRisks().contains( risk ) ) {
                    doCommand( new UpdatePlanObject(
                            getScenario(),
                            "risks",
                            risk,
                            UpdateObject.Action.Add
                    ) );
                }
            }
        }

        /*       public void setConfirmed( boolean confirmed ) {
                    this.confirmed = confirmed;
                    if ( confirmed ) {
                        assert markedForCreation;
                        if ( !getScenario().getRisks().contains( risk ) ) {
                            doCommand( new UpdatePlanObject(
                                    getScenario(),
                                    "risks",
                                    risk,
                                    UpdateObject.Action.Add
                            ) );
                        }
                    } else if ( !markedForCreation ) {
                        selectedRisk = null;
                        if ( getScenario().getRisks().contains( risk ) ) {
                            doCommand( new UpdatePlanObject(
                                    getScenario(),
                                    "risks",
                                    risk,
                                    UpdateObject.Action.Remove
                            ) );
                        }
                    }
                }
        */
        public Issue.Level getSeverity() {
            return risk.getSeverity();
        }

        public void setSeverity( Issue.Level value ) {
            if ( markedForCreation ) {
                risk.setSeverity( value );
                addIfComplete();
            } else {
                if ( value != risk.getSeverity() ) {
                    int index = getScenario().getRisks().indexOf( risk );
                    if ( index >= 0 ) {
                        doCommand( new UpdatePlanObject(
                                getScenario(),
                                "risks[" + index + "].severity",
                                value,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
            }
        }

        public Risk.Type getType() {
            return risk.getType();
        }

        public void setType( Risk.Type value ) {
            if ( markedForCreation ) {
                risk.setType( value );
                addIfComplete();
            } else {
                if ( value != risk.getType() ) {
                    int index = getScenario().getRisks().indexOf( risk );
                    if ( index >= 0 ) {
                        doCommand( new UpdatePlanObject(
                                getScenario(),
                                "risks[" + index + "].type",
                                value,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
            }
        }

        public String getOrganizationName() {
            Organization org = risk.getOrganization();
            return org != null ? org.getName() : "";
        }

        public void setOrganizationName( String name ) {
            String oldName = getOrganizationName();
            if ( name != null && !isSame( name, oldName ) ) {
                Organization org = getQueryService().findOrCreate( Organization.class, name );
                if ( markedForCreation ) {
                    risk.setOrganization( org );
                    addIfComplete();
                } else {
                    int index = getScenario().getRisks().indexOf( risk );
                    if ( index >= 0 ) {
                        doCommand( new UpdatePlanObject(
                                getScenario(),
                                "risks[" + index + "].organization",
                                org,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
                getCommander().cleanup( Organization.class, oldName );
            }
        }

        public String getDescription() {
            return risk.getDescription();
        }

        public void setDescription( String value ) {
            String oldValue = risk.getDescription();
            if ( !oldValue.equals( value ) ) {
                if ( markedForCreation ) {
                    risk.setDescription( value );
                } else {
                    int index = getScenario().getRisks().indexOf( risk );
                    if ( index >= 0 ) {
                        doCommand( new UpdatePlanObject(
                                getScenario(),
                                "risks[" + index + "].description",
                                value,
                                UpdateObject.Action.Set
                        ) );
                    }
                }
            }
        }

        private boolean isUndergoingCreation() {
            return isMarkedForCreation() && !isComplete();
        }
    }

    /**
     * Mitigations table panel.
     */
    public class MitigationsTable extends AbstractTablePanel<Risk> {
        /**
         * The risk for which mitigations are shown.
         */
        private Risk risk;
        /**
         * Scenario.
         */
        private Scenario scenario;

        public MitigationsTable(
                String id,
                IModel<? extends Identifiable> scenarioModel,
                int pageSize,
                Risk risk ) {
            super( id, scenarioModel, pageSize, null );
            scenario = (Scenario) scenarioModel.getObject();
            this.risk = risk;
            initTable();
        }

        @SuppressWarnings( "unchecked" )
        private void initTable() {
            List<Mitigator> mitigators = new ArrayList<Mitigator>();
            for ( Part part : getQueryService().findMitigations( scenario, risk ) ) {
                mitigators.add( new Mitigator( part, risk ) );
            }
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            columns.add( makeColumn( "Mitigation", "kind", EMPTY ) );
            columns.add( makeLinkColumn( "Task", "part", "part.task", EMPTY ) );
            columns.add( makeLinkColumn( "Actor", "part.actor", "part.actor.name", EMPTY ) );
            columns.add( makeLinkColumn( "Role", "part.role", "part.role.name", EMPTY ) );
            columns.add( makeLinkColumn( "Organization", "part.organization", "part.organization.name", EMPTY ) );
            add( new AjaxFallbackDefaultDataTable(
                    "mitigations",
                    columns,
                    new SortableBeanProvider<Mitigator>( mitigators, "part.task" ),
                    getPageSize() ) );
        }
    }

    /**
     * Part a s risk mitigator.
     */
    public class Mitigator implements Serializable {
        /**
         * Part.
         */
        private Part part;
        /**
         * Risk.
         */
        private Risk risk;

        public Mitigator( Part part, Risk risk ) {
            this.part = part;
            this.risk = risk;
        }

        public Part getPart() {
            return part;
        }

        public Risk getRisk() {
            return risk;
        }

        /**
         * Get kind of mitigator.
         *
         * @return a string
         */
        public String getKind() {
            if ( part.isTerminatesEvent() ) {
                return "Ends event causing risk";
            } else if ( part.getMitigations().contains( risk ) ) {
                return "Reduces risk";
            } else {
                return "";
            }
        }
    }
}
