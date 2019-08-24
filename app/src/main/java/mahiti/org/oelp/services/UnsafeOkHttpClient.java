package mahiti.org.oelp.services;


import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import mahiti.org.oelp.utils.MyOwnRuntime;
import okhttp3.OkHttpClient;

/**
 * Created by sandeep HR on 01/02/19.
 */
public class UnsafeOkHttpClient {


    private UnsafeOkHttpClient() {
    }

    public static OkHttpClient getUnsafeOkHttpClient() throws MyOwnRuntime {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            /*
                            Doing Nothing
                             */
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            /*
                            Doing Nothing
                             */
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
            .sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0])
            .hostnameVerifier((hostname, session) -> {
                return hostname != null && session != null;

            });
            return builder.build();
        } catch (Exception e) {
            throw new MyOwnRuntime(UnsafeOkHttpClient.class.getSimpleName(),e);
        }
    }


}
