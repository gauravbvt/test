package com.mindalliance.channels.pages;

import com.mindalliance.channels.engine.command.Change;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * A link to a model object.
 */
public class ModelObjectLink extends AbstractUpdatablePanel {

    private IModel<? extends ModelObject> moModel;
    private IModel<String> textModel;
    private String hint;
    private String css;

    public ModelObjectLink( String id, IModel<? extends ModelObject> mo ) {
        this( id, mo, null );
    }

    public ModelObjectLink( String id, IModel<? extends ModelObject> mo, IModel<String> text ) {
        this( id, mo, text, null );
    }

    public ModelObjectLink(
            String id, final IModel<? extends ModelObject> mo, IModel<String> text, String hint ) {
        this( id, mo, text, hint, null );
    }

    public ModelObjectLink(
            String id, final IModel<? extends ModelObject> mo, IModel<String> text, String hint, String css ) {
        super( id );
        moModel = mo;
        textModel = text;
        this.hint = hint;
        this.css = css;
        init();
    }

    private void init() {
        final ModelObject mo = moModel.getObject();
        AjaxFallbackLink link = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                if ( mo != null ) {
                    if ( mo.isEntity() ) {
                        update( target, new Change( Change.Type.Expanded, mo ) );
                    } else {
                        update( target, new Change( Change.Type.Selected, mo ) );
                    }
                }
            }
        };
        add( link );
        if ( hint != null && !hint.isEmpty() ) {
            link.add( new AttributeModifier( "title", true, new Model<String>( hint ) ) );
        }
        if ( css != null ) {
            link.add( new AttributeModifier( "class", true, new Model<String>( css ) ) );
        } else {
            if ( mo != null ) {
                link.add( new AttributeModifier(
                        "class",
                        true,
                        new Model<String>(
                                mo.isEntity()
                                        ? "entity-link"
                                        : mo instanceof Part
                                        ? "part-link"
                                        : mo instanceof Flow
                                        ? "flow-link"
                                        : "model-object-link"
                        ) ) );
            }
        }
        Label textLabel = new Label( "text", textModel );
        link.add( textLabel );
    }
}
