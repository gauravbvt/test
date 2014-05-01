package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.ElementOfInformationData;
import com.mindalliance.channels.api.directory.ContactData;
import com.mindalliance.channels.api.procedures.AbstractFlowData;
import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.api.procedures.CycleData;
import com.mindalliance.channels.api.procedures.NotificationData;
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
        addProducerTask();
        addImpact();
        addFailureImpact();
        addContacts();
        addPreferredMedia();
        addMaxDelay();
        addOnFailure();
        addEois();
        addCycle();
        addInstructions();
        addDocumentation();
    }

    private void addProducerTask() {
        WebMarkupContainer producerTaskContainer = new WebMarkupContainer( "producerTaskContainer" );
        add( producerTaskContainer );
        boolean visible = flowData.isRequest() && !flowData.isInitiating();
        if ( visible ) {
            producerTaskContainer.add(
                    new ChecklistDataLinkPanel(
                            "producerTaskLink",
                            flowData.getAssignmentData(),
                            getFinder() )
            );
        } else {
            producerTaskContainer.add( new Label( "producerTaskLink", "" ) );
        }
        producerTaskContainer.setVisible( visible );
    }

    private void addImpact() {
        WebMarkupContainer impactContainer = new WebMarkupContainer( "impactContainer" );
        add( impactContainer );
        String impact = getCommitmentImpact();
        Label impactLabel = new Label( "impact", impact );
        impactContainer.add( impactLabel );
        impactContainer.setVisible( !impact.isEmpty() );
    }

    private String getModeText() {
        if ( received ) {
            return flowData.isNotification()
                    ? "When notified of"
                    : "When asking for";
        } else {
            return flowData.isNotification()
                    ? "When notifying of"
                    : "When answering with";
        }
    }


    private String getCommitmentImpact() {
        if ( !received && flowData.isNotification() ) {
            NotificationData notificationData = (NotificationData) flowData;
            StringBuilder sb = new StringBuilder();
            if ( notificationData.getConsumingTask() != null ) {
                String impactOnConsuming = notificationData.getImpactOnConsumingTask();
                if ( impactOnConsuming != null ) {
                    sb.append( "It will " );
                    if ( impactOnConsuming.equalsIgnoreCase( "triggers" ) )
                        sb.append( "trigger" );
                    else if ( impactOnConsuming.equalsIgnoreCase( "terminates" ) )
                        sb.append( "terminate" );
                    else if ( impactOnConsuming.equalsIgnoreCase( "critical" ) )
                        sb.append( "make possible the" );
                    else sb.append( "help the" );
                    sb.append( " execution of task \"" );
                    sb.append( notificationData.getConsumingTask().getLabel() );
                    sb.append( "\"" );
                }
            }
            return sb.toString();
        } else {
            return "";
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
                        getFinder() ) );
            }
        };
        contactsContainer.add( contactsListView );
    }

    private String getContactsText() {
        List<ContactData> contacts = flowData.getContacts();
        if ( received ) {
            return contacts.size() == 1 ? "My source is" : "My sources are";
        } else {
            if ( flowData.isNotification() ) {
                if ( contacts.size() == 1 ) {
                    return "Contact";
                } else {
                    return flowData.getContactAll()
                            ? "Contact all of"
                            : "Contact one of";
                }
            } else {
                return "When asked by";
            }
        }
    }

    private void addPreferredMedia() {
        List<ChannelData> channelDataList = flowData.getChannelDataList();
        WebMarkupContainer channelsContainer = new WebMarkupContainer( "channelContainer" );
        add( channelsContainer );
        channelsContainer.add( new Label(
                "via",
                channelDataList.size() > 1
                        ? "by (in order of preference)"
                        : "by"
        ) );
        ListView<ChannelData> channelListView = new ListView<ChannelData>( "channel", channelDataList ) {
            @Override
            protected void populateItem( ListItem<ChannelData> item ) {
                ChannelData channelData = item.getModelObject();
                item.add( new Label( "mediumName", channelData.getMedium() ) );
                String formatName = channelData.getFormat();
                Label formatLabel = new Label(
                        "usingFormat",
                        formatName == null ? "" : ( "using " + formatName ) );
                item.add( formatLabel );
            }
        };
        channelsContainer.add( channelListView );
        channelsContainer.setVisible( !channelDataList.isEmpty() );
    }

    private void addMaxDelay() {
        TimeDelayData timeDelay = flowData.getMaxDelay();
        WebMarkupContainer maxDelayContainer = new WebMarkupContainer( "maxDelayContainer" );
        add( maxDelayContainer );
        Label whenLabel = new Label(
                "when",
                received
                        ? flowData.isNotification()
                        ? "I can expect to receive a notification"
                        : "I can expect an answer"
                        : flowData.isNotification()
                        ? "Send notification"
                        : "Answer"
        );
        maxDelayContainer.add( whenLabel );
        Label delayLabel = new Label(
                "maxDelay", makeDelayLabel( timeDelay ) );
        delayLabel.setVisible( timeDelay != null );
        maxDelayContainer.add( delayLabel );
    }

    private String makeDelayLabel( TimeDelayData timeDelay ) {
        if ( timeDelay == null ) return "";
        if ( timeDelay.isImmediate() ) {
            return flowData.isNotification()
                    ? "As soon as the information becomes available"
                    : "Immediately";
        } else {
            return timeDelay.getLabel()
                    + ( flowData.isNotification() ? " of the information becoming available" : "" );
        }
    }

    private void addOnFailure() {
        Label failureLabel = new Label(
                "onFailure",
                flowData.getTaskFailed()
                        ? "BUT ONLY IF I failed to execute the task"
                        : ""
        );
        failureLabel.setVisible( flowData.getTaskFailed() && !( received && flowData.isNotification() ) );
        add( failureLabel );
    }


    private void addEois() {
        List<ElementOfInformationData> eois = flowData.getInformation().getEOIs();
        WebMarkupContainer eoisContainer = new WebMarkupContainer( "eoisContainer" );
        add( eoisContainer );
        eoisContainer.setVisible( !eois.isEmpty() );
        Label eoisLabel = new Label( "eoisLabel", getEoisLabel() );
        eoisContainer.add( eoisLabel );
        eoisContainer.add( new EoisPanel( "eois", eois, getFinder() ) );
    }

    private String getEoisLabel() {
        if ( received ) {
            return flowData.isNotification()
                    ? "I can expect these elements"
                    : "I can request these elements";
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
                        ? ( "Mention the context \"" + communicableContext + "\"" )
                        : ( " Do NOT mention the context \"" + communicableContext + "\"" )
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
                        : instructionsText
        );
        instructionsLabel.setVisible( instructionsText != null && !instructionsText.trim().isEmpty() );
        instructionContainer.setVisible( isInstructed() );
        instructionContainer.add( instructionsLabel );
    }

    private void addDocumentation() {
        DocumentationPanel docPanel = new DocumentationPanel( "documentation", flowData.getDocumentation(), getFinder() );
        docPanel.setVisible( flowData.getDocumentation().hasReportableDocuments() );
        add( docPanel );
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


    private void addFailureImpact() {
        WebMarkupContainer failureImpactContainer = new WebMarkupContainer( "failureImpactContainer" );
        add( failureImpactContainer );
        Level severity = flowData.getFailureSeverity();
        String severityText = flowData.getFailureImpact().toLowerCase();
        WebMarkupContainer failureImpact = new WebMarkupContainer( "failureImpact" );
        failureImpact.add( new AttributeModifier( "class", "failure-impact " + severityText ) );
        failureImpactContainer.add( failureImpact );
        failureImpactContainer.setVisible( !received && ( severity.ordinal() > Level.Low.ordinal() ) );
        Label severityLabel = new Label( "severity", severityText );
        failureImpact.add( severityLabel );
    }

    private void addCycle() {
        CycleData cycleData = flowData.getCycle();
        WebMarkupContainer cycleContainer = new WebMarkupContainer( "cycle" );
        cycleContainer.setVisible( cycleData != null );
        add( cycleContainer );
        cycleContainer.add( cycleData != null
                        && flowData.isInitiating()
                        ? new Label( "cycleLabel", cycleData.getLabel() )
                        : new Label( "cycleLabel", ""  )
        );
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

    @SuppressWarnings("unchecked")
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
