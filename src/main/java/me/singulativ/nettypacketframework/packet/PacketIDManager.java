/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.packet;

import java.util.ArrayList;

public class PacketIDManager {

    ArrayList<Class<? extends IPacketOverclass>> outgoingPackets = new ArrayList<>();
    ArrayList<Class<? extends IPacketOverclass>> incomingPackets = new ArrayList<>();

    public int getPacketID(Class<? extends IPacketOverclass> packetClass) {
        return outgoingPackets.indexOf(packetClass);
    }

    public Class<? extends IPacketOverclass> getPacketClass(int id) {
        return incomingPackets.get(id);
    }

    public void registerOutgoingPacket(Class<? extends IPacketOverclass> packet) {
        outgoingPackets.add(packet);
    }

    public void registerIncomingPacket(Class<? extends IPacketOverclass> packet) {
        incomingPackets.add(packet);
    }

}
