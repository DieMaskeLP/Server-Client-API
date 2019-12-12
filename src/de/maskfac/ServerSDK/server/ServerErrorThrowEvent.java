package de.maskfac.ServerSDK.server;

import de.maskfac.ServerSDK.Event;

//Created by Frederic | DieMaskeLP at 07.12.2019, 21:59
public interface ServerErrorThrowEvent extends Event {

    public void onServerErrorThrown(Server server, Exception e);

}
