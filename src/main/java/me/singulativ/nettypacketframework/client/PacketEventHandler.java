/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.client;

import io.netty.channel.Channel;
import me.singulativ.nettypacketframework.packet.IPacket;
import me.singulativ.nettypacketframework.core.PacketListener;

import java.util.HashMap;

public class PacketEventHandler {

    private HashMap<Class<? extends IPacket>, PacketListener> listeners = new HashMap<>();

    public void setListener(Class<? extends IPacket> packetClass, PacketListener packetListener) {
        listeners.put(packetClass, packetListener);
    }

    public void call(IPacket packet, Channel sender) {
        listeners.get(packet.getClass()).call(packet, sender);
    }

}
