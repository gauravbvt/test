package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Channel;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Set;

/**
 * A cutesy card for an actor, role or company.
 */
public class VCardPanel extends AbstractUpdatablePanel {

    /** The subject of the card. */
    private final ResourceSpec spec;

    @SpringBean
    private ImagingService imagingService;

    private String prefix;

    public VCardPanel( String id, ResourceSpec spec, String prefix ) {
        this( id, spec, null, null, prefix );
    }

    public VCardPanel(
            String id, ResourceSpec spec, Set<TransmissionMedium> unicasts, Collection<Channel> broadcasts,
            String prefix ) {

        super( id );
        this.prefix = prefix;
        this.spec = spec;

        setDefaultModel( new CompoundPropertyModel<Object>( this ) );
        setRenderBodyOnly( true );
        ModelObject object = getSubject();
        add( new Label( "name" ),
             new Label( "title" ),
             new Label( "description" ).setVisible( !getDescription().isEmpty() ),
             new WebMarkupContainer( "pic" )
                     .add( new AttributeModifier(
                                "src", new Model<String>( getPictureUrl( object ) ) ),
                           new AttributeModifier(
                                "alt", new Model<String>( object == null ? "" : getName() ) ) ),

             new ChannelsBannerPanel( "channels", spec, unicasts, broadcasts )
        );
    }

    private String getPictureUrl( ModelObject object ) {
        if ( object != null ) {
//            String s = object.getImageUrl();
            String s = imagingService.getSquareIconUrl( object );
            if ( s != null )
                try {
                    URI u = new URI( s );
                    return u.isAbsolute() ? s : prefix + s;
                } catch ( URISyntaxException ignored ) {
                    LoggerFactory.getLogger( getClass() ).warn( "Invalid image URL for ", object );
                }
        }

        return getDefaultPictureUrl();
    }

    public String getDefaultPictureUrl() {
        return prefix + ( spec.isActor() ?  "images/actor.user.png"
                        : spec.isOrganization()? "images/organization.building.png"
                        : spec.isRole()? "images/role.png"
                        : "images/system.png" );
    }

    private ModelObject getSubject() {
        return spec.isActor() ? spec.getActor()
             : spec.isOrganization() ? spec.getOrganization()
             : spec.getRole();
    }

    public final String getName() {
        return spec.getReportTitle();
    }

    public final String getDescription() {
        return spec.getDescription();
    }

    public String getTitle() {
        Actor actor = spec.getActor();
        String title;
        if ( actor == null || Actor.UNKNOWN.equals( actor ) ) {
            if ( spec.isOrganization() ) {
                Organization org = spec.getOrganization();
                Place place = org.getLocation();
                return place == null ? "No known address" : place.getFullAddress();

            } else
                title = "";
        } else {
            String t = getQueryService().getTitle( actor );
            if ( t.isEmpty() )
                title = "";
            else
                title = t + ", ";
        }

        String s = spec.toString();
        int as = s.indexOf( " as " );
        String s1 = as >= 0 ? s.substring( as + 4 ) : s ;
        return title + s1;
    }
}
