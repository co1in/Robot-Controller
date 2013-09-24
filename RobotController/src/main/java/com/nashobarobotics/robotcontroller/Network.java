package com.nashobarobotics.robotcontroller;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Network
{
    String address;
    int port;
    Socket socket;
    PrintWriter writer;
    BufferedReader reader;

    public Network()
    {

    }

    public void connect(String address, int port)
    {
        this.address = address;
        this.port = port;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    socket = new Socket(Network.this.address, Network.this.port);
                    writer = new PrintWriter(socket.getOutputStream(), true);
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
                catch (IOException e)
                {
                    Log.e("Connection Error", e.toString());
                }
            }
        }).start();
    }

    private float[] values;
    public void sendValues(float[] values)
    {
        this.values = values;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String sendString = new String();

                for(int i = 0; i < 3; i++)
                {
                    sendString += Network.this.values[i] + ":";
                }
                writer.println(sendString);
            }
        }).start();
    }
}
