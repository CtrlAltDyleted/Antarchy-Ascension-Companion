# Antarchy - Ascension Companion

Companion mod for the Antarchy - Ascension NeoForge 1.21.1 modpack.

This mod's purpose is to provide pack-specific:
- Compatibility fixes between mods in the pack
- Performance fixes that require Java-level intervention
- Cross-mod integrations that cannot be achieved through configuration alone
- Progression functionality tied to the pack's design
- Custom systems that are better implemented in Java than in configuration or KubeJS

## Windows first-world memory trim

On Windows clients, the companion is configured to trim Minecraft's own process working set once after the first playable world loads. This is to combat the large memory use of the modpack. The default delay is 5 seconds. The feature is configured in `config/antarchy_ascension_companion-client.toml` with `trimFirstWorldWorkingSet` and `firstWorldWorkingSetTrimDelaySeconds`. It does nothing on Linux, macOS, or dedicated servers.

## Environment

| Component   | Version  |
|-------------|----------|
| Minecraft   | 1.21.1   |
| NeoForge    | 21.1.248 |
| Java        | 21       |

## Status

This project is in active development.

## License

Copyright (c) 2026 CtrlAltDyleted. All Rights Reserved.

See [LICENSE](LICENSE) for full terms. This project does not claim ownership of any third-party Minecraft mods or their content.
