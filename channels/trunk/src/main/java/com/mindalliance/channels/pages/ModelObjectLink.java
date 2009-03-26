package com.mindalliance.channels.pages;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.InternalFlow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Place;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * A link to an aggregate object.
 * @todo rework to use bookmarkable page links.
 */
public class ModelObjectLink extends ExternalLink {

    public ModelObjectLink( String id, IModel<? extends ModelObject> mo ) {
        this( id, mo, null );
    }

    public ModelObjectLink( String id, IModel<? extends ModelObject> mo, IModel<String> text)  {
        this( id, mo, text, null );
    }

    public ModelObjectLink(
            String id, final IModel<? extends ModelObject> mo, IModel<String> text, final String hint ) {
        super(
            id,
            new AbstractReadOnlyModel<String>() {
                @Override
                public String getObject() {
                    ModelObject obj = mo.getObject();
                    String result;
                    if ( obj instanceof Scenario )
                        result = linkFor( (Scenario) obj );
                    else if ( obj instanceof Role )
                        result = linkFor( (Role) obj );
                    else if ( obj instanceof Part )
                        result = linkFor( (Part) obj );
                    else if ( obj instanceof Actor )
                        result = linkFor( (Actor) obj );
                    else if ( obj instanceof Organization )
                        result = linkFor( (Organization) obj );
                    else if ( obj instanceof InternalFlow )
                        result = linkFor( (InternalFlow) obj, hint );
                    else {
                        result = "#";
                        if ( obj != null )
                            LoggerFactory.getLogger( ModelObjectLink.class ).warn(
                                    MessageFormat.format( "Links to {0} are not implemented",
                                                          obj.getClass() ) );
                    }
                    return result;
                }
            },
            text );
    }

    public static String linkFor( Scenario scenario ) {
        return linkFor( scenario.getDefaultPart() );
    }

    public static String linkForEntity( ModelObject mo ) {
        if (mo instanceof Actor) return linkFor( (Actor) mo);
        else if (mo instanceof Role) return linkFor( (Role) mo);
        else if (mo instanceof Organization) return linkFor( (Organization) mo);
        else if (mo instanceof Place) return linkFor( (Place) mo);
        else throw new IllegalArgumentException( mo + " is not an entity");
    }

    public static String linkFor( Role role ) {
        return MessageFormat.format( "role.html?id={0,number,0}", role.getId() );                  // NON-NLS
    }

    public static String linkFor( Organization organization ) {
        return MessageFormat.format( "organization.html?id={0,number,0}", organization.getId() );  // NON-NLS
    }

    public static String linkFor( Actor actor ) {
        return MessageFormat.format( "actor.html?id={0,number,0}", actor.getId() );                // NON-NLS
    }

    public static String linkFor( Place place ) {
        return MessageFormat.format( "place.html?id={0,number,0}", place.getId() );                // NON-NLS
    }

    public static String linkFor( Part part ) {
        return MessageFormat.format(
                "node.html?scenario={0,number,0}&node={1,number,0}",                                    // NON-NLS
                part.getScenario().getId(),
                part.getId()

        );
    }
    private static String linkFor( InternalFlow flow, final String hint ) {
        final Node source = ( hint != null && hint.equals( "target" )) ? flow.getTarget() : flow.getSource();
        return MessageFormat.format(
                "node.html?scenario={0,number,0}&node={1,number,0}&expand={2,number,0}",               // NON-NLS
                source.getScenario().getId(),
                source.getId(),
                flow.getId()
        );
    }
}
