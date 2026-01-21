package com.hypixel.hytale.server.core.modules.accesscontrol.provider;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public interface AccessProvider {
  @Nonnull
  CompletableFuture<Optional<String>> getDisconnectReason(UUID paramUUID);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\accesscontrol\provider\AccessProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */