# Antarchy - Ascension Companion Changelog

## Alpha 0.1.2

### 2026-09-17

#### Changed

- Renamed the Companion runtime mod ID from `antarchy_ascension_companion` to `antarchyascension`

- Renamed the displayed mod name from `Antarchy - Ascension Companion` to `Antarchy - Ascension`

- Migrated Companion-owned items, blocks, recipes, loot tables, tags, models, textures, Curios data, JEI integration, and other resources to the `antarchyascension` namespace

- Updated NeoForge from 21.1.248 to 21.1.250

## Alpha 0.1.1

### 2026-09-14

#### Changed

- Engineer's Goggles now forward all nonzero head equipment modifiers in their custom Curios slot
- Jetpacks now forward all nonzero chest equipment modifiers in their custom Curios slot
- Added dedicated Curios modifier headings for Engineer's Goggles and Jetpacks

#### Fixed

- Added the missing Time in a Bottle Curios slot display name

#### Removed

- Removed the custom AE2 Import Export Card Curios tank bridge because the mod now provides native Curios support

## Alpha 0.1.0

### 2026-08-30

#### Added

- Added JEI blacklist removal support so blacklisted ingredients are fully removed from JEI

#### Fixed

- Fixed Applied Pneumatics Amadron Process Stations not ticking correctly with TIAB Entity Fix installed
- Fixed Biolith interfering with unsupported MultiNoise dimensions
- Fixed Deimos generating recipes with invalid output stack sizes
- Fixed Integrated Mekanism trying to use unsupported factory tiers added by Evolved Mekanism
- Fixed Lucky Block NeoForge client initialization
- Fixed NeoForge SimpleTier crashes caused by null repair ingredients
- Fixed Quark Stoneling, Toretoise, and Foxhound mob categories
- Fixed Tectonic Fabric-only pack metadata being read on NeoForge
- Fixed Actually Additions Engineer's Goggles using incorrect Curios item IDs
- Fixed final Alpha issues found during regression testing

#### Changed

- Updated JEI API support to 19.51.0.417
- Normalized all Companion Mixin and injected member names to use the full `antarchyascensioncompanion$` prefix
- Removed obsolete warning-only compatibility hooks
- Removed the experimental Windows working-set trim and its client config
- Removed the unused JNA dependency
- Cleaned stale Companion code, comments, and resource files
- Renamed the release from development version 0.1.1 to Alpha 0.1.0

### 2026-08-20

#### Added

- Added Applied Flux Forge Energy support in JEI

#### Fixed

- (kinda) Fixed Better Combat attack animations while morphed into non-player mobs
- Fixed Lucky Block duplicating blocks when shift-clicking luck modification recipes
- Fixed Smithing Template Viewer attempting to process invalid smithing recipes

### 2026-08-19

#### Added

- Added dedicated Curios slots for jetpacks, Filling/Fueling Tanks, Engineer's Goggles, and wireless terminals
- Added armor attribute support for equipment moved out of vanilla armor slots
- Added a Windows working-set trim after first world load with a client config for enabling the trim and changing its delay

### 2026-08-18

#### Fixed

- Fixed Personality crawling conflicting with Reliquified Artifacts' Helium Flamingo forced swimming pose

### 2026-08-17

#### Added

- Added Dense Uranium Nugget, Raw Dense Uranium, Raw Dense Uranium Block, Dense Uranium Ingot, and Dense Uranium Block
- Added Dense Uranium crafting, smelting, blasting, tags, loot tables, mining requirements, models, textures, and localization
- Added Raw Uranium Scrap processing into Dense Uranium progression
- Added repository audit, review, and changelog tools

## Alpha Pre-Release 0.1.0

#### Added

- Added official Antarchy - Ascension Companion branding and mod icon

#### Fixed

- Fixed major server lag caused by Applied Construction Sticks and AE2 integration because Applied Construction Sticks was performing AE2 linking checks when a valid linked stick was not being held
