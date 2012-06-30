package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.AbstractFlowData;
import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.api.procedures.ElementOfInformationData;
import com.mindalliance.channels.api.procedures.TimeDelayData;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.List;

/**
 * Commitment data panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/26/12
 * Time: 4:42 PM
 */
public class CommitmentDataPanel extends AbstractDataPanel {

    private final AbstractFlowData flowData;
    private final boolean received;

    public CommitmentDataPanel( String id, AbstractFlowData flowData, boolean received, ProtocolsFinder finder ) {
        super( id, finder );
        this.flowData = flowData;
        this.received = received;
        init();
    }

    private void init() {
        addHeader();
        addFailureImpact();
        addContacts();
        addMaxDelay();
        addOnFailure();
        addEois();
        addInstructions();
        addBypassContacts();
    }

    private void addHeader() {
        add( new Label(
                "mode",
                getModeText() ) );
        add( new Label( "information", flowData.getInformation().getName() ) );
    }

    private String getModeText() {
        if ( received ) {
            return flowData.isNotification()
                    ? "Expect to be notified of"
                    : "You can ask for";
        } else {
            return flowData.isNotification()
                    ? "To notify of"
                    : "When asked, provide";
        }
    }

    private void addContacts() {
        List<ContactData> contacts = flowData.getContacts();
        WebMarkupContainer contactsContainer = new WebMarkupContainer( "contactsContainer" );
        contactsContainer.setVisible( !contacts.isEmpty() );
        add( contactsContainer );
        Label anyAllLabel = new Label(
                "anyAll",
                getContactsText()
        );
        contactsContainer.add( anyAllLabel );
        ListView<ContactData> contactsListView = new ListView<ContactData>(
                "contacts",
                contacts
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                ContactData contactData = item.getModelObject();
                item.add( new ContactLinkPanel(
                        "contact",
                        contactData,
                        getWorkChannels( contactData ),
                        getPersonalChannels( contactData ),
                        getFinder() ) );
            }
        };
        contactsContainer.add( contactsListView );
    }

    private String getContactsText() {
        if ( received ) {
            return "Your sources are";
        } else {
            if ( flowData.isNotification() )
                return flowData.getContactAll()
                        ? "Contact all of"
                        : "Contact any of";
            else
                return "To";
        }
    }

    private void addMaxDelay() {
        TimeDelayData timeDelay = flowData.getMaxDelay();
        WebMarkupContainer maxDelayContainer = new WebMarkupContainer( "maxDelayContainer" );
        add( maxDelayContainer );
        Label whenLabel = new Label(
                "when",
                received
                    ? flowData.isNotification()
                         ? "Expect to receive notification"
                         : "Expect an answer"
                    : flowData.isNotification()
                        ? "Notify"
                        : "Answer");
        maxDelayContainer.add( whenLabel);
        Label delayLabel = new Label(
                "maxDelay", makeDelayLabel( timeDelay ));
        delayLabel.setVisible( timeDelay != null );
        maxDelayContainer.add( delayLabel );
    }

    private String makeDelayLabel( TimeDelayData timeDelay ) {
        if ( timeDelay == null ) return "";
        else {
            String prefix = timeDelay.isImmediate() ? "" : "within ";
            return prefix + timeDelay.getLabel();
        }
    }

    private void addOnFailure() {
        Label failureLabel = new Label(
                "onFailure",
                flowData.getTaskFailed()
                        ? "BUT ONLY IF you failed to execute the task"
                        : ""
        );
        failureLabel.setVisible( flowData.getTaskFailed() &&  !( received && flowData.isNotification() ) );
        add( failureLabel );
    }


    private void addEois() {
        List<ElementOfInformationData> eois = flowData.getInformation().getEOIs();
        WebMarkupContainer eoisContainer = new WebMarkupContainer( "eoisContainer" );
        add( eoisContainer );
        eoisContainer.setVisible( !eois.isEmpty() );
        Label eoisLabel = new Label( "eoisLabel" , getEoisLabel() );
        eoisContainer.add( eoisLabel );
        eoisContainer.add( new EoisPanel( "eois", eois, getFinder() ) );
    }

    private String getEoisLabel() {
        if ( received ) {
            return flowData.isNotification()
                    ? "Expect these elements"
                    : "Request these elements";
        } else {
            return flowData.isNotification()
                    ? "Include these elements"
                    : "Answer with these elements";
        }
    }

    private void addInstructions() {
        WebMarkupContainer instructionContainer = new WebMarkupContainer( "allInstructions" );
        add( instructionContainer );
        boolean contextCommunicated = flowData.isContextCommunicated();
        String communicableContext = ChannelsUtils.lcFirst( flowData.getCommunicableContext() );
        Label communicatedContextLabel = new Label(
                "communicatedContext",
                contextCommunicated
                        ? ( "Mention " + communicableContext )
                        : ( " Do NOT mention " + communicableContext )
        );
        instructionContainer.add( communicatedContextLabel );
        boolean receiptConfirmation = flowData.getReceiptConfirmationRequested();
        Label receiptLabel = new Label(
                "receiptConfirmation",
                receiptConfirmation
                        ? "Request confirmation of receipt"
                        : ""
        );
        receiptLabel.setVisible( receiptConfirmation );
        instructionContainer.add( receiptLabel );
        String instructionsText = flowData.getInstructions();
        Label instructionsLabel = new Label(
                "instructions",
                instructionsText == null || instructionsText.trim().isEmpty()
                        ? ""
                        : instructionsText );
        instructionsLabel.setVisible( instructionsText != null && !instructionsText.trim().isEmpty() );
        instructionContainer.setVisible( isInstructed() );
        instructionContainer.add( instructionsLabel );
    }

    private boolean isInitiated() {
        return !received && flowData.isNotification() || received && !flowData.isNotification();
    }

    private boolean isInstructed() {
        String instructionsText = flowData.getInstructions();
        boolean canBeInstructed = isInitiated();
        return canBeInstructed && (
                instructionsText != null && !instructionsText.trim().isEmpty()
                        || flowData.getReceiptConfirmationRequested()
                        || flowData.isContextCommunicated() );
    }

    private void addBypassContacts() {
        List<ContactData> contacts = flowData.getBypassContacts();
        WebMarkupContainer contactsContainer = new WebMarkupContainer( "bypassContactsContainer" );
        contactsContainer.setVisible( !contacts.isEmpty() );
        add( contactsContainer );
        Label anyAllLabel = new Label(
                "anyAll",
                "If the above contacts are unreachable, try instead..."
        );
        contactsContainer.add( anyAllLabel );
        ListView<ContactData> contactsListView = new ListView<ContactData>(
                "contacts",
                contacts
        ) {
            @Override
            protected void populateItem( ListItem<ContactData> item ) {
                ContactData contactData = item.getModelObject();
                item.add( new ContactLinkPanel(
                        "contact",
                        contactData,
                        getWorkChannels( contactData ),
                        getPersonalChannels( contactData ),
                        getFinder() ) );
            }
        };
        contactsContainer.add( contactsListView );
    }

    private void addFailureImpact() {
        Level severity = flowData.getFailureSeverity();
        String severityText = flowData.getFailureImpact().toLowerCase();
        WebMarkupContainer impactContainer = new WebMarkupContainer( "failureImpact" );
        impactContainer.add( new AttributeModifier( "class", "failureImpact-small " + severityText) );
        add( impactContainer );
        impactContainer.setVisible( severity.ordinal() > Level.Low.ordinal() );
        Label severityLabel = new Label( "severity", severityText );
        impactContainer.add( severityLabel );
    }

    @SuppressWarnings( "unchecked" )
    private List<ChannelData> getWorkChannels( ContactData contactData ) {
        final List<Long> mediumIds = flowData.getMediumIds();
        return (List<ChannelData>) CollectionUtils.select(
                contactData.getWorkChannels(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return mediumIds.contains( ( (ChannelData) object ).getMediumId() );
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private List<ChannelData> getPersonalChannels( ContactData contactData ) {
        final List<Long> mediumIds = flowData.getMediumIds();
        return (List<ChannelData>) CollectionUtils.select(
                contactData.getPersonalChannels(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return mediumIds.contains( ( (ChannelData) object ).getMediumId() );
                    }
                }
        );
    }

}
