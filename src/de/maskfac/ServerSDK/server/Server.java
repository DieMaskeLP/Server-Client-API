package de.maskfac.ServerSDK.server;

import de.maskfac.ServerSDK.Event;
import de.maskfac.ServerSDK.client.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

//Created by Frederic | DieMaskeLP at 01.12.2019, 19:11
public class Server {

    private static int defaultPort = 4556;
    private int port;
    private ServerSocket serverSocket;
    boolean isRunning = false;
    private static List<Event> events = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();
    private Server thisServer;
    private Thread consoleInputThread;
    private boolean consoleInputEnabled;

    public void setConsoleInputEnabled(boolean value){
        if (consoleInputThread != null){
            consoleInputEnabled = value;
            if (value){
                consoleInputThread.start();
            } else consoleInputThread.stop();
        }
    }

    public void setConsole(String prefix, ServerConsoleInputEvent event){
        consoleInputThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (true){
                    String s;
                    try {
                        System.out.print(prefix);
                        while ((s = reader.readLine()) != null && consoleInputEnabled) {
                            event.onServerConsoleInput(s);
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


    public List<Client> getConnectedClients(){
        return clients;
    }

    public static int getDefaultPort() {
        return defaultPort;
    }

    public Server() {
        port = defaultPort;
        thisServer = this;
    }

    public Server(int port) {
        this.port = port;
        thisServer = this;
    }

    public void registerEvent(Event event) {
        events.add(event);
    }

    private void throwException(Exception e){
        for (Event event : events){
            if (event instanceof ServerErrorThrowEvent){
                ((ServerErrorThrowEvent) event).onServerErrorThrown(thisServer, e);
            }
        }
    }

    public InetAddress getInetAddress() {
        return serverSocket.getInetAddress();
    }

    public void sendDataToConnectedClients(String data){
        for (Client client : clients){
            client.sendData(data.endsWith("\n") ? data : data + "\n");
        }
    }

    public void closeConnections(){
        for (Client client : clients){
            client.breakConnection();
        }
        clients.clear();
    }

    public SocketAddress getLocalSocketAddress() {
        return serverSocket.getLocalSocketAddress();
    }

    public boolean startServer() {
        if (!isRunning) {
            isRunning = true;
            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        serverSocket = new ServerSocket(port);

                        while (isRunning) {

                            Socket socket = serverSocket.accept();
                            PrintWriter writer = new PrintWriter(socket.getOutputStream());
                            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            Client client = new Client(socket, writer, reader, true);
                            clients.add(client);
                            for (Event event : events) {
                                if (event instanceof ServerClientConnectEvent) {
                                    ((ServerClientConnectEvent) event).onServerClientConnect(client, thisServer);
                                }
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (client.isConnected() && !client.isClosed()) {
                                        try {
                                            String s;
                                            while (client.isAreStreamsOpen() && (s = reader.readLine()) != null) {
                                                for (Event event : events) {
                                                    if (event instanceof ServerDataReceivedEvent) {
                                                        ((ServerDataReceivedEvent) event).onServerDataReceived(client, thisServer, s);
                                                    }
                                                }
                                            }
                                        } catch (IOException e) {
                                            throwException(e);
                                            break;
                                        }
                                    }
                                    for (Event event : events) {
                                        if (event instanceof ServerClientDisconnectEvent) {
                                            ((ServerClientDisconnectEvent) event).onServerDisconnect(client, thisServer);
                                        }
                                    }
                                    clients.remove(client);
                                }
                            }).start();


                        }
                    } catch (IOException e) {
                        throwException(e);
                    }
                }
            });
            thread1.start();
            return true;

        }
        return false;
    }

}
