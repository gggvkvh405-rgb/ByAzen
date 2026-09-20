package org.zenith.config;

import com.google.gson.JsonObject;

public interface ProtocolMessage {
   String type();

   JsonObject TaskQueue();
}
