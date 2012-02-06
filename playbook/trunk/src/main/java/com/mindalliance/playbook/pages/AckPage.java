package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.AckDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Ack;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.NAck;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.pages.panels.ContactField;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Request acknowledgement page.
 */
public class AckPage extends MobilePage {

    private static final Logger LOG = LoggerFactory.getLogger( AckPage.class );

    private final Collaboration collaboration;

    private final ConfirmationReq req;

    public enum AnswerType {
        UNKNOWN,
        YES,
        NO,
        MAYBE
    }

    private final Ack ack;

    private Contact referral;
    
    private String referralNote;

    private final NAck nAck;
    
    private String newPlay;
    
    private Play existingPlay;

    private AnswerType answerType = AnswerType.UNKNOWN;

    @SpringBean
    private Account account;

    @SpringBean
    private AckDao ackDao;

    public AckPage( final ConfirmationReq request ) {
        setDefaultModel( new CompoundPropertyModel<AckPage>( this ) );

        // Build all possible answers.
        // Actually save only the one specified by answerType.
        ack = new Ack();
        nAck = new NAck();

        req = request;
        collaboration = this.req.getCollaboration();
        Contact contact = getContact( collaboration );

        long contactId = contact.getId();
        boolean hasPhoto = contact.getPhoto() != null;

        final Component yesDiv = new WebMarkupContainer( "yesDiv" ).add(
            new TextField<String>( "newPlay" ),

            new Select<Play>( "existingPlay" ).add(
                new SelectOptions<Play>(
                    "plays",
                    new PropertyModel<Collection<? extends Play>>( account, "playbook.plays" ),
                    new IOptionRenderer<Play>() {
                        @Override
                        public String getDisplayValue( Play object ) {
                            return object.getTitle();
                        }

                        @Override
                        public IModel<Play> getModel( Play value ) {
                            return new Model<Play>( value );
                        }
                    } ) ) ).setRenderBodyOnly( true ).setVisible( false );

        final Component noDiv = new WebMarkupContainer( "noDiv" ).add( 
                new TextArea( "nAck.reason" )
            ).setVisible( false );
        final Component maybeDiv = new WebMarkupContainer( "maybeDiv" ).add(
                new ContactField( "referral", new PropertyModel<Contact>( this, "referral" ) ),
                new TextArea( "referralNote" )
            )
                .setRenderBodyOnly( true )
                .setVisible( false );

        final WebMarkupContainer formList = new WebMarkupContainer( "formList" );

        formList.add(
            new RadioGroup<AnswerType>( "answerType" ).add(
                new Radio<AnswerType>( "typeYes", new Model<AnswerType>( AnswerType.YES ) ).add(
                    new AjaxEventBehavior( "onchange" ) {
                        @Override
                        protected void onEvent( AjaxRequestTarget target ) {
                            setAnswerType( AnswerType.YES );
                            setSubpane( target, formList, yesDiv, noDiv, maybeDiv );
                        }
                    } ),

                new Radio<AnswerType>( "typeNo", new Model<AnswerType>( AnswerType.NO ) ).add(
                    new AjaxEventBehavior( "onchange" ) {
                        @Override
                        protected void onEvent( AjaxRequestTarget target ) {
                            setAnswerType( AnswerType.NO );
                            setSubpane( target, formList, yesDiv, noDiv, maybeDiv );
                        }
                    } ),

                new Radio<AnswerType>( "typeMaybe", new Model<AnswerType>( AnswerType.MAYBE ) ).add(
                    new AjaxEventBehavior( "onchange" ) {
                        @Override
                        protected void onEvent( AjaxRequestTarget target ) {
                            setAnswerType( AnswerType.MAYBE );
                            setSubpane( target, formList, yesDiv, noDiv, maybeDiv );
                        }
                    } )

            ).setRenderBodyOnly( true ), yesDiv, noDiv, maybeDiv );

        formList.setOutputMarkupId( true );

        final MarkupContainer form = new Form( "form" ) {
            @Override
            protected void onSubmit() {
                // TODO implement this
                if ( canSubmit() ) {
                    LOG.debug( "Submitted" );
                    setResponsePage( MessagesPage.class );
                }
            }
        }.add( formList );

        add(
            new Label( "hTitle", getPageTitle() ), 
            new StatelessLink( "cancel" ) {
                @Override
                public void onClick() {
                    setResponsePage( MessagesPage.class );
                }
            },

            new Label( "req.collaboration.play.playbook.me" ),

            // TODO figure out what is the right way of doing this...
            new WebMarkupContainer( "photo" ).add(
                new AttributeModifier(
                    "src", new Model<String>(
                    "contacts/" + contactId ) ) ).setVisible( contactId != 0L && hasPhoto ),

            new Label( "req.description" ),

            form );
    }
    
    private boolean canSubmit() {
        return answerType == AnswerType.YES && ( newPlay != null || existingPlay != null )
            || answerType == AnswerType.NO
            ;
    }

    private static Contact getContact( Collaboration collaboration ) {
        return collaboration.getPlay().getPlaybook().getMe();
    }

    private void setSubpane( AjaxRequestTarget target, Component formList, Component yesDiv, Component noDiv,
                             Component maybeDiv ) {
        yesDiv.setVisible( answerType == AnswerType.YES );
        noDiv.setVisible( answerType == AnswerType.NO );
        maybeDiv.setVisible( answerType == AnswerType.MAYBE );
        target.add( formList );
        target.appendJavaScript( "$('#formList').listview(); $('#formList').trigger('create');" );
    }

    @Override
    public String getPageTitle() {
        return "Requested confirmation";
    }

    public Play getExistingPlay() {
        return existingPlay;
    }

    public void setExistingPlay( Play existingPlay ) {
        this.existingPlay = existingPlay;
    }

    public String getNewPlay() {
        return newPlay;
    }

    public void setNewPlay( String newPlay ) {
        this.newPlay = newPlay;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType( AnswerType answerType ) {
        this.answerType = answerType;
    }

    public Ack getAck() {
        return ack;
    }

    public NAck getNAck() {
        return nAck;
    }

    public Contact getReferral() {
        return referral;
    }

    public void setReferral( Contact referral ) {
        this.referral = referral;
    }

    public Collaboration getCollaboration() {
        return collaboration;
    }

    public String getReferralNote() {
        return referralNote;
    }

    public void setReferralNote( String referralNote ) {
        this.referralNote = referralNote;
    }

    public ConfirmationReq getReq() {
        return req;
    }
}
