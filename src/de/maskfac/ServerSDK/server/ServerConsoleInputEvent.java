package de.maskfac.ServerSDK.server;

import de.maskfac.ServerSDK.Event;

//Created by Frederic | DieMaskeLP at 06.12.2019, 20:00
public interface ServerConsoleInputEvent extends Event {

    public void onServerConsoleInput(Server server, String input);

}
