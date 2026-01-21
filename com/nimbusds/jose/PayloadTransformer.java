package com.nimbusds.jose;

public interface PayloadTransformer<T> {
  T transform(Payload paramPayload);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\nimbusds\jose\PayloadTransformer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */