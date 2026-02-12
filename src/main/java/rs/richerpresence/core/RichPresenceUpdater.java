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

public class RichPresenceUpdater {
  protected static String OVERVIEW_PRESENCE;
  
  protected static String ACTION_PRESENCE;
  
  protected static String REMARKABLE__PRESENCE;
  
  private static boolean battleState = false;
  
  protected static void UpdateOverviewPresence(AbstractPlayer player, int ascension, int floorNum, int actNum) {
    String displayName = getCharacterDisplayName(player);
    String msg = getCharacterRichPresenceOverviewDisplay(displayName, ascension, floorNum, actNum);
    OVERVIEW_PRESENCE = msg;
  }
  
  protected static void UpdateActionPresence(int ascension, int floorNum, int actNum) {
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
          msg = getCharacterBattleRichPresenceDisplay(monsters, ascension, floorNum, actNum);
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
      msg = getCharacterEventRichPresenceDisplay(eventName, event, ascension, floorNum, actNum);
      RPUtils.Log("Event presence: " + msg);
    } else if (isRestRoom) {
      RPUtils.Log("UpdateActionPresence: Detected RestRoom, calling getCharacterRestRichPresenceDisplay");
      msg = getCharacterRestRichPresenceDisplay(ascension, floorNum, actNum);
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
  
  public static void UpdateRemarkablePresence(RemarkableThing remarkable, int ascension, int floorNum, int actNum) {
    Object remark;
    switch (remarkable.desc) {
      case 0:
        remark = remarkable.remark;
        if (remark instanceof AbstractCard) {
          AbstractCard card = (AbstractCard)remark;
          REMARKABLE__PRESENCE = getCharacterRichPresenceDisplayOnUpgrade(card, ascension, floorNum, actNum);
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
  
  public static boolean isInBattleState() {
    return battleState;
  }
  
  public static void setInBattleState(boolean inBattle) {
    battleState = inBattle;
    RPUtils.Log("Battle state set to: " + battleState);
  }
}
