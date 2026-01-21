package com.hypixel.hytale.registry;

import java.util.function.BooleanSupplier;

public interface RegistrationWrapFunction<T extends Registration> {
  T wrap(T paramT, BooleanSupplier paramBooleanSupplier, Runnable paramRunnable);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\registry\Registry$RegistrationWrapFunction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */