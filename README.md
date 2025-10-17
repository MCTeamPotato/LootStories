### LootStories

**LootStories** adds a touch of mystery and storytelling to Minecraft by placing *written books* inside loot chests.

Each book contains a short story, which can be:

* Preloaded from the mod’s default stories
* Generated or inspired by **DeepSeek**/**ChatGPT**
* Added by you as `.txt` files in the `config/lootstories/stories/` folder

**File naming format:**

```
StoryTitlebyAuthor.txt
```

For example: `TheLostMinebyAlex.txt`.

#### Features

* **Configurable Loot Chance:** Adjust the probability of books appearing in chests via **Jsonate** (optional).
* **Story Weights:** Control how often each story appears.
* **Chest-Specific Stories:** Bind different stories to different loot tables.
* **Automatic Resource Extraction:** README and default stories are automatically extracted to your config folder.

#### Dependencies

* **Jsonate** (optional) — for customizing loot probabilities/weights/chestBinds/...
* **Modern UI** — required. Or your texts will be pieces of shit.