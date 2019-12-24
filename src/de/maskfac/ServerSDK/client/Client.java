package de.maskfac.ServerSDK.client;

import de.maskfac.ServerSDK.Event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

//Created by Frederic | DieMaskeLP at 01.12.2019, 19:37
public class Client {

    public Socket socket;
    private List<Event> events = new ArrayList<>();
    private PrintWriter writer;
    private BufferedReader reader;
    boolean areStreamsOpen = false;
    private Thread consoleInputThread, dataListenerThread;
    private boolean consoleInputEnabled;

    public void registerEvent(Event event){
        events.add(event);
    }

    public Client(){
        socket = new Socket();
    }

    public Client(Socket socket, PrintWriter writer, BufferedReader reader, boolean areStreamsOpen){
        this.areStreamsOpen = areStreamsOpen;
        this.writer = writer;
        this.reader = reader;
        this.socket = socket;
    }

    public boolean isAreStreamsOpen(){
        return areStreamsOpen;
    }

    public boolean isClosed(){
        return socket.isClosed();
    }

    private void throwException(Exception e){
        for (Event event : events){
            if (event instanceof ClientErrorThrowEvent){
                ((ClientErrorThrowEvent) event).onClientErrorThrown(e);
            }
        }
    }

    public String getAddress(){
        return socket.getRemoteSocketAddress().toString();
    }

    public void breakConnection() {
        try {
            areStreamsOpen = false;
            writer.close();
            reader.close();
            socket.close();
            socket = new Socket();
        } catch (IOException ignored) {
            throwException(ignored);
        }
    }

    public InetAddress getInetAddress(){
        return socket.getInetAddress();
    }

    public SocketAddress getRemoteSocketAddress(){
        return socket.getRemoteSocketAddress();
    }

    public void setConsoleInputEnabled(boolean value){
        if (consoleInputThread != null){
            consoleInputEnabled = value;
            if (value){
                consoleInputThread.start();
            } else consoleInputThread.stop();
        }
    }

    public void setConsole(String prefix, ClientConsoleInputEvent event){
        consoleInputThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (true){
                    String s;
                    try {
                        System.out.print(prefix);
                        while ((s = reader.readLine()) != null && consoleInputEnabled) {
                            event.onClientConsoleInput(Client.this, s, s.split(" "));
                            System.out.print(prefix);
                        }
                    } catch (IOException e){
                        throwException(e);
                        break;
                    }

                }
            }
        });
    }

    public void sendData(String data) {
        String s = data.endsWith("\n") ? data : data + "\n";
        writer.write(s);
        for (Event event : events){
            if (event instanceof ClientDataSentEvent){
                ((ClientDataSentEvent) event).onClientDataSent(this, s);
            }
        }
        writer.flush();
    }

    private void startListeningForData(){
        dataListenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isConnected() && !isClosed() && areStreamsOpen){
                    String s;
                    try {
                        while ((s = reader.readLine()) != null && areStreamsOpen){
                            for (Event event : events){
                                if (event instanceof ClientDataReceivedEvent){
                                    ((ClientDataReceivedEvent) event).onClientDataReceived(Client.this, s);
                                }
                            }
                        }
                    } catch (IOException e) {
                        throwException(e);
                        areStreamsOpen = false;
                        break;
                    }
                }
            }
        });
        dataListenerThread.start();
    }

    public PrintWriter getPrintWriter() {
       return writer;
    }

    public BufferedReader getBufferedReader() {
            return reader;
    }

    public void connectToServer(SocketAddress socketAddress) {
        try {
            Client c = this;

            socket.connect(socketAddress);
            writer = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            areStreamsOpen = true;

            for (Event event : events){
                if (event instanceof ClientConnectToServerEvent){
                    ((ClientConnectToServerEvent) event).onClientConnect(Client.this);
                }
            }

            startListeningForData();

        } catch (IOException e) {
            throwException(e);
        }
    }

    public boolean isConnected() {
        return socket.isConnected();
    }
}
