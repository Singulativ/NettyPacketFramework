/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.server;

import io.netty.channel.Channel;
import me.singulativ.nettypacketframework.client.PacketEventHandler;
import me.singulativ.nettypacketframework.core.PacketListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.singulativ.nettypacketframework.packet.*;
import me.singulativ.nettypacketframework.packet.types.CallbackPacket;
import me.singulativ.nettypacketframework.packet.types.DataPacket;
import me.singulativ.nettypacketframework.packet.types.InquiryPacket;

import java.net.InetAddress;

public class NettyServer {

    //Netty
    private EventLoopGroup bossGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    private EventLoopGroup workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();

    //NettyPacketFramework
    private PacketIDManager packetIDManager = new PacketIDManager();
    private ClientManager clientManager = new ClientManager();
    private PacketEventHandler packetEventHandler = new PacketEventHandler();
    private ServerEventManager serverEventManager = new ServerEventManager();
    private CallbackManager callbackManager = new CallbackManager();

    /**
     * Start the serer.
     * @param hostAdress The Host Address of the Server
     * @param hostPort The Host Port of the Server.
     */
    public void start(InetAddress hostAdress, int hostPort) {

        //Init
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .option(ChannelOption.SO_BACKLOG, 128)
                .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new PacketDecoder(packetIDManager))
                                .addLast(new PacketEncoder(packetIDManager))
                                .addLast(new ServerHandler(clientManager, packetEventHandler, serverEventManager, callbackManager));
                    }
                });

        //Starting me.singulativ.nettypacketframework.server
        bootstrap.bind(hostAdress, hostPort).syncUninterruptibly();

    }

    /**
     * Broadcast an packet to all connected clients.
     * @param packet The packet to broadcast
     * @param except If you want you can except client from the broadcast here
     */
    public void broadcast(DataPacket packet, Client... except) {
        for (Client client : clientManager.getClients()) {
            for (Client exceptClient : except) {
                if (client.equals(exceptClient))
                    continue;
            }
            client.sendPacket(packet);
        }
    }

    /**
     * Add a server event Listener, with which you can catch events like an client connect or disconnect.
     * @param listener The Server Event Listener
     */
    public void addServerEventListener(ServerEventListener listener) {
        serverEventManager.registerListener(listener);
    }

    /**
     * Disconnect all clients and shut the server down.
     */
    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    /**
     * Register an incoming Packet with an Listener.
     * The Listener will be called if an packet of the type of the Packet Class arrives the server.
     * @param packetClass Class of the Packet
     * @param packetListener The Listener
     */
    public void registerIncomingPacket(Class<? extends DataPacket> packetClass, PacketListener packetListener) {
        packetIDManager.registerIncomingPacket(packetClass);
        packetEventHandler.setListener(packetClass, packetListener);
    }

    /**
     * Register an incoming Packet with an default handler option
     * @param packetClass Class of the packet
     * @param defaultHandleOption Default handler option
     */
    public void registerIncomingPacket(Class<? extends DataPacket> packetClass, DefaultHandleOption defaultHandleOption) {
        packetIDManager.registerIncomingPacket(packetClass);
        switch (defaultHandleOption) {
            case BROADCAST:
                packetEventHandler.setListener(packetClass, new PacketListener<DataPacket>() {
                    @Override
                    public void call(DataPacket packet, Channel sender) {
                        broadcast(packet);
                    }
                });
                break;
            case BACK_TO_SENDER:
                packetEventHandler.setListener(packetClass, new PacketListener<DataPacket>() {
                    @Override
                    public void call(DataPacket packet, Channel sender) {
                        sender.writeAndFlush(packet);
                    }
                });
                break;
            case BROADCAST_WITHOUT_SENDER:
                packetEventHandler.setListener(packetClass, new PacketListener<DataPacket>() {
                    @Override
                    public void call(DataPacket packet, Channel sender) {
                        broadcast(packet, clientManager.getClientByChannel(sender));
                    }
                });
                break;
        }

    }

    /**
     * Register an incoming Packet with an Listener and an default handler option.
     * The Listener will be called if an packet of the type of the Packet Class arrives the server.
     * First the DefaultHandler were called an then the packet listener were called.
     * @param packetClass Class of the Packet
     * @param defaultHandleOption Default handler option
     * @param packetListener The Listener
     */
    public void registerIncomingPacket(Class<? extends DataPacket> packetClass, DefaultHandleOption defaultHandleOption, final PacketListener packetListener) {
        packetIDManager.registerIncomingPacket(packetClass);
        switch (defaultHandleOption) {
            case BROADCAST:
                packetEventHandler.setListener(packetClass, new PacketListener<DataPacket>() {
                    @Override
                    public void call(DataPacket packet, Channel sender) {
                        broadcast(packet);
                        packetListener.call(packet, sender);
                    }
                });
                break;
            case BACK_TO_SENDER:
                packetEventHandler.setListener(packetClass, new PacketListener<DataPacket>() {
                    @Override
                    public void call(DataPacket packet, Channel sender) {
                        sender.writeAndFlush(packet);
                    }
                });
                break;
            case BROADCAST_WITHOUT_SENDER:
                packetEventHandler.setListener(packetClass, new PacketListener<DataPacket>() {
                    @Override
                    public void call(DataPacket packet, Channel sender) {
                        broadcast(packet, clientManager.getClientByChannel(sender));
                    }
                });
                break;
        }

    }

    /**
     * Register outgoing packets.
     * Every packet have to be registered first before you can send it.
     * @param packetClass Class of the packet.
     */
    public void registerOutgoingPacket(Class<? extends DataPacket> packetClass) {
        packetIDManager.registerOutgoingPacket(packetClass);
    }

    /**
     * Register an callback packet.
     * @param inquiryPacket The Class of the inquiry packet, which you send to the client
     * @param callbackPacket The Class of the callback packet, which you get back from the client
     */
    public void registerCallbackAsRequestor(Class<? extends InquiryPacket> inquiryPacket, Class<? extends CallbackPacket> callbackPacket) {
        packetIDManager.registerOutgoingPacket(inquiryPacket);
        packetIDManager.registerIncomingPacket(callbackPacket);
    }

    /**
     * Register an callback handlers, which handles inquiry packets with sending callback packets back.
     * @param inquiryPacket The Class of the inquiry packet, which you get by the client
     * @param callbackPacket The Class of the Callback packet which you send back to the client
     * @param packetListener The Handler, which send back the callback packet
     */
    public void registerCallbackAsHandler(Class<? extends InquiryPacket> inquiryPacket, Class<? extends CallbackPacket> callbackPacket, PacketListener packetListener) {
        packetIDManager.registerIncomingPacket(inquiryPacket);
        packetIDManager.registerOutgoingPacket(callbackPacket);
        packetEventHandler.setListener(inquiryPacket, packetListener);
    }

}
