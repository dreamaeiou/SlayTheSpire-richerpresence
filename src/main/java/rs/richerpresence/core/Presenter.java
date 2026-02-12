package rs.richerpresence.core;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.ISubscriber;
import basemod.interfaces.OnPlayerTurnStartSubscriber;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.PostBattleSubscriber;
import com.codedisaster.steamworks.SteamFriends;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.integrations.DistributorFactory;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import rs.lazymankits.LMDebug;
import rs.richerpresence.utils.RPUtils;

@SpireInitializer
public class Presenter implements EditStringsSubscriber, OnStartBattleSubscriber, OnPlayerTurnStartSubscriber, PostBattleSubscriber {
  public static final String MOD_ID = "RicherPresence";
  
  public static final String PREFIX = "richerpre";
  
  public static final String[] AUTHORS = new String[] { "Somdy" };
  
  public static final String DESCRIPTION = "Richer presence information";
  
  public static final String UNDEFINED_DISTRIBUTOR = "undefined distributor";
  
  public static final String STEAM = "steam";
  
  public static final String DISCORD = "discord";
  
  protected static String GameDistributor = "undefined distributor";
  
  public static boolean UsingDefaultDistributor = false;
  
  public static void initialize() {
    Presenter instance = new Presenter();
    BaseMod.subscribe((ISubscriber)instance);
  }
  
  public static void Log(String msg) {
    LMDebug.deLog(Presenter.class, "[Richer Presence]:: " + msg);
  }
  
  public static void SetDistributor(DistributorFactory.Distributor distributor) {
    switch (distributor) {
      case STEAM:
        GameDistributor = "steam";
        break;
      case DISCORD:
        GameDistributor = "discord";
        break;
    } 
  }
  
  public static void SetRichPresenceDisplay(String key, String msg) {
    switch (GameDistributor) {
      case "steam":
        SetSteamRichPresenceDisplay(key, msg);
        return;
      case "discord":
        SetDiscordRichPresenceDisplay("", msg);
        return;
    } 
    Log("Unsupported distributor [" + GameDistributor + "]");
  }
  
  public static void SetSteamRichPresenceDisplay(String key, String msg) {
    setSteamRichPresenceData(key, msg);
    setSteamRichPresenceData("steam_display", "#" + key);
  }
  
  public static void SetDiscordRichPresenceDisplay(String key, String msg) {
    setDiscordRichPresenceData(key, msg);
  }
  
  public static void ClearRichPresenceDisplay() {
    switch (GameDistributor) {
      case "steam":
        clearSteamRichPresenceData();
        return;
      case "discord":
        clearDiscordRichPresenceData();
        return;
    } 
    Log("Unsupported distributor [" + GameDistributor + "]");
  }
  
  private static void setSteamRichPresenceData(String key, String value) {
    SteamFriends steamFriends = (SteamFriends)ReflectionHacks.getPrivateStatic(SteamIntegration.class, "steamFriends");
    if (steamFriends != null && !steamFriends.setRichPresence(key, value))
      Log("Failed to set STEAM rich presence [ key = " + key + ", value = " + value + " ]"); 
  }
  
  private static void clearSteamRichPresenceData() {
    SteamFriends steamFriends = (SteamFriends)ReflectionHacks.getPrivateStatic(SteamIntegration.class, "steamFriends");
    steamFriends.clearRichPresence();
  }
  
  private static void setDiscordRichPresenceData(String state, String details) {
    DiscordRichPresence rich = (new DiscordRichPresence.Builder(state)).setDetails(details).build();
    DiscordRPC.discordUpdatePresence(rich);
  }
  
  private static void clearDiscordRichPresenceData() {
    DiscordRPC.discordClearPresence();
  }
  
  public void receiveEditStrings() {
    String lang = RPUtils.SupLang();
    Log("Loading strings for language: " + lang);
    Log("Resource path: RPAssets/locals/" + lang + "/ui.json");
    BaseMod.loadCustomStringsFile(UIStrings.class, "RPAssets/locals/" + lang + "/ui.json");
  }
  
  public void receiveOnBattleStart(AbstractRoom room) {
    Log("receiveOnBattleStart called");
    RichPresenceDistributor.OnBattlePerTurn();
  }
  
  public void receiveOnPlayerTurnStart() {
    Log("receiveOnPlayerTurnStart called");
    RichPresenceDistributor.OnBattlePerTurn();
  }
  
  public void receivePostBattle(AbstractRoom room) {
    Log("receivePostBattle called");
    RichPresenceDistributor.OnRoomTransition();
  }
}
