/*     */ package com.hypixel.hytale.server.core.ui.browser;
/*     */ public final class FileBrowserConfig extends Record { @Nonnull
/*     */   private final String listElementId;
/*     */   @Nullable
/*     */   private final String rootSelectorId;
/*     */   @Nullable
/*     */   private final String searchInputId;
/*     */   @Nullable
/*     */   private final String currentPathId;
/*     */   @Nonnull
/*     */   private final List<RootEntry> roots;
/*     */   @Nonnull
/*     */   private final Set<String> allowedExtensions;
/*     */   private final boolean enableRootSelector;
/*     */   
/*  16 */   public FileBrowserConfig(@Nonnull String listElementId, @Nullable String rootSelectorId, @Nullable String searchInputId, @Nullable String currentPathId, @Nonnull List<RootEntry> roots, @Nonnull Set<String> allowedExtensions, boolean enableRootSelector, boolean enableSearch, boolean enableDirectoryNav, boolean enableMultiSelect, int maxResults, @Nullable FileListProvider customProvider, boolean assetPackMode, @Nullable String assetPackSubPath, @Nullable Predicate<Path> terminalDirectoryPredicate) { this.listElementId = listElementId; this.rootSelectorId = rootSelectorId; this.searchInputId = searchInputId; this.currentPathId = currentPathId; this.roots = roots; this.allowedExtensions = allowedExtensions; this.enableRootSelector = enableRootSelector; this.enableSearch = enableSearch; this.enableDirectoryNav = enableDirectoryNav; this.enableMultiSelect = enableMultiSelect; this.maxResults = maxResults; this.customProvider = customProvider; this.assetPackMode = assetPackMode; this.assetPackSubPath = assetPackSubPath; this.terminalDirectoryPredicate = terminalDirectoryPredicate; } private final boolean enableSearch; private final boolean enableDirectoryNav; private final boolean enableMultiSelect; private final int maxResults; @Nullable private final FileListProvider customProvider; private final boolean assetPackMode; @Nullable private final String assetPackSubPath; @Nullable private final Predicate<Path> terminalDirectoryPredicate; public final String toString() { // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/server/core/ui/browser/FileBrowserConfig;)Ljava/lang/String;
/*     */     //   6: areturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #16	-> 0
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	7	0	this	Lcom/hypixel/hytale/server/core/ui/browser/FileBrowserConfig; } public final int hashCode() { // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/server/core/ui/browser/FileBrowserConfig;)I
/*     */     //   6: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #16	-> 0
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	7	0	this	Lcom/hypixel/hytale/server/core/ui/browser/FileBrowserConfig; } public final boolean equals(Object o) { // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: aload_1
/*     */     //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/server/core/ui/browser/FileBrowserConfig;Ljava/lang/Object;)Z
/*     */     //   7: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #16	-> 0
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	8	0	this	Lcom/hypixel/hytale/server/core/ui/browser/FileBrowserConfig;
/*  16 */     //   0	8	1	o	Ljava/lang/Object; } @Nonnull public String listElementId() { return this.listElementId; } @Nullable public String rootSelectorId() { return this.rootSelectorId; } @Nullable public String searchInputId() { return this.searchInputId; } @Nullable public String currentPathId() { return this.currentPathId; } @Nonnull public List<RootEntry> roots() { return this.roots; } @Nonnull public Set<String> allowedExtensions() { return this.allowedExtensions; } public boolean enableRootSelector() { return this.enableRootSelector; } public boolean enableSearch() { return this.enableSearch; } public boolean enableDirectoryNav() { return this.enableDirectoryNav; } public boolean enableMultiSelect() { return this.enableMultiSelect; } public int maxResults() { return this.maxResults; } @Nullable public FileListProvider customProvider() { return this.customProvider; } public boolean assetPackMode() { return this.assetPackMode; } @Nullable public String assetPackSubPath() { return this.assetPackSubPath; } @Nullable public Predicate<Path> terminalDirectoryPredicate() { return this.terminalDirectoryPredicate; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class RootEntry
/*     */     extends Record
/*     */   {
/*     */     @Nonnull
/*     */     private final LocalizableString displayName;
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final Path path;
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Path path()
/*     */     {
/*  36 */       return this.path; } @Nonnull public LocalizableString displayName() { return this.displayName; } public final boolean equals(Object o) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: aload_1
/*     */       //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/server/core/ui/browser/FileBrowserConfig$RootEntry;Ljava/lang/Object;)Z
/*     */       //   7: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #36	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	8	0	this	Lcom/hypixel/hytale/server/core/ui/browser/FileBrowserConfig$RootEntry;
/*     */       //   0	8	1	o	Ljava/lang/Object; } public final int hashCode() { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/server/core/ui/browser/FileBrowserConfig$RootEntry;)I
/*     */       //   6: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #36	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/ui/browser/FileBrowserConfig$RootEntry; } public final String toString() { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/server/core/ui/browser/FileBrowserConfig$RootEntry;)Ljava/lang/String;
/*     */       //   6: areturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #36	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*  36 */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/ui/browser/FileBrowserConfig$RootEntry; } public RootEntry(@Nonnull LocalizableString displayName, @Nonnull Path path) { this.displayName = displayName; this.path = path; }
/*     */      public RootEntry(@Nonnull String displayName, @Nonnull Path path) {
/*  38 */       this(LocalizableString.fromString(displayName), path);
/*     */     }
/*     */   }
/*     */   
/*     */   public static Builder builder() {
/*  43 */     return new Builder();
/*     */   }
/*     */   
/*     */   public static class Builder {
/*  47 */     private String listElementId = "#FileList";
/*  48 */     private String rootSelectorId = "#RootSelector";
/*  49 */     private String searchInputId = "#SearchInput";
/*  50 */     private String currentPathId = null;
/*  51 */     private List<FileBrowserConfig.RootEntry> roots = List.of();
/*  52 */     private Set<String> allowedExtensions = Set.of();
/*     */     private boolean enableRootSelector = true;
/*     */     private boolean enableSearch = true;
/*     */     private boolean enableDirectoryNav = true;
/*     */     private boolean enableMultiSelect = false;
/*  57 */     private int maxResults = 50;
/*  58 */     private FileListProvider customProvider = null;
/*     */     private boolean assetPackMode = false;
/*  60 */     private String assetPackSubPath = null;
/*  61 */     private Predicate<Path> terminalDirectoryPredicate = null;
/*     */     
/*     */     public Builder listElementId(@Nonnull String listElementId) {
/*  64 */       this.listElementId = listElementId;
/*  65 */       return this;
/*     */     }
/*     */     
/*     */     public Builder rootSelectorId(@Nullable String rootSelectorId) {
/*  69 */       this.rootSelectorId = rootSelectorId;
/*  70 */       return this;
/*     */     }
/*     */     
/*     */     public Builder searchInputId(@Nullable String searchInputId) {
/*  74 */       this.searchInputId = searchInputId;
/*  75 */       return this;
/*     */     }
/*     */     
/*     */     public Builder currentPathId(@Nullable String currentPathId) {
/*  79 */       this.currentPathId = currentPathId;
/*  80 */       return this;
/*     */     }
/*     */     
/*     */     public Builder roots(@Nonnull List<FileBrowserConfig.RootEntry> roots) {
/*  84 */       this.roots = roots;
/*  85 */       return this;
/*     */     }
/*     */     
/*     */     public Builder allowedExtensions(@Nonnull String... extensions) {
/*  89 */       this.allowedExtensions = Set.of(extensions);
/*  90 */       return this;
/*     */     }
/*     */     
/*     */     public Builder allowedExtensions(@Nonnull Set<String> extensions) {
/*  94 */       this.allowedExtensions = extensions;
/*  95 */       return this;
/*     */     }
/*     */     
/*     */     public Builder enableRootSelector(boolean enable) {
/*  99 */       this.enableRootSelector = enable;
/* 100 */       return this;
/*     */     }
/*     */     
/*     */     public Builder enableSearch(boolean enable) {
/* 104 */       this.enableSearch = enable;
/* 105 */       return this;
/*     */     }
/*     */     
/*     */     public Builder enableDirectoryNav(boolean enable) {
/* 109 */       this.enableDirectoryNav = enable;
/* 110 */       return this;
/*     */     }
/*     */     
/*     */     public Builder enableMultiSelect(boolean enable) {
/* 114 */       this.enableMultiSelect = enable;
/* 115 */       return this;
/*     */     }
/*     */     
/*     */     public Builder maxResults(int maxResults) {
/* 119 */       this.maxResults = maxResults;
/* 120 */       return this;
/*     */     }
/*     */     
/*     */     public Builder customProvider(@Nullable FileListProvider provider) {
/* 124 */       this.customProvider = provider;
/* 125 */       return this;
/*     */     }
/*     */     
/*     */     public Builder assetPackMode(boolean enable, @Nullable String subPath) {
/* 129 */       if (enable && subPath == null) {
/* 130 */         throw new IllegalArgumentException("assetPackSubPath cannot be null when assetPackMode is enabled");
/*     */       }
/* 132 */       this.assetPackMode = enable;
/* 133 */       this.assetPackSubPath = subPath;
/* 134 */       return this;
/*     */     }
/*     */     
/*     */     public Builder terminalDirectoryPredicate(@Nullable Predicate<Path> predicate) {
/* 138 */       this.terminalDirectoryPredicate = predicate;
/* 139 */       return this;
/*     */     }
/*     */     
/*     */     public FileBrowserConfig build() {
/* 143 */       return new FileBrowserConfig(this.listElementId, this.rootSelectorId, this.searchInputId, this.currentPathId, this.roots, this.allowedExtensions, this.enableRootSelector, this.enableSearch, this.enableDirectoryNav, this.enableMultiSelect, this.maxResults, this.customProvider, this.assetPackMode, this.assetPackSubPath, this.terminalDirectoryPredicate);
/*     */     }
/*     */   } }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\ui\browser\FileBrowserConfig.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */