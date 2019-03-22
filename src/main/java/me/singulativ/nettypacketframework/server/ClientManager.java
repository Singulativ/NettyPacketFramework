/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.server;

import io.netty.channel.Channel;

import java.util.ArrayList;

public class ClientManager {

    private ArrayList<Client> clients = new ArrayList<>();

    public void addClient(Client client) {
        clients.add(client);
    }

    public void removeClient(Client client) {
        clients.remove(client);
    }

    public Client getClientByChannel(Channel channel) {
        for (Client client : clients) {
            if (client.compareChannel(channel))
                return client;
        }
        return null;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }
}
