package de.maskfac.ServerSDK.server;

import de.maskfac.ServerSDK.Event;
import de.maskfac.ServerSDK.client.Client;

//Created by Frederic | DieMaskeLP at 04.12.2019, 15:49
public interface ServerClientDisconnectEvent extends Event {

    public void onServerClientDisconnect(Client client, Server server);

}
