package com.google.crypto.tink.subtle;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

public interface StreamSegmentEncrypter {
  ByteBuffer getHeader();
  
  void encryptSegment(ByteBuffer paramByteBuffer1, boolean paramBoolean, ByteBuffer paramByteBuffer2) throws GeneralSecurityException;
  
  void encryptSegment(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2, boolean paramBoolean, ByteBuffer paramByteBuffer3) throws GeneralSecurityException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\subtle\StreamSegmentEncrypter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */