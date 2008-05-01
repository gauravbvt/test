package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * ...
 */
public class FormPanel extends Panel {

    private AbstractElementForm form;

    public FormPanel( String id, IModel content ) {
        super( id, content );
        setOutputMarkupId( true );
        resetForm();
    }

    public void terminate() {
        if ( form != null )
            form.terminate();
    }

    public void resetForm() {
        Ref ref = (Ref) getModelObject();
        if ( ref != null ) {
            Class formClass = ref.formClass();
            if ( formClass != null ) try {
                Constructor c = formClass.getConstructor( String.class, Ref.class );
                form = (AbstractElementForm) c.newInstance( "form-details", ref );
                addOrReplace( form );
                return;

            } catch ( NoSuchMethodException e ) {
                e.printStackTrace();
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                e.printStackTrace();
            } catch ( InstantiationException e ) {
                e.printStackTrace();
            }

            addOrReplace( new Label( "form-details", ref.deref().toString() ) );
        } else {
            addOrReplace( new Label( "form-details", "" ) );
        }
    }

    protected void onModelChanged() {
        resetForm();
    }
}
