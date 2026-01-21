package com.hypixel.hytale.server.npc.asset.builder.validators;

public abstract class DoubleArrayValidator extends Validator {
  public abstract boolean test(double[] paramArrayOfdouble);
  
  public abstract String errorMessage(double[] paramArrayOfdouble, String paramString);
  
  public abstract String errorMessage(double[] paramArrayOfdouble);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\npc\asset\builder\validators\DoubleArrayValidator.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */