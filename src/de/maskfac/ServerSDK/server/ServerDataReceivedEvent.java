package de.maskfac.ServerSDK.server;

import de.maskfac.ServerSDK.Event;
import de.maskfac.ServerSDK.client.Client;

//Created by Frederic | DieMaskeLP at 28.11.2019, 17:26
public interface ServerDataReceivedEvent extends Event {

    public void onServerDataReceived(Client client, Server server, String data);

}
