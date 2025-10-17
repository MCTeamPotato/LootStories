package me.kall.lootstories.config;

import me.kall.lootstories.LootStories;
import net.minecraftforge.fml.loading.FMLLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public final class Readme {

    private static final String CONTENT_ZH = """
            ============================
            LootStories 配置说明（中文）
            ============================

            文件位置: config/lootstories/
            本模组会自动在此目录生成故事文本 (.txt) 文件与配置文件。

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
                minecraft:chests/village/village_library;The Great Adventure

            【4】故事文件命名规则
            - 文件放在 config/lootstories/ 下。
            - 文件名格式为: 标题by作者.txt
              例如:
                The Great AdventurebyAlex.txt
              若省略作者，则默认为 unknown。

            【5】注意事项
            - 修改配置后请重启游戏生效。
            - 如果删除故事文件，游戏会重新解压默认故事。

            """;

    private static final String CONTENT_EN = """
            ============================
            LootStories Configuration Guide (English)
            ============================

            Location: config/lootstories/
            This mod automatically generates story text (.txt) files and config files here.

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

            """;

    static {
        Path configDir = FMLLoader.getGamePath().resolve("config").resolve(LootStories.MOD_ID);
        Path readmeZh = configDir.resolve("README_zh.txt");
        Path readmeEn = configDir.resolve("README_en.txt");

        try {
            Files.createDirectories(configDir);
            createIfMissing(readmeZh, CONTENT_ZH);
            createIfMissing(readmeEn, CONTENT_EN);
        } catch (IOException e) {
            LootStories.LOGGER.warn("Failed to create lootstories README files", e);
        }
    }

    private static void createIfMissing(Path path, String content) throws IOException {
        if (!Files.exists(path)) {
            Files.writeString(path, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            LootStories.LOGGER.info("Generated {}", path.getFileName());
        }
    }

    public static void init() {
        // Class load trigger
    }
}
