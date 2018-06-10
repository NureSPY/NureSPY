package ua.nure.nurespy;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;

import android.app.Application;

import java.net.URI;
import java.net.URISyntaxException;

public class Global extends Application {
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(new URI("http://178.165.46.109:3002/"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }



}
