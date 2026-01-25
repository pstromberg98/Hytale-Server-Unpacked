/*     */ package com.hypixel.hytale.server.core.util;
/*     */ 
/*     */ import com.hypixel.hytale.protocol.Color;
/*     */ import com.hypixel.hytale.protocol.FormattedMessage;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.ParamValue;
/*     */ import com.hypixel.hytale.protocol.packets.asseteditor.FailureReply;
/*     */ import com.hypixel.hytale.protocol.packets.asseteditor.SuccessReply;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ import org.jline.utils.AttributedString;
/*     */ import org.jline.utils.AttributedStringBuilder;
/*     */ import org.jline.utils.AttributedStyle;
/*     */ import org.jline.utils.Colors;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MessageUtil
/*     */ {
/*     */   public static AttributedString toAnsiString(@Nonnull Message message) {
/*  27 */     AttributedStyle style = AttributedStyle.DEFAULT;
/*     */     
/*  29 */     String color = message.getColor();
/*  30 */     if (color != null) style = hexToStyle(color);
/*     */     
/*  32 */     AttributedStringBuilder sb = new AttributedStringBuilder();
/*  33 */     sb.style(style).append(message.getAnsiMessage());
/*     */     
/*  35 */     List<Message> children = message.getChildren();
/*  36 */     for (Message child : children) {
/*  37 */       sb.append(toAnsiString(child));
/*     */     }
/*  39 */     return sb.toAttributedString();
/*     */   }
/*     */   
/*     */   public static AttributedStyle hexToStyle(@Nonnull String str) {
/*  43 */     Color color = ColorParseUtil.parseColor(str);
/*  44 */     if (color == null) return AttributedStyle.DEFAULT;
/*     */ 
/*     */     
/*  47 */     int colorId = Colors.roundRgbColor(color.red & 0xFF, color.green & 0xFF, color.blue & 0xFF, 256);
/*  48 */     return AttributedStyle.DEFAULT.foreground(colorId);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static void sendSuccessReply(@Nonnull PlayerRef playerRef, int token) {
/*  53 */     sendSuccessReply(playerRef, token, null);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static void sendSuccessReply(@Nonnull PlayerRef playerRef, int token, @Nullable Message message) {
/*  58 */     FormattedMessage msg = (message != null) ? message.getFormattedMessage() : null;
/*  59 */     playerRef.getPacketHandler().writeNoCache((Packet)new SuccessReply(token, msg));
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static void sendFailureReply(@Nonnull PlayerRef playerRef, int token, @Nonnull Message message) {
/*  64 */     FormattedMessage msg = (message != null) ? message.getFormattedMessage() : null;
/*  65 */     playerRef.getPacketHandler().writeNoCache((Packet)new FailureReply(token, msg));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static String formatText(String text, @Nullable Map<String, ParamValue> params, @Nullable Map<String, FormattedMessage> messageParams) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: ifnonnull -> 14
/*     */     //   4: new java/lang/IllegalArgumentException
/*     */     //   7: dup
/*     */     //   8: ldc 'text cannot be null'
/*     */     //   10: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   13: athrow
/*     */     //   14: aload_1
/*     */     //   15: ifnonnull -> 24
/*     */     //   18: aload_2
/*     */     //   19: ifnonnull -> 24
/*     */     //   22: aload_0
/*     */     //   23: areturn
/*     */     //   24: aload_0
/*     */     //   25: invokevirtual length : ()I
/*     */     //   28: istore_3
/*     */     //   29: new java/lang/StringBuilder
/*     */     //   32: dup
/*     */     //   33: aload_0
/*     */     //   34: invokevirtual length : ()I
/*     */     //   37: invokespecial <init> : (I)V
/*     */     //   40: astore #4
/*     */     //   42: iconst_0
/*     */     //   43: istore #5
/*     */     //   45: iconst_0
/*     */     //   46: istore #6
/*     */     //   48: iload #6
/*     */     //   50: iload_3
/*     */     //   51: if_icmpge -> 1419
/*     */     //   54: aload_0
/*     */     //   55: iload #6
/*     */     //   57: invokevirtual charAt : (I)C
/*     */     //   60: istore #7
/*     */     //   62: iload #7
/*     */     //   64: bipush #123
/*     */     //   66: if_icmpne -> 1350
/*     */     //   69: iload #6
/*     */     //   71: iconst_1
/*     */     //   72: iadd
/*     */     //   73: iload_3
/*     */     //   74: if_icmpge -> 128
/*     */     //   77: aload_0
/*     */     //   78: iload #6
/*     */     //   80: iconst_1
/*     */     //   81: iadd
/*     */     //   82: invokevirtual charAt : (I)C
/*     */     //   85: bipush #123
/*     */     //   87: if_icmpne -> 128
/*     */     //   90: iload #6
/*     */     //   92: iload #5
/*     */     //   94: if_icmple -> 108
/*     */     //   97: aload #4
/*     */     //   99: aload_0
/*     */     //   100: iload #5
/*     */     //   102: iload #6
/*     */     //   104: invokevirtual append : (Ljava/lang/CharSequence;II)Ljava/lang/StringBuilder;
/*     */     //   107: pop
/*     */     //   108: aload #4
/*     */     //   110: bipush #123
/*     */     //   112: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   115: pop
/*     */     //   116: iinc #6, 1
/*     */     //   119: iload #6
/*     */     //   121: iconst_1
/*     */     //   122: iadd
/*     */     //   123: istore #5
/*     */     //   125: goto -> 1413
/*     */     //   128: aload_0
/*     */     //   129: iload #6
/*     */     //   131: invokestatic findMatchingBrace : (Ljava/lang/String;I)I
/*     */     //   134: istore #8
/*     */     //   136: iload #8
/*     */     //   138: ifge -> 144
/*     */     //   141: goto -> 1413
/*     */     //   144: iload #6
/*     */     //   146: iload #5
/*     */     //   148: if_icmple -> 162
/*     */     //   151: aload #4
/*     */     //   153: aload_0
/*     */     //   154: iload #5
/*     */     //   156: iload #6
/*     */     //   158: invokevirtual append : (Ljava/lang/CharSequence;II)Ljava/lang/StringBuilder;
/*     */     //   161: pop
/*     */     //   162: iload #6
/*     */     //   164: iconst_1
/*     */     //   165: iadd
/*     */     //   166: istore #9
/*     */     //   168: aload_0
/*     */     //   169: bipush #44
/*     */     //   171: iload #9
/*     */     //   173: iload #8
/*     */     //   175: invokevirtual indexOf : (III)I
/*     */     //   178: istore #10
/*     */     //   180: iload #10
/*     */     //   182: iflt -> 200
/*     */     //   185: aload_0
/*     */     //   186: bipush #44
/*     */     //   188: iload #10
/*     */     //   190: iconst_1
/*     */     //   191: iadd
/*     */     //   192: iload #8
/*     */     //   194: invokevirtual indexOf : (III)I
/*     */     //   197: goto -> 201
/*     */     //   200: iconst_m1
/*     */     //   201: istore #11
/*     */     //   203: iload #9
/*     */     //   205: istore #12
/*     */     //   207: iload #10
/*     */     //   209: iflt -> 224
/*     */     //   212: iload #10
/*     */     //   214: iload #8
/*     */     //   216: if_icmpge -> 224
/*     */     //   219: iload #10
/*     */     //   221: goto -> 226
/*     */     //   224: iload #8
/*     */     //   226: istore #13
/*     */     //   228: aload_0
/*     */     //   229: iload #12
/*     */     //   231: iload #13
/*     */     //   233: iconst_1
/*     */     //   234: isub
/*     */     //   235: invokestatic trimStart : (Ljava/lang/String;II)I
/*     */     //   238: istore #14
/*     */     //   240: aload_0
/*     */     //   241: iload #14
/*     */     //   243: iload #13
/*     */     //   245: iconst_1
/*     */     //   246: isub
/*     */     //   247: invokestatic trimEnd : (Ljava/lang/String;II)I
/*     */     //   250: istore #15
/*     */     //   252: iload #15
/*     */     //   254: ifle -> 271
/*     */     //   257: aload_0
/*     */     //   258: iload #14
/*     */     //   260: iload #14
/*     */     //   262: iload #15
/*     */     //   264: iadd
/*     */     //   265: invokevirtual substring : (II)Ljava/lang/String;
/*     */     //   268: goto -> 273
/*     */     //   271: ldc ''
/*     */     //   273: astore #16
/*     */     //   275: aconst_null
/*     */     //   276: astore #17
/*     */     //   278: iload #10
/*     */     //   280: iflt -> 352
/*     */     //   283: iload #10
/*     */     //   285: iload #8
/*     */     //   287: if_icmpge -> 352
/*     */     //   290: iload #10
/*     */     //   292: iconst_1
/*     */     //   293: iadd
/*     */     //   294: istore #18
/*     */     //   296: iload #11
/*     */     //   298: iflt -> 306
/*     */     //   301: iload #11
/*     */     //   303: goto -> 308
/*     */     //   306: iload #8
/*     */     //   308: istore #19
/*     */     //   310: aload_0
/*     */     //   311: iload #18
/*     */     //   313: iload #19
/*     */     //   315: iconst_1
/*     */     //   316: isub
/*     */     //   317: invokestatic trimStart : (Ljava/lang/String;II)I
/*     */     //   320: istore #20
/*     */     //   322: aload_0
/*     */     //   323: iload #20
/*     */     //   325: iload #19
/*     */     //   327: iconst_1
/*     */     //   328: isub
/*     */     //   329: invokestatic trimEnd : (Ljava/lang/String;II)I
/*     */     //   332: istore #21
/*     */     //   334: iload #21
/*     */     //   336: ifle -> 352
/*     */     //   339: aload_0
/*     */     //   340: iload #20
/*     */     //   342: iload #20
/*     */     //   344: iload #21
/*     */     //   346: iadd
/*     */     //   347: invokevirtual substring : (II)Ljava/lang/String;
/*     */     //   350: astore #17
/*     */     //   352: aconst_null
/*     */     //   353: astore #18
/*     */     //   355: iload #11
/*     */     //   357: iflt -> 415
/*     */     //   360: iload #11
/*     */     //   362: iload #8
/*     */     //   364: if_icmpge -> 415
/*     */     //   367: iload #11
/*     */     //   369: iconst_1
/*     */     //   370: iadd
/*     */     //   371: istore #19
/*     */     //   373: aload_0
/*     */     //   374: iload #19
/*     */     //   376: iload #8
/*     */     //   378: iconst_1
/*     */     //   379: isub
/*     */     //   380: invokestatic trimStart : (Ljava/lang/String;II)I
/*     */     //   383: istore #20
/*     */     //   385: aload_0
/*     */     //   386: iload #20
/*     */     //   388: iload #8
/*     */     //   390: iconst_1
/*     */     //   391: isub
/*     */     //   392: invokestatic trimEnd : (Ljava/lang/String;II)I
/*     */     //   395: istore #21
/*     */     //   397: iload #21
/*     */     //   399: ifle -> 415
/*     */     //   402: aload_0
/*     */     //   403: iload #20
/*     */     //   405: iload #20
/*     */     //   407: iload #21
/*     */     //   409: iadd
/*     */     //   410: invokevirtual substring : (II)Ljava/lang/String;
/*     */     //   413: astore #18
/*     */     //   415: aload_1
/*     */     //   416: ifnull -> 433
/*     */     //   419: aload_1
/*     */     //   420: aload #16
/*     */     //   422: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   427: checkcast com/hypixel/hytale/protocol/ParamValue
/*     */     //   430: goto -> 434
/*     */     //   433: aconst_null
/*     */     //   434: astore #19
/*     */     //   436: aload_2
/*     */     //   437: ifnull -> 454
/*     */     //   440: aload_2
/*     */     //   441: aload #16
/*     */     //   443: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   448: checkcast com/hypixel/hytale/protocol/FormattedMessage
/*     */     //   451: goto -> 455
/*     */     //   454: aconst_null
/*     */     //   455: astore #20
/*     */     //   457: aload #20
/*     */     //   459: ifnull -> 550
/*     */     //   462: aload #20
/*     */     //   464: getfield rawText : Ljava/lang/String;
/*     */     //   467: ifnull -> 484
/*     */     //   470: aload #4
/*     */     //   472: aload #20
/*     */     //   474: getfield rawText : Ljava/lang/String;
/*     */     //   477: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   480: pop
/*     */     //   481: goto -> 1337
/*     */     //   484: aload #20
/*     */     //   486: getfield messageId : Ljava/lang/String;
/*     */     //   489: ifnull -> 1337
/*     */     //   492: invokestatic get : ()Lcom/hypixel/hytale/server/core/modules/i18n/I18nModule;
/*     */     //   495: ldc 'en-US'
/*     */     //   497: aload #20
/*     */     //   499: getfield messageId : Ljava/lang/String;
/*     */     //   502: invokevirtual getMessage : (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
/*     */     //   505: astore #21
/*     */     //   507: aload #21
/*     */     //   509: ifnull -> 536
/*     */     //   512: aload #4
/*     */     //   514: aload #21
/*     */     //   516: aload #20
/*     */     //   518: getfield params : Ljava/util/Map;
/*     */     //   521: aload #20
/*     */     //   523: getfield messageParams : Ljava/util/Map;
/*     */     //   526: invokestatic formatText : (Ljava/lang/String;Ljava/util/Map;Ljava/util/Map;)Ljava/lang/String;
/*     */     //   529: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   532: pop
/*     */     //   533: goto -> 547
/*     */     //   536: aload #4
/*     */     //   538: aload #20
/*     */     //   540: getfield messageId : Ljava/lang/String;
/*     */     //   543: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   546: pop
/*     */     //   547: goto -> 1337
/*     */     //   550: aload #19
/*     */     //   552: ifnull -> 1326
/*     */     //   555: ldc ''
/*     */     //   557: astore #21
/*     */     //   559: aload #17
/*     */     //   561: astore #22
/*     */     //   563: iconst_0
/*     */     //   564: istore #23
/*     */     //   566: aload #22
/*     */     //   568: iload #23
/*     */     //   570: <illegal opcode> typeSwitch : (Ljava/lang/Object;I)I
/*     */     //   575: tableswitch default -> 1163, -1 -> 1163, 0 -> 608, 1 -> 636, 2 -> 664, 3 -> 1023
/*     */     //   608: aload #19
/*     */     //   610: instanceof com/hypixel/hytale/protocol/StringParamValue
/*     */     //   613: ifeq -> 633
/*     */     //   616: aload #19
/*     */     //   618: checkcast com/hypixel/hytale/protocol/StringParamValue
/*     */     //   621: astore #24
/*     */     //   623: aload #24
/*     */     //   625: getfield value : Ljava/lang/String;
/*     */     //   628: invokevirtual toUpperCase : ()Ljava/lang/String;
/*     */     //   631: astore #21
/*     */     //   633: goto -> 1163
/*     */     //   636: aload #19
/*     */     //   638: instanceof com/hypixel/hytale/protocol/StringParamValue
/*     */     //   641: ifeq -> 661
/*     */     //   644: aload #19
/*     */     //   646: checkcast com/hypixel/hytale/protocol/StringParamValue
/*     */     //   649: astore #24
/*     */     //   651: aload #24
/*     */     //   653: getfield value : Ljava/lang/String;
/*     */     //   656: invokevirtual toLowerCase : ()Ljava/lang/String;
/*     */     //   659: astore #21
/*     */     //   661: goto -> 1163
/*     */     //   664: aload #18
/*     */     //   666: astore #24
/*     */     //   668: iconst_0
/*     */     //   669: istore #25
/*     */     //   671: aload #24
/*     */     //   673: iload #25
/*     */     //   675: <illegal opcode> typeSwitch : (Ljava/lang/Object;I)I
/*     */     //   680: tableswitch default -> 866, -1 -> 866, 0 -> 708, 1 -> 866
/*     */     //   708: aload #19
/*     */     //   710: dup
/*     */     //   711: invokestatic requireNonNull : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   714: pop
/*     */     //   715: astore #26
/*     */     //   717: iconst_0
/*     */     //   718: istore #27
/*     */     //   720: aload #26
/*     */     //   722: iload #27
/*     */     //   724: <illegal opcode> typeSwitch : (Ljava/lang/Object;I)I
/*     */     //   729: tableswitch default -> 859, 0 -> 764, 1 -> 779, 2 -> 804, 3 -> 823, 4 -> 841
/*     */     //   764: aload #26
/*     */     //   766: checkcast com/hypixel/hytale/protocol/StringParamValue
/*     */     //   769: astore #28
/*     */     //   771: aload #28
/*     */     //   773: getfield value : Ljava/lang/String;
/*     */     //   776: goto -> 861
/*     */     //   779: aload #26
/*     */     //   781: checkcast com/hypixel/hytale/protocol/BoolParamValue
/*     */     //   784: astore #29
/*     */     //   786: aload #29
/*     */     //   788: getfield value : Z
/*     */     //   791: ifeq -> 799
/*     */     //   794: ldc '1'
/*     */     //   796: goto -> 861
/*     */     //   799: ldc '0'
/*     */     //   801: goto -> 861
/*     */     //   804: aload #26
/*     */     //   806: checkcast com/hypixel/hytale/protocol/DoubleParamValue
/*     */     //   809: astore #30
/*     */     //   811: aload #30
/*     */     //   813: getfield value : D
/*     */     //   816: d2i
/*     */     //   817: invokestatic toString : (I)Ljava/lang/String;
/*     */     //   820: goto -> 861
/*     */     //   823: aload #26
/*     */     //   825: checkcast com/hypixel/hytale/protocol/IntParamValue
/*     */     //   828: astore #31
/*     */     //   830: aload #31
/*     */     //   832: getfield value : I
/*     */     //   835: invokestatic toString : (I)Ljava/lang/String;
/*     */     //   838: goto -> 861
/*     */     //   841: aload #26
/*     */     //   843: checkcast com/hypixel/hytale/protocol/LongParamValue
/*     */     //   846: astore #32
/*     */     //   848: aload #32
/*     */     //   850: getfield value : J
/*     */     //   853: invokestatic toString : (J)Ljava/lang/String;
/*     */     //   856: goto -> 861
/*     */     //   859: ldc ''
/*     */     //   861: astore #21
/*     */     //   863: goto -> 1020
/*     */     //   866: aload #19
/*     */     //   868: dup
/*     */     //   869: invokestatic requireNonNull : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   872: pop
/*     */     //   873: astore #26
/*     */     //   875: iconst_0
/*     */     //   876: istore #27
/*     */     //   878: aload #26
/*     */     //   880: iload #27
/*     */     //   882: <illegal opcode> typeSwitch : (Ljava/lang/Object;I)I
/*     */     //   887: tableswitch default -> 1016, 0 -> 920, 1 -> 935, 2 -> 960, 3 -> 980, 4 -> 998
/*     */     //   920: aload #26
/*     */     //   922: checkcast com/hypixel/hytale/protocol/StringParamValue
/*     */     //   925: astore #28
/*     */     //   927: aload #28
/*     */     //   929: getfield value : Ljava/lang/String;
/*     */     //   932: goto -> 1018
/*     */     //   935: aload #26
/*     */     //   937: checkcast com/hypixel/hytale/protocol/BoolParamValue
/*     */     //   940: astore #29
/*     */     //   942: aload #29
/*     */     //   944: getfield value : Z
/*     */     //   947: ifeq -> 955
/*     */     //   950: ldc '1'
/*     */     //   952: goto -> 1018
/*     */     //   955: ldc '0'
/*     */     //   957: goto -> 1018
/*     */     //   960: aload #26
/*     */     //   962: checkcast com/hypixel/hytale/protocol/DoubleParamValue
/*     */     //   965: astore #30
/*     */     //   967: aload #30
/*     */     //   969: getfield value : D
/*     */     //   972: d2i
/*     */     //   973: i2d
/*     */     //   974: invokestatic toString : (D)Ljava/lang/String;
/*     */     //   977: goto -> 1018
/*     */     //   980: aload #26
/*     */     //   982: checkcast com/hypixel/hytale/protocol/IntParamValue
/*     */     //   985: astore #31
/*     */     //   987: aload #31
/*     */     //   989: getfield value : I
/*     */     //   992: invokestatic toString : (I)Ljava/lang/String;
/*     */     //   995: goto -> 1018
/*     */     //   998: aload #26
/*     */     //   1000: checkcast com/hypixel/hytale/protocol/LongParamValue
/*     */     //   1003: astore #32
/*     */     //   1005: aload #32
/*     */     //   1007: getfield value : J
/*     */     //   1010: invokestatic toString : (J)Ljava/lang/String;
/*     */     //   1013: goto -> 1018
/*     */     //   1016: ldc ''
/*     */     //   1018: astore #21
/*     */     //   1020: goto -> 1163
/*     */     //   1023: aload #18
/*     */     //   1025: ifnull -> 1163
/*     */     //   1028: aload #18
/*     */     //   1030: invokestatic parsePluralOptions : (Ljava/lang/String;)Ljava/util/Map;
/*     */     //   1033: astore #24
/*     */     //   1035: aload #19
/*     */     //   1037: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   1040: invokestatic parseInt : (Ljava/lang/String;)I
/*     */     //   1043: istore #25
/*     */     //   1045: iload #25
/*     */     //   1047: ldc 'en-US'
/*     */     //   1049: invokestatic getPluralCategory : (ILjava/lang/String;)Ljava/lang/String;
/*     */     //   1052: astore #26
/*     */     //   1054: aload #24
/*     */     //   1056: aload #26
/*     */     //   1058: invokeinterface containsKey : (Ljava/lang/Object;)Z
/*     */     //   1063: ifeq -> 1083
/*     */     //   1066: aload #24
/*     */     //   1068: aload #26
/*     */     //   1070: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   1075: checkcast java/lang/String
/*     */     //   1078: astore #27
/*     */     //   1080: goto -> 1151
/*     */     //   1083: aload #24
/*     */     //   1085: ldc_w 'other'
/*     */     //   1088: invokeinterface containsKey : (Ljava/lang/Object;)Z
/*     */     //   1093: ifeq -> 1114
/*     */     //   1096: aload #24
/*     */     //   1098: ldc_w 'other'
/*     */     //   1101: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   1106: checkcast java/lang/String
/*     */     //   1109: astore #27
/*     */     //   1111: goto -> 1151
/*     */     //   1114: aload #24
/*     */     //   1116: invokeinterface isEmpty : ()Z
/*     */     //   1121: ifeq -> 1129
/*     */     //   1124: ldc ''
/*     */     //   1126: goto -> 1149
/*     */     //   1129: aload #24
/*     */     //   1131: invokeinterface values : ()Ljava/util/Collection;
/*     */     //   1136: invokeinterface iterator : ()Ljava/util/Iterator;
/*     */     //   1141: invokeinterface next : ()Ljava/lang/Object;
/*     */     //   1146: checkcast java/lang/String
/*     */     //   1149: astore #27
/*     */     //   1151: aload #27
/*     */     //   1153: aload_1
/*     */     //   1154: aload_2
/*     */     //   1155: invokestatic formatText : (Ljava/lang/String;Ljava/util/Map;Ljava/util/Map;)Ljava/lang/String;
/*     */     //   1158: astore #21
/*     */     //   1160: goto -> 1163
/*     */     //   1163: aload #17
/*     */     //   1165: ifnonnull -> 1315
/*     */     //   1168: aload #19
/*     */     //   1170: dup
/*     */     //   1171: invokestatic requireNonNull : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   1174: pop
/*     */     //   1175: astore #22
/*     */     //   1177: iconst_0
/*     */     //   1178: istore #23
/*     */     //   1180: aload #22
/*     */     //   1182: iload #23
/*     */     //   1184: <illegal opcode> typeSwitch : (Ljava/lang/Object;I)I
/*     */     //   1189: tableswitch default -> 1311, 0 -> 1224, 1 -> 1239, 2 -> 1257, 3 -> 1275, 4 -> 1293
/*     */     //   1224: aload #22
/*     */     //   1226: checkcast com/hypixel/hytale/protocol/StringParamValue
/*     */     //   1229: astore #24
/*     */     //   1231: aload #24
/*     */     //   1233: getfield value : Ljava/lang/String;
/*     */     //   1236: goto -> 1313
/*     */     //   1239: aload #22
/*     */     //   1241: checkcast com/hypixel/hytale/protocol/BoolParamValue
/*     */     //   1244: astore #25
/*     */     //   1246: aload #25
/*     */     //   1248: getfield value : Z
/*     */     //   1251: invokestatic toString : (Z)Ljava/lang/String;
/*     */     //   1254: goto -> 1313
/*     */     //   1257: aload #22
/*     */     //   1259: checkcast com/hypixel/hytale/protocol/DoubleParamValue
/*     */     //   1262: astore #26
/*     */     //   1264: aload #26
/*     */     //   1266: getfield value : D
/*     */     //   1269: invokestatic toString : (D)Ljava/lang/String;
/*     */     //   1272: goto -> 1313
/*     */     //   1275: aload #22
/*     */     //   1277: checkcast com/hypixel/hytale/protocol/IntParamValue
/*     */     //   1280: astore #27
/*     */     //   1282: aload #27
/*     */     //   1284: getfield value : I
/*     */     //   1287: invokestatic toString : (I)Ljava/lang/String;
/*     */     //   1290: goto -> 1313
/*     */     //   1293: aload #22
/*     */     //   1295: checkcast com/hypixel/hytale/protocol/LongParamValue
/*     */     //   1298: astore #28
/*     */     //   1300: aload #28
/*     */     //   1302: getfield value : J
/*     */     //   1305: invokestatic toString : (J)Ljava/lang/String;
/*     */     //   1308: goto -> 1313
/*     */     //   1311: ldc ''
/*     */     //   1313: astore #21
/*     */     //   1315: aload #4
/*     */     //   1317: aload #21
/*     */     //   1319: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   1322: pop
/*     */     //   1323: goto -> 1337
/*     */     //   1326: aload #4
/*     */     //   1328: aload_0
/*     */     //   1329: iload #6
/*     */     //   1331: iload #8
/*     */     //   1333: invokevirtual append : (Ljava/lang/CharSequence;II)Ljava/lang/StringBuilder;
/*     */     //   1336: pop
/*     */     //   1337: iload #8
/*     */     //   1339: istore #6
/*     */     //   1341: iload #8
/*     */     //   1343: iconst_1
/*     */     //   1344: iadd
/*     */     //   1345: istore #5
/*     */     //   1347: goto -> 1413
/*     */     //   1350: iload #7
/*     */     //   1352: bipush #125
/*     */     //   1354: if_icmpne -> 1413
/*     */     //   1357: iload #6
/*     */     //   1359: iconst_1
/*     */     //   1360: iadd
/*     */     //   1361: iload_3
/*     */     //   1362: if_icmpge -> 1413
/*     */     //   1365: aload_0
/*     */     //   1366: iload #6
/*     */     //   1368: iconst_1
/*     */     //   1369: iadd
/*     */     //   1370: invokevirtual charAt : (I)C
/*     */     //   1373: bipush #125
/*     */     //   1375: if_icmpne -> 1413
/*     */     //   1378: iload #6
/*     */     //   1380: iload #5
/*     */     //   1382: if_icmple -> 1396
/*     */     //   1385: aload #4
/*     */     //   1387: aload_0
/*     */     //   1388: iload #5
/*     */     //   1390: iload #6
/*     */     //   1392: invokevirtual append : (Ljava/lang/CharSequence;II)Ljava/lang/StringBuilder;
/*     */     //   1395: pop
/*     */     //   1396: aload #4
/*     */     //   1398: bipush #125
/*     */     //   1400: invokevirtual append : (C)Ljava/lang/StringBuilder;
/*     */     //   1403: pop
/*     */     //   1404: iinc #6, 1
/*     */     //   1407: iload #6
/*     */     //   1409: iconst_1
/*     */     //   1410: iadd
/*     */     //   1411: istore #5
/*     */     //   1413: iinc #6, 1
/*     */     //   1416: goto -> 48
/*     */     //   1419: iload #5
/*     */     //   1421: iload_3
/*     */     //   1422: if_icmpge -> 1435
/*     */     //   1425: aload #4
/*     */     //   1427: aload_0
/*     */     //   1428: iload #5
/*     */     //   1430: iload_3
/*     */     //   1431: invokevirtual append : (Ljava/lang/CharSequence;II)Ljava/lang/StringBuilder;
/*     */     //   1434: pop
/*     */     //   1435: aload #4
/*     */     //   1437: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   1440: areturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #70	-> 0
/*     */     //   #71	-> 14
/*     */     //   #73	-> 24
/*     */     //   #74	-> 29
/*     */     //   #75	-> 42
/*     */     //   #77	-> 45
/*     */     //   #78	-> 54
/*     */     //   #81	-> 62
/*     */     //   #82	-> 69
/*     */     //   #84	-> 90
/*     */     //   #85	-> 108
/*     */     //   #86	-> 116
/*     */     //   #87	-> 119
/*     */     //   #88	-> 125
/*     */     //   #92	-> 128
/*     */     //   #93	-> 136
/*     */     //   #96	-> 144
/*     */     //   #98	-> 162
/*     */     //   #100	-> 168
/*     */     //   #101	-> 180
/*     */     //   #104	-> 203
/*     */     //   #105	-> 207
/*     */     //   #106	-> 228
/*     */     //   #107	-> 240
/*     */     //   #108	-> 252
/*     */     //   #111	-> 275
/*     */     //   #112	-> 278
/*     */     //   #113	-> 290
/*     */     //   #114	-> 296
/*     */     //   #115	-> 310
/*     */     //   #116	-> 322
/*     */     //   #117	-> 334
/*     */     //   #121	-> 352
/*     */     //   #122	-> 355
/*     */     //   #123	-> 367
/*     */     //   #124	-> 373
/*     */     //   #125	-> 385
/*     */     //   #126	-> 397
/*     */     //   #129	-> 415
/*     */     //   #130	-> 436
/*     */     //   #131	-> 457
/*     */     //   #132	-> 462
/*     */     //   #133	-> 470
/*     */     //   #134	-> 484
/*     */     //   #135	-> 492
/*     */     //   #136	-> 507
/*     */     //   #137	-> 512
/*     */     //   #139	-> 536
/*     */     //   #141	-> 547
/*     */     //   #142	-> 550
/*     */     //   #143	-> 555
/*     */     //   #145	-> 559
/*     */     //   #147	-> 608
/*     */     //   #148	-> 623
/*     */     //   #150	-> 633
/*     */     //   #153	-> 636
/*     */     //   #154	-> 651
/*     */     //   #156	-> 661
/*     */     //   #160	-> 664
/*     */     //   #162	-> 708
/*     */     //   #163	-> 764
/*     */     //   #164	-> 779
/*     */     //   #165	-> 804
/*     */     //   #166	-> 823
/*     */     //   #167	-> 841
/*     */     //   #168	-> 859
/*     */     //   #169	-> 861
/*     */     //   #170	-> 863
/*     */     //   #175	-> 866
/*     */     //   #176	-> 920
/*     */     //   #177	-> 935
/*     */     //   #178	-> 960
/*     */     //   #179	-> 980
/*     */     //   #180	-> 998
/*     */     //   #181	-> 1016
/*     */     //   #182	-> 1018
/*     */     //   #186	-> 1020
/*     */     //   #191	-> 1023
/*     */     //   #192	-> 1028
/*     */     //   #194	-> 1035
/*     */     //   #195	-> 1045
/*     */     //   #198	-> 1054
/*     */     //   #199	-> 1066
/*     */     //   #200	-> 1083
/*     */     //   #201	-> 1096
/*     */     //   #204	-> 1114
/*     */     //   #207	-> 1151
/*     */     //   #208	-> 1160
/*     */     //   #217	-> 1163
/*     */     //   #218	-> 1168
/*     */     //   #219	-> 1224
/*     */     //   #220	-> 1239
/*     */     //   #221	-> 1257
/*     */     //   #222	-> 1275
/*     */     //   #223	-> 1293
/*     */     //   #224	-> 1311
/*     */     //   #225	-> 1313
/*     */     //   #228	-> 1315
/*     */     //   #229	-> 1323
/*     */     //   #231	-> 1326
/*     */     //   #234	-> 1337
/*     */     //   #235	-> 1341
/*     */     //   #236	-> 1347
/*     */     //   #240	-> 1350
/*     */     //   #241	-> 1378
/*     */     //   #242	-> 1396
/*     */     //   #243	-> 1404
/*     */     //   #244	-> 1407
/*     */     //   #77	-> 1413
/*     */     //   #249	-> 1419
/*     */     //   #251	-> 1435
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   296	56	18	formatStart	I
/*     */     //   310	42	19	formatEndExclusive	I
/*     */     //   322	30	20	fs	I
/*     */     //   334	18	21	fl	I
/*     */     //   373	42	19	optionsStart	I
/*     */     //   385	30	20	os	I
/*     */     //   397	18	21	ol	I
/*     */     //   507	40	21	message	Ljava/lang/String;
/*     */     //   623	10	24	s	Lcom/hypixel/hytale/protocol/StringParamValue;
/*     */     //   651	10	24	s	Lcom/hypixel/hytale/protocol/StringParamValue;
/*     */     //   771	8	28	s	Lcom/hypixel/hytale/protocol/StringParamValue;
/*     */     //   786	18	29	b	Lcom/hypixel/hytale/protocol/BoolParamValue;
/*     */     //   811	12	30	d	Lcom/hypixel/hytale/protocol/DoubleParamValue;
/*     */     //   830	11	31	iv	Lcom/hypixel/hytale/protocol/IntParamValue;
/*     */     //   848	11	32	l	Lcom/hypixel/hytale/protocol/LongParamValue;
/*     */     //   927	8	28	s	Lcom/hypixel/hytale/protocol/StringParamValue;
/*     */     //   942	18	29	b	Lcom/hypixel/hytale/protocol/BoolParamValue;
/*     */     //   967	13	30	d	Lcom/hypixel/hytale/protocol/DoubleParamValue;
/*     */     //   987	11	31	iv	Lcom/hypixel/hytale/protocol/IntParamValue;
/*     */     //   1005	11	32	l	Lcom/hypixel/hytale/protocol/LongParamValue;
/*     */     //   1080	3	27	selected	Ljava/lang/String;
/*     */     //   1111	3	27	selected	Ljava/lang/String;
/*     */     //   1035	125	24	pluralTexts	Ljava/util/Map;
/*     */     //   1045	115	25	value	I
/*     */     //   1054	106	26	category	Ljava/lang/String;
/*     */     //   1151	9	27	selected	Ljava/lang/String;
/*     */     //   1231	8	24	s	Lcom/hypixel/hytale/protocol/StringParamValue;
/*     */     //   1246	11	25	b	Lcom/hypixel/hytale/protocol/BoolParamValue;
/*     */     //   1264	11	26	d	Lcom/hypixel/hytale/protocol/DoubleParamValue;
/*     */     //   1282	11	27	iv	Lcom/hypixel/hytale/protocol/IntParamValue;
/*     */     //   1300	11	28	l	Lcom/hypixel/hytale/protocol/LongParamValue;
/*     */     //   559	764	21	formattedReplacement	Ljava/lang/String;
/*     */     //   136	1214	8	end	I
/*     */     //   168	1182	9	contentStart	I
/*     */     //   180	1170	10	c1	I
/*     */     //   203	1147	11	c2	I
/*     */     //   207	1143	12	nameStart	I
/*     */     //   228	1122	13	nameEndExclusive	I
/*     */     //   240	1110	14	ns	I
/*     */     //   252	1098	15	nl	I
/*     */     //   275	1075	16	key	Ljava/lang/String;
/*     */     //   278	1072	17	format	Ljava/lang/String;
/*     */     //   355	995	18	options	Ljava/lang/String;
/*     */     //   436	914	19	replacement	Lcom/hypixel/hytale/protocol/ParamValue;
/*     */     //   457	893	20	replacementMessage	Lcom/hypixel/hytale/protocol/FormattedMessage;
/*     */     //   62	1351	7	ch	C
/*     */     //   48	1371	6	i	I
/*     */     //   0	1441	0	text	Ljava/lang/String;
/*     */     //   0	1441	1	params	Ljava/util/Map;
/*     */     //   0	1441	2	messageParams	Ljava/util/Map;
/*     */     //   29	1412	3	len	I
/*     */     //   42	1399	4	sb	Ljava/lang/StringBuilder;
/*     */     //   45	1396	5	lastWritePos	I
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   1035	125	24	pluralTexts	Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;
/*     */     //   0	1441	1	params	Ljava/util/Map<Ljava/lang/String;Lcom/hypixel/hytale/protocol/ParamValue;>;
/*     */     //   0	1441	2	messageParams	Ljava/util/Map<Ljava/lang/String;Lcom/hypixel/hytale/protocol/FormattedMessage;>;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int findMatchingBrace(@Nonnull String text, int start) {
/* 256 */     int depth = 0;
/* 257 */     int len = text.length();
/* 258 */     for (int i = start; i < len; i++) {
/* 259 */       if (text.charAt(i) == '{') {
/* 260 */         depth++;
/*     */       } else {
/* 262 */         depth--;
/* 263 */         if (text.charAt(i) == '}' && depth == 0) return i;
/*     */       
/*     */       } 
/*     */     } 
/* 267 */     return -1;
/*     */   }
/*     */   
/*     */   private static int trimStart(@Nonnull String text, int start, int end) {
/* 271 */     int i = start;
/* 272 */     for (; i <= end && Character.isWhitespace(text.charAt(i)); i++);
/* 273 */     return i;
/*     */   }
/*     */   
/*     */   private static int trimEnd(@Nonnull String text, int start, int end) {
/* 277 */     int i = start;
/* 278 */     for (; end >= i && Character.isWhitespace(text.charAt(i)); end--);
/* 279 */     return (end >= i) ? (end - i + 1) : 0;
/*     */   }
/*     */   
/* 282 */   private static final String[] ICU_PLURAL_KEYWORDS = new String[] { "zero", "one", "two", "few", "many", "other" };
/*     */   
/*     */   @Nonnull
/*     */   private static Map<String, String> parsePluralOptions(@Nonnull String options) {
/* 286 */     HashMap<String, String> result = new HashMap<>();
/*     */     
/* 288 */     for (String keyword : ICU_PLURAL_KEYWORDS) {
/* 289 */       String searchPattern = keyword + " {";
/* 290 */       int idx = options.indexOf(searchPattern);
/* 291 */       if (idx >= 0) {
/*     */         
/* 293 */         int braceStart = idx + keyword.length() + 1;
/* 294 */         int end = findMatchingBrace(options, braceStart);
/* 295 */         if (end > braceStart + 1) {
/* 296 */           result.put(keyword, options.substring(braceStart + 1, end));
/*     */         }
/*     */       } 
/*     */     } 
/* 300 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getPluralCategory(int n, @Nonnull String locale) {
/* 308 */     String lang = locale.contains("-") ? locale.substring(0, locale.indexOf('-')) : locale;
/*     */ 
/*     */     
/* 311 */     switch (lang) { case "en": case "fr": case "de": case "pt": return 
/*     */ 
/*     */ 
/*     */           
/* 315 */           (locale.equals("pt-BR") || locale.equals("pt_BR")) ? getPortugueseBrazilianPluralCategory(n) : getPortuguesePluralCategory(n);
/*     */       case "ru": 
/*     */       case "es": 
/*     */       case "pl": 
/*     */       case "tr": 
/*     */       case "uk": 
/*     */       case "it": 
/*     */       case "nl": 
/*     */       case "da": 
/*     */       case "fi": 
/*     */       case "no": case "nb": case "nn": 
/*     */       case "zh": 
/*     */       case "ja": 
/*     */       case "ko":
/* 329 */        }  return getEnglishPluralCategory(n);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getEnglishPluralCategory(int n) {
/* 336 */     return (n == 1) ? "one" : "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getFrenchPluralCategory(int n) {
/* 342 */     return (n == 0 || n == 1) ? "one" : "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getGermanPluralCategory(int n) {
/* 348 */     return (n == 1) ? "one" : "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getPortuguesePluralCategory(int n) {
/* 354 */     return (n == 1) ? "one" : "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getPortugueseBrazilianPluralCategory(int n) {
/* 360 */     return (n == 0 || n == 1) ? "one" : "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getRussianPluralCategory(int n) {
/* 366 */     int absN = Math.abs(n);
/* 367 */     int mod10 = absN % 10;
/* 368 */     int mod100 = absN % 100;
/*     */     
/* 370 */     if (mod10 == 1 && mod100 != 11) return "one"; 
/* 371 */     if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) return "few"; 
/* 372 */     if (mod10 == 0 || (mod10 >= 5 && mod10 <= 9) || (mod100 >= 11 && mod100 <= 14)) return "many";
/*     */     
/* 374 */     return "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getSpanishPluralCategory(int n) {
/* 380 */     return (n == 1) ? "one" : "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getPolishPluralCategory(int n) {
/* 386 */     int absN = Math.abs(n);
/* 387 */     int mod10 = absN % 10;
/* 388 */     int mod100 = absN % 100;
/*     */     
/* 390 */     if (n == 1) return "one"; 
/* 391 */     if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) return "few"; 
/* 392 */     if (mod10 == 0 || mod10 == 1 || (mod10 >= 5 && mod10 <= 9) || (mod100 >= 12 && mod100 <= 14)) return "many";
/*     */     
/* 394 */     return "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getTurkishPluralCategory(int n) {
/* 400 */     return (n == 1) ? "one" : "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getUkrainianPluralCategory(int n) {
/* 406 */     int absN = Math.abs(n);
/* 407 */     int mod10 = absN % 10;
/* 408 */     int mod100 = absN % 100;
/*     */     
/* 410 */     if (mod10 == 1 && mod100 != 11) return "one"; 
/* 411 */     if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) return "few"; 
/* 412 */     if (mod10 == 0 || (mod10 >= 5 && mod10 <= 9) || (mod100 >= 11 && mod100 <= 14)) return "many";
/*     */     
/* 414 */     return "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getItalianPluralCategory(int n) {
/* 420 */     return (n == 1) ? "one" : "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getDutchPluralCategory(int n) {
/* 426 */     return (n == 1) ? "one" : "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getDanishPluralCategory(int n) {
/* 432 */     return (n == 1) ? "one" : "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getFinnishPluralCategory(int n) {
/* 438 */     return (n == 1) ? "one" : "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getNorwegianPluralCategory(int n) {
/* 444 */     return (n == 1) ? "one" : "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getChinesePluralCategory(int n) {
/* 450 */     return "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getJapanesePluralCategory(int n) {
/* 456 */     return "other";
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static String getKoreanPluralCategory(int n) {
/* 462 */     return "other";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\util\MessageUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */