package org.bouncycastle.pqc.crypto.picnic;

class View {
  final int[] inputShare;
  
  final byte[] communicatedBits;
  
  final int[] outputShare;
  
  public View(PicnicEngine paramPicnicEngine) {
    this.inputShare = new int[paramPicnicEngine.stateSizeWords];
    this.communicatedBits = new byte[paramPicnicEngine.andSizeBytes];
    this.outputShare = new int[paramPicnicEngine.stateSizeWords];
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\picnic\View.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */