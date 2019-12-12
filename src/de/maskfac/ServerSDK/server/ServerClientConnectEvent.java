package de.maskfac.ServerSDK.server;

import de.maskfac.ServerSDK.Event;
import de.maskfac.ServerSDK.client.Client;

import java.net.Socket;

//Created by Frederic | DieMaskeLP at 30.11.2019, 20:04
public interface ServerClientConnectEvent extends Event {

    public void onServerClientConnect(Client client, Server server);

}
