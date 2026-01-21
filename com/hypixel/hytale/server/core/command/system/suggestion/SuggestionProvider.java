package com.hypixel.hytale.server.core.command.system.suggestion;

import com.hypixel.hytale.server.core.command.system.CommandSender;
import javax.annotation.Nonnull;

@FunctionalInterface
public interface SuggestionProvider {
  void suggest(@Nonnull CommandSender paramCommandSender, @Nonnull String paramString, int paramInt, @Nonnull SuggestionResult paramSuggestionResult);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\system\suggestion\SuggestionProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */