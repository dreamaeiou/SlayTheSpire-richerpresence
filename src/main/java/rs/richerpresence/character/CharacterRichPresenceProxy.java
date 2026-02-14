package rs.richerpresence.character;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rs.richerpresence.utils.RPUtils;

/**
 * 角色富状态显示代理类
 * 负责处理Slay the Spire游戏中各种状态的本地化显示文本生成
 * 包括角色名称、概览状态、战斗状态、事件状态等
 */
public class CharacterRichPresenceProxy {
  /** 角色名称相关的本地化字符串 */
  private static final UIStrings strings = RPUtils.UIStrings("VanillaCharacterStrings");
  
  /** 角色名称数组，索引对应不同角色 */
  public static final String[] TEXT = strings.TEXT;
  
  /** 富状态显示相关的本地化字符串 */
  private static final UIStrings presence = RPUtils.UIStrings("RichPresence");
  
  /** 富状态显示文本数组，包含各种状态模板 */
  public static final String[] PTEXT = presence.TEXT;
  
  /**
   * 根据楼层号计算实际的幕数
   * Exordium: 1-17层 (第1幕)
   * The City: 18-24层 (第2幕)
   * The Beyond: 25-51层 (第3幕)
   * The Heart: 52+层 (第4幕)
   * 
   * @param floorNum 游戏内部的楼层号（从0开始）
   * @return 实际的幕数（从1开始）
   */
  public static int getRealActNumber(int floorNum) {
    // floorNum在游戏内部是从0开始的，所以需要+1来匹配显示的楼层
    int displayFloor = floorNum + 1;
    
    if (displayFloor <= 17) {
      return 1; // Exordium - 第1幕
    } else if (displayFloor <= 24) {
      return 2; // The City - 第2幕
    } else if (displayFloor <= 51) {
      return 3; // The Beyond - 第3幕
    } else {
      return 4; // The Heart - 第4幕
    }
  }
  
  /**
   * 获取卡牌升级时的富状态显示文本
   * 
   * @param player 当前玩家
   * @param card 升级的卡牌
   * @param ascension 当前难度等级
   * @param floorNum 当前楼层号
   * @param ascensionLevel 当前进阶等级
   * @return 卡牌升级状态文本，如果不符合条件则返回null
   */
  public static String GetUpgradeRichPresenceDisplay(@NotNull AbstractPlayer player, AbstractCard card, int ascension, int floorNum, int ascensionLevel) {
    boolean inDungeon = RPUtils.RoomAvailable();
    boolean outOfCombat = !RPUtils.RoomChecker(MonsterRoom.class, AbstractRoom.RoomPhase.COMBAT);
    if (inDungeon && outOfCombat) {
      boolean remarkable;
      if (card instanceof com.megacrit.cardcrawl.cards.red.SearingBlow)
        return String.format(PTEXT[9], new Object[] { Integer.valueOf(card.timesUpgraded) }); 
      switch (player.chosenClass) {
        case IRONCLAD:
          remarkable = (card instanceof com.megacrit.cardcrawl.cards.red.Corruption || card instanceof com.megacrit.cardcrawl.cards.red.DarkEmbrace || card instanceof com.megacrit.cardcrawl.cards.red.DemonForm || card instanceof com.megacrit.cardcrawl.cards.red.Barricade || card instanceof com.megacrit.cardcrawl.cards.red.Metallicize);
          break;
        case THE_SILENT:
          remarkable = (card instanceof com.megacrit.cardcrawl.cards.green.CalculatedGamble || card instanceof com.megacrit.cardcrawl.cards.green.BouncingFlask || card instanceof com.megacrit.cardcrawl.cards.green.WraithForm || card instanceof com.megacrit.cardcrawl.cards.green.AThousandCuts || card instanceof com.megacrit.cardcrawl.cards.green.GrandFinale || card instanceof com.megacrit.cardcrawl.cards.green.Nightmare);
          break;
        case DEFECT:
          remarkable = (card instanceof com.megacrit.cardcrawl.cards.blue.Electrodynamics || card instanceof com.megacrit.cardcrawl.cards.blue.CreativeAI || card instanceof com.megacrit.cardcrawl.cards.blue.EchoForm || card instanceof com.megacrit.cardcrawl.cards.blue.Buffer || card instanceof com.megacrit.cardcrawl.cards.blue.BiasedCognition || card instanceof com.megacrit.cardcrawl.cards.blue.Reprogram);
          break;
        case WATCHER:
          remarkable = (card instanceof com.megacrit.cardcrawl.cards.purple.Rushdown || card instanceof com.megacrit.cardcrawl.cards.purple.Devotion || card instanceof com.megacrit.cardcrawl.cards.purple.DevaForm || card instanceof com.megacrit.cardcrawl.cards.purple.Nirvana || card instanceof com.megacrit.cardcrawl.cards.purple.Judgement);
          break;
        default:
          remarkable = (card.rarity == AbstractCard.CardRarity.RARE);
          break;
      } 
      if (!remarkable)
        remarkable = (card.rarity == AbstractCard.CardRarity.BASIC); 
      if (remarkable)
        return String.format(PTEXT[8], new Object[] { card.name }); 
    } 
    return null;
  }
  
  /**
   * 获取事件房间的富状态显示文本
   * 
   * @param player 当前玩家
   * @param eventName 事件名称
   * @param event 当前事件对象
   * @param ascension 当前难度等级
   * @param floorNum 当前楼层号
   * @param ascensionLevel 当前进阶等级
   * @return 事件状态文本
   */
  public static String GetEventRichPresenceDisplay(@NotNull AbstractPlayer player, String eventName, AbstractEvent event, int ascension, int floorNum, int ascensionLevel) {
    return String.format(PTEXT[6], new Object[] { eventName });
  }
  
  /**
   * 获取战斗房间的富状态显示文本
   * 
   * @param player 当前玩家
   * @param monsters 怪物列表
   * @param ascension 当前难度等级
   * @param floorNum 当前楼层号
   * @param ascensionLevel 当前进阶等级
   * @return 战斗状态文本，如果没有合适的状态则返回null
   */
  @Nullable
  public static String GetBattleRichPresenceDisplay(@NotNull AbstractPlayer player, List<AbstractMonster> monsters, int ascension, int floorNum, int ascensionLevel) {
    StringBuilder sb = new StringBuilder();
    
    // 检查是否是Boss房间
    boolean isBossRoom = RPUtils.RoomChecker(MonsterRoomBoss.class);
    
    RPUtils.Log("GetBattleRichPresenceDisplay - Boss room: " + isBossRoom + ", monsters count: " + monsters.size());
    
    if (isBossRoom) {
      // 在Boss房间中，优先显示Boss类型的怪物
      long bossCount = monsters.stream().filter(m -> (m.type == AbstractMonster.EnemyType.BOSS)).count();
      RPUtils.Log("Boss room - Boss count: " + bossCount);
      
      monsters.stream().filter(m -> (m.type == AbstractMonster.EnemyType.BOSS))
        .forEach(m -> {
            if (sb.length() > 0)
              sb.append(", "); 
            sb.append(m.name);
          });
      
      // 如果找到了Boss，使用Boss战文本格式
      if (sb.length() > 0) {
        String result = String.format(PTEXT[5], new Object[] { sb });
        RPUtils.Log("Boss battle presence: " + result);
        return result; 
      }
      
      // 如果没有找到Boss类型的怪物，但仍在Boss房间，显示所有怪物
      // 这种情况可能发生在Boss被击败后，但房间类型仍然是MonsterRoomBoss
      RPUtils.Log("No boss found in boss room, showing all monsters");
      int maxEnemiesShown = Math.min(monsters.size(), 3);
      for (int i = 0; i < maxEnemiesShown; i++) {
        AbstractMonster m = monsters.get(i);
        if (sb.length() > 0)
          sb.append(", "); 
        sb.append(m.name);
      }
      
      // 如果在Boss房间但显示的是普通怪物，使用普通战斗文本格式
      if (sb.length() > 0) {
        String result = String.format(PTEXT[4], new Object[] { sb });
        RPUtils.Log("Boss room but normal battle presence: " + result);
        return result; 
      }
    } else {
      // 普通怪物房间，显示前3个怪物
      RPUtils.Log("Normal monster room");
      int maxEnemiesShown = Math.min(monsters.size(), 3);
      for (int i = 0; i < maxEnemiesShown; i++) {
        AbstractMonster m = monsters.get(i);
        if (sb.length() > 0)
          sb.append(", "); 
        sb.append(m.name);
      } 
      
      if (sb.length() > 0) {
        String result = String.format(PTEXT[4], new Object[] { sb });
        RPUtils.Log("Normal battle presence: " + result);
        return result; 
      }
    }
    
    RPUtils.Log("No battle presence generated");
    return null;
  }
  
  public static String getCharacterBattleRichPresenceDisplay(List<AbstractMonster> monsters, int ascension, int floorNum, int ascensionLevel) {
    AbstractPlayer player = com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;
    if (player != null) {
      return GetBattleRichPresenceDisplay(player, monsters, ascension, floorNum, ascensionLevel);
    }
    return null;
  }
  
  /**
   * 获取游戏概览状态的富状态显示文本
   * 格式为：[角色名] - [游戏模式]，[进度状态]，已到达第[楼层]层
   * 
   * @param player 当前玩家
   * @param displayName 角色显示名称
   * @param ascension 当前难度等级
   * @param floorNum 当前楼层号（内部索引，从0开始）
   * @param ascensionLevel 当前进阶等级
   * @return 概览状态文本
   */
  @NotNull
  public static String GetRichPresenceOverviewDisplay(@NotNull AbstractPlayer player, String displayName, int ascension, int floorNum, int ascensionLevel) {
    StringBuilder sb = new StringBuilder(displayName + " - ");
    if (Settings.isDailyRun)
      sb.append(PTEXT[0]); 
    if (Settings.isTrial)
      sb.append(PTEXT[1]); 
    if (ascensionLevel > 0)
      sb.append(String.format(PTEXT[2], new Object[] { Integer.valueOf(ascensionLevel) })); 
    switch (player.chosenClass) {
      case IRONCLAD:
        sb.append(TEXT[4]);
        break;
      case THE_SILENT:
        sb.append(TEXT[5]);
        break;
      case DEFECT:
        sb.append(TEXT[6]);
        break;
      case WATCHER:
        sb.append(TEXT[7]);
        break;
    } 
    sb.append(String.format(PTEXT[3], new Object[] { Integer.valueOf(floorNum) }));
    RPUtils.Log("setting overview presence: " + sb);
    return sb.toString();
  }
  
  public static String getCharacterRichPresenceOverviewDisplay(String displayName, int ascension, int floorNum, int ascensionLevel) {
    AbstractPlayer player = com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;
    if (player != null) {
      // 使用正确的ascensionLevel值（显示进阶等级）
      return GetRichPresenceOverviewDisplay(player, displayName, ascension, floorNum, ascensionLevel);
    }
    return displayName + " - Playing Slay the Spire";
  }
  
  public static String getCharacterRestRichPresenceDisplay(int ascension, int floorNum, int ascensionLevel) {
    // 如果PTEXT[10]不存在，默认返回休息相关的文本
    if (PTEXT.length > 10) {
      return CharacterRichPresenceProxy.PTEXT[10];
    } else {
      // 根据语言环境返回默认的休息文本
      String language = com.megacrit.cardcrawl.core.Settings.language.name();
      if (language.equals("ZHS") || language.equals("ZHT")) {
        return "正在篝火旁休息";
      } else {
        return "Resting at Campfire";
      }
    }
  }
  
  public static String getCharacterShopRichPresenceDisplay(int ascension, int floorNum, int ascensionLevel) {
    // 如果PTEXT[11]不存在，默认返回商店相关的文本
    if (PTEXT.length > 11) {
      return CharacterRichPresenceProxy.PTEXT[11];
    } else {
      // 根据语言环境返回默认的商店文本
      String language = com.megacrit.cardcrawl.core.Settings.language.name();
      if (language.equals("ZHS") || language.equals("ZHT")) {
        return "正在访问商店";
      } else {
        return "Visiting the Shop";
      }
    }
  }
  
  /**
   * 获取玩家角色的显示名称
   * 
   * @param player 游戏中的玩家角色
   * @return 对应的本地化角色名称
   */
  public static String GetDisplayName(AbstractPlayer player) {
    switch (player.chosenClass) {
      case IRONCLAD:
        return TEXT[0];
      case THE_SILENT:
        return TEXT[1];
      case DEFECT:
        return TEXT[2];
      case WATCHER:
        return player.getLocalizedCharacterName();
    } 
    return "Unsupported vanilla character";
  }
}
