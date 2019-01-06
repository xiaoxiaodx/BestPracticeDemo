package com.example.dmj.wificonnecttest;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClient extends Thread {

    private int servicePort = 0;
    private String serviceIp = "";
    String TAG = "TcpClient*";
    Socket socket;
    OutputStream outputStream;
    boolean isConnect = false;

    public TcpClient(int servicePort, String serviceIp) {
        super();
        this.servicePort = servicePort;
        this.serviceIp = serviceIp;
    }

    @Override
    public void run() {
        InetAddress ipAddress;
        try {
            ipAddress = InetAddress.getByName(serviceIp);
            socket = new Socket(ipAddress, servicePort);
            outputStream = socket.getOutputStream();
            if (socket.isConnected()) {
                isConnect = true;
                ReviceThread reviceThread = new ReviceThread();
                reviceThread.start();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ReviceThread extends Thread{
        @Override
        public void run() {

            try {
                while (isConnect) {
                    InputStream inputStream = socket.getInputStream();
                    byte[] buff = new byte[1024];
                    int len = inputStream.read(buff);
                    if (len > 0) {
                        byte[] validBuff = new byte[len];
                        System.arraycopy(buff, 0, validBuff, 0, len);
                        Log.e(TAG, validBuff.toString());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
