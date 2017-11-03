package com.voxelwind.api.server.event.server;

import com.voxelwind.api.server.event.Event;

/**
 * This event will be fired after plugins are initialized but before the server initializes levels and binds to a port.
 * You will typically want to perform any major server initialization tasks at this point.
 */
public class ServerInitializeEvent implements Event {
    public static final ServerInitializeEvent INSTANCE = new ServerInitializeEvent();

    private ServerInitializeEvent() {

    }
}
