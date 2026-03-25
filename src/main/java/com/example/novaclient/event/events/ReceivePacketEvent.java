package com.example.novaclient.event.events;

import com.example.novaclient.event.CancellableEvent;
import net.minecraft.network.packet.Packet;

public class ReceivePacketEvent extends CancellableEvent {
    private final Packet<?> packet;
    
    public ReceivePacketEvent(Packet<?> packet) {
        this.packet = packet;
    }
    
    public Packet<?> getPacket() {
        return packet;
    }
}