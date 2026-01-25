/*     */ package com.hypixel.hytale.server.core.ui.browser;
/*     */ 
/*     */ import java.nio.file.Path;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.function.Predicate;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
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
/*     */ public class Builder
/*     */ {
/*  47 */   private String listElementId = "#FileList";
/*  48 */   private String rootSelectorId = "#RootSelector";
/*  49 */   private String searchInputId = "#SearchInput";
/*  50 */   private String currentPathId = null;
/*  51 */   private List<FileBrowserConfig.RootEntry> roots = List.of();
/*  52 */   private Set<String> allowedExtensions = Set.of();
/*     */   private boolean enableRootSelector = true;
/*     */   private boolean enableSearch = true;
/*     */   private boolean enableDirectoryNav = true;
/*     */   private boolean enableMultiSelect = false;
/*  57 */   private int maxResults = 50;
/*  58 */   private FileListProvider customProvider = null;
/*     */   private boolean assetPackMode = false;
/*  60 */   private String assetPackSubPath = null;
/*  61 */   private Predicate<Path> terminalDirectoryPredicate = null;
/*     */   
/*     */   public Builder listElementId(@Nonnull String listElementId) {
/*  64 */     this.listElementId = listElementId;
/*  65 */     return this;
/*     */   }
/*     */   
/*     */   public Builder rootSelectorId(@Nullable String rootSelectorId) {
/*  69 */     this.rootSelectorId = rootSelectorId;
/*  70 */     return this;
/*     */   }
/*     */   
/*     */   public Builder searchInputId(@Nullable String searchInputId) {
/*  74 */     this.searchInputId = searchInputId;
/*  75 */     return this;
/*     */   }
/*     */   
/*     */   public Builder currentPathId(@Nullable String currentPathId) {
/*  79 */     this.currentPathId = currentPathId;
/*  80 */     return this;
/*     */   }
/*     */   
/*     */   public Builder roots(@Nonnull List<FileBrowserConfig.RootEntry> roots) {
/*  84 */     this.roots = roots;
/*  85 */     return this;
/*     */   }
/*     */   
/*     */   public Builder allowedExtensions(@Nonnull String... extensions) {
/*  89 */     this.allowedExtensions = Set.of(extensions);
/*  90 */     return this;
/*     */   }
/*     */   
/*     */   public Builder allowedExtensions(@Nonnull Set<String> extensions) {
/*  94 */     this.allowedExtensions = extensions;
/*  95 */     return this;
/*     */   }
/*     */   
/*     */   public Builder enableRootSelector(boolean enable) {
/*  99 */     this.enableRootSelector = enable;
/* 100 */     return this;
/*     */   }
/*     */   
/*     */   public Builder enableSearch(boolean enable) {
/* 104 */     this.enableSearch = enable;
/* 105 */     return this;
/*     */   }
/*     */   
/*     */   public Builder enableDirectoryNav(boolean enable) {
/* 109 */     this.enableDirectoryNav = enable;
/* 110 */     return this;
/*     */   }
/*     */   
/*     */   public Builder enableMultiSelect(boolean enable) {
/* 114 */     this.enableMultiSelect = enable;
/* 115 */     return this;
/*     */   }
/*     */   
/*     */   public Builder maxResults(int maxResults) {
/* 119 */     this.maxResults = maxResults;
/* 120 */     return this;
/*     */   }
/*     */   
/*     */   public Builder customProvider(@Nullable FileListProvider provider) {
/* 124 */     this.customProvider = provider;
/* 125 */     return this;
/*     */   }
/*     */   
/*     */   public Builder assetPackMode(boolean enable, @Nullable String subPath) {
/* 129 */     if (enable && subPath == null) {
/* 130 */       throw new IllegalArgumentException("assetPackSubPath cannot be null when assetPackMode is enabled");
/*     */     }
/* 132 */     this.assetPackMode = enable;
/* 133 */     this.assetPackSubPath = subPath;
/* 134 */     return this;
/*     */   }
/*     */   
/*     */   public Builder terminalDirectoryPredicate(@Nullable Predicate<Path> predicate) {
/* 138 */     this.terminalDirectoryPredicate = predicate;
/* 139 */     return this;
/*     */   }
/*     */   
/*     */   public FileBrowserConfig build() {
/* 143 */     return new FileBrowserConfig(this.listElementId, this.rootSelectorId, this.searchInputId, this.currentPathId, this.roots, this.allowedExtensions, this.enableRootSelector, this.enableSearch, this.enableDirectoryNav, this.enableMultiSelect, this.maxResults, this.customProvider, this.assetPackMode, this.assetPackSubPath, this.terminalDirectoryPredicate);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\ui\browser\FileBrowserConfig$Builder.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */