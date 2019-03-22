/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.singulativ.nettypacketframework.packet.types.CallbackPacket;
import me.singulativ.nettypacketframework.packet.types.InquiryPacket;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private PacketIDManager packetIDManager;

    public PacketDecoder(PacketIDManager packetIDManager) {
        this.packetIDManager = packetIDManager;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        //Getting Packet Class by Packet ID
        int packetID = byteBuf.readInt();
        Class packetClass = packetIDManager.getPacketClass(packetID);

        if (packetClass != null) {

            //Create Packet Object and initialize it
            IPacket packet = (IPacket) packetClass.newInstance();
            if (packet instanceof InquiryPacket) {
                ((InquiryPacket) packet).setChannel(channelHandlerContext.channel());
            }
            packet.init(byteBuf);

            //Add Packet to Output
            list.add(packet);

        } else {
            throw new Exception("Cannot process Packet with ID: " + packetID + " (Unknown Packet ID)");
        }
    }

}
