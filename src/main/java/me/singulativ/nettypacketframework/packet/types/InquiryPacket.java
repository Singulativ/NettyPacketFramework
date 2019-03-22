/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.packet.types;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import me.singulativ.nettypacketframework.packet.IPacket;
import me.singulativ.nettypacketframework.utils.ByteUtils;

import java.util.UUID;

public abstract class InquiryPacket implements IPacket {

    private UUID callbackUUID = UUID.randomUUID();

    public UUID getCallbackUUID() {
        return callbackUUID;
    }

    private Channel channel;

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public final void init(ByteBuf byteBuf) {
        callbackUUID = ByteUtils.decodeNextUUID(byteBuf);
        initObject(byteBuf);
    }

    public final ByteBuf getData(ByteBuf byteBuf) {
        ByteUtils.encodeUUID(byteBuf, callbackUUID);
        writePacketData(byteBuf);
        return byteBuf;
    }

    public final void sendCallbackPacket(CallbackPacket callbackPacket) {
        callbackPacket.setCallbackUUID(callbackUUID);
        channel.writeAndFlush(callbackPacket);
    }
}
