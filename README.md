# Sun Drain

A Minecraft Fabric mod that adds a sun draining mechanic. The longer you stay exposed to direct sunlight, the more the sun "drains" you, causing debilitating effects.

## Features

- **Sun Drain Bar**: A visual HUD bar (client-side) showing your current sun exposure level
- **Progressive Effects**: As sun exposure increases, you receive increasingly severe debuffs
- **Recovery in Shade**: Move to shade, go underground, or wait for nighttime to recover
- **Weather Awareness**: Rain and clouds provide protection from the sun

## Mechanics

### Sun Exposure Levels

| Level | Threshold | Effects |
|-------|-----------|---------|
| Safe | 0-74% | No effects |
| Warning | 75-99% | Weakness I |
| Critical | 100% | Weakness I, Slowness I, Hunger II |

### What Triggers Sun Drain

- Being outdoors during daytime
- Clear sky above you (no blocks blocking the sun)
- High sky light level (≥12)

### What Provides Protection

- Being underground or indoors
- Standing under any solid block
- Nighttime (sun drain recovers at night)
- Rainy weather
- Creative/Spectator mode (unaffected)

## Requirements

- Minecraft 1.21.1
- Fabric Loader 0.16.0+
- Fabric API

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.1
2. Install [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
3. Download `sun-drain-1.0.0.jar` from the releases
4. Place the JAR file in your `mods` folder
5. Launch Minecraft with the Fabric profile

## Building from Source

```bash
git clone https://github.com/Simplifine-gamedev/sun-drain.git
cd sun-drain
./gradlew build
```

The compiled JAR will be in `build/libs/`.

## Tips

- Plan your outdoor activities carefully - carry building materials to create temporary shade
- Caves and mines are safe havens during the day
- Use the HUD bar to monitor your exposure and retreat before reaching critical levels
- Consider building covered walkways between important locations

## License

This mod is released under the MIT License.

## Author

Created by ali77sina
