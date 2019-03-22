/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.packet;

import io.netty.buffer.ByteBuf;

public interface IPacket extends IPacketOverclass {

    void initObject(ByteBuf byteBuf);

    void writePacketData(ByteBuf byteBuf);

}
