package org.jline.builtins;

public interface ConsoleOptionGetter {
  Object consoleOption(String paramString);
  
  <T> T consoleOption(String paramString, T paramT);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\jline\builtins\ConsoleOptionGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */