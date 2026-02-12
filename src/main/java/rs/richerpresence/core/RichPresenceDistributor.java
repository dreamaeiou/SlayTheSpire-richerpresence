package rs.richerpresence.core;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import rs.richerpresence.character.CharacterRichPresenceProxy;

public class RichPresenceDistributor {
  public static void OnRoomTransition() {
    // 安全检查：确保游戏已经初始化完成
    if (AbstractDungeon.player == null || AbstractDungeon.getCurrRoom() == null) {
      Presenter.Log("OnRoomTransition skipped: Game not fully initialized");
      return;
    }
    
    // 检查当前是否处于战斗状态，如果是则跳过状态更新，防止覆盖战斗信息
    if (RichPresenceUpdater.isInBattleState()) {
      Presenter.Log("OnRoomTransition: Currently in battle state, skipping to prevent override");
      return;
    }
    
    AbstractPlayer p = AbstractDungeon.player;
    RichPresenceUpdater.UpdateOverviewPresence(p, AbstractDungeon.ascensionLevel, AbstractDungeon.floorNum, AbstractDungeon.actNum);
    
    // 检查是否是怪物房间，如果是则更新战斗状态
    boolean isMonsterRoom = rs.richerpresence.utils.RPUtils.RoomChecker(com.megacrit.cardcrawl.rooms.MonsterRoom.class);
    
    // 检查当前是否在战斗阶段
    boolean isCombatPhase = AbstractDungeon.getCurrRoom() != null && 
                            AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    
    if (isMonsterRoom && isCombatPhase) {
      // 在怪物房间且处于战斗阶段，但避免在房间转换时过早调用怪物列表获取
      // 只设置基础状态，战斗状态由战斗开始事件处理
      String overview = RichPresenceUpdater.OVERVIEW_PRESENCE;
      Presenter.SetRichPresenceDisplay("status", overview);
      Presenter.Log("OnRoomTransition: Monster room in combat phase, but delaying battle state update to avoid crash");
    } else if (isMonsterRoom && !isCombatPhase) {
      // 在怪物房间但不在战斗阶段（可能是战斗结束或准备阶段），只设置概览状态
      String overview = RichPresenceUpdater.OVERVIEW_PRESENCE;
      Presenter.SetRichPresenceDisplay("status", overview);
      Presenter.Log("OnRoomTransition: Monster room but not in combat phase, setting overview only");
    } else {
      // 不是怪物房间，只设置概览状态
      String overview = RichPresenceUpdater.OVERVIEW_PRESENCE;
      Presenter.SetRichPresenceDisplay("status", overview);
      Presenter.Log("OnRoomTransition: Not monster room, setting overview only");
    }
  }
  
  public static void OnBattlePerTurn() {
    // 更新概览状态
    RichPresenceUpdater.UpdateOverviewPresence(AbstractDungeon.player, AbstractDungeon.ascensionLevel, AbstractDungeon.floorNum, AbstractDungeon.actNum);
    
    // 只有在需要时才更新动作状态
    boolean isMonsterRoom = rs.richerpresence.utils.RPUtils.RoomChecker(com.megacrit.cardcrawl.rooms.MonsterRoom.class);
    if (isMonsterRoom) {
      RichPresenceUpdater.UpdateActionPresence(AbstractDungeon.ascensionLevel, AbstractDungeon.floorNum, AbstractDungeon.actNum);
    }
    
    // 构建战斗状态信息
    String battleview = RichPresenceUpdater.OVERVIEW_PRESENCE;
    if (RichPresenceUpdater.ACTION_PRESENCE != null && !RichPresenceUpdater.ACTION_PRESENCE.isEmpty())
      battleview = battleview + " - " + RichPresenceUpdater.ACTION_PRESENCE; 
    Presenter.SetRichPresenceDisplay("status", battleview);
    
    // 标记当前处于战斗状态，防止被后续事件覆盖
    RichPresenceUpdater.setInBattleState(true);
    
    Presenter.Log("Battle presence: " + battleview);
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
