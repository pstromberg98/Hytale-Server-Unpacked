package com.nimbusds.jose.util.events;

public interface Event<S, C extends com.nimbusds.jose.proc.SecurityContext> {
  S getSource();
  
  C getContext();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\nimbusds\jos\\util\events\Event.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */