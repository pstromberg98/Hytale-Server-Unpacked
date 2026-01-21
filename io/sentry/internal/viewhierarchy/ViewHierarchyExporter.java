package io.sentry.internal.viewhierarchy;

import io.sentry.protocol.ViewHierarchyNode;
import org.jetbrains.annotations.NotNull;

public interface ViewHierarchyExporter {
  boolean export(@NotNull ViewHierarchyNode paramViewHierarchyNode, @NotNull Object paramObject);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\internal\viewhierarchy\ViewHierarchyExporter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */