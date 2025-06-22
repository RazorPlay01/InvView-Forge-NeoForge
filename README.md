# InvView Neoforge
A server-side mod for Minecraft 1.21.1 that allows administrators to inspect and modify player inventories, including vanilla inventories, Ender Chests, and Curios slots (functional and cosmetic). Supports LuckPerms for permission management and works with offline players.

[![Versions](http://cf.way2muchnoise.eu/versions/997252.svg)](https://www.curseforge.com/minecraft/mc-mods/inv-view-forge)
[![Downloads](http://cf.way2muchnoise.eu/full_997252_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/inv-view-forge)
[![Downloads Modrinth](https://img.shields.io/modrinth/dt/inv-view-forge?color=00AF5C&label=downloads&logo=modrinth)](https://modrinth.com/mod/inv-view-forge)

## Features
- Inspect and edit player inventories with `/view inv <player>`.
- Access Ender Chests with `/view echest <player>`.
- View Curios inventories with `/view curios <player>` and `/view curios_cosmetic <player>` (requires Curios).
- Fine-grained permissions with LuckPerms (e.g., `inv_view.inv`, `inv_view.curios`).
- Inventory locking to prevent conflicts.
- Offline player support with persistent data saving.

## Permissions (with LuckPerms)
- `inv_view.inv`: Access player inventories.
- `inv_view.echest`: Access Ender Chests.
- `inv_view.curios`: Access Curios functional slots.
- `inv_view.curios_cosmetic`: Access Curios cosmetic slots.

## Installation
1. Install NeoForge 1.21.1 on your server.
2. (Optional) Install Curios and/or LuckPerms for additional features.
3. Place the mod JAR in the `mods` folder.
4. Use the `/view` commands with appropriate permissions.

## Examples
<a href="https://youtu.be/I_Lo3sls0f0">
<img src="https://imgur.com/Lon9xgK.jpg">
</a>
<a href="https://youtu.be/SIJjHWIhZwg">
<img src="https://imgur.com/wtz3bwG.jpg">
</a>
<a href="https://youtu.be/rwDyySPDZQY">
<img src="https://imgur.com/uNPGy09.jpg">
</a>

## Notes
- Requires NeoForge 21.1.0 or higher.
- Curios and LuckPerms are optional dependencies.
- Report issues or suggest features on [GitHub link](https://github.com/RazorPlay01/InvView_Forge/issues).