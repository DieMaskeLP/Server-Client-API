package de.maskfac.ServerSDK.client;

import de.maskfac.ServerSDK.Event;

//Created by Frederic | DieMaskeLP at 06.12.2019, 20:00
public interface ClientConsoleInputEvent extends Event {

    public void onClientConsoleInput(String input, String[] args);

}
