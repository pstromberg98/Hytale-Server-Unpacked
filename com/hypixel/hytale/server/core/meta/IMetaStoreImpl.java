package com.hypixel.hytale.server.core.meta;

import com.hypixel.hytale.codec.ExtraInfo;
import java.util.function.BiConsumer;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public interface IMetaStoreImpl<K> extends IMetaStore<K> {
  IMetaRegistry<K> getRegistry();
  
  void decode(BsonDocument paramBsonDocument, ExtraInfo paramExtraInfo);
  
  BsonDocument encode(ExtraInfo paramExtraInfo);
  
  void forEachUnknownEntry(BiConsumer<String, BsonValue> paramBiConsumer);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\meta\IMetaStoreImpl.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */