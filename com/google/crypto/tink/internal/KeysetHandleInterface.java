package com.google.crypto.tink.internal;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;

public interface KeysetHandleInterface {
  Entry getPrimary();
  
  int size();
  
  Entry getAt(int paramInt);
  
  public static interface Entry {
    Key getKey();
    
    KeyStatus getStatus();
    
    int getId();
    
    boolean isPrimary();
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\internal\KeysetHandleInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */