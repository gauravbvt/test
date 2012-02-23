package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.dao.StepDao.Status;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Step;
import com.mindalliance.playbook.model.Step.Type;
import com.mindalliance.playbook.pages.panels.ReceivePanel;
import com.mindalliance.playbook.pages.panels.SendPanel;
import com.mindalliance.playbook.pages.panels.SubplayPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Play step editor.
 */
public class EditStep extends NavigablePage {

    private static final Logger LOG = LoggerFactory.getLogger( EditStep.class );

    private static final long serialVersionUID = -6456427913542600570L;

    @SpringBean
    Account account;

    @SpringBean
    StepDao stepDao;

    @SpringBean
    PlayDao playDao;

    private Type stepType;

    public EditStep( PageParameters parameters ) {
        super( parameters );

        setStatelessHint( true );
        final Step step = getStep( parameters );

        IModel<Step> model = new CompoundPropertyModel<Step>( step );
        setDefaultModel( model );
        stepType = step.getType();

        Status status = stepDao.getStatus( step );

        add(
            new Form( "form" ) {
                @Override
                protected void onSubmit() {
                    save( step );
                }
            }.add(
                new BookmarkablePageLink<TodoPage>( "home", TodoPage.class ),
                new Label( "hTitle", new PropertyModel<String>( step, "title" ) ),
                new FeedbackPanel( "feedback" ),
                new TextField<String>( "title" ),
                new TextArea<String>( "description" ),
                new TextField<String>( "duration" ),

                new RadioGroup<Type>( "stepType", new PropertyModel<Type>( this, "stepType" ) ).add(
                    new Radio<Type>( "typeTask", new Model<Type>( Type.TASK ) ).add(
                        new AjaxEventBehavior( "onchange" ) {
                            @Override
                            protected void onEvent( AjaxRequestTarget target ) {
                                target.appendJavaScript( "Wicket.$('stepForm').submit()" );
                            }
                        } ), new Radio<Type>( "typeSend", new Model<Type>( Type.SEND ) ).add(
                    new AjaxEventBehavior( "onchange" ) {
                        @Override
                        protected void onEvent( AjaxRequestTarget target ) {
                            target.appendJavaScript( "Wicket.$('stepForm').submit()" );
                        }
                    } ), new Radio<Type>( "typeReceive", new Model<Type>( Type.RECEIVE ) ).add(
                    new AjaxEventBehavior( "onchange" ) {
                        @Override
                        protected void onEvent( AjaxRequestTarget target ) {
                            target.appendJavaScript( "Wicket.$('stepForm').submit()" );
                        }
                    } ), new Radio<Type>( "typePlay", new Model<Type>( Type.SUBPLAY ) ).add(
                    new AjaxEventBehavior( "onchange" ) {
                        @Override
                        protected void onEvent( AjaxRequestTarget target ) {
                            target.appendJavaScript( "Wicket.$('stepForm').submit()" );
                        }
                    } ) ),

                details( stepType, model ),

                new StatelessLink( "delete" ) {
                    @Override
                    public void onClick() {                        
                        stepDao.delete( step );
                        gotoPlay( step.getPlay() );
                    }
                },

                new Button( "confirm" ) {
                    @Override
                    public void onSubmit() {
                        LOG.debug( "Confirm button submitted" );
                        save( step );
                        redirectToInterceptPage( new ConfirmPage( (Collaboration) step ) );
                    }
                }.add(
                    new AttributeModifier( "value", getText( status ) ) 
                )   .setVisible( step.isCollaboration() )
                    .setEnabled( stepDao.isConfirmable( step ) ) ) );
    }

    private static String getText( Status status ) {
        switch ( status ) {
        case CONFIRMED:
            return "Confirmed";
        case REJECTED:
            return "Rejected";
        case PENDING:
            return "Pending";
        case AGREED:
            return "As Agreed";
        case UNCONFIRMED:
        default:
            return "Confirm";
        }
    }

    private void save( Step step ) {
        if ( step.getType() == stepType ) {
            stepDao.save( step );
            gotoStep( step );
        } else {
            gotoStep( stepDao.switchStep( stepType, step ) );
        }
    }

    private static Component details( Type stepType, IModel<Step> stepModel ) {
        switch ( stepType ) {

        case SEND:
            return new SendPanel( "details", stepModel ).setOutputMarkupId( true );

        case RECEIVE:
            return new ReceivePanel( "details", stepModel ).setOutputMarkupId( true );

        case SUBPLAY:
            return new SubplayPanel( "details", stepModel ).setOutputMarkupId( true );

        default:
            return new WebMarkupContainer( "details" ).setOutputMarkupId( true );
        }
    }

    private Step getStep( PageParameters parameters ) {
        StringValue id = parameters.get( "id" );
        if ( id.isNull() )
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_NOT_FOUND, "Not Found" );

        Step step = stepDao.load( id.toLong() );
        if ( step == null )
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_NOT_FOUND, "Not Found" );

        if ( step.getPlay().getAccountId() != account.getId() )
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_FORBIDDEN, "Unauthorized" );
        return step;
    }

    private void gotoPlay( Play play ) {
        setResponsePage( EditPlay.class, new PageParameters().add( "id", play.getId() ) );
    }

    private void gotoStep( Step step ) {
        setResponsePage( EditStep.class, new PageParameters().add( "id", step.getId() ) );
    }

    @Override
    public String getPageTitle() {
        return "Edit play step";
    }

    public Type getStepType() {
        return stepType;
    }

    public void setStepType( Type stepType ) {
        this.stepType = stepType;
    }
}