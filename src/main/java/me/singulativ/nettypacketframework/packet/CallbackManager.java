/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.packet;

import me.singulativ.nettypacketframework.core.Callback;
import me.singulativ.nettypacketframework.packet.IPacket;
import me.singulativ.nettypacketframework.packet.types.CallbackPacket;

import java.util.HashMap;
import java.util.UUID;

public class CallbackManager {

    private HashMap<UUID, Callback> callbacks = new HashMap<>();

    public void registerCallback(UUID uuid, Callback callback) {
        callbacks.put(uuid, callback);
    }

    public void callCallback(UUID uuid, CallbackPacket packet) {
        callbacks.get(uuid).call(packet);
        callbacks.remove(uuid);
    }

}
