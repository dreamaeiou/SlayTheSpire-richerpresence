package rs.richerpresence.core;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class RichPresenceDistributor {
  public static void OnRoomTransition() {
    AbstractPlayer p = AbstractDungeon.player;
    RichPresenceUpdater.UpdateOverviewPresence(p, AbstractDungeon.ascensionLevel, AbstractDungeon.floorNum, AbstractDungeon.actNum);
    String overview = RichPresenceUpdater.OVERVIEW_PRESENCE;
    Presenter.SetRichPresenceDisplay("status", overview);
  }
  
  public static void OnBattlePerTurn() {
    RichPresenceUpdater.UpdateActionPresence(AbstractDungeon.ascensionLevel, AbstractDungeon.floorNum, AbstractDungeon.actNum);
    String battleview = RichPresenceUpdater.OVERVIEW_PRESENCE;
    if (RichPresenceUpdater.ACTION_PRESENCE != null)
      battleview = battleview + " - " + RichPresenceUpdater.ACTION_PRESENCE; 
    Presenter.SetRichPresenceDisplay("status", battleview);
  }
  
  public static void OnPonderingEvent() {
    RichPresenceUpdater.UpdateActionPresence(AbstractDungeon.ascensionLevel, AbstractDungeon.floorNum, AbstractDungeon.actNum);
    String eventview = RichPresenceUpdater.OVERVIEW_PRESENCE;
    if (RichPresenceUpdater.ACTION_PRESENCE != null)
      eventview = eventview + " - " + RichPresenceUpdater.ACTION_PRESENCE; 
    Presenter.SetRichPresenceDisplay("status", eventview);
  }
  
  public static void OnUpgradeCard() {
    String remarkable = RichPresenceUpdater.OVERVIEW_PRESENCE;
    if (RichPresenceUpdater.REMARKABLE__PRESENCE != null)
      remarkable = remarkable + " - " + RichPresenceUpdater.REMARKABLE__PRESENCE; 
    Presenter.SetRichPresenceDisplay("status", remarkable);
  }
}
