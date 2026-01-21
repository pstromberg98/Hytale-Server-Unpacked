package com.google.protobuf;

import java.util.List;

public interface ApiOrBuilder extends MessageOrBuilder {
  String getName();
  
  ByteString getNameBytes();
  
  List<Method> getMethodsList();
  
  Method getMethods(int paramInt);
  
  int getMethodsCount();
  
  List<? extends MethodOrBuilder> getMethodsOrBuilderList();
  
  MethodOrBuilder getMethodsOrBuilder(int paramInt);
  
  List<Option> getOptionsList();
  
  Option getOptions(int paramInt);
  
  int getOptionsCount();
  
  List<? extends OptionOrBuilder> getOptionsOrBuilderList();
  
  OptionOrBuilder getOptionsOrBuilder(int paramInt);
  
  String getVersion();
  
  ByteString getVersionBytes();
  
  boolean hasSourceContext();
  
  SourceContext getSourceContext();
  
  SourceContextOrBuilder getSourceContextOrBuilder();
  
  List<Mixin> getMixinsList();
  
  Mixin getMixins(int paramInt);
  
  int getMixinsCount();
  
  List<? extends MixinOrBuilder> getMixinsOrBuilderList();
  
  MixinOrBuilder getMixinsOrBuilder(int paramInt);
  
  int getSyntaxValue();
  
  Syntax getSyntax();
  
  String getEdition();
  
  ByteString getEditionBytes();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\protobuf\ApiOrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */