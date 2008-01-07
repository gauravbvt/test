package com.mindalliance.channels.modeler.accessors



import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.accessor.AbstractAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import org.ten60.netkernel.xml.representation.IAspectXDA

/**
*
*/
class Login extends AbstractAccessor {
    static final SESSION_COOKIE_NAME = "NETKERNELSESSION";
    static final AUTHENTICATE_QUERY_URI = "ffcpl:/resources/xqueries/authenticate.xq";
    static final PROJECTS_CONFIG_URI = "ffcpl:/etc/projects.xml";
    static final VIEWLINKS_URI = "ffcpl:/etc/viewLinks.xml";
    static final INVALID_LOGIN_URI = "ffcpl:/analyst/view/invalidLogin";

    void source(Context context) {
        use(NetKernelCategory) {
            String userid = context.params.userid;
            String project = context.params.project;
            String password = context.params.password;
            if (userid && password && project
                    && authenticate(userid, password, project, context)) {
                if (context.'cookie?') {
                    def cookie = context.xdaHelper.getCookie(SESSION_COOKIE_NAME);
                    println cookie;
                }
                // Store session credentials
                def session = context.session;
                session.credentials  = userid;
                // Store session project
                session.project =  project;
                // Issue HTTP Redirect
                String url = context.params.url;
                context.subrequest("active:HTTPRedirect",
                        ["operator": string("<url>${url}</url>"),
                                "mimeType": "text/xml",
                                "expired": true])
            }
            else {
                context.subrequest("active:source", [
                        "uri": INVALID_LOGIN_URI,
                        "param": context.params,
                        "link": VIEWLINKS_URI,
                        "url": string(context.params.url),
                        "mimeType": "text/xml",
                        "expired": true,
                        "type": "source"])
            }
        }
    }

    def authenticate(String userid, String password, String project, Context ctx) throws Exception {
        use(NetKernelCategory) {
            def iax = ctx.transrept("active:xquery", IAspectXDA, [
                    'operator': AUTHENTICATE_QUERY_URI,
                    'input': PROJECTS_CONFIG_URI,
                    'userid': string("<string>${userid}</string>"),
                    "password": string("<string>${password}</string>"),
                    "project": string("<string>${project}</string>")
            ])

            String tf = ctx.xdaHelper.textAtXPath(iax, ".//b");
            return tf.equals("t");
        }
    }

}