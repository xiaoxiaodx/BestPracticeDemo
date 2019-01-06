package com.example.dmj.wificonnecttest;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpService extends Thread {

    String serviceIp = "";
    int servicePort = 0;
    String TAG = "TcpService*";
    ServerSocket serverSocket;
    Socket socket;
    boolean isListen = true;
    InputStream inputStream;
    public TcpService(String serviceIp, int servicePort) {
        super();
        this.serviceIp = serviceIp;
        this.servicePort = servicePort;
    }
    private Socket getSocket(ServerSocket serverSocket) {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "run: 监听超时");
            return null;
        }
    }
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(servicePort);
            while (isListen) {
                socket = getSocket(serverSocket);
                if (socket != null) {
                    isListen = false;

                    Receive_Thread receive_Thread = new Receive_Thread();
                    receive_Thread.start();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Receive_Thread extends Thread{

        @Override
        public void run() {
            try {
                while (!isListen) {
                    final byte[] buffer = new byte[1024];//创建接收缓冲区
                    if(socket!= null){
                        inputStream = socket.getInputStream();
                        if (inputStream.available() != 0) {
                            final int len = inputStream.read(buffer);//数据读出来，并且返回数据的长度
                            byte[] by = new byte[len];
                            System.arraycopy(buffer, 0, by, 0, len);
                            Log.e(TAG,by.toString());
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
