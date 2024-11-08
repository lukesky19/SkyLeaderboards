# SkyLeaderboards
## Description
* SkyLeaderboards is a plugin that handles parsing PlaceholderAPI placeholders on signs, for updating heads, and for updating NPC skins (Citizens).

## Features
* Parse PlaceholderAPI placeholders and update sign text.
* Parse PlaceholderAPI placeholders and update head textures.
* Parse PlaceholderAPI placeholders and update NPC skins (Citizens).

## Disclaimer
* This plugin does NOT track or store any data to act as a leaderboard.
* It relies on other plugins handling their own leaderboard.
* The plugin will simply parse placeholders provided by other plugins and displays that data.

## Dependencies
* Citizens
* PlaceholderAPI

## Commands
* /skyleaderboards reload
* /skyleaderboards update
* /slb reload
* /slb update

## Permissions
* skyleaderboards.command.skyleaderboards
* skyleaderboards.command.skyleaderboards.reload
* skyleaderboards.command.skyleaderboards.update

## FAQ
Q: What versions does this plugin support?

A: 1.21.0, 1.21.1, 1.21.2, and 1.21.3

Q: Are there any plans to support any other versions?

A: No.

Q: Does this work on Spigot and Paper?

A: Paper and forks of Paper (untested) are supported. Spigot is not supported.

Q: Is Folia supported?

A: There is no Folia support at this time. I may look into it in the future though.

Q: My heads don't have the correct skin!

A: Player Heads will only update with the proper skin if the player has been online before the last update and their PlayerProfile is cached.

## Issues or Bugs
* Create a new GitHub Issue describing your issue.
* Please post any relevant logs containing errors related to SkyLeaderboards and your configuration files.
* I will attempt to solve any issues to the best of my ability.

## Building
```./gradlew build```

## Why AGPL3?
I wanted a license that will keep my code open source. I believe in open source software and in-case this project goes unmaintained by me, I want it to live on through the work of others. And I want that work to remain open source to prevent a time when a fork can never be continued (i.e., closed-sourced and abandoned).
