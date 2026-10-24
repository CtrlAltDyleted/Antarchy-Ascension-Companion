# Antarchy - Ascension Companion Changelog

All notable changes are documented here. The format follows [Keep a Changelog 1.1.0](https://keepachangelog.com/en/1.1.0/); new versions use [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html). Historical release names are retained as published.

Ongoing **0.3.0-alpha** development is listed under Unreleased, not as a published release. Alpha world compatibility is not guaranteed.

## [Unreleased]

### Added

- Progression items including Nether Core, Molten Heart, Industrial Core, Keystone of Industry, sigils, keys, silica materials, Basic Circuit Press, and Calculation Microchip.
- Personal progression checks for rituals and Seal of the Underworld use.

### Changed

- **Progression change:** Progression credit and ritual access now follow the pack's staged unlock requirements.
- Unfinished assembly items use grayscale versions of their finished-item appearance.
- Pigmee crafting screens use clearer station titles and labels while preserving full names in tooltips.
- Moved pack-specific recipe overrides into the modpack; use the matching pack version for those recipes.

### Removed

- Time in a Bottle Entity Fix compatibility hooks after the pack removed that mod.
- The previous Better Combat animation workaround for morphed players.

### Fixed

- Jetpack rendering in dedicated Curios slots and flight-control tooltip key names.
- Ritual progression credit and advancement unlocking.
- Visual Workbench crafting displays and modded screen rendering in What Are They Up To.
- Curios Equipment now shift clicks into the correct slot when curios menu is open.

## 0.2.0-alpha - 2026-09-23

### Added

- Added JEI recognition for the Ars Technica Arcane Wrench variant of Create's wrench.

### Changed

- **Compatibility break:** Changed the runtime mod ID and content namespace to `antarchyascension`; older worlds and references to the previous namespace are not migrated.
- Renamed the displayed mod name from `Antarchy - Ascension Companion` to `Antarchy - Ascension`.
- Updated Companion content and integrations to use the new content namespace.
- Updated NeoForge from 21.1.248 to 21.1.250.

### Removed

- Removed legacy registry aliases for the old Companion namespace as part of the alpha clean break.
- Removed the disabled Not Enough Patterns Import Card from compatible provider upgrades and its upgrade slot.

### Fixed

- Fixed End Remastered ancient portal frames showing eyes in empty frames and accepting duplicate eyes.
- Fixed AE2 Helpers automatic recipe item insertion continuing after a crafting request is cancelled.
- Moved the AE2 Helpers Pattern Provider upgrade panel to a separate row to prevent overlapping slots.
- Restored AE2 machine ticking with Time in a Bottle Entity Fix.

## Alpha 0.1.1 - 2026-09-14

### Added

- Added dedicated Curios modifier headings for Engineer's Goggles and Jetpacks.
- Added the missing Time in a Bottle Curios slot display name.

### Changed

- Engineer's Goggles now forward all nonzero head equipment modifiers in their custom Curios slot.
- Jetpacks now forward all nonzero chest equipment modifiers in their custom Curios slot.

### Removed

- Removed the custom AE2 Import Export Card Curios tank bridge because the mod now provides native Curios support.

## Alpha 0.1.0 - 2026-08-30

Earlier dated development notes are retained with their original dates; they are not separate releases.

### Added

- Added JEI blacklist removal support so blacklisted ingredients are fully removed from JEI.
- Added Applied Flux Forge Energy support in JEI. (Development note: 2026-08-20.)
- Added dedicated Curios slots for jetpacks, Filling/Fueling Tanks, Engineer's Goggles, and wireless terminals. (Development note: 2026-08-19.)
- Added armor attribute support for equipment moved out of vanilla armor slots. (Development note: 2026-08-19.)
- Added a Windows working-set trim after first world load with a client config for enabling the trim and changing its delay. (Development note: 2026-08-19.)
- Added Dense Uranium Nugget, Raw Dense Uranium, Raw Dense Uranium Block, Dense Uranium Ingot, and Dense Uranium Block. (Development note: 2026-08-17.)
- Added Dense Uranium crafting, smelting, blasting, tags, loot tables, mining requirements, models, textures, and localization. (Development note: 2026-08-17.)
- Added Raw Uranium Scrap processing into Dense Uranium progression. (Development note: 2026-08-17.)
- Added repository audit, review, and changelog tools. (Development note: 2026-08-17.)

### Changed

- Updated JEI API support to 19.51.0.417.
- Standardized internal compatibility naming to make future fixes easier to maintain.
- Cleaned stale Companion code, comments, and resource files.
- Renamed the release from development version 0.1.1 to Alpha 0.1.0.

### Removed

- Removed obsolete warning-only compatibility hooks.
- Removed the experimental Windows working-set trim and its client config.
- Removed the unused JNA dependency.

### Fixed

- Fixed Applied Pneumatics Amadron Process Stations not ticking correctly with TIAB Entity Fix installed.
- Fixed Biolith interfering with unsupported MultiNoise dimensions.
- Fixed Deimos generating recipes with invalid output stack sizes.
- Fixed Integrated Mekanism trying to use unsupported factory tiers added by Evolved Mekanism.
- Fixed Lucky Block NeoForge client initialization.
- Fixed NeoForge SimpleTier crashes caused by null repair ingredients.
- Fixed Quark Stoneling, Toretoise, and Foxhound mob categories.
- Fixed Tectonic Fabric-only pack metadata being read on NeoForge.
- Fixed Actually Additions Engineer's Goggles using incorrect Curios item IDs.
- Fixed final Alpha issues found during regression testing.
- (kinda) Fixed Better Combat attack animations while morphed into non-player mobs. (Development note: 2026-08-20.)
- Fixed Lucky Block duplicating blocks when shift-clicking luck modification recipes. (Development note: 2026-08-20.)
- Fixed Smithing Template Viewer attempting to process invalid smithing recipes. (Development note: 2026-08-20.)
- Fixed Personality crawling conflicting with Reliquified Artifacts' Helium Flamingo forced swimming pose. (Development note: 2026-08-18.)

## Alpha Pre-Release 0.1.0

Release date was not recorded in the original changelog.

### Added

- Added official Antarchy - Ascension Companion branding and mod icon.

### Fixed

- Fixed major server lag caused by Applied Construction Sticks and AE2 integration because Applied Construction Sticks was performing AE2 linking checks when a valid linked stick was not being held.


Comparison links use the recorded post-0.2.0 changelog snapshot because no 0.2.0 release tag is available. They cover committed changes; Unreleased also includes pending working-tree changes.

[Unreleased]: https://github.com/CtrlAltDyleted/Antarchy-Ascension-Companion/compare/6fe7a40f8852cb6130031285af0144ac6a1e2d1b...HEAD
