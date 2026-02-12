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
      List<AbstractMonster> monsters = LMSK.GetAllExptMstr(m -> true);
      if (monsters != null) {
        RPUtils.Log("Found " + monsters.size() + " monsters");
        monsters.sort(Comparator.comparing(m -> m.type));
        msg = getCharacterBattleRichPresenceDisplay(monsters, ascension, floorNum, actNum);
        RPUtils.Log("Battle presence: " + msg);
      } else {
        RPUtils.Log("Monsters list is null");
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
    }
    ACTION_PRESENCE = msg;
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
    return null;
  }
  
  private static String getCharacterEventRichPresenceDisplay(String eventName, AbstractEvent event, int ascension, int floorNum, int actNum) {
    return null;
  }
  
  private static String getCharacterBattleRichPresenceDisplay(List<AbstractMonster> monsters, int ascension, int floorNum, int actNum) {
    return null;
  }
  
  private static String getCharacterRestRichPresenceDisplay(int ascension, int floorNum, int actNum) {
    return CharacterRichPresenceProxy.PTEXT[10];
  }
  
  private static String getCharacterRichPresenceOverviewDisplay(String displayName, int ascension, int floorNum, int actNum) {
    return null;
  }
  
  private static String getCharacterDisplayName(@NotNull AbstractPlayer player) {
    return player.getLocalizedCharacterName();
  }
}
