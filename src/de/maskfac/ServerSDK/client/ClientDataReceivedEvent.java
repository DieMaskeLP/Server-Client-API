package de.maskfac.ServerSDK.client;

import de.maskfac.ServerSDK.Event;

//Created by Frederic | DieMaskeLP at 28.11.2019, 17:42
public interface ClientDataReceivedEvent extends Event {

    public void onClientDataReceived(Client client, String data);

}
