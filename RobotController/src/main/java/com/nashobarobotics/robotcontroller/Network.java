package com.nashobarobotics.robotcontroller;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Network
{
    Socket socket;
    PrintWriter writer;
    BufferedReader reader;

    public Network()
    {

    }

    public void connect(String address, int port)
    {
        try
        {
            socket = new Socket(address, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e)
        {
            Log.e("Connection Error", e.toString());
        }
    }

    public void sendValues(float[] values)
    {
        String sendString = new String();

        for(int i = 0; i < 3; i++)
        {
            sendString += values[i] + ":";
        }

        writer.println(sendString);
    }
}
