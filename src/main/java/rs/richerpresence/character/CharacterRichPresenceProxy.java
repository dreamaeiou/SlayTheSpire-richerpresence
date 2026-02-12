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

public class CharacterRichPresenceProxy {
  private static final UIStrings strings = RPUtils.UIStrings("VanillaCharacterStrings");
  
  public static final String[] TEXT = strings.TEXT;
  
  private static final UIStrings presence = RPUtils.UIStrings("RichPresence");
  
  public static final String[] PTEXT = presence.TEXT;
  
  public static String GetUpgradeRichPresenceDisplay(@NotNull AbstractPlayer player, AbstractCard card, int ascension, int floorNum, int actNum) {
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
  
  public static String GetEventRichPresenceDisplay(@NotNull AbstractPlayer player, String eventName, AbstractEvent event, int ascension, int floorNum, int actNum) {
    return String.format(PTEXT[6], new Object[] { eventName });
  }
  
  @Nullable
  public static String GetBattleRichPresenceDisplay(@NotNull AbstractPlayer player, List<AbstractMonster> monsters, int ascension, int floorNum, int actNum) {
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
  
  public static String getCharacterBattleRichPresenceDisplay(List<AbstractMonster> monsters, int ascension, int floorNum, int actNum) {
    AbstractPlayer player = com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;
    if (player != null) {
      return GetBattleRichPresenceDisplay(player, monsters, ascension, floorNum, actNum);
    }
    return null;
  }
  
  @NotNull
  public static String GetRichPresenceOverviewDisplay(@NotNull AbstractPlayer player, String displayName, int ascension, int floorNum, int actNum) {
    StringBuilder sb = new StringBuilder(displayName + " - ");
    if (Settings.isDailyRun)
      sb.append(PTEXT[0]); 
    if (Settings.isTrial)
      sb.append(PTEXT[1]); 
    if (ascension > 0)
      sb.append(String.format(PTEXT[2], new Object[] { Integer.valueOf(ascension) })); 
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
  
  public static String getCharacterRichPresenceOverviewDisplay(String displayName, int ascension, int floorNum, int actNum) {
    AbstractPlayer player = com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;
    if (player != null) {
      return GetRichPresenceOverviewDisplay(player, displayName, ascension, floorNum, actNum);
    }
    return displayName + " - Playing Slay the Spire";
  }
  
  public static String getCharacterRestRichPresenceDisplay(int ascension, int floorNum, int actNum) {
    return CharacterRichPresenceProxy.PTEXT[10];
  }
  
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
