package org.jline.reader;

import java.util.List;

public interface ParsedLine {
  String word();
  
  int wordCursor();
  
  int wordIndex();
  
  List<String> words();
  
  String line();
  
  int cursor();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\jline\reader\ParsedLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */