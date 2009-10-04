package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdateScenarioObject;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Risk;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Risk mitigations panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 11, 2009
 * Time: 6:24:53 AM
 */
public class MitigationsPanel extends AbstractCommandablePanel {

    public MitigationsPanel(
            String id,
            IModel<? extends Identifiable> model,
            Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        List<MitigationWrapper> wrappers = getWrappedMitigations();
        ListView<MitigationWrapper> mitigationList = new ListView<MitigationWrapper>(
                "mitigation",
                wrappers
        ) {
            protected void populateItem( ListItem<MitigationWrapper> item ) {
                item.setOutputMarkupId( true );
                item.add( new MitigatedRiskPanel(
                        "mitigatedRisk",
                        item ) );
                addConfirmedCell( item );
            }
        };
        add( mitigationList );
    }

    private void addConfirmedCell( ListItem<MitigationWrapper> item ) {
        final MitigationWrapper wrapper = item.getModelObject();
        final CheckBox confirmedCheckBox = new CheckBox(
                "confirmed",
                new PropertyModel<Boolean>( wrapper, "confirmed" ) );
        makeVisible( confirmedCheckBox, wrapper.isConfirmed() );
        confirmedCheckBox.setEnabled( isLockedByUser( getPart() ) );
        item.addOrReplace( confirmedCheckBox );
        confirmedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target,
                        new Change( Change.Type.Updated, getPart(), "mitigations"
                        ) );
            }
        } );
    }

    private List<MitigationWrapper> getWrappedMitigations() {
        List<MitigationWrapper> wrappers = new ArrayList<MitigationWrapper>();
        List<Risk> mitigations = getPart().getMitigations();
        // Mitigated risks
        for ( Risk risk : mitigations ) {
            wrappers.add( new MitigationWrapper( risk, true ) );
        }
        MitigationWrapper creationWrapper = new MitigationWrapper( null, false );
        creationWrapper.setMarkedForCreation( true );
        wrappers.add( creationWrapper );
        return wrappers;
    }

    /**
     * Get edited part.
     *
     * @return a part
     */
    public Part getPart() {
        return (Part) getModel().getObject();
    }

    /**
     * Mitigation wrapper.
     */
    public class MitigationWrapper implements Serializable {
        /**
         * Mitigated risk. Can be null.
         */
        private Risk risk;
        /**
         * Whether mitigation is confirmed.
         */
        private boolean confirmed;
        private boolean markedForCreation;

        public MitigationWrapper( Risk risk, boolean confirmed ) {
            this.risk = risk;
            this.confirmed = confirmed;
            markedForCreation = false;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public void setMarkedForCreation( boolean markedForCreation ) {
            this.markedForCreation = markedForCreation;
        }

        public Risk getRisk() {
            return risk;
        }

        public void setRisk( Risk risk ) {
            this.risk = risk;
            setConfirmed( true );
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public void setConfirmed( boolean confirmed ) {
            this.confirmed = confirmed;
            assert risk != null;
            if ( confirmed ) {
                if ( !getPart().getMitigations().contains( risk ) ) {
                    doCommand( new UpdateScenarioObject(
                            getPart(),
                            "mitigations",
                            risk,
                            UpdateObject.Action.Add
                    ) );
                }
            } else {
                if ( getPart().getMitigations().contains( risk ) ) {
                    doCommand( new UpdateScenarioObject(
                            getPart(),
                            "mitigations",
                            risk,
                            UpdateObject.Action.Remove
                    ) );
                }
            }
        }

        public boolean canBeConfirmed() {
            return risk != null;
        }
    }

    /**
     * Panel showing mitigated risk as label or a choice of risks to be mitigated.
     */
    public class MitigatedRiskPanel extends Panel {
        /**
         * Item in list view this panel is a component of.
         */
        private ListItem<MitigationWrapper> item;

        public MitigatedRiskPanel( String id, ListItem<MitigationWrapper> item ) {
            super( id );
            this.item = item;
            init();
        }

        private void init() {
            DropDownChoice<Risk> riskChoice = new DropDownChoice<Risk>(
                    "riskChoice",
                    new PropertyModel<Risk>( getWrapper(), "risk" ),
                    getCandidateMitigations(),
                    new IChoiceRenderer<Risk>() {
                        public Object getDisplayValue( Risk risk ) {
                            return risk.toString();
                        }

                        public String getIdValue( Risk risk, int index ) {
                            return Integer.toString( index );
                        }
                    }
            );
            riskChoice.add(
                    new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            update( target,
                                    new Change( Change.Type.Updated, getPart(), "mitigations"
                                    ) );
                        }
                    } );
            add( riskChoice );
            riskChoice.setVisible( getWrapper().isMarkedForCreation()
                    && !getCandidateMitigations().isEmpty() && isLockedByUser( getPart() ) );
            Risk risk = getWrapper().getRisk();
            Label riskLabel = new Label(
                    "riskLabel",
                    new Model<String>( risk != null ? risk.toString() : "" ) );
            add( riskLabel );
            riskLabel.setVisible( !getWrapper().isMarkedForCreation() );
        }

        private List<Risk> getCandidateMitigations() {
            List<Risk> candidates = new ArrayList<Risk>();
            List<Risk> mitigations = getPart().getMitigations();
            for ( Risk risk : getPart().getScenario().getRisks() ) {
                if ( !mitigations.contains( risk ) ) candidates.add( risk );
            }
            return candidates;
        }

        private MitigationWrapper getWrapper() {
            return item.getModelObject();
        }
    }

}
