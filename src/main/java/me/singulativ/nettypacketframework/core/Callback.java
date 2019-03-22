/*
 * Copyright (c) 2018-2019 Nicolas Frömel
 */

package me.singulativ.nettypacketframework.core;

public interface Callback<T> {

    void call(T callback);

}
