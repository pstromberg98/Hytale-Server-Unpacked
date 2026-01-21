package com.hypixel.hytale.server.core.modules.accesscontrol.ban;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@FunctionalInterface
public interface BanParser {
  Ban parse(JsonObject paramJsonObject) throws JsonParseException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\accesscontrol\ban\BanParser.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */