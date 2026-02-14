package rs.richerpresence.core;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import rs.richerpresence.character.CharacterRichPresenceProxy;

/**
 * 富状态分发器
 * 负责在游戏的不同事件和状态变化时触发相应的富状态更新
 * 处理房间转换、战斗、升级卡牌等各种游戏事件的状态更新
 */
public class RichPresenceDistributor {
  /**
   * 房间转换时的状态更新处理
   * 当玩家从一个房间移动到另一个房间时调用
   * 根据房间类型和游戏状态更新相应的富状态信息
   */
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
    RichPresenceUpdater.UpdateOverviewPresence(p, AbstractDungeon.ascensionLevel, AbstractDungeon.floorNum, AbstractDungeon.ascensionLevel);
    
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
  
  /**
   * 每回合战斗时的状态更新处理
   * 在战斗开始或玩家回合开始时调用，更新战斗相关的状态信息
   */
  public static void OnBattlePerTurn() {
    // 更新概览状态
    RichPresenceUpdater.UpdateOverviewPresence(AbstractDungeon.player, AbstractDungeon.ascensionLevel, AbstractDungeon.floorNum, AbstractDungeon.ascensionLevel);
    
    // 只有在需要时才更新动作状态
    boolean isMonsterRoom = rs.richerpresence.utils.RPUtils.RoomChecker(com.megacrit.cardcrawl.rooms.MonsterRoom.class);
    if (isMonsterRoom) {
      RichPresenceUpdater.UpdateActionPresence(AbstractDungeon.ascensionLevel, AbstractDungeon.floorNum, AbstractDungeon.ascensionLevel);
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
  
  /**
   * 思考事件时的状态更新处理
   * 处理游戏中需要思考选择的事件状态更新
   */
  public static void OnPonderingEvent() {
    RichPresenceUpdater.UpdateActionPresence(AbstractDungeon.ascensionLevel, AbstractDungeon.floorNum, AbstractDungeon.ascensionLevel);
    String eventview = RichPresenceUpdater.OVERVIEW_PRESENCE;
    if (RichPresenceUpdater.ACTION_PRESENCE != null)
      eventview = eventview + " - " + RichPresenceUpdater.ACTION_PRESENCE; 
    Presenter.SetRichPresenceDisplay("status", eventview);
  }
  
  /**
   * 卡牌升级时的状态更新处理
   * 处理玩家升级卡牌时的状态更新
   */
  public static void OnUpgradeCard() {
    String remarkable = RichPresenceUpdater.OVERVIEW_PRESENCE;
    if (RichPresenceUpdater.REMARKABLE__PRESENCE != null)
      remarkable = remarkable + " - " + RichPresenceUpdater.REMARKABLE__PRESENCE; 
    Presenter.SetRichPresenceDisplay("status", remarkable);
  }
  
  /**
   * 篝火事件时的状态更新处理
   * 处理玩家在篝火房间休息时的状态更新
   */
  public static void OnCampfire() {
    // 安全检查：确保游戏已经初始化完成
    if (AbstractDungeon.player == null) {
      Presenter.Log("OnCampfire skipped: Game not fully initialized");
      return;
    }
    
    Presenter.Log("OnCampfire: Starting campfire presence update");
    AbstractPlayer p = AbstractDungeon.player;
    RichPresenceUpdater.UpdateOverviewPresence(p, AbstractDungeon.ascensionLevel, AbstractDungeon.floorNum, AbstractDungeon.ascensionLevel);
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
