/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.server;

import io.netty.channel.Channel;
import me.singulativ.nettypacketframework.core.Callback;
import me.singulativ.nettypacketframework.packet.CallbackManager;
import me.singulativ.nettypacketframework.packet.IPacket;
import me.singulativ.nettypacketframework.packet.types.CallbackPacket;
import me.singulativ.nettypacketframework.packet.types.InquiryPacket;

import java.net.InetSocketAddress;

public class Client {

    private Channel channel;
    private CallbackManager callbackManager;

    public Client(Channel channel, CallbackManager callbackManager) {
        this.channel = channel;
        this.callbackManager = callbackManager;
    }

    public void sendPacket(IPacket packet) {
        channel.writeAndFlush(packet);
    }

    public void sendPacket(InquiryPacket packet, Callback<? extends CallbackPacket> callback) {
        callbackManager.registerCallback(packet.getCallbackUUID(), callback);
        channel.writeAndFlush(packet);
    }

    public boolean compareChannel(Channel channel) {
        return channel.equals(this.channel);
    }

    public InetSocketAddress getInetAdress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

}
