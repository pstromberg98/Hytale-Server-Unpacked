/*     */ package com.hypixel.hytale.protocol.packets.connection;
/*     */ 
/*     */ import com.hypixel.hytale.protocol.HostAddress;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.io.PacketIO;
/*     */ import com.hypixel.hytale.protocol.io.ProtocolException;
/*     */ import com.hypixel.hytale.protocol.io.ValidationResult;
/*     */ import com.hypixel.hytale.protocol.io.VarInt;
/*     */ import io.netty.buffer.ByteBuf;
/*     */ import java.util.Arrays;
/*     */ import java.util.Objects;
/*     */ import java.util.UUID;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ public class Connect
/*     */   implements Packet
/*     */ {
/*     */   public static final int PACKET_ID = 0;
/*     */   public static final boolean IS_COMPRESSED = false;
/*     */   public static final int NULLABLE_BIT_FIELD_SIZE = 1;
/*     */   public static final int FIXED_BLOCK_SIZE = 46;
/*     */   
/*     */   public int getId() {
/*  26 */     return 0;
/*     */   }
/*     */   public static final int VARIABLE_FIELD_COUNT = 5; public static final int VARIABLE_BLOCK_START = 66; public static final int MAX_SIZE = 38013; public int protocolCrc;
/*     */   public int protocolBuildNumber;
/*     */   @Nonnull
/*  31 */   public String clientVersion = ""; @Nonnull
/*  32 */   public ClientType clientType = ClientType.Game; @Nonnull
/*  33 */   public UUID uuid = new UUID(0L, 0L); @Nonnull
/*  34 */   public String username = ""; @Nullable
/*     */   public String identityToken; @Nonnull
/*  36 */   public String language = "";
/*     */   
/*     */   @Nullable
/*     */   public byte[] referralData;
/*     */   @Nullable
/*     */   public HostAddress referralSource;
/*     */   
/*     */   public Connect(int protocolCrc, int protocolBuildNumber, @Nonnull String clientVersion, @Nonnull ClientType clientType, @Nonnull UUID uuid, @Nonnull String username, @Nullable String identityToken, @Nonnull String language, @Nullable byte[] referralData, @Nullable HostAddress referralSource) {
/*  44 */     this.protocolCrc = protocolCrc;
/*  45 */     this.protocolBuildNumber = protocolBuildNumber;
/*  46 */     this.clientVersion = clientVersion;
/*  47 */     this.clientType = clientType;
/*  48 */     this.uuid = uuid;
/*  49 */     this.username = username;
/*  50 */     this.identityToken = identityToken;
/*  51 */     this.language = language;
/*  52 */     this.referralData = referralData;
/*  53 */     this.referralSource = referralSource;
/*     */   }
/*     */   
/*     */   public Connect(@Nonnull Connect other) {
/*  57 */     this.protocolCrc = other.protocolCrc;
/*  58 */     this.protocolBuildNumber = other.protocolBuildNumber;
/*  59 */     this.clientVersion = other.clientVersion;
/*  60 */     this.clientType = other.clientType;
/*  61 */     this.uuid = other.uuid;
/*  62 */     this.username = other.username;
/*  63 */     this.identityToken = other.identityToken;
/*  64 */     this.language = other.language;
/*  65 */     this.referralData = other.referralData;
/*  66 */     this.referralSource = other.referralSource;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static Connect deserialize(@Nonnull ByteBuf buf, int offset) {
/*  71 */     Connect obj = new Connect();
/*  72 */     byte nullBits = buf.getByte(offset);
/*  73 */     obj.protocolCrc = buf.getIntLE(offset + 1);
/*  74 */     obj.protocolBuildNumber = buf.getIntLE(offset + 5);
/*  75 */     obj.clientVersion = PacketIO.readFixedAsciiString(buf, offset + 9, 20);
/*  76 */     obj.clientType = ClientType.fromValue(buf.getByte(offset + 29));
/*  77 */     obj.uuid = PacketIO.readUUID(buf, offset + 30);
/*     */ 
/*     */     
/*  80 */     int varPos0 = offset + 66 + buf.getIntLE(offset + 46);
/*  81 */     int usernameLen = VarInt.peek(buf, varPos0);
/*  82 */     if (usernameLen < 0) throw ProtocolException.negativeLength("Username", usernameLen); 
/*  83 */     if (usernameLen > 16) throw ProtocolException.stringTooLong("Username", usernameLen, 16); 
/*  84 */     obj.username = PacketIO.readVarString(buf, varPos0, PacketIO.ASCII);
/*     */     
/*  86 */     if ((nullBits & 0x1) != 0) {
/*  87 */       int varPos1 = offset + 66 + buf.getIntLE(offset + 50);
/*  88 */       int identityTokenLen = VarInt.peek(buf, varPos1);
/*  89 */       if (identityTokenLen < 0) throw ProtocolException.negativeLength("IdentityToken", identityTokenLen); 
/*  90 */       if (identityTokenLen > 8192) throw ProtocolException.stringTooLong("IdentityToken", identityTokenLen, 8192); 
/*  91 */       obj.identityToken = PacketIO.readVarString(buf, varPos1, PacketIO.UTF8);
/*     */     } 
/*     */     
/*  94 */     int varPos2 = offset + 66 + buf.getIntLE(offset + 54);
/*  95 */     int languageLen = VarInt.peek(buf, varPos2);
/*  96 */     if (languageLen < 0) throw ProtocolException.negativeLength("Language", languageLen); 
/*  97 */     if (languageLen > 16) throw ProtocolException.stringTooLong("Language", languageLen, 16); 
/*  98 */     obj.language = PacketIO.readVarString(buf, varPos2, PacketIO.ASCII);
/*     */     
/* 100 */     if ((nullBits & 0x2) != 0) {
/* 101 */       int varPos3 = offset + 66 + buf.getIntLE(offset + 58);
/* 102 */       int referralDataCount = VarInt.peek(buf, varPos3);
/* 103 */       if (referralDataCount < 0) throw ProtocolException.negativeLength("ReferralData", referralDataCount); 
/* 104 */       if (referralDataCount > 4096) throw ProtocolException.arrayTooLong("ReferralData", referralDataCount, 4096); 
/* 105 */       int varIntLen = VarInt.length(buf, varPos3);
/* 106 */       if ((varPos3 + varIntLen) + referralDataCount * 1L > buf.readableBytes())
/* 107 */         throw ProtocolException.bufferTooSmall("ReferralData", varPos3 + varIntLen + referralDataCount * 1, buf.readableBytes()); 
/* 108 */       obj.referralData = new byte[referralDataCount];
/* 109 */       for (int i = 0; i < referralDataCount; i++) {
/* 110 */         obj.referralData[i] = buf.getByte(varPos3 + varIntLen + i * 1);
/*     */       }
/*     */     } 
/* 113 */     if ((nullBits & 0x4) != 0) {
/* 114 */       int varPos4 = offset + 66 + buf.getIntLE(offset + 62);
/* 115 */       obj.referralSource = HostAddress.deserialize(buf, varPos4);
/*     */     } 
/*     */     
/* 118 */     return obj;
/*     */   }
/*     */   
/*     */   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
/* 122 */     byte nullBits = buf.getByte(offset);
/* 123 */     int maxEnd = 66;
/*     */     
/* 125 */     int fieldOffset0 = buf.getIntLE(offset + 46);
/* 126 */     int pos0 = offset + 66 + fieldOffset0;
/* 127 */     int sl = VarInt.peek(buf, pos0); pos0 += VarInt.length(buf, pos0) + sl;
/* 128 */     if (pos0 - offset > maxEnd) maxEnd = pos0 - offset;
/*     */     
/* 130 */     if ((nullBits & 0x1) != 0) {
/* 131 */       int fieldOffset1 = buf.getIntLE(offset + 50);
/* 132 */       int pos1 = offset + 66 + fieldOffset1;
/* 133 */       sl = VarInt.peek(buf, pos1); pos1 += VarInt.length(buf, pos1) + sl;
/* 134 */       if (pos1 - offset > maxEnd) maxEnd = pos1 - offset;
/*     */     
/*     */     } 
/* 137 */     int fieldOffset2 = buf.getIntLE(offset + 54);
/* 138 */     int pos2 = offset + 66 + fieldOffset2;
/* 139 */     sl = VarInt.peek(buf, pos2); pos2 += VarInt.length(buf, pos2) + sl;
/* 140 */     if (pos2 - offset > maxEnd) maxEnd = pos2 - offset;
/*     */     
/* 142 */     if ((nullBits & 0x2) != 0) {
/* 143 */       int fieldOffset3 = buf.getIntLE(offset + 58);
/* 144 */       int pos3 = offset + 66 + fieldOffset3;
/* 145 */       int arrLen = VarInt.peek(buf, pos3); pos3 += VarInt.length(buf, pos3) + arrLen * 1;
/* 146 */       if (pos3 - offset > maxEnd) maxEnd = pos3 - offset; 
/*     */     } 
/* 148 */     if ((nullBits & 0x4) != 0) {
/* 149 */       int fieldOffset4 = buf.getIntLE(offset + 62);
/* 150 */       int pos4 = offset + 66 + fieldOffset4;
/* 151 */       pos4 += HostAddress.computeBytesConsumed(buf, pos4);
/* 152 */       if (pos4 - offset > maxEnd) maxEnd = pos4 - offset; 
/*     */     } 
/* 154 */     return maxEnd;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void serialize(@Nonnull ByteBuf buf) {
/* 160 */     int startPos = buf.writerIndex();
/* 161 */     byte nullBits = 0;
/* 162 */     if (this.identityToken != null) nullBits = (byte)(nullBits | 0x1); 
/* 163 */     if (this.referralData != null) nullBits = (byte)(nullBits | 0x2); 
/* 164 */     if (this.referralSource != null) nullBits = (byte)(nullBits | 0x4); 
/* 165 */     buf.writeByte(nullBits);
/*     */     
/* 167 */     buf.writeIntLE(this.protocolCrc);
/* 168 */     buf.writeIntLE(this.protocolBuildNumber);
/* 169 */     PacketIO.writeFixedAsciiString(buf, this.clientVersion, 20);
/* 170 */     buf.writeByte(this.clientType.getValue());
/* 171 */     PacketIO.writeUUID(buf, this.uuid);
/*     */     
/* 173 */     int usernameOffsetSlot = buf.writerIndex();
/* 174 */     buf.writeIntLE(0);
/* 175 */     int identityTokenOffsetSlot = buf.writerIndex();
/* 176 */     buf.writeIntLE(0);
/* 177 */     int languageOffsetSlot = buf.writerIndex();
/* 178 */     buf.writeIntLE(0);
/* 179 */     int referralDataOffsetSlot = buf.writerIndex();
/* 180 */     buf.writeIntLE(0);
/* 181 */     int referralSourceOffsetSlot = buf.writerIndex();
/* 182 */     buf.writeIntLE(0);
/*     */     
/* 184 */     int varBlockStart = buf.writerIndex();
/* 185 */     buf.setIntLE(usernameOffsetSlot, buf.writerIndex() - varBlockStart);
/* 186 */     PacketIO.writeVarAsciiString(buf, this.username, 16);
/* 187 */     if (this.identityToken != null) {
/* 188 */       buf.setIntLE(identityTokenOffsetSlot, buf.writerIndex() - varBlockStart);
/* 189 */       PacketIO.writeVarString(buf, this.identityToken, 8192);
/*     */     } else {
/* 191 */       buf.setIntLE(identityTokenOffsetSlot, -1);
/*     */     } 
/* 193 */     buf.setIntLE(languageOffsetSlot, buf.writerIndex() - varBlockStart);
/* 194 */     PacketIO.writeVarAsciiString(buf, this.language, 16);
/* 195 */     if (this.referralData != null) {
/* 196 */       buf.setIntLE(referralDataOffsetSlot, buf.writerIndex() - varBlockStart);
/* 197 */       if (this.referralData.length > 4096) throw ProtocolException.arrayTooLong("ReferralData", this.referralData.length, 4096);  VarInt.write(buf, this.referralData.length); for (byte item : this.referralData) buf.writeByte(item); 
/*     */     } else {
/* 199 */       buf.setIntLE(referralDataOffsetSlot, -1);
/*     */     } 
/* 201 */     if (this.referralSource != null) {
/* 202 */       buf.setIntLE(referralSourceOffsetSlot, buf.writerIndex() - varBlockStart);
/* 203 */       this.referralSource.serialize(buf);
/*     */     } else {
/* 205 */       buf.setIntLE(referralSourceOffsetSlot, -1);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int computeSize() {
/* 211 */     int size = 66;
/* 212 */     size += VarInt.size(this.username.length()) + this.username.length();
/* 213 */     if (this.identityToken != null) size += PacketIO.stringSize(this.identityToken); 
/* 214 */     size += VarInt.size(this.language.length()) + this.language.length();
/* 215 */     if (this.referralData != null) size += VarInt.size(this.referralData.length) + this.referralData.length * 1; 
/* 216 */     if (this.referralSource != null) size += this.referralSource.computeSize();
/*     */     
/* 218 */     return size;
/*     */   }
/*     */   
/*     */   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
/* 222 */     if (buffer.readableBytes() - offset < 66) {
/* 223 */       return ValidationResult.error("Buffer too small: expected at least 66 bytes");
/*     */     }
/*     */     
/* 226 */     byte nullBits = buffer.getByte(offset);
/*     */ 
/*     */ 
/*     */     
/* 230 */     int usernameOffset = buffer.getIntLE(offset + 46);
/* 231 */     if (usernameOffset < 0) {
/* 232 */       return ValidationResult.error("Invalid offset for Username");
/*     */     }
/* 234 */     int pos = offset + 66 + usernameOffset;
/* 235 */     if (pos >= buffer.writerIndex()) {
/* 236 */       return ValidationResult.error("Offset out of bounds for Username");
/*     */     }
/* 238 */     int usernameLen = VarInt.peek(buffer, pos);
/* 239 */     if (usernameLen < 0) {
/* 240 */       return ValidationResult.error("Invalid string length for Username");
/*     */     }
/* 242 */     if (usernameLen > 16) {
/* 243 */       return ValidationResult.error("Username exceeds max length 16");
/*     */     }
/* 245 */     pos += VarInt.length(buffer, pos);
/* 246 */     pos += usernameLen;
/* 247 */     if (pos > buffer.writerIndex()) {
/* 248 */       return ValidationResult.error("Buffer overflow reading Username");
/*     */     }
/*     */ 
/*     */     
/* 252 */     if ((nullBits & 0x1) != 0) {
/* 253 */       int identityTokenOffset = buffer.getIntLE(offset + 50);
/* 254 */       if (identityTokenOffset < 0) {
/* 255 */         return ValidationResult.error("Invalid offset for IdentityToken");
/*     */       }
/* 257 */       pos = offset + 66 + identityTokenOffset;
/* 258 */       if (pos >= buffer.writerIndex()) {
/* 259 */         return ValidationResult.error("Offset out of bounds for IdentityToken");
/*     */       }
/* 261 */       int identityTokenLen = VarInt.peek(buffer, pos);
/* 262 */       if (identityTokenLen < 0) {
/* 263 */         return ValidationResult.error("Invalid string length for IdentityToken");
/*     */       }
/* 265 */       if (identityTokenLen > 8192) {
/* 266 */         return ValidationResult.error("IdentityToken exceeds max length 8192");
/*     */       }
/* 268 */       pos += VarInt.length(buffer, pos);
/* 269 */       pos += identityTokenLen;
/* 270 */       if (pos > buffer.writerIndex()) {
/* 271 */         return ValidationResult.error("Buffer overflow reading IdentityToken");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 276 */     int languageOffset = buffer.getIntLE(offset + 54);
/* 277 */     if (languageOffset < 0) {
/* 278 */       return ValidationResult.error("Invalid offset for Language");
/*     */     }
/* 280 */     pos = offset + 66 + languageOffset;
/* 281 */     if (pos >= buffer.writerIndex()) {
/* 282 */       return ValidationResult.error("Offset out of bounds for Language");
/*     */     }
/* 284 */     int languageLen = VarInt.peek(buffer, pos);
/* 285 */     if (languageLen < 0) {
/* 286 */       return ValidationResult.error("Invalid string length for Language");
/*     */     }
/* 288 */     if (languageLen > 16) {
/* 289 */       return ValidationResult.error("Language exceeds max length 16");
/*     */     }
/* 291 */     pos += VarInt.length(buffer, pos);
/* 292 */     pos += languageLen;
/* 293 */     if (pos > buffer.writerIndex()) {
/* 294 */       return ValidationResult.error("Buffer overflow reading Language");
/*     */     }
/*     */ 
/*     */     
/* 298 */     if ((nullBits & 0x2) != 0) {
/* 299 */       int referralDataOffset = buffer.getIntLE(offset + 58);
/* 300 */       if (referralDataOffset < 0) {
/* 301 */         return ValidationResult.error("Invalid offset for ReferralData");
/*     */       }
/* 303 */       pos = offset + 66 + referralDataOffset;
/* 304 */       if (pos >= buffer.writerIndex()) {
/* 305 */         return ValidationResult.error("Offset out of bounds for ReferralData");
/*     */       }
/* 307 */       int referralDataCount = VarInt.peek(buffer, pos);
/* 308 */       if (referralDataCount < 0) {
/* 309 */         return ValidationResult.error("Invalid array count for ReferralData");
/*     */       }
/* 311 */       if (referralDataCount > 4096) {
/* 312 */         return ValidationResult.error("ReferralData exceeds max length 4096");
/*     */       }
/* 314 */       pos += VarInt.length(buffer, pos);
/* 315 */       pos += referralDataCount * 1;
/* 316 */       if (pos > buffer.writerIndex()) {
/* 317 */         return ValidationResult.error("Buffer overflow reading ReferralData");
/*     */       }
/*     */     } 
/*     */     
/* 321 */     if ((nullBits & 0x4) != 0) {
/* 322 */       int referralSourceOffset = buffer.getIntLE(offset + 62);
/* 323 */       if (referralSourceOffset < 0) {
/* 324 */         return ValidationResult.error("Invalid offset for ReferralSource");
/*     */       }
/* 326 */       pos = offset + 66 + referralSourceOffset;
/* 327 */       if (pos >= buffer.writerIndex()) {
/* 328 */         return ValidationResult.error("Offset out of bounds for ReferralSource");
/*     */       }
/* 330 */       ValidationResult referralSourceResult = HostAddress.validateStructure(buffer, pos);
/* 331 */       if (!referralSourceResult.isValid()) {
/* 332 */         return ValidationResult.error("Invalid ReferralSource: " + referralSourceResult.error());
/*     */       }
/* 334 */       pos += HostAddress.computeBytesConsumed(buffer, pos);
/*     */     } 
/* 336 */     return ValidationResult.OK;
/*     */   }
/*     */   
/*     */   public Connect clone() {
/* 340 */     Connect copy = new Connect();
/* 341 */     copy.protocolCrc = this.protocolCrc;
/* 342 */     copy.protocolBuildNumber = this.protocolBuildNumber;
/* 343 */     copy.clientVersion = this.clientVersion;
/* 344 */     copy.clientType = this.clientType;
/* 345 */     copy.uuid = this.uuid;
/* 346 */     copy.username = this.username;
/* 347 */     copy.identityToken = this.identityToken;
/* 348 */     copy.language = this.language;
/* 349 */     copy.referralData = (this.referralData != null) ? Arrays.copyOf(this.referralData, this.referralData.length) : null;
/* 350 */     copy.referralSource = (this.referralSource != null) ? this.referralSource.clone() : null;
/* 351 */     return copy;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*     */     Connect other;
/* 357 */     if (this == obj) return true; 
/* 358 */     if (obj instanceof Connect) { other = (Connect)obj; } else { return false; }
/* 359 */      return (this.protocolCrc == other.protocolCrc && this.protocolBuildNumber == other.protocolBuildNumber && Objects.equals(this.clientVersion, other.clientVersion) && Objects.equals(this.clientType, other.clientType) && Objects.equals(this.uuid, other.uuid) && Objects.equals(this.username, other.username) && Objects.equals(this.identityToken, other.identityToken) && Objects.equals(this.language, other.language) && Arrays.equals(this.referralData, other.referralData) && Objects.equals(this.referralSource, other.referralSource));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 364 */     int result = 1;
/* 365 */     result = 31 * result + Integer.hashCode(this.protocolCrc);
/* 366 */     result = 31 * result + Integer.hashCode(this.protocolBuildNumber);
/* 367 */     result = 31 * result + Objects.hashCode(this.clientVersion);
/* 368 */     result = 31 * result + Objects.hashCode(this.clientType);
/* 369 */     result = 31 * result + Objects.hashCode(this.uuid);
/* 370 */     result = 31 * result + Objects.hashCode(this.username);
/* 371 */     result = 31 * result + Objects.hashCode(this.identityToken);
/* 372 */     result = 31 * result + Objects.hashCode(this.language);
/* 373 */     result = 31 * result + Arrays.hashCode(this.referralData);
/* 374 */     result = 31 * result + Objects.hashCode(this.referralSource);
/* 375 */     return result;
/*     */   }
/*     */   
/*     */   public Connect() {}
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\protocol\packets\connection\Connect.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */