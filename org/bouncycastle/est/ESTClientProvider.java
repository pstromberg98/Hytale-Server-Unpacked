package org.bouncycastle.est;

public interface ESTClientProvider {
  ESTClient makeClient() throws ESTException;
  
  boolean isTrusted();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\est\ESTClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */