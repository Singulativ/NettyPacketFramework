/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.server;

public interface ServerEventListener {

    void onClientConnected(Client client);
    void onClientDisconnected(Client client);

}
