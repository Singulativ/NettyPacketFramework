/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.core;

import io.netty.channel.Channel;

public interface PacketListener<T> {

    void call(T packet, Channel sender);

}
