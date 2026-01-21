package org.bouncycastle.cert.crmf;

public interface EncryptedValuePadder {
  byte[] getPaddedData(byte[] paramArrayOfbyte);
  
  byte[] getUnpaddedData(byte[] paramArrayOfbyte);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\cert\crmf\EncryptedValuePadder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */