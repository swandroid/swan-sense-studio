package interdroid.swan.crossdevice.swanplus;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by vladimir on 12/1/15.
 */
public class WDReceiver extends AsyncTask<Void, String, Void> {

    final static int PORT = 2222;

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    public String receive() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String( receivePacket.getData());
            serverSocket.close();

            return sentence.trim();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
