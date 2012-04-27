package com.mindalliance.playbook.pages.panels;

import com.mindalliance.playbook.model.Account;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Step;
import com.mindalliance.playbook.model.Subplay;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collection;

/**
 * Details for a subplay step.
 */
public class SubplayPanel extends Panel {

    private static final long serialVersionUID = 1198706889911357653L;

    @SpringBean
    private Account account;

    public SubplayPanel( String id, IModel<Step> model ) {
        super( id, model );
                
        add( new Select<Play>( "play", new PropertyModel<Play>( model, "subplay" ) )
            .add(
                new SelectOptions<Play>( "plays",  
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
                                         })
            ).add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    updateTo( getSubplay(), target );
                }
            } ) );
    }

    public void updateTo( Subplay subplay, AjaxRequestTarget target ) {
    }
    
    public Subplay getSubplay() {
        return (Subplay) getDefaultModelObject();
    }
}
