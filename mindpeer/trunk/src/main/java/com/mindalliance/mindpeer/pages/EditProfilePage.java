// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.pages;

import com.mindalliance.mindpeer.dao.ProfileDao;
import com.mindalliance.mindpeer.dao.TagDao;
import com.mindalliance.mindpeer.model.Profile;
import com.mindalliance.mindpeer.model.Tag;
import com.mindalliance.mindpeer.model.User;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import static java.awt.RenderingHints.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Profile editing form.
 */
public class EditProfilePage extends AbstractUserPage {

    private static final long serialVersionUID = -7107885325009507292L;

    /** Width of resized pictures. */
    private static final int DIMW = 45;

    /** Height of resized pictures. */
    private static final int DIMH = 55;

    private static final Logger LOG = LoggerFactory.getLogger( EditProfilePage.class );

    @SpringBean
    private ProfileDao profileDao;

    @SpringBean
    private TagDao tagDao;

    private FileUpload upload;

    /**
     * Constructor which receives wrapped query string parameters for a request.
     * @param parameters Wrapped query string parameters.
     */
    public EditProfilePage( PageParameters parameters ) {
        super( parameters );
    }

    /**
     * Add default components to the page.
     */
    @Override
    @Secured( { "ROLE_ADMIN", "ROLE_USER" } )
    public void init() {
        super.init();

        Form<Profile> form =
                new Form<Profile>( "profile" ) {
                    private static final long serialVersionUID = 1098034351512492706L;

                    @Override
                    protected void onSubmit() {
                        super.onSubmit();

                        Profile profile = getModelObject();
                        if ( upload != null )
                            profile.setPicture( resize( upload.getBytes() ) );

                        saveUser( profile.getUser() );

                        LOG.debug( "Saved profile for {}", getUserId() );
                        setRedirect( true );
                        setResponsePage( EditProfilePage.class );
                    }
                };

        form.setMultiPart( true );
        form.setMaxSize( Bytes.megabytes( 2L ) );

        add(
            new FeedbackPanel( "feedback" ),

            form.add( new TextField<String>( "profile.name" ),
                      new TextField<String>( "profile.organization" ),
                      new TextField<String>( "profile.location" ),
                      new TextField<String>( "profile.phone" ),
                      new TextField<String>( "profile.fax" ),
                      new TextField<String>( "profile.website" ),
                      new TextField<String>( "email" ),
                      new TextField<String>( "profile.designation" ),
                      new TextArea<String>( "profile.description" ),
                      new TextField<String>( "profile.interests",
                                             new PropertyModel<String>( this, "interests" ) ),
                      new ContextImage( "current", getPictureUrl() ),
                      new FileUploadField( "picture",
                                           new PropertyModel<FileUpload>( this, "upload" ) ),
                      new Button( "reset" ) {
                        @Override
                        public void onSubmit() {
                            super.onSubmit();
                            Profile profile = getProfile();
                            profile.setPicture( null );
                            profileDao.save( profile );
                            LOG.debug( "Profile picture reset for {}", getUserId() );
                        }
                    }
            )
        );
    }

    /**
     * Return the page's title.
     * @return the value of title
     */
    @Override
    public String getTitle() {
        return "Profile";
    }

    /**
     * Return the page's selectedTopItem.
     * @return the value of selectedTopItem
     */
    @Override
    protected int getSelectedTopItem() {
        return 2;
    }

    /**
     * Return the EditProfilePage's profile.
     * @return the value of profile
     */
    public Profile getProfile() {
        User user = (User) getDefaultModelObject();
        return user.getProfile();
    }

    /**
     * Resize a picture into a proper size png.
     *
     * @param picture the given picture
     * @return byte[] the png bytes
     */
    private static byte[] resize( byte[] picture ) {
        try {
            BufferedImage original = ImageIO.read( new ByteArrayInputStream( picture ) );
            BufferedImage result = new BufferedImage( DIMW, DIMH, BufferedImage.TYPE_INT_RGB );

            int oh = original.getHeight();
            int ow = original.getWidth();
            int left;
            int sh;
            int sw;
            if ( DIMW * oh < DIMH * ow ) {
                // DX oh >= DY ow
                // DX oh / ow >= DY
                sh = DIMH;
                sw = DIMH * ow /oh;
                left = ( DIMW - sw ) / 2;
            } else {
                left = 0 ; // ( ow - DIMW * oh / DIMH ) / 2;
                sh = DIMW * oh / ow;
                sw = DIMW;
            }
            Graphics2D g = result.createGraphics();
            g.setRenderingHint( KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC );
            g.setRenderingHint( KEY_RENDERING, VALUE_RENDER_QUALITY );
            g.setRenderingHint( KEY_ANTIALIASING, VALUE_ANTIALIAS_ON );
            g.drawImage( original, left, 0, sw, sh, null );
            g.dispose();

            LOG.debug( "Changed profile picture" );

            return getPngBytes( result );

        } catch ( IOException e ) {
            LOG.error( "Unable to resize uploaded picture", e );
            return picture;
        }
    }

    private static byte[] getPngBytes( BufferedImage result ) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write( result, "png", out );
        return out.toByteArray();
    }

    /**
     * Return the EditProfilePage's upload.
     * @return the value of upload
     */
    public FileUpload getUpload() {
        return upload;
    }

    /**
     * Sets the upload of this EditProfilePage.
     * @param upload the new upload value.
     *
     */
    public void setUpload( FileUpload upload ) {
        this.upload = upload;
    }

    /**
     * Return the EditProfilePage's interests.
     * @return the value of interests
     */
    public String getInterests() {
        StringBuilder buf = new StringBuilder();
        List<Tag> tags = new ArrayList<Tag>( getProfile().getInterests() );
        Collections.sort( tags );

        for ( Tag tag : tags ) {
            if ( buf.length() > 0 )
                buf.append( ", " );
            buf.append( tag.getDescription() );
        }

        return buf.toString();
    }

    /**
     * Sets the interests of this EditProfilePage.
     * @param interests the new interests value.
     */
    public void setInterests( String interests ) {
        Set<String> result = new HashSet<String>();
        if ( interests != null ) {
            StringTokenizer stringTokenizer = new StringTokenizer( interests, "," );
            while ( stringTokenizer.hasMoreTokens() ) {
                String tok = stringTokenizer.nextToken().trim();
                result.add( tok );
            }
        }

        getProfile().setInterests( tagDao.get(  result ) );
    }
}
