/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.packet.types;

import io.netty.buffer.ByteBuf;
import me.singulativ.nettypacketframework.packet.IPacket;

public abstract class DataPacket implements IPacket {

    @Override
    public final void init(ByteBuf byteBuf) {
        initObject(byteBuf);
    }

    @Override
    public final ByteBuf getData(ByteBuf byteBuf) {
        writePacketData(byteBuf);
        return byteBuf;
    }
}
