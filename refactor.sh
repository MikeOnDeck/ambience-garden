#!/bin/bash
set -euo pipefail

# Comprehensive refactoring script to merge charmony modules into standalone ambience_garden

echo "=== Step 1: Copy ambient_sounds code ==="
cp -r mods/ambient_sounds/src/main/java/charmony/ambient_sounds/* src/main/java/com/mikeondeck/ambiencegarden/

echo "=== Step 2: Copy needed API classes ==="
mkdir -p src/main/java/com/mikeondeck/ambiencegarden/api/core
cp mods/api/src/main/java/charmony/api/core/ModDefinition.java src/main/java/com/mikeondeck/ambiencegarden/api/core/
cp mods/api/src/main/java/charmony/api/core/FeatureDefinition.java src/main/java/com/mikeondeck/ambiencegarden/api/core/
cp mods/api/src/main/java/charmony/api/core/Side.java src/main/java/com/mikeondeck/ambiencegarden/api/core/
cp mods/api/src/main/java/charmony/api/core/Configurable.java src/main/java/com/mikeondeck/ambiencegarden/api/core/

echo "=== Step 3: Copy needed core classes ==="
mkdir -p src/main/java/com/mikeondeck/ambiencegarden/core/{base,helpers,common,events,enums}

# Base classes
cp mods/core/src/main/java/charmony/core/base/Mod.java src/main/java/com/mikeondeck/ambiencegarden/core/base/
cp mods/core/src/main/java/charmony/core/base/Setup.java src/main/java/com/mikeondeck/ambiencegarden/core/base/
cp mods/core/src/main/java/charmony/core/base/SidedFeature.java src/main/java/com/mikeondeck/ambiencegarden/core/base/
cp mods/core/src/main/java/charmony/core/base/Feature.java src/main/java/com/mikeondeck/ambiencegarden/core/base/ 2>/dev/null || true
cp mods/core/src/main/java/charmony/core/base/Log.java src/main/java/com/mikeondeck/ambiencegarden/core/base/
cp mods/core/src/main/java/charmony/core/base/Config.java src/main/java/com/mikeondeck/ambiencegarden/core/base/

# Helpers
cp mods/core/src/main/java/charmony/core/helpers/WorldHelper.java src/main/java/com/mikeondeck/ambiencegarden/core/helpers/ 2>/dev/null || true

# Core
cp mods/core/src/main/java/charmony/core/Charmony.java src/main/java/com/mikeondeck/ambiencegarden/core/ 2>/dev/null || true

# Client
mkdir -p src/main/java/com/mikeondeck/ambiencegarden/core/client
cp mods/core/src/main/java/charmony/core/client/ClientInitializer.java src/main/java/com/mikeondeck/ambiencegarden/core/client/ 2>/dev/null || true

# Common registry
cp mods/core/src/main/java/charmony/core/common/CommonRegistry.java src/main/java/com/mikeondeck/ambiencegarden/core/common/ 2>/dev/null || true

# Events
cp mods/core/src/main/java/charmony/core/events/*.java src/main/java/com/mikeondeck/ambiencegarden/core/events/ 2>/dev/null || true

# Enums
cp mods/core/src/main/java/charmony/core/enums/*.java src/main/java/com/mikeondeck/ambiencegarden/core/enums/ 2>/dev/null || true

echo "=== Step 4: Rename packages in all Java files ==="
find src/main/java -name "*.java" -type f -exec sed -i 's/package charmony\.ambient_sounds/package com.mikeondeck.ambiencegarden/g' {} +
find src/main/java -name "*.java" -type f -exec sed -i 's/package charmony\.api/package com.mikeondeck.ambiencegarden.api/g' {} +
find src/main/java -name "*.java" -type f -exec sed -i 's/package charmony\.core/package com.mikeondeck.ambiencegarden.core/g' {} +

echo "=== Step 5: Update imports in all Java files ==="
find src/main/java -name "*.java" -type f -exec sed -i 's/import charmony\.ambient_sounds/import com.mikeondeck.ambiencegarden/g' {} +
find src/main/java -name "*.java" -type f -exec sed -i 's/import charmony\.api/import com.mikeondeck.ambiencegarden.api/g' {} +
find src/main/java -name "*.java" -type f -exec sed -i 's/import charmony\.core/import com.mikeondeck.ambiencegarden.core/g' {} +

echo "=== Step 6: Update class references ==="
find src/main/java -name "*.java" -type f -exec sed -i 's/charmony\.core\.client\.ClientInitializer/com.mikeondeck.ambiencegarden.core.client.ClientInitializer/g' {} +

echo "=== Done! Refactored code is in src/main/java ==="
