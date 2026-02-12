package rs.richerpresence.core;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import rs.richerpresence.character.CharacterRichPresenceProxy;

public class RichPresenceDistributor {
  public static void OnRoomTransition() {
    // 安全检查：确保游戏已经初始化完成
    if (AbstractDungeon.player == null || AbstractDungeon.getCurrRoom() == null) {
      Presenter.Log("OnRoomTransition skipped: Game not fully initialized");
      return;
    }
    
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
  
  public static void OnCampfire() {
    // 安全检查：确保游戏已经初始化完成
    if (AbstractDungeon.player == null) {
      Presenter.Log("OnCampfire skipped: Game not fully initialized");
      return;
    }
    
    Presenter.Log("OnCampfire: Starting campfire presence update");
    AbstractPlayer p = AbstractDungeon.player;
    RichPresenceUpdater.UpdateOverviewPresence(p, AbstractDungeon.ascensionLevel, AbstractDungeon.floorNum, AbstractDungeon.actNum);
    Presenter.Log("OnCampfire: Overview presence updated");
    
    // 设置篝火状态
    String campfireStatus = CharacterRichPresenceProxy.PTEXT[10];
    Presenter.Log("OnCampfire: Setting campfire status: " + campfireStatus);
    
    String overview = RichPresenceUpdater.OVERVIEW_PRESENCE;
    Presenter.Log("OnCampfire: Overview: " + overview);
    
    if (campfireStatus != null) {
      overview = overview + " - " + campfireStatus; 
      Presenter.Log("OnCampfire: Final status with campfire: " + overview);
    }
    
    Presenter.SetRichPresenceDisplay("status", overview);
  }
}
