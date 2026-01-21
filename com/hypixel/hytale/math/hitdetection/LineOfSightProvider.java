/*   */ package com.hypixel.hytale.math.hitdetection;
/*   */ 
/*   */ public interface LineOfSightProvider {
/*   */   public static final LineOfSightProvider DEFAULT_TRUE = (fromX, fromY, fromZ, toX, toY, toZ) -> true;
/*   */   
/*   */   boolean test(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);
/*   */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\math\hitdetection\LineOfSightProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */