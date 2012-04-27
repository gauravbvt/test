package com.mindalliance.channels.pages.surveys;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.services.RFIForwardService;
import com.mindalliance.channels.social.services.RFIService;
import com.mindalliance.channels.social.services.SurveysDAO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Panel showing an RFI to a user.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/24/12
 * Time: 3:26 PM
 */
public class RFIPanel extends AbstractUpdatablePanel {

    @SpringBean
    private SurveysDAO surveysDAO;

    @SpringBean
    private RFIService rfiService;

    @SpringBean
    private RFIForwardService rfiForwardService;

    WebMarkupContainer rfiContainer;
    /**
     * Modal dialog window.
     */
    private ModalWindow dialogWindow;
    private AjaxLink<String> declineButton;
    private WebMarkupContainer headerContainer;


    public RFIPanel( String id, IModel<RFI> rfiModel ) {
        super( id, rfiModel );
        init();
    }

    private void init() {
        rfiContainer = new WebMarkupContainer( "rfiContainer" );
        add( rfiContainer );
        addModalDialog();
        addHeader();
        addAnswerSetsPanel();
        addDeclineButton();
        addForwarding();
    }

    private void addModalDialog(
    ) {
        dialogWindow = new ModalWindow( "dialog" );
        dialogWindow.setOutputMarkupId( true );
        dialogWindow.setResizable( true );
        dialogWindow.setContent(
                new Label(
                        dialogWindow.getContentId(),
                        "" ) );
        dialogWindow.setTitle( "" );
        dialogWindow.setCookieName( "rfi-action" );
        dialogWindow.setCloseButtonCallback(
                new ModalWindow.CloseButtonCallback() {
                    public boolean onCloseButtonClicked( AjaxRequestTarget target ) {
                        return true;
                    }
                } );
        dialogWindow.setWindowClosedCallback( new ModalWindow.WindowClosedCallback() {
            public void onClose( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        dialogWindow.setHeightUnit( "px" );
        dialogWindow.setInitialHeight( 0 );
        dialogWindow.setInitialWidth( 0 );
        rfiContainer.addOrReplace( dialogWindow );
    }

    private void showDialog(
            String title,
            int height,
            int width,
            Component contents,
            AjaxRequestTarget target ) {
        dialogWindow.setTitle( title );
        dialogWindow.setInitialHeight( height );
        dialogWindow.setInitialWidth( width );
        dialogWindow.setContent( contents );
        dialogWindow.show( target );
    }

    private void hideDialog( AjaxRequestTarget target ) {
        dialogWindow.close( target );
    }


    private void addHeader() {
        RFI rfi = getRFI();
        headerContainer = new WebMarkupContainer( "header" );
        headerContainer.setOutputMarkupId( true );
        // name
        Label headerLabel = new Label( "name", getHeaderString() );
        headerLabel.setOutputMarkupId( true );
        headerContainer.addOrReplace( headerLabel );
        // sent by
        Label sentByLabel = new Label( "sentBy", getSentBy() );
        headerContainer.add( sentByLabel );
        // deadline
        Label deadlineLabel = new Label( "deadline", getDeadline() );
        headerContainer.add( deadlineLabel );
        // completion
        Label sentCompletion = new Label( "completion", getCompletion() );
        headerContainer.add( sentCompletion );

        rfiContainer.addOrReplace(  headerContainer );
    }

    private String getSentBy() {
        String sentBy = getRFI().getRfiSurvey().getUsername();
        return "Sent by " + getUserFullName( sentBy );
    }

    private String getDeadline() {
        Date deadline = getRFI().getDeadline();
        long delta = deadline.getTime() - new Date( ).getTime();
        String interval = ChannelsUtils.getShortTimeIntervalString( delta );
        return delta < 0 ? "Overdue by " : "Due in " + interval;
    }

    private String getCompletion() {
        RFI rfi = getRFI();
        int requiredQuestionsCount = surveysDAO.getRequiredQuestionCount( rfi );
        int requiredAnswersCount = surveysDAO.getRequiredAnswersCount( rfi );
        int optionalQuestionsCount = surveysDAO.getOptionalQuestionCount( rfi );
        int optionalAnswersCount = surveysDAO.getOptionalAnswersCount( rfi );
        int percent =  ( requiredAnswersCount / requiredQuestionsCount ) * 100;
        StringBuilder sb = new StringBuilder(  );
        sb.append( percent )
                .append( "% done: " );
        if ( requiredQuestionsCount != 0 ) {
            sb.append( "Missing " )
                    .append( requiredQuestionsCount - requiredAnswersCount )
                    .append( " out of " )
                    .append( requiredQuestionsCount )
                    .append( " required " )
                    .append( requiredQuestionsCount > 1 ? "answers" : "answer" )
                    .append( ". " );
        }
        if ( optionalQuestionsCount != 0 ) {
            sb.append(  "Missing " )
                .append( optionalQuestionsCount - optionalAnswersCount )
                    .append( " out of " )
                    .append( optionalQuestionsCount )
                    .append( " optional " )
                    .append( optionalQuestionsCount > 1 ? "answers" : "answer" )
                    .append( "." );
        }
        return sb.toString();
    }

    private String getHeaderString() {
        StringBuilder sb = new StringBuilder();
        RFI rfi = getRFI();
        int requiredQuestionsCount = surveysDAO.getRequiredQuestionCount( rfi );
        int requiredAnswersCount = surveysDAO.getRequiredAnswersCount( rfi );
        int percent = ( requiredAnswersCount / requiredQuestionsCount ) * 100;
        if ( rfi.isDeclined() ) {
            sb.append( "Declined: " );
        }
        sb.append( rfi.getRfiSurvey().getQuestionnaire().getName() )
                .append( " (" )
                .append( percent )
                .append( "% done - " )
                .append( rfi.getShortTimeLeft().toLowerCase() )
                .append( ")" );
        return sb.toString();
    }

    private void addForwarding() {
        boolean canForward = getRFI().getRfiSurvey().isCanBeForwarded();
        // button
        AjaxLink<String> forwardButton = new AjaxLink<String>( "forward" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                ForwardRFIPanel forwardingRFIPanel = new ForwardRFIPanel(
                        dialogWindow.getContentId(),
                        new Model<RFI>( getRFI() )
                );
                showDialog( "Forwarding survey", 600, 500, forwardingRFIPanel, target );
            }
        };
        forwardButton.setOutputMarkupId( true );
        forwardButton.setVisible( canForward );
        rfiContainer.addOrReplace( forwardButton );
        // can't forward notice
        Label cantForwardLabel = new Label( "cantForward", "This survey can not be forwarded" );
        cantForwardLabel.setVisible( !canForward ) ;
        rfiContainer.add( cantForwardLabel );
    }

    private void addAnswerSetsPanel() {
        rfiContainer.add( new Label("answers", "ANSWERS FORM UNDER CONSTRUCTION") );
        // todo
    }

    private void addDeclineButton() {
        final boolean declining = !getRFI().isDeclined();
        declineButton = new AjaxLink<String>( "decline" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( declining ) {
                    DeclineRFIPanel declineRFIPanel = new DeclineRFIPanel(
                            dialogWindow.getContentId(),
                            new Model<RFI>( getRFI() ) );
                    showDialog( "Declining survey", 600, 500, declineRFIPanel, target );
                } else {
                    rfiService.toggleDecline( getRFI(), null );
                    update( target, new Change( Change.Type.Updated, getRFI(), "accepted" ) );
                }
            }
        };
        declineButton.add( new AttributeModifier(
                "value",
                new Model<String>( getRFI().isDeclined() ? "Accept survey" : "Decline survey..." ) ) );
        declineButton.setOutputMarkupId( true );
        rfiContainer.addOrReplace( declineButton );

    }

    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updatables ) {
        if ( change.isForInstanceOf( RFI.class ) ) {
            if ( change.isForProperty( "declined" ) ) {
                String reason = (String) change.getQualifier( "reasonDeclined" );
                rfiService.toggleDecline( getRFI(), reason );
                addDeclineButton();
                target.add( declineButton );
                hideDialog( target );
            } else if ( change.isForProperty( "forwarded" ) ) {
                String emails = (String) change.getQualifier( "emails" );
                String message = (String) change.getQualifier( "message" );
                List<String> forwardedTo = forwardRFI( emails, message );
                if ( !forwardedTo.isEmpty() ) {
                    target.appendJavaScript( "alert(' Survey forwarded to " + StringUtils.join( forwardedTo, ", " ) + "');" );
                }
                hideDialog( target );
            }
        }
        super.updateWith( target, change, updatables );
    }

    private List<String> forwardRFI( String emails, String message ) {
        Set<String> validatedEmails = new HashSet<String>();
        EmailValidator emailValidator = EmailValidator.getInstance();
        for ( String email : StringUtils.split( emails, "," ) ) {
            if ( emailValidator.isValid( email ) )
                validatedEmails.add( email );
        }
        List<String> forwardedTo = new ArrayList<String>( validatedEmails );
        rfiForwardService.forwardRFI( getPlan(), getUser(), getRFI(), forwardedTo, message );
        return forwardedTo;
    }

    private RFI getRFI() {
        RFI rfi = (RFI) getModel().getObject();
        rfiService.refresh( rfi );
        return rfi;
    }

    /**
     * Decline RFI panel.
     */
    private class DeclineRFIPanel extends AbstractUpdatablePanel {

        private RFI rfi;
        private String reason;
        private AjaxLink<String> doItButton;

        private DeclineRFIPanel( String id, IModel<RFI> rfiModel ) {
            super( id, rfiModel );
            rfi = rfiModel.getObject();
            initialize();
        }

        private void initialize() {
            addSurveyName();
            addReasonText();
            addDoItButton();
            addCancelButton();
        }

        private void addSurveyName() {
            Label surveyNameLabel = new Label( "surveyName", rfi.getRfiSurvey().getQuestionnaire().getName() );
            add( surveyNameLabel );
        }

        private void addReasonText() {
            TextArea<String> reasonArea = new TextArea<String>(
                    "reason",
                    new PropertyModel<String>( this, "reason" ) );
            reasonArea.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    // do nothing
                }
            } );
            add( reasonArea );
        }

        private void addDoItButton() {
            doItButton = new AjaxLink<String>( "doIt" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    Change change = new Change( Change.Type.Updated, rfi, "declined" );
                    change.addQualifier( "reasonDeclined", reason );
                    update( target, change );
                }
            };
            doItButton.setOutputMarkupId( true );
            add( doItButton );
        }

        private void addCancelButton() {
            AjaxLink<String> cancelButton = new AjaxLink<String>( "cancel" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    RFIPanel.this.hideDialog( target );
                }
            };
            add( cancelButton );
        }

        public String getReason() {
            return reason == null ? "" : reason;
        }

        public void setReason( String val ) {
            reason = val == null || val.trim().isEmpty() ? null : val;
        }
    }

    /**
     * Forward RFI panel.
     */

    private class ForwardRFIPanel extends AbstractUpdatablePanel {
        private RFI rfi;
        private String emails;
        private String message;
        private List<String> invalidEmails;
        private List<String> validEmails;
        private AjaxLink<String> doItButton;
        private WebMarkupContainer invalidsContainer;

        private ForwardRFIPanel( String id, IModel<RFI> rfiModel ) {
            super( id, rfiModel );
            rfi = rfiModel.getObject();
            initialize();
        }

        private void initialize() {
            addSurveyName();
            addEmailsText();
            addMessage();
            addInvalids();
            addDoItButton();
            addCancelButton();
        }

        private void addSurveyName() {
            Label surveyNameLabel = new Label( "surveyName", rfi.getRfiSurvey().getQuestionnaire().getName() );
            add( surveyNameLabel );
        }

        private void addEmailsText() {
            TextArea<String> emailsArea = new TextArea<String>(
                    "emails",
                    new PropertyModel<String>( this, "emails" ) );
            emailsArea.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    addInvalids();
                    target.add( invalidsContainer );
                }
            } );
            add( emailsArea );
        }

        private void addInvalids() {
            invalidsContainer = new WebMarkupContainer( "invalidsContainer" );
            invalidsContainer.setOutputMarkupId( true );
            String invalids = invalidEmails == null ? "" : StringUtils.join( invalidEmails,", " );
            invalidsContainer.add( new Label( "invalids", invalids ) );
            makeVisible( invalidsContainer, !invalids.isEmpty() );
            addOrReplace( invalidsContainer );
        }

        private void addMessage() {
            TextArea<String> messageArea = new TextArea<String>(
                    "message",
                    new PropertyModel<String>( this, "message" ) );
            messageArea.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    // do nothing
                }
            } );
            add( messageArea );
        }

        private void addDoItButton() {
            doItButton = new AjaxLink<String>( "doIt" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    Change change = new Change( Change.Type.Updated, rfi, "forwarded" );
                    change.addQualifier( "emails", StringUtils.join( validEmails, "," ) );
                    change.addQualifier( "message", message );
                    update( target, change );
                }
            };
            doItButton.setOutputMarkupId( true );
            add( doItButton );
        }

        private void addCancelButton() {
            AjaxLink<String> cancelButton = new AjaxLink<String>( "cancel" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    RFIPanel.this.hideDialog( target );
                }
            };
            add( cancelButton );
        }

        public String getEmails() {
            return emails;
        }

        public void setEmails( String emails ) {
            this.emails = emails == null || emails.trim().isEmpty() ? null : emails;
            validateEmails();
        }

        private void validateEmails() {
            if ( getEmails() != null ) {
                validEmails = new ArrayList<String>(  );
                invalidEmails = new ArrayList<String>(  );
                EmailValidator emailValidator = EmailValidator.getInstance();
                for ( String email : StringUtils.split( getEmails(), "," ) ) {
                    if ( emailValidator.isValid( email ) ) {
                        validEmails.add( email );
                    } else {
                        invalidEmails.add( email );
                    }
                }
            }
        }

        public String getMessage() {
            return message;
        }

        public void setMessage( String message ) {
            this.message = message == null || message.trim().isEmpty() ? null : message;
        }
    }

}
