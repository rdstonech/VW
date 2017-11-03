package com.voxelwind.api.server.event.server;

import com.voxelwind.api.server.event.Event;

/**
 * This event will be fired when the server has disconnected clients, stopped accepting new connections and after levels
 * have been deinitialized but before the server process exits. At this point, you should perform any last-minute clean
 * up before the process exits.
 */
public class ServerStopEvent implements Event {
    public static final ServerStopEvent INSTANCE = new ServerStopEvent();

    private ServerStopEvent() {

    }
}
