package de.maskfac.ServerSDK.client;

import de.maskfac.ServerSDK.Event;

//Created by Frederic | DieMaskeLP at 09.12.2019, 18:22
public interface ClientConnectToServerEvent extends Event {

    public void onClientConnect(Client client);

}
