# 1MB Redstone Disabler
Made by zivthemagniv for one of my 1MB networked servers; BuildingKingdoms.com. 

This little plugin lets you quickly disable a wide variety of redstone elements on a Paper Minecraft 1.21.4+ server and the `config.yml` changes can be reloaded with `/redstonedisabler reload`

## Configuration YAML 
```yaml
# RedstoneDisabler Configuration
# Set to true to disable the redstone component

disabled-components:
  REDSTONE_WIRE: true      # Redstone dust
  REPEATER: false          # Redstone repeater
  COMPARATOR: false        # Redstone comparator
  PISTON: false            # Regular piston
  STICKY_PISTON: false     # Sticky piston
  REDSTONE_TORCH: false    # Redstone torch
  REDSTONE_LAMP: false     # Redstone lamp
  DISPENSER: false         # Dispenser
  DROPPER: false           # Dropper
  HOPPER: false            # Hopper
  OBSERVER: false          # Observer
  DAYLIGHT_DETECTOR: false # Daylight detector
  LEVER: false             # Lever
  STONE_BUTTON: false      # Stone button
  WOODEN_BUTTON: false     # Wooden button
  PRESSURE_PLATE: false    # All pressure plates
  TRIPWIRE: false          # Tripwire
  TRIPWIRE_HOOK: false     # Tripwire hook
  TARGET: false            # Target block
  SCULK_SENSOR: false      # Sculk sensor
  NOTE_BLOCK: false        # Note block
  CALIBRATED_SCULK_SENSOR: false # Calibrated sculk sensor
  SCULK_SHRIEKER: false    # Sculk shrieker
  TNT: false               # TNT
  POWERED_RAIL: false      # Powered rail
  ACTIVATOR_RAIL: false    # Activator rail
  DETECTOR_RAIL: false     # Detector rail
  DOOR: false              # All doors
  TRAPDOOR: false          # All trapdoors
  FENCE_GATE: false        # All fence gates
  BELL: false              # Bell
  CAMPFIRE: false          # Campfire (redstone extinguishing)
  LECTERN: false           # Lectern
  JUKEBOX: false           # Jukebox
  DRAGON_EGG: false        # Dragon egg
  HOPPER_MINECART: false   # Minecart with hopper
  TNT_MINECART: false      # Minecart with TNT

# Message shown when a player tries to use a disabled redstone component
disabled-message: "&cThis redstone component has been disabled by the server administrator."

# Set to true to enable debug messages
debug: false
```

## Commands
```
/redstonedisabler reload
```

## Permissions
```
redstonedisabler.reload
```
(default: op)
Example: lp group default permission set redstonedisabler.reload false
Example: lp group owner permission set redstonedisabler.reload true

## Installation
- Put in `~/plugins/` folder, and start the server
- Go through config.yml and set your preferences
- `/redstonedisabler reload`
- Test it .. 

## Idea / Credits
I guess me, though nothing new here.. just needed a solution to a problem on a server and couldn't find a working 1.21.4+ release. Code created by zivthemagniv (spigotmc username)

