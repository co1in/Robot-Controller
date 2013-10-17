package com.nashobarobotics.robotcontroller;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Network
{
    private NetworkTable networkTable;

    String address;
    PrintWriter writer;
    BufferedReader reader;

    public Network()
    {

    }

    public void connect(String address)
    {
        this.address = address;

        NetworkTable.setClientMode();
        NetworkTable.setIPAddress(address);
        networkTable = NetworkTable.getTable("accelerometer-values");
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
                networkTable.putNumber("x", Network.this.values[0]);
                networkTable.putNumber("y", Network.this.values[1]);
                networkTable.putNumber("z", Network.this.values[2]);
            }
        }).start();
    }
}