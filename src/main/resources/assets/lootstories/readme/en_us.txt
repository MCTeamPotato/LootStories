============================
LootStories Configuration Guide (English)
============================

[1] BookLootPossibility(%)
- Controls the chance (0–100) that a loot chest will contain a story book.

[2] StoryWeight
- Defines each story’s weight in the global random pool.
  Example:
    The Great Adventure;15
    Desert Secrets;5

[3] ChestBindStory
- Binds specific loot tables to specific stories.
  Example:
    minecraft:chests/desert_pyramid;Desert Secrets
    minecraft:chests/desert_pyramid;The Great Adventure
    minecraft:chests/village/village_library;The Great Adventure

[4] Story File Naming
- Files are stored under config/lootstories/
- Naming format: TitlebyAuthor.txt
  Example:
    The Great AdventurebyAlex.txt
  If author is omitted, defaults to "unknown".

[5] Notes
- Restart the game after editing config.
- If you delete story files, default stories will be extracted again.
- You can also extract this mod's jar to get the default stories files.