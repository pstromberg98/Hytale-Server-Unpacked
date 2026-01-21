package com.google.errorprone.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface InlineMeValidationDisabled {
  String value();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\errorprone\annotations\InlineMeValidationDisabled.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */