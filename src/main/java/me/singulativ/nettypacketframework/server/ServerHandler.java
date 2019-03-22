/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.server;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.singulativ.nettypacketframework.client.PacketEventHandler;
import me.singulativ.nettypacketframework.packet.CallbackManager;
import me.singulativ.nettypacketframework.packet.IPacket;
import me.singulativ.nettypacketframework.packet.types.CallbackPacket;

public class ServerHandler extends ChannelDuplexHandler {

    private ClientManager clientManager;
    private PacketEventHandler packetEventHandler;
    private ServerEventManager serverEventManager;
    private CallbackManager callbackManager;

    public ServerHandler(ClientManager clientManager, PacketEventHandler packetEventHandler, ServerEventManager serverEventManager, CallbackManager callbackManager) {
        this.clientManager = clientManager;
        this.packetEventHandler = packetEventHandler;
        this.serverEventManager = serverEventManager;
        this.callbackManager = callbackManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof IPacket) {
            IPacket packet = (IPacket) msg;
            if (packet instanceof CallbackPacket) {
                CallbackPacket callbackPacket = (CallbackPacket) packet;
                callbackManager.callCallback(callbackPacket.getCallbackUUID(), callbackPacket);
            } else {
                packetEventHandler.call(packet, ctx.channel());
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Client client = new Client(ctx.channel(), callbackManager);
        clientManager.addClient(client);
        serverEventManager.callClientConnected(client);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Client client = clientManager.getClientByChannel(ctx.channel());
        clientManager.removeClient(client);
        serverEventManager.callClientDisconnected(client);
    }
}
