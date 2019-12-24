package de.maskfac.ServerSDK.server;

import de.maskfac.ServerSDK.Event;

//Created by Frederic | DieMaskeLP at 23.12.2019, 19:38
public interface ServerSuccessfulStartEvent extends Event {

    public void onServerStarted(Server server);

}
