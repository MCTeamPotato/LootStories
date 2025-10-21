============================
LootStories 配置说明（中文）
============================

【1】BookLootPossibility(%)
- 控制每个战利品箱子生成故事书的几率 (0~100)。

【2】StoryWeight
- 决定每个故事在全局随机中的权重。
  格式示例:
    The Great Adventure;15
    Desert Secrets;5

【3】ChestBindStory
- 指定特定 lootTable 只能生成特定的故事。
  格式示例:
    minecraft:chests/desert_pyramid;Desert Secrets
    minecraft:chests/desert_pyramid;The Great Adventure
    minecraft:chests/village/village_library;The Great Adventure

【4】故事文件命名规则
- 文件放在 config/lootstories/语种 下。
- 文件内部第一行的格式为: 标题by作者
  例如:
    The Great Adventure by Alex
  若省略作者，则默认为 unknown。

【5】注意事项
- 对于同一篇故事的不同语种，请保持txt的文件名相同。
- 修改配置后请重启游戏生效。
- 如果删除故事文件，游戏会重新解压默认故事。
- 你也可以自己解压本模组的jar来获取到默认的故事文件。