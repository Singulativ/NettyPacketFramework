/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.utils;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.UUID;

public class ByteUtils {

    public static void encodeString(ByteBuf byteBuf, String string) {
        byteBuf.writeInt(string.length());
        byteBuf.writeCharSequence(string, Charset.defaultCharset());
    }

    public static String decodeNextString(ByteBuf byteBuf) {
        int length = byteBuf.readInt();
        return byteBuf.readCharSequence(length, Charset.defaultCharset()).toString();
    }

    public static void encodeEnum(ByteBuf byteBuf, Enum aEnum) {
        encodeString(byteBuf, aEnum.name());
    }

    public static Enum decodeNextEnum(ByteBuf byteBuf, Class<? extends Enum> enumClass) {
        return Enum.valueOf(enumClass, decodeNextString(byteBuf));
    }

    public static void encodeUUID(ByteBuf byteBuf, UUID uuid) {
        encodeString(byteBuf, uuid.toString());
    }

    public static UUID decodeNextUUID(ByteBuf byteBuf) {
        return UUID.fromString(decodeNextString(byteBuf));
    }

}
