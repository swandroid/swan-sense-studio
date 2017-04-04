package interdroid.swan.remote;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Roshan Bharath Das on 03/04/2017.
 */

public class ServerConnectionSocket implements Runnable {

    private String url;

    private int port;

    private String applicationKey;

    Socket clientSocket;

    ObjectOutputStream out = null;

    public ServerConnectionSocket(String url, int port, String applicationKey){

        this.url =url;
        this.applicationKey = applicationKey;
        this.port =port;

    }


   /* public void sendResult(String field) {

        try {
            this.out.writeObject(""+field);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/


    @Override
    public void run() {

        try {
            clientSocket = new Socket(url, port);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Socket getClientSocket(){
        return this.clientSocket;
    }

    public ObjectOutputStream getObjectOutputStream(){

        return this.out;
    }




}
