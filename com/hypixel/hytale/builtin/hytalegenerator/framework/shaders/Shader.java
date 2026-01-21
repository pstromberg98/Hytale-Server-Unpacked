package com.hypixel.hytale.builtin.hytalegenerator.framework.shaders;

public interface Shader<T> {
  T shade(T paramT, long paramLong);
  
  T shade(T paramT, long paramLong1, long paramLong2);
  
  T shade(T paramT, long paramLong1, long paramLong2, long paramLong3);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\framework\shaders\Shader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */