/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.client;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.singulativ.nettypacketframework.packet.CallbackManager;
import me.singulativ.nettypacketframework.packet.IPacket;
import me.singulativ.nettypacketframework.packet.types.CallbackPacket;

public class ClientHandler extends ChannelDuplexHandler {

    private PacketEventHandler packetEventHandler;

    private CallbackManager callbackManager;

    public ClientHandler(PacketEventHandler packetEventHandler, CallbackManager callbackManager) {
        this.packetEventHandler = packetEventHandler;
        this.callbackManager = callbackManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof IPacket) {
            IPacket packet = (IPacket) msg;
            if (packet instanceof CallbackPacket) {
                CallbackPacket callbackPacket = (CallbackPacket) packet;
                callbackManager.callCallback(callbackPacket.getCallbackUUID(), callbackPacket);
            } else {
                packetEventHandler.call((IPacket) msg, ctx.channel());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
