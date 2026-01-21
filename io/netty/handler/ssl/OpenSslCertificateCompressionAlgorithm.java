package io.netty.handler.ssl;

import javax.net.ssl.SSLEngine;

public interface OpenSslCertificateCompressionAlgorithm {
  byte[] compress(SSLEngine paramSSLEngine, byte[] paramArrayOfbyte) throws Exception;
  
  byte[] decompress(SSLEngine paramSSLEngine, int paramInt, byte[] paramArrayOfbyte) throws Exception;
  
  int algorithmId();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\ssl\OpenSslCertificateCompressionAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */