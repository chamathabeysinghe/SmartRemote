package lk.ac.mrt.cse.smartremotecontroller.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by chamath on 7/7/17.
 */

public class BluetoothManager {
    private static BluetoothManager bluetoothManager;
    private final String DEVICE_NAME = "Gesture";
    private final String DEVICE_ADDRESS = "20:16:01:20:45:37";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;    //Bluetooth device connected -chamath
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    boolean deviceConnected = false;

    Thread thread;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;
    int i = 0;
    private int[] values;


    boolean initError = false;
    String error;
    String getStringValue;


    Context context;


    private boolean connected=false;
    private BluetoothManager(Context context) {
        this.context = context;
    }
    public static BluetoothManager getInstance(Context context) {
        if (bluetoothManager == null)
            bluetoothManager = new BluetoothManager(context);
        return bluetoothManager;
    }

    public boolean BTinit() {
        boolean found = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Device doesnt Support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) context).startActivityForResult(enableAdapter, 0);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.isEmpty()) {
            Toast.makeText(context, "Please Pair the Device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                Log.e("MYCODE", "NICEEEE");
                Log.e("MYCODE", iterator.getAddress());
                if (iterator.getName().equals(DEVICE_NAME)) {
                    device = iterator;

                    found = true;
                    break;
                }
            }
        }
        Log.e("MYCODE", "initialize " + found);
        return found;
    }

    public boolean BTconnect() {
        boolean connected = true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }
        if (connected) {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        Log.e("MYCODE", "Connected   " + connected);
        return connected;
    }

    String beginListenForData(){
        try {
            if (i >= 5) {
                i = 0;
            }
            int byteCount = inputStream.available();
            if (byteCount > 0) {
                Log.e("MYCODE", "RUNNSSS");
                byte[] rawBytes = new byte[byteCount];
                inputStream.read(rawBytes);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final String string1 = new String(rawBytes, "UTF-8");
                final String string = string1.trim();
                if (string == "t") {
                    i = 0;
                }
                if(string!=null)
                Log.e("MYCODE",string);
                return string;

            }
        } catch (IOException ex) {
            stopThread = true;
        }


//        try {
//            int byteCount = inputStream.available();
//            Log.e("MYCODE","BYTECOUNT "+byteCount);
//            if (byteCount > 0) {
//                byte[] rawBytes = new byte[byteCount];
//                inputStream.read(rawBytes);
//                final String string1 = new String(rawBytes, "UTF-8");
//                final String string = string1.trim();
//                Log.e("MYCODE",string);
//                return string;
//            }
//        }
//        catch (Exception ex){
//            ex.printStackTrace();
//            return "ERROR ex happened";
//
//        }
        return "ERROR";
    }
    void beginSendingData(final String message) {

        try {
            BufferedOutputStream i=new BufferedOutputStream(outputStream);
            i.write(message.getBytes());
            i.flush();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    String beginListenForDataTest() {
        final Handler handler = new Handler();
        stopThread = false;
        values = new int[5];

        buffer = new byte[1024];
        Thread thread = new Thread(new Runnable() {
            public synchronized void run() {
                while(true) {
                    try {
                        if (i >= 5) {
                            i = 0;
                        }
                        int byteCount = inputStream.available();
                        if (byteCount > 0) {
                            Log.e("MYCODE", "RUNNSSS");
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            final String string1 = new String(rawBytes, "UTF-8");
                            final String string = string1.trim();
                            if (string == "t") {
                                i = 0;
                            }
                            handler.post(new Runnable() {
                                public synchronized void run() {


                                    if (string != null) {
                                        Log.e("MYCODE", string);

                                    }


                                }
                            });


                        }
                    } catch (IOException ex) {
                        stopThread = true;
                    }
                }

            }

        });

        thread.start();
        return "MYCODE";
    }

    public String getSignalTest() {
        return "#323ffaa";
    }

    public void init(){
        if(connected)return;
        if(BTinit()){
            if(BTconnect()){
                Log.e("MYCODE","COnnected in the init");
                connected=true;
            }
        }
    }

    public String getSignal() {
        Log.e("MYCODE","Get Signal");
        String signal="";
        init();
        if (connected) {
               Log.e("MYCODE","Get the signal in BT 234322222");
                while(!signal.matches("\\*\\*\\*\\*\\*.+:.+:.+:.+:.+:.+\\+\\+\\+\\+\\+")){
                    deviceConnected = true;
                    beginSendingData("?READ?");
                    signal=beginListenForData();
                }
        }
        Log.e("MYCODE","Signal matches "+signal);
        return signal;
    }

    public void sendSignal(String signal, String brand) {
        init();
        if(connected){
            Log.e("MYCODE","sending first signal");
            beginSendingData("?WRITE?");
            String message=signal;
            Log.e("MYCODE","sending second signal "+message);
            beginSendingData(message);
        }

    }


    public void onClickStart() {
        if (BTinit()) {
            if (BTconnect()) {
                Log.e("BT", "Receiving Data");
                deviceConnected = true;
                beginListenForData();
            }
        }
    }
}
