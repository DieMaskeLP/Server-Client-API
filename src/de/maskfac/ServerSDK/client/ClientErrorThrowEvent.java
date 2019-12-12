package de.maskfac.ServerSDK.client;

import de.maskfac.ServerSDK.Event;

//Created by Frederic | DieMaskeLP at 07.12.2019, 21:59
public interface ClientErrorThrowEvent extends Event {

    public void onClientErrorThrown(Exception e);

}
