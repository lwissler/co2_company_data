package auxiliary;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class SSLHelper {

    static public Connection getConnection(String url){
    	String cookie = "VnVlL2NxYVNBSHFMbmh5Ry9DaDRHdEc4cjBwSE9ZNGRUYldEL2VFTDdhRk5DN0s4ZUhoeWlwOXlKY3VXcGJ4S1Z5QkhSYTI1OGQzek9rM3IxZTJDZHY1STJFZjJ1OU9nRmZLQlZ2QTJqVUJGdnY5M0RoV3krMldHYlZOOUVRQUtXOU14VTA4ektSalhVQXRVRkRiQnpDeFdKdEdmeGwzS2RsU05WTCsyOTFWdFZDVFhmL0dkK3R3ZDlDQjU5dGxkemZWdWRrMjZBN21FUlQrTG5GZVNCeU1VT3JnZFROZVJYTngxZlZwNWFEWXFPaXAyR2tnSms0Ui9WRFNCRXBrL3J4ZDZuYXVYWk1kakt6Vjc2S0dqcUJqeXFOR3Q0ZXYydHlHSnR4bWI2TSs1WHRhUG8xa0N2VDZ3MEc0K3hyOVJkV2MyVGxFMWhvQ2pqUU9zTUJCNnVnPT0tLUM3blBzazB0WlJwWXFlSVkrN2hPK2c9PQ%3D%3D--163497a63ea57e9e0a6d93060e0c0267dc29a2fd";
        return Jsoup.connect(url).cookie(" _wikirate_session", cookie).sslSocketFactory(SSLHelper.socketFactory());
    }

    static private SSLSocketFactory socketFactory() {
    	X509TrustManager[] trustAllCerts = new X509TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

//            @Override
//            public void checkClientTrusted(X509Certificate[] certs, String authType) {
//            }

//            public void checkServerTrusted(X509Certificate[] certs, String authType) {
//            }

			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				
			}
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory result = sslContext.getSocketFactory();

            return result;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }
}