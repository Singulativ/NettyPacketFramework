/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.packet;

import io.netty.buffer.ByteBuf;

public interface IPacketOverclass {

    void init(ByteBuf byteBuf);

    ByteBuf getData(ByteBuf byteBuf);

}
