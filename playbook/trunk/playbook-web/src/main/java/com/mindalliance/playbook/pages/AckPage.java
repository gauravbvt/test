package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.AckDao;
import com.mindalliance.playbook.dao.ConfirmationReqDao;
import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.ConfirmationReq;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.NAck;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.RedirectReq;
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
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Collection;

/**
 * Request acknowledgement page.
 */
public class AckPage extends MobilePage {

    private static final Logger LOG = LoggerFactory.getLogger( AckPage.class );

    private static final long serialVersionUID = -268665698142575634L;

    private ConfirmationReq req;

    @SpringBean
    private PlayDao playDao;

    @SpringBean
    private StepDao stepDao;

    @SpringBean
    private ConfirmationReqDao reqDao;

    public enum AnswerType {
        UNKNOWN,
        YES,
        NO,
        MAYBE
    }

    public enum PlayType {
        UNSET,
        NEW,
        OLD
    }

    private Contact referral;

    private String referralNote;

    private String nAckReason;

    private PlayType create = PlayType.UNSET;

    private String newPlay;

    private Play existingPlay;

    private AnswerType answerType = AnswerType.UNKNOWN;

    @SpringBean
    private Account account;

    @SpringBean
    private AckDao ackDao;

    public AckPage( PageParameters parameters ) {
        super( parameters );

        try {
            long id = parameters.get( "id" ).toLong();
            ConfirmationReq r = reqDao.load( id );
            if ( r == null )
                throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_NOT_FOUND, "Not Found" );
            
            else if ( account.getOwner().isMergeableWith( r.getRecipient() ) )
                init( r );
            
            else
                throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_FORBIDDEN, "Unauthorized" );

        } catch ( StringValueConversionException ignored ) {
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_NOT_FOUND, "Not Found" );
        }
    }

    public AckPage( final ConfirmationReq request ) {
        init( request );
    }

    private void init( ConfirmationReq request ) {
        setDefaultModel( new CompoundPropertyModel<AckPage>( this ) );

        // Build all possible answers.
        // Actually save only the one specified by answerType.

        req = request;
        final Contact contact = request.getSender();

        long contactId = contact.getId();
        boolean hasPhoto = contact.getPhoto() != null;

        final WebMarkupContainer formList = new WebMarkupContainer( "formList" );

        final Component yesNew = new WebMarkupContainer( "yesNew" ).add(
            new TextField<String>( "newPlay" ) ).setVisible( false );

        final Component yesOld = new WebMarkupContainer( "yesOld" ).add(
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
                    } ) ) ).setVisible( false );

        final Component yesButton = new Button( "yesButton" ).setEnabled( false );

        final Component yesDiv = new WebMarkupContainer( "yesDiv" ).add(
            new RadioGroup<PlayType>( "create" ).add(
                new Radio<PlayType>( "new", new Model<PlayType>( PlayType.NEW ) ).add( 
                    new AjaxEventBehavior( "onchange" ) {
                        @Override
                        protected void onEvent( AjaxRequestTarget target ) {
                            setCreate( PlayType.NEW );
                            yesNew.setVisible( true );
                            yesOld.setVisible( false );
                            yesButton.setEnabled( true );
                            target.add( formList );
                            target.appendJavaScript( "$('#formList').listview(); $('#formList').trigger('create');" );
                        }
                    } ),
                new Radio<PlayType>( "old", new Model<PlayType>( PlayType.OLD ) ).add(
                new AjaxEventBehavior( "onchange" ) {
                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        setCreate( PlayType.OLD );
                        yesNew.setVisible( false );
                        yesOld.setVisible( true );
                        yesButton.setEnabled( true );
                        target.add( formList );
                        target.appendJavaScript( "$('#formList').listview(); $('#formList').trigger('create');" );
                    }
                } ).setEnabled( !account.getPlaybook().getPlays().isEmpty() ) ),

            yesNew,
            yesOld, 
            yesButton

        ).setRenderBodyOnly( true ).setVisible( false );

        final Component noDiv = new WebMarkupContainer( "noDiv" ).add(
            new TextArea( "nAckReason" ) ).setRenderBodyOnly( true ).setVisible( false );

        final Component maybeDiv = new WebMarkupContainer( "maybeDiv" ).add(
            new ContactField( "referral", new PropertyModel<Contact>( this, "referral" ) ),
            new TextArea( "referralNote" ), new Button( "maybeButton" )
        ).setRenderBodyOnly( true ).setVisible( false );

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
                    } ).setEnabled( req.isForwardable() )

            ).setRenderBodyOnly( true ), yesDiv, noDiv, maybeDiv );

        formList.setOutputMarkupId( true );

        final MarkupContainer form = new Form( "form" ) {
            @Override
            protected void onSubmit() {
                // TODO implement this

                switch ( answerType ) {

                case YES:
                    Play play = newPlay != null && !newPlay.trim().isEmpty() ?
                                ackDao.saveInPlay( newPlay.trim(), req ) :
                                existingPlay == null ?
                                ackDao.saveInPlay( (String) null, req ) :
                                ackDao.saveInPlay( existingPlay, req );

                    setResponsePage( EditPlay.class, new PageParameters().add( "id", play.getId() ) );

                    break;

                case MAYBE:
                    reqDao.redirect( new RedirectReq( account.getPlaybook(), referral, req, referralNote ) );
                    setResponsePage( MessagesPage.class );

                    break;

                case NO:
                    ackDao.refuse( new NAck( req, nAckReason ) );

                case UNKNOWN:
                    setResponsePage( MessagesPage.class );
                    break;
                }
            }
        }.add( formList );

        add(
            new Label( "hTitle", getPageTitle() ), new FeedbackPanel( "feedback" ), new StatelessLink( "cancel" ) {
            @Override
            public void onClick() {
                setResponsePage( MessagesPage.class );
            }
        },

            new Label( "req.sender" ),
            new Label( "req.summary" ),

            new WebMarkupContainer( "photo" ).add(
                new AttributeModifier( "src", photoUrl( contactId ) ) ).setVisible( contactId != 0L && hasPhoto ),

            new Label( "req.description" ), 
            
            createForwardInfo( req ),
            form );
    }

    private Serializable photoUrl( Long contactId ) {
        return contactId == null ? "#"
             : (Serializable) urlFor( ContactPic.class, new PageParameters().add( "id", contactId ) );
    }

    private Component createForwardInfo( ConfirmationReq req ) {
        RedirectReq rreq = null;
        ConfirmationReq originalRequest;
        Contact sender;
        Long contactId = null;
        String description;
        String fullName;
        if ( req.isRedirect() ) {
            rreq = (RedirectReq) req;
            originalRequest = rreq.getOriginalRequest();
            sender = originalRequest.getSender();
            contactId = sender.hasPhoto() ? sender.getId() : null;
            description = originalRequest.getDescription();
            fullName = sender.getFullName();
        }
        else {
            description = "";
            fullName = "";
        }

        return new WebMarkupContainer( "fwd" ).add(
            new Label( "sender", fullName ), 
            new Label( "description", description ),
            new WebMarkupContainer( "photo" ).add(
                new AttributeModifier( "src", photoUrl( contactId ) ) ).setVisible( contactId != null )

        ).setVisible( rreq != null );
    }

    private boolean canSubmit() {
        return answerType == AnswerType.YES && ( newPlay != null || existingPlay != null )
               || answerType == AnswerType.NO;
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

    public Contact getReferral() {
        return referral;
    }

    public void setReferral( Contact referral ) {
        this.referral = referral;
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

    public String getNAckReason() {
        return nAckReason;
    }

    public void setNAckReason( String nAckReason ) {
        this.nAckReason = nAckReason;
    }

    public PlayType getCreate() {
        return create;
    }

    public void setCreate( PlayType create ) {
        this.create = create;
    }
}
