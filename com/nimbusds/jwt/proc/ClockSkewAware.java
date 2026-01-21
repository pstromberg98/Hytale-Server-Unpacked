package com.nimbusds.jwt.proc;

public interface ClockSkewAware {
  int getMaxClockSkew();
  
  void setMaxClockSkew(int paramInt);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\nimbusds\jwt\proc\ClockSkewAware.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */