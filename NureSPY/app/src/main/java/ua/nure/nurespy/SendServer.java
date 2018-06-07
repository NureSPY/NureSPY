package ua.nure.nurespy;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SendServer {
    private static String server = "http://xxx.xxx.x.xxx/";
    public static String mlogin = null;
    public static String mpassword = null;

    public static String sendRegData(String login, String password) throws IOException {

        String result = null;
        mlogin = login;
        mpassword = password;

        try {
            URL url = new URL("" + server + "apiregistration/create/" + mlogin + "/" + mpassword + "");

            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(in));

                result = r.readLine();

                Log.w("res", "" + result + "");


            } else {

            }


        } catch (MalformedURLException e) {
        } catch (IOException e1) {
        }

        return result;

    }
}
