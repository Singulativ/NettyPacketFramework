/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<IPacketOverclass> {

    private PacketIDManager packetIDManager;

    public PacketEncoder(PacketIDManager packetIDManager) {
        this.packetIDManager = packetIDManager;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IPacketOverclass packet, ByteBuf byteBuf) {

        //Writing Packet ID in the ByteBuf
        byteBuf.writeInt(packetIDManager.getPacketID(packet.getClass()));

        //Writing Packet Data in the ByteBuf
        packet.getData(byteBuf);

    }

}
