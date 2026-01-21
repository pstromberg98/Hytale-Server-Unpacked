package com.nimbusds.jose.util.health;

public interface HealthReportListener<S, C extends com.nimbusds.jose.proc.SecurityContext> {
  void notify(HealthReport<S, C> paramHealthReport);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\nimbusds\jos\\util\health\HealthReportListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */