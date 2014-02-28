package com.mindalliance.channels.pages.surveys;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.surveys.RFI;
import com.mindalliance.channels.db.services.surveys.RFIService;
import com.mindalliance.channels.db.services.surveys.SurveysDAO;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.social.rfi.SurveyAnswersPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static final String NAME_PART_SEP = "-";

    @SpringBean( name="surveysDao" )
    private SurveysDAO surveysDAO;

    @SpringBean
    private RFIService rfiService;

    WebMarkupContainer rfiContainer;
    private AjaxLink<String> declineButton;
    private WebMarkupContainer headerContainer;


    public RFIPanel( String id, IModel<RFI> rfiModel ) {
        super( id, rfiModel );
        init();
    }

    private void init() {
        rfiContainer = new WebMarkupContainer( "rfiContainer" );
        add( rfiContainer );
        addHeader();
        addAnswerSetsPanel();
        addDeclineButton();
        addForwarding();
    }


    private void addHeader() {
        headerContainer = new WebMarkupContainer( "header" );
        headerContainer.setOutputMarkupId( true );
        addName();
        // sent by
        Label sentByLabel = new Label( "sentBy", getSentBy() );
        headerContainer.add( sentByLabel );
        // deadline
        String deadlineText = getDeadlineText();
        Label deadlineLabel = new Label( "deadline", deadlineText );
        headerContainer.add( deadlineLabel );
        // completion
        Label sentCompletion = new Label( "completion", getCompletion() );
        headerContainer.add( sentCompletion );
        // forwarded to
        String emails = getForwardedTo();
        Label forwardedTo = new Label( "forwardedTo", emails );
        forwardedTo.setVisible( !emails.isEmpty() );
        headerContainer.add( forwardedTo );

        rfiContainer.addOrReplace( headerContainer );
    }

    private void addName() {
        WebMarkupContainer nameContainer = new WebMarkupContainer( "name" );
        nameContainer.setOutputMarkupId( true );
        ListView<String> namePartListView = new ListView<String>(
                "nameParts",
                getNameParts()
        ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                String namePart = item.getModelObject();
                item.add( new Label( "namePart", namePart ) );
            }
        };
        nameContainer.add( namePartListView );
        headerContainer.addOrReplace( nameContainer );
    }

    private List<String> getNameParts() {
        String name = getHeaderString();
        return Arrays.asList( name.split( NAME_PART_SEP ) );
    }

    private String getForwardedTo() {
        String emails = StringUtils.join( rfiService.findForwardedTo( getRFI() ), ", " );
        return emails.isEmpty() ? "" : "Forwarded to " + emails;
    }

    private String getSentBy() {
        String sentBy = getRFI().getRfiSurvey( getCommunityService() ).getUsername();
        return "A survey by "
                + (getCommunityService().getPlanCommunity().isModelCommunity() ? "collaboration model developer " : "community planner ")
                + getUserFullName( sentBy );
    }

    private String getDeadlineText() {
        Date deadline = getRFI().getDeadline();
        if ( deadline == null ) {
            return "No deadline";
        } else {
            long delta = deadline.getTime() - new Date().getTime();
            String interval = ChannelsUtils.getShortTimeIntervalString( delta );
            return ( delta < 0 ? "Overdue by " : "Due in " ) + interval;
        }
    }

    private String getCompletion() {
        RFI rfi = getRFI();
        int requiredQuestionsCount = surveysDAO.getRequiredQuestionCount( rfi );
        int requiredAnswersCount = surveysDAO.getRequiredAnswersCount( rfi );
        int optionalQuestionsCount = surveysDAO.getOptionalQuestionCount( rfi );
        int optionalAnswersCount = surveysDAO.getOptionalAnswersCount( rfi );
        int percent = surveysDAO.getPercentCompletion( rfi );
        StringBuilder sb = new StringBuilder();
        sb.append( percent )
                .append( "% done" );
        if ( requiredQuestionsCount != 0 ) {
            sb.append( "  |  Missing " )
                    .append( requiredQuestionsCount - requiredAnswersCount )
                    .append( " out of " )
                    .append( requiredQuestionsCount )
                    .append( " required " )
                    .append( requiredQuestionsCount > 1 ? "answers" : "answer" )
                    .append( " " );
        }
        if ( optionalQuestionsCount != 0 ) {
            sb.append( "  |  Missing " )
                    .append( optionalQuestionsCount - optionalAnswersCount )
                    .append( " out of " )
                    .append( optionalQuestionsCount )
                    .append( " optional " )
                    .append( optionalQuestionsCount > 1 ? "answers" : "answer" )
                    .append( " " );
        }
        return sb.toString();
    }

    private String getHeaderString() {
        StringBuilder sb = new StringBuilder();
        RFI rfi = getRFI();
        if ( rfi.isDeclined() ) {
            sb.append( "Declined: " );
        }
        sb.append( rfi.getRfiSurvey( getCommunityService() ).getName() );
        return sb.toString();
    }

    private void addForwarding() {
        boolean canForward = getRFI().getRfiSurvey( getCommunityService() ).isCanBeForwarded();
        // button
        AjaxLink<String> forwardButton = new AjaxLink<String>( "forward" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                ForwardRFIPanel forwardingRFIPanel = new ForwardRFIPanel(
                        getModalableParent().getModalContentId(),
                        new Model<RFI>( getRFI() )
                );
                getModalableParent().showDialog(
                        "Forwarding survey",
                        300,
                        500,
                        forwardingRFIPanel,
                        RFIPanel.this,
                        target );
            }
        };
        forwardButton.setOutputMarkupId( true );
        forwardButton.setVisible( canForward );
        rfiContainer.addOrReplace( forwardButton );
        // can't forward notice
        Label cantForwardLabel = new Label( "cantForward", "This survey can not be forwarded" );
        cantForwardLabel.setVisible( !canForward );
        rfiContainer.add( cantForwardLabel );
    }

    private void addAnswerSetsPanel() {
        rfiContainer.add( new SurveyAnswersPanel( "answers", new Model<RFI>( getRFI() ) ) );
    }

    private void addDeclineButton() {
        final boolean declining = !getRFI().isDeclined();
        declineButton = new AjaxLink<String>( "decline" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( declining ) {
                    DeclineRFIPanel declineRFIPanel = new DeclineRFIPanel(
                            getModalableParent().getModalContentId(),
                            new Model<RFI>( getRFI() ) );
                    getModalableParent().showDialog(
                            "Declining survey",
                            300,
                            500,
                            declineRFIPanel,
                            RFIPanel.this,
                            target );
                } else {
                    rfiService.toggleDecline( getRFI(), null );
                    update( target, new Change( Change.Type.Updated, getRFI(), "accepted" ) );
                }
            }
        };
        declineButton.add( new AttributeModifier(
                "value",
                new Model<String>( getRFI().isDeclined() ? "Accept survey" : "Decline survey" ) ) );
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
                getModalableParent().hideDialog( target );
            } else if ( change.isForProperty( "forwarded" ) ) {
                String emails = (String) change.getQualifier( "emails" );
                String message = (String) change.getQualifier( "message" );
                if ( emails != null ) {
                    List<String> forwardedTo = forwardRFI( emails, message, getQueryService() );
                    if ( !forwardedTo.isEmpty() ) {
                        addHeader();
                        target.add( headerContainer );
                        change.setMessage( "Survey forwarded to " + StringUtils.join( forwardedTo, ", " ) );
                        //    target.appendJavaScript( "alert(' Survey forwarded to " + StringUtils.join( forwardedTo, ", " ) + "');" );
                    }
                    getModalableParent().hideDialog( target );
                } else {
                    // do nothing
                    change.setType( Change.Type.None );
                    change.setMessage( "You need to enter at least one email address to forward the survey to." );
                }
            }
        }
        super.updateWith( target, change, updatables );
    }

    private List<String> forwardRFI( String emails, String message, QueryService queryService ) {
        Set<String> validatedEmails = new HashSet<String>();
        EmailValidator emailValidator = EmailValidator.getInstance();
        for ( String email : StringUtils.split( emails, "," ) ) {
            if ( emailValidator.isValid( email ) )
                validatedEmails.add( email );
        }
        List<String> forwardedTo = new ArrayList<String>( validatedEmails );
        return surveysDAO.forwardRFI( getCommunityService(), getUser(), getRFI(), forwardedTo, message );
    }

    private RFI getRFI() {
        RFI rfi = (RFI) getModel().getObject();
        rfi = rfiService.load( rfi.getUid() );
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
            Label surveyNameLabel = new Label( "surveyName", rfi.getRfiSurvey( getCommunityService() ).getName() );
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
                    RFIPanel.this.getModalableParent().hideDialog( target );
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
        private Label invalidsLabel;

        private ForwardRFIPanel( String id, IModel<RFI> rfiModel ) {
            super( id, rfiModel );
            rfi = rfiModel.getObject();
            initialize();
        }

        private void initialize() {
            addSurveyName();
            addAlreadyForwardedTo();
            addEmailsText();
            addMessage();
            addInvalids();
            addDoItButton();
            addCancelButton();
        }

        private void addAlreadyForwardedTo() {
            String already = StringUtils.join( rfiService.findForwardedTo( getRFI() ), ", " );
            Label alreadyForwardedTo = new Label( "alreadyForwardedTo", "Already forwarded to " + already );
            add( alreadyForwardedTo );
            alreadyForwardedTo.setVisible( !already.isEmpty() );
        }

        private void addSurveyName() {
            Label surveyNameLabel = new Label( "surveyName", rfi.getRfiSurvey( getCommunityService() ).getName() );
            add( surveyNameLabel );
        }

        private void addEmailsText() {
            TextArea<String> emailsArea = new TextArea<String>(
                    "emails",
                    new PropertyModel<String>( this, "emails" ) );
            emailsArea.add( new AjaxFormComponentUpdatingBehavior( "onkeyup" ) {
                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    // addInvalids();
                    target.add( invalidsLabel );
                }
            } );
            emailsArea.add( new AjaxFormComponentUpdatingBehavior( "onblur" ) {
                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    // addInvalids();
                    target.add( invalidsLabel );
                }
            } );
            add( emailsArea );
        }

        private void addInvalids() {
            invalidsLabel = new Label( "invalids", new PropertyModel<String>( this, "invalids" ) );
            invalidsLabel.setOutputMarkupId( true );
            add( invalidsLabel );
        }

        public String getInvalids() {
            return invalidEmails == null || invalidEmails.isEmpty() ? "" : "Not valid: " + StringUtils.join( invalidEmails, ", " );
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
                    if ( !getInvalids().isEmpty() ) {
                        target.appendJavaScript( "alert('" + getInvalids() + "');" );
                    } else {
                        Change change = new Change( Change.Type.Updated, rfi, "forwarded" );
                        change.addQualifier( "emails", StringUtils.join( validEmails, "," ) );
                        change.addQualifier( "message", message );
                        update( target, change );
                    }
                }
            };
            doItButton.setOutputMarkupId( true );
            add( doItButton );
        }

        private void addCancelButton() {
            AjaxLink<String> cancelButton = new AjaxLink<String>( "cancel" ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    RFIPanel.this.getModalableParent().hideDialog( target );
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
                validEmails = new ArrayList<String>();
                invalidEmails = new ArrayList<String>();
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
