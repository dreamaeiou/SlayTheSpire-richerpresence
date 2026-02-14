package rs.richerpresence.core;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import java.util.Comparator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.utils.LMSK;
import rs.richerpresence.character.CharacterRichPresenceProxy;
import rs.richerpresence.utils.RPUtils;
import rs.richerpresence.utils.RemarkableThing;

/**
 * 富状态更新器
 * 负责更新和管理Steam/Discord富状态的各种显示信息
 * 包括概览状态、行动状态、特殊事件状态等
 */
public class RichPresenceUpdater {
  /** 概览状态文本 - 显示玩家角色、进度等基本信息 */
  protected static String OVERVIEW_PRESENCE;
  
  /** 行动状态文本 - 显示当前房间的具体活动（战斗、事件、休息等） */
  protected static String ACTION_PRESENCE;
  
  /** 特殊事件状态文本 - 显示特殊的升级、选择等事件 */
  protected static String REMARKABLE__PRESENCE;
  
  /** 战斗状态标志 - 标识当前是否处于战斗状态 */
  private static boolean battleState = false;
  
  /**
   * 更新概览状态
   * 概览状态包含玩家角色、游戏模式、进度等基本信息
   * 
   * @param player 当前玩家对象
   * @param ascension 当前难度等级
   * @param floorNum 当前楼层号
   * @param ascensionLevel 当前进阶等级
   */
  protected static void UpdateOverviewPresence(AbstractPlayer player, int ascension, int floorNum, int ascensionLevel) {
    String displayName = getCharacterDisplayName(player);
    String msg = getCharacterRichPresenceOverviewDisplay(displayName, ascension, floorNum, ascensionLevel);
    OVERVIEW_PRESENCE = msg;
  }
  
  /**
   * 更新行动状态
   * 行动状态根据当前房间类型显示不同的活动信息（战斗、事件、休息等）
   * 
   * @param ascension 当前难度等级
   * @param floorNum 当前楼层号
   * @param ascensionLevel 当前进阶等级
   */
  protected static void UpdateActionPresence(int ascension, int floorNum, int ascensionLevel) {
    String msg = null;
    boolean isMonsterRoom = RPUtils.RoomChecker(MonsterRoom.class);
    boolean isEventRoom = RPUtils.RoomChecker(EventRoom.class);
    boolean isRestRoom = RPUtils.RoomChecker(RestRoom.class);
    AbstractRoom.RoomPhase phase = AbstractDungeon.getCurrRoom() != null ? AbstractDungeon.getCurrRoom().phase : null;
    
    RPUtils.Log("UpdateActionPresence - Room: " + (AbstractDungeon.getCurrRoom() != null ? AbstractDungeon.getCurrRoom().getClass().getSimpleName() : "null") + 
                ", Phase: " + phase + 
                ", isMonsterRoom: " + isMonsterRoom + 
                ", isEventRoom: " + isEventRoom + 
                ", isRestRoom: " + isRestRoom);
    
    if (isMonsterRoom) {
      // 安全检查：确保游戏已经完全初始化，避免在初始化阶段调用导致崩溃
      if (AbstractDungeon.getCurrRoom() == null || AbstractDungeon.getCurrRoom().monsters == null) {
        RPUtils.Log("UpdateActionPresence: Game not fully initialized, skipping monster list retrieval");
        // 保持之前的战斗状态，不设置为null
        if (ACTION_PRESENCE != null && ACTION_PRESENCE.contains("正在与")) {
          RPUtils.Log("Keeping previous battle state: " + ACTION_PRESENCE);
          return;
        }
      } else {
        List<AbstractMonster> monsters = LMSK.GetAllExptMstr(m -> true);
        if (monsters != null && !monsters.isEmpty()) {
          RPUtils.Log("Found " + monsters.size() + " monsters");
          monsters.sort(Comparator.comparing(m -> m.type));
          msg = getCharacterBattleRichPresenceDisplay(monsters, ascension, floorNum, ascensionLevel);
          RPUtils.Log("Battle presence: " + msg);
        } else {
          RPUtils.Log("Monsters list is null or empty");
          // 保持之前的战斗状态，不设置为null
          if (ACTION_PRESENCE != null && ACTION_PRESENCE.contains("正在与")) {
            RPUtils.Log("Keeping previous battle state: " + ACTION_PRESENCE);
            return;
          }
        }
      }
    } else if (isEventRoom) {
      AbstractEvent event = (AbstractDungeon.getCurrRoom()).event;
      String eventName = getEventName(event);
      msg = getCharacterEventRichPresenceDisplay(eventName, event, ascension, floorNum, ascensionLevel);
      RPUtils.Log("Event presence: " + msg);
    } else if (isRestRoom) {
      RPUtils.Log("UpdateActionPresence: Detected RestRoom, calling getCharacterRestRichPresenceDisplay");
      msg = getCharacterRestRichPresenceDisplay(ascension, floorNum, ascensionLevel);
      RPUtils.Log("Rest presence: " + msg);
    } else {
      RPUtils.Log("UpdateActionPresence: Not a monster, event, or rest room");
      // 只有在明确离开战斗状态时才清除战斗信息
      if (phase == AbstractRoom.RoomPhase.COMBAT) {
        RPUtils.Log("Still in combat phase, keeping battle state");
        return;
      }
    }
    
    // 只有当msg不为null时才更新ACTION_PRESENCE
    if (msg != null) {
      ACTION_PRESENCE = msg;
    }
  }
  
  /**
   * 更新特殊事件状态
   * 处理游戏中特殊事件的状态更新，如卡牌升级等
   * 
   * @param remarkable 特殊事件对象
   * @param ascension 当前难度等级
   * @param floorNum 当前楼层号
   * @param ascensionLevel 当前进阶等级
   */
  public static void UpdateRemarkablePresence(RemarkableThing remarkable, int ascension, int floorNum, int ascensionLevel) {
    Object remark;
    switch (remarkable.desc) {
      case 0:
        remark = remarkable.remark;
        if (remark instanceof AbstractCard) {
          AbstractCard card = (AbstractCard)remark;
          REMARKABLE__PRESENCE = getCharacterRichPresenceDisplayOnUpgrade(card, ascension, floorNum, ascensionLevel);
          RichPresenceDistributor.OnUpgradeCard();
        } 
        break;
    } 
  }
  
  private static String getEventName(AbstractEvent event) {
    String eventName = CharacterRichPresenceProxy.PTEXT[7];
    if (event != null)
      if (event instanceof AbstractImageEvent) {
        eventName = (String)ReflectionHacks.getPrivate(event, AbstractImageEvent.class, "title");
      } else {
        EventStrings strings = (EventStrings)ReflectionHacks.getPrivateStatic(event.getClass(), "eventStrings");
        eventName = strings.NAME;
      }  
    return eventName;
  }
  
  private static String getCharacterRichPresenceDisplayOnUpgrade(AbstractCard card, int ascension, int floorNum, int actNum) {
    AbstractPlayer player = com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;
    if (player != null) {
      return rs.richerpresence.character.CharacterRichPresenceProxy.GetUpgradeRichPresenceDisplay(player, card, ascension, floorNum, actNum);
    }
    return "Upgrading card: " + card.name;
  }
  
  private static String getCharacterEventRichPresenceDisplay(String eventName, AbstractEvent event, int ascension, int floorNum, int actNum) {
    AbstractPlayer player = com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;
    if (player != null) {
      return rs.richerpresence.character.CharacterRichPresenceProxy.GetEventRichPresenceDisplay(player, eventName, event, ascension, floorNum, actNum);
    }
    return "Playing event: " + eventName;
  }
  
  private static String getCharacterBattleRichPresenceDisplay(List<AbstractMonster> monsters, int ascension, int floorNum, int actNum) {
    return rs.richerpresence.character.CharacterRichPresenceProxy.getCharacterBattleRichPresenceDisplay(monsters, ascension, floorNum, actNum);
  }
  
  private static String getCharacterRestRichPresenceDisplay(int ascension, int floorNum, int actNum) {
    return rs.richerpresence.character.CharacterRichPresenceProxy.getCharacterRestRichPresenceDisplay(ascension, floorNum, actNum);
  }
  
  private static String getCharacterRichPresenceOverviewDisplay(String displayName, int ascension, int floorNum, int actNum) {
    return rs.richerpresence.character.CharacterRichPresenceProxy.getCharacterRichPresenceOverviewDisplay(displayName, ascension, floorNum, actNum);
  }
  
  private static String getCharacterDisplayName(@NotNull AbstractPlayer player) {
    return player.getLocalizedCharacterName();
  }
  
  /**
   * 检查当前是否处于战斗状态
   * 
   * @return 如果处于战斗状态返回true，否则返回false
   */
  public static boolean isInBattleState() {
    return battleState;
  }
  
  /**
   * 设置战斗状态
   * 
   * @param inBattle 是否处于战斗状态
   */
  public static void setInBattleState(boolean inBattle) {
    battleState = inBattle;
    RPUtils.Log("Battle state set to: " + battleState);
  }
}
