package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.FeatureEvaluatorHelper;
import javax.annotation.Nullable;

public abstract class RequiredFeatureValidator extends Validator {
  public abstract boolean validate(FeatureEvaluatorHelper paramFeatureEvaluatorHelper);
  
  @Nullable
  public abstract String getErrorMessage(String paramString);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\npc\asset\builder\validators\RequiredFeatureValidator.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */