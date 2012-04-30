package com.mindalliance.playbook.pages;

import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.dao.StepInformation;
import com.mindalliance.playbook.dao.impl.StepDaoImpl.StepInformationImpl;
import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Medium;
import com.mindalliance.playbook.model.Step;
import com.mindalliance.playbook.model.Step.Type;
import com.mindalliance.playbook.model.Subplay;
import com.mindalliance.playbook.pages.panels.ReceivePanel;
import com.mindalliance.playbook.pages.panels.SendPanel;
import com.mindalliance.playbook.pages.panels.SubplayPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
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

    private Type stepType;
    
    private StepInformation stepInformation;

    private final WebMarkupContainer detailsDiv;

    private final Button confirmButton;

    public EditStep( PageParameters parameters ) {
        super( parameters );

        setStatelessHint( true );
        stepInformation = getStep( parameters );

        IModel<StepInformation> model = new CompoundPropertyModel<StepInformation>( stepInformation );
        setDefaultModel( model );
        stepType = stepInformation.getStep().getType();

        detailsDiv = new WebMarkupContainer( "detailsDiv" );

        final Component title = new Label( "hTitle", "Edit step" );

        confirmButton = new AjaxButton( "confirm", new PropertyModel<String>( this, "status" ) ) {
            @Override
            public boolean isEnabled() {
                return stepInformation.isConfirmable();
            }

            @Override
            protected void onSubmit( AjaxRequestTarget target, Form<?> form ) {
                LOG.debug( "Confirm button submitted" );
                redirectToInterceptPage( new ConfirmPage( (Collaboration) stepInformation.getStep() ) );
            }

            @Override
            protected void onError( AjaxRequestTarget target, Form<?> form ) {
            }
        };

        confirmButton.setOutputMarkupId( true );
        confirmButton.setDefaultFormProcessing( false );

        add(
            new Form( "form" ).add(
                new BookmarkablePageLink<TodoPage>( "home", TodoPage.class ),
                title,
                new FeedbackPanel( "feedback" ),
                new TextField<String>( "step.title" ).add(
                    new AjaxFormComponentUpdatingBehavior( "onblur" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            stepDao.save( stepInformation.getStep() );
                            target.add( title );
                        }
                    } ),
                new TextArea<String>( "step.description" ).add(
                    new AjaxFormComponentUpdatingBehavior( "onblur" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            stepDao.save( stepInformation.getStep() );
                        }
                    } ),

                new RadioGroup<Type>( "stepType", new PropertyModel<Type>( this, "stepType" ) ).add(
                    new Radio<Type>( "typeTask", new Model<Type>( Type.TASK ) ).add(
                        new AjaxEventBehavior( "onchange" ) {
                            @Override
                            protected void onEvent( AjaxRequestTarget target ) {
                                changeStepType( target, Type.TASK );
                            }
                        } ), new Radio<Type>( "typeSend", new Model<Type>( Type.SEND ) ).add(
                    new AjaxEventBehavior( "onchange" ) {
                        @Override
                        protected void onEvent( AjaxRequestTarget target ) {
                            changeStepType( target, Type.SEND );
                        }
                    } ), new Radio<Type>( "typeReceive", new Model<Type>( Type.RECEIVE ) ).add(
                    new AjaxEventBehavior( "onchange" ) {
                        @Override
                        protected void onEvent( AjaxRequestTarget target ) {
                            changeStepType( target, Type.RECEIVE );
                        }
                    } ), new Radio<Type>( "typePlay", new Model<Type>( Type.SUBPLAY ) ).add(
                    new AjaxEventBehavior( "onchange" ) {
                        @Override
                        protected void onEvent( AjaxRequestTarget target ) {
                            changeStepType( target, Type.SUBPLAY );
                        }
                    } ) ),

                detailsDiv.add( details( stepType, model ) ).setOutputMarkupId( true ),

                new AjaxButton( "delete" ) {
                    @Override
                    protected void onSubmit( AjaxRequestTarget target, Form<?> form ) {
                        stepDao.delete( stepInformation.getStep() );
                        EditStep.this.setResponsePage(
                            EditPlay.class, new PageParameters().add( "id", stepInformation.getPlayId() ) );
                    }

                    @Override
                    protected void onError( AjaxRequestTarget target, Form<?> form ) {
                    }
                }.setDefaultFormProcessing( false ),

                confirmButton ) );
    }

    private void changeStepType( AjaxRequestTarget target, Type type ) {
        if ( stepType != type ) {
            stepType = type;
            Step step = stepDao.switchStep( type, stepInformation.getStep() );
            stepInformation = new StepInformationImpl( step, null, null );
            setDefaultModelObject( stepInformation );
            
            detailsDiv.get( "details" ).remove();
            detailsDiv.add( details( type, (IModel<StepInformation>) getDefaultModel() ) );
            target.add( detailsDiv );
            target.appendJavaScript(
                "history.replaceState({},'" + getPageTitle() + "'," + urlFor(
                    getClass(),
                    new PageParameters().add( "id", step.getId() ) ) + ");" 
                + "$('#" + detailsDiv.getMarkupId() + "').trigger('create');" 
                + "$('#" + detailsDiv.getMarkupId() + "').trigger('updateLayout');"
            );
            updateConfirmButton( target );
        }
    }

    private void updateConfirmButton( AjaxRequestTarget target ) {
        target.appendJavaScript(
            "$('#" + confirmButton.getMarkupId() + "').parent().find('.ui-btn-text').text('" + getStatus() + "');"
            + "$('#" + confirmButton.getMarkupId() + "').button('" 
                + ( stepInformation.isConfirmable() ? "enable" : "disable" ) + "');" 
        );
    }

    public String getStatus() {
        switch ( stepInformation.getStatus() ) {
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

    private Component details( Type stepType, IModel<StepInformation> infoModel ) {
        PropertyModel<Step> stepModel = new PropertyModel<Step>( infoModel, "step" );
        switch ( stepType ) {

        case SEND:
            return new SendPanel( "details", stepModel ) {
                @Override
                public void updateTo( Contact contact,  AjaxRequestTarget target ) {
                    Collaboration collaboration = (Collaboration) stepInformation.getStep();
                    collaboration.setUsing( null );
                    stepDao.deleteRequest( collaboration );
                    stepInformation = new StepInformationImpl( collaboration, null, null );
                    stepDao.save( collaboration );
                    updateConfirmButton( target );
                }

                @Override
                public void updateTo( Medium medium, AjaxRequestTarget target ) {
                    Collaboration collaboration = (Collaboration) stepInformation.getStep();
                    collaboration.setUsing( medium );
                    stepDao.deleteConfirmation( collaboration );
                    stepInformation = new StepInformationImpl( collaboration, null, null );
                    stepDao.save( collaboration );
                    updateConfirmButton( target );
                }
            }.setOutputMarkupId( true );

        case RECEIVE:
            return new ReceivePanel( "details", stepModel ) {
                @Override
                public void updateTo( Contact with, AjaxRequestTarget target ) {
                    Collaboration collaboration = (Collaboration) stepInformation.getStep();
                    collaboration.setUsing( null );
                    stepDao.deleteRequest( collaboration );
                    stepInformation = new StepInformationImpl( collaboration, null, null );
                    stepDao.save( collaboration );
                    updateConfirmButton( target );
                }

                @Override
                public void updateTo( Medium medium, AjaxRequestTarget target ) {
                    Collaboration collaboration = (Collaboration) stepInformation.getStep();
                    collaboration.setUsing( medium );
                    stepDao.deleteConfirmation( collaboration );
                    stepInformation = new StepInformationImpl( collaboration, null, null );
                    stepDao.save( collaboration );
                    updateConfirmButton( target );
                }
            }.setOutputMarkupId( true );

        case SUBPLAY:
            return new SubplayPanel( "details", stepModel ) {
                @Override
                public void updateTo( Subplay subplay, AjaxRequestTarget target ) {
                    stepDao.save( stepInformation.getStep() );
                }
            }.setOutputMarkupId( true );

        default:
            return new WebMarkupContainer( "details" ).setOutputMarkupId( true );
        }
    }

    private StepInformation getStep( PageParameters parameters ) {
        StringValue id = parameters.get( "id" );
        if ( id.isNull() )
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_NOT_FOUND, "Not Found" );

        StepInformation info = stepDao.getInformation( id.toLong() );
        if ( info == null )
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_NOT_FOUND, "Not Found" );

        // Use getId() because .equals() doesn't work on proxies, for some reason...  
        if ( account.getId() != info.getAccount().getId() )
            throw new AbortWithHttpErrorCodeException( HttpServletResponse.SC_FORBIDDEN, "Unauthorized" );
        return info;
    }

    @Override
    public String getPageTitle() {
        return "Edit step";
    }

    public Type getStepType() {
        return stepType;
    }

    public void setStepType( Type stepType ) {
        this.stepType = stepType;
    }
}