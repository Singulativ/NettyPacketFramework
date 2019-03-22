/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.client;

import me.singulativ.nettypacketframework.core.Callback;
import me.singulativ.nettypacketframework.core.PacketListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.singulativ.nettypacketframework.packet.CallbackManager;
import me.singulativ.nettypacketframework.packet.PacketDecoder;
import me.singulativ.nettypacketframework.packet.PacketEncoder;
import me.singulativ.nettypacketframework.packet.PacketIDManager;
import me.singulativ.nettypacketframework.packet.types.CallbackPacket;
import me.singulativ.nettypacketframework.packet.types.DataPacket;
import me.singulativ.nettypacketframework.packet.types.InquiryPacket;

import java.net.InetAddress;

/**
 * Netty Client, which you can connect to an {@link me.singulativ.nettypacketframework.server.NettyServer}
 * @author Nicolas Frömel
 */
public class NettyClient {

    //Netty
    private EventLoopGroup workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();

    //NettyPacketFramework
    private PacketIDManager packetIDManager = new PacketIDManager();
    private PacketEventHandler packetEventHandler = new PacketEventHandler();
    private CallbackManager callbackManager = new CallbackManager();

    private Channel channel;

    /**
     * Connect to the server.
     * @param address Server Address
     * @param port Server Port
     */
    public void connect(final InetAddress address, final int port) {

        //Init NettyClient
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<Channel>() {

            protected void initChannel(Channel channel) throws Exception {
                setChannel(channel);
                channel.pipeline()
                        .addLast(new PacketDecoder(packetIDManager))
                        .addLast(new PacketEncoder(packetIDManager))
                        .addLast(new ClientHandler(packetEventHandler, callbackManager));
            }

        });

        //Connecting to Server
        bootstrap.connect(address, port).syncUninterruptibly();

    }

    /**
     * Close the connection to the server and shut the Client down.
     */
    public void shutdown() {
        workerGroup.shutdownGracefully();
    }

    private void setChannel(Channel channel) {
        this.channel = channel;
    }

    /**
     * Register an incoming Packet with an Listener.
     * The Listener will be called if an packet of the type of the Packet Class arrives the client.
     * @param packetClass Class of the Packet
     * @param packetListener The Listener
     */
    public void registerIncomingPacket(Class<? extends DataPacket> packetClass, PacketListener packetListener) {
        packetIDManager.registerIncomingPacket(packetClass);
        packetEventHandler.setListener(packetClass, packetListener);
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
     * @param inquiryPacket The Class of the inquiry packet, which you send to the server
     * @param callbackPacket The Class of the callback packet, which you get back from the server
     */
    public void registerCallbackAsRequestor(Class<? extends InquiryPacket> inquiryPacket, Class<? extends CallbackPacket> callbackPacket) {
        packetIDManager.registerOutgoingPacket(inquiryPacket);
        packetIDManager.registerIncomingPacket(callbackPacket);
    }

    /**
     * Register an callback handlers, which handles inquiry packets with sending callback packets back.
     * @param inquiryPacket The Class of the inquiry packet, which you get by the server
     * @param callbackPacket The Class of the Callback packet which you send back to the server
     * @param packetListener The Handler, which send back the callback packet
     */
    public void registerCallbackAsHandler(Class<? extends InquiryPacket> inquiryPacket, Class<? extends CallbackPacket> callbackPacket, PacketListener packetListener) {
        packetIDManager.registerIncomingPacket(inquiryPacket);
        packetIDManager.registerOutgoingPacket(callbackPacket);
        packetEventHandler.setListener(inquiryPacket, packetListener);
    }

    /**
     * Send a packet to the client.
     * @param packet The packet to send
     */
    public void sendPacket(DataPacket packet) {
        channel.writeAndFlush(packet);
    }

    /**
     * Send a inquiry packet to the server.
     * @param packet The packet to send
     * @param callback Listener to catch the callback packet
     */
    public void sendPacket(InquiryPacket packet, Callback<?> callback) {
        callbackManager.registerCallback(packet.getCallbackUUID(), callback);
        channel.writeAndFlush(packet);
    }

}
