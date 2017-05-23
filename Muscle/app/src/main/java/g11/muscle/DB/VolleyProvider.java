package g11.muscle.DB;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import g11.muscle.R;


public class VolleyProvider {
    private static VolleyProvider instance;
    private RequestQueue queue;

    private VolleyProvider(Context ctx){

        try {
            InputStream instream = ctx.getApplicationContext().getResources()
                    .openRawResource(R.raw.keystorev1);
            KeyStore trustStore = KeyStore.getInstance("BKS");

            try {
                trustStore.load(instream,"mustcle".toCharArray());


            } catch (Exception e) {

                e.printStackTrace();
            } finally {

                try {instream.close();} catch (Exception ignore) {}
            }

            String algorithm = TrustManagerFactory.getDefaultAlgorithm();

            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(algorithm);

            tmf.init(trustStore);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);


            final SSLSocketFactory sslFactory = context.getSocketFactory();
            //hurlStack = new HurlStack(null, sslFactory);

            HurlStack hurlStack = new HurlStack() {
                @Override
                protected HttpURLConnection createConnection(URL url) throws IOException {
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                    try {
                        httpsURLConnection.setSSLSocketFactory(sslFactory);
                        httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return httpsURLConnection;
                }
            };


            queue = Volley.newRequestQueue(ctx, hurlStack);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static VolleyProvider getInstance(Activity callingActivity){
        if(instance == null){
            updateAndroidSecurityProvider(callingActivity);
            instance = new VolleyProvider(callingActivity.getApplicationContext());
        }
        return instance;
    }

    public RequestQueue getQueue(){
        return this.queue;
    }

    public <T> Request<T> addRequest(Request<T> req) {
        return getQueue().add(req);
    }

    public <T> Request<T> addRequest(Request<T> req, String tag) {
        req.setTag(tag);
        return getQueue().add(req);
    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                //HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                //return hv.verify("138.68.158.127", session);
            }
        };
    }
    // to deal with sslv3 problems
    private static void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(callingActivity);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            apiAvailability.getErrorDialog(callingActivity, e.getConnectionStatusCode(), 0).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }

}
