package it.unimi.dsi.fastutil.io;

import java.io.IOException;

public interface RepositionableStream {
  void position(long paramLong) throws IOException;
  
  long position() throws IOException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\i\\unimi\dsi\fastutil\io\RepositionableStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */