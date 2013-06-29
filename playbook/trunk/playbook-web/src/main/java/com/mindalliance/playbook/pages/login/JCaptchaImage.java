package com.mindalliance.playbook.pages.login;

import com.octo.captcha.service.image.ImageCaptchaService;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * The display of an actual captcha.
 */
public class JCaptchaImage extends WebPage {

    private static final Logger LOG = LoggerFactory.getLogger( JCaptchaImage.class );

    @SpringBean
    private ImageCaptchaService captchaService;

    public JCaptchaImage() {
    }

    @Override
    public void renderPage() {
        Session session = getSession();
        WebResponse response = (WebResponse) getResponse();

        LOG.debug( "Rendering captcha for session {}", session.getId() );
        byte[] bytes = encode( captchaService.getImageChallengeForID( session.getId(), session.getLocale() ) );
        response.setContentLength( (long) bytes.length );
        setHeaders( response );
        response.write( bytes );
    }

    @Override
    protected void setHeaders( WebResponse response ) {
        super.setHeaders( response );

        response.setHeader( "Cache-Control", "no-store" );
        response.setHeader( "Pragma", "no-cache" );
        response.setDateHeader( "Expires", Time.START_OF_UNIX_TIME );
        response.setContentType( "image/jpeg" );
    }

    private static byte[] encode( RenderedImage image ) {
        // TODO resize and convert to PNG

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();       
        try {
            ImageIO.write( image, "jpg", outputStream );
 
        } catch ( IOException e ) {
            LOG.error( "Error converting captcha to jpg", e );
        }

        return outputStream.toByteArray();
    }
    
    

}
