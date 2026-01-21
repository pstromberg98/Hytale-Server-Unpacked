package it.unimi.dsi.fastutil.booleans;

public interface BooleanHash {
  public static interface Strategy {
    int hashCode(boolean param1Boolean);
    
    boolean equals(boolean param1Boolean1, boolean param1Boolean2);
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\i\\unimi\dsi\fastutil\booleans\BooleanHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */