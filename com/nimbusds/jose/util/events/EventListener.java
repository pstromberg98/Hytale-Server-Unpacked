package com.nimbusds.jose.util.events;

public interface EventListener<S, C extends com.nimbusds.jose.proc.SecurityContext> {
  void notify(Event<S, C> paramEvent);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\nimbusds\jos\\util\events\EventListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */