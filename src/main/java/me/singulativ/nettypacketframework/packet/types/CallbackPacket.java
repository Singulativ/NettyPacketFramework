/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.packet.types;

import io.netty.buffer.ByteBuf;
import me.singulativ.nettypacketframework.packet.IPacket;
import me.singulativ.nettypacketframework.utils.ByteUtils;

import java.util.UUID;

public abstract class CallbackPacket implements IPacket {

    private UUID callbackUUID;

    public UUID getCallbackUUID() {
        return callbackUUID;
    }

    void setCallbackUUID(UUID callbackUUID) {
        this.callbackUUID = callbackUUID;
    }

    @Override
    public final void init(ByteBuf byteBuf) {
        callbackUUID = ByteUtils.decodeNextUUID(byteBuf);
        initObject(byteBuf);
    }

    @Override
    public final ByteBuf getData(ByteBuf byteBuf) {
        ByteUtils.encodeUUID(byteBuf, callbackUUID);
        writePacketData(byteBuf);
        return byteBuf;
    }

}
