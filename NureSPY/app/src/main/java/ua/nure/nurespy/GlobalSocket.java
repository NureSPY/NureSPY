package ua.nure.nurespy;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import android.app.Application;

import java.net.URI;
import java.net.URISyntaxException;

public class GlobalSocket extends Application {
    private Socket mSocket;

    private final String TAG = "Application Class";

    public Socket getSocket() {
        return mSocket;
    }

    public Socket createSocket() {

        try {
            mSocket = IO.socket( new URI("http://localhost:3002/"));
        } catch (URISyntaxException URIse) {
            URIse.printStackTrace();
        }

        return mSocket;
    }
    }
