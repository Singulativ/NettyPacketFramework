/*
 * Copyright (c) 2018 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.server;

import java.util.ArrayList;
import java.util.List;

public class ServerEventManager {

    private List<ServerEventListener> listenerList = new ArrayList<>();

    protected void registerListener(ServerEventListener listener) {
        listenerList.add(listener);
    }

    protected void callClientConnected(Client client) {
        for (ServerEventListener listener : listenerList) {
            listener.onClientConnected(client);
        }
    }

    protected void callClientDisconnected(Client client) {
        for (ServerEventListener listener : listenerList) {
            listener.onClientDisconnected(client);
        }
    }

}
