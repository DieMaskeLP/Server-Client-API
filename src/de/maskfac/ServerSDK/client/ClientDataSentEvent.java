package de.maskfac.ServerSDK.client;

import de.maskfac.ServerSDK.Event;

//Created by Frederic | DieMaskeLP at 30.11.2019, 20:23
public interface ClientDataSentEvent extends Event {

    public void onClientDataSent(Client client, String rawData);

}
