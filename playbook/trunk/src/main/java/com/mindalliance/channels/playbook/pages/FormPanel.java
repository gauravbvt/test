package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * ...
 */
public class FormPanel extends Panel {

    public FormPanel( String id, IModel content ) {
        super( id, content );
        resetForm();
    }

    private void resetForm() {
        Ref ref = (Ref) getModelObject();
        Class formClass = ref.formClass();
        if ( formClass != null ) try {
            Constructor c = formClass.getConstructor( String.class, Ref.class );
            Component form = (Component) c.newInstance( "form-details", ref );
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
    }

    protected void onModelChanged() {
        resetForm();
    }
}
