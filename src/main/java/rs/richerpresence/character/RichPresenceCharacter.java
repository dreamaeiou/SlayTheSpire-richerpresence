package rs.richerpresence.character;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.events.AbstractEvent;
import java.util.List;

import rs.richerpresence.utils.classutils.NewMethodInfo;

public class RichPresenceCharacter {
  private static class Names {
    private static final String M_GET_DISPLAY_NAME = "getRichPresenceDisplayName";
    
    private static final String M_GET_OVERVIEW_PRESENCE_NAME = "getRichPresenceOverviewDisplay";
    
    private static final String M_GET_BATTLE_PRESENCE_NAME = "getBattlePresenceDisplay";
    
    private static final String M_GET_EVENT_PRESENCE_NAME = "getEventPresenceDisplay";
    
    private static final String M_GET_UPGRADE_PRESENCE_NAME = "getPresenceDisplayOnUpgrade";
  }
  
  public static class Methods {
    public static final NewMethodInfo GET_DISPLAY_NAME = (new NewMethodInfo("getRichPresenceDisplayName", 1, String.class, new Class[0]))
      .setDefaultBody("{return getLocalizedCharacterName();}");
    
    public static final NewMethodInfo GET_OVERVIEW_DISPLAY = (new NewMethodInfo("getRichPresenceOverviewDisplay", 1, String.class, new Class[] { String.class, int.class, int.class, int.class })).setDefaultBody("{return " + CharacterRichPresenceProxy.class.getName() + ".GetRichPresenceOverviewDisplay($0, $1, $2, $3, $4);}");
    
    public static final NewMethodInfo GET_BATTLE_DISPLAY = (new NewMethodInfo("getBattlePresenceDisplay", 1, String.class, new Class[] { List.class, int.class, int.class, int.class })).setDefaultBody("{return " + CharacterRichPresenceProxy.class.getName() + ".GetBattleRichPresenceDisplay($0, $1, $2, $3, $4);}");
    
    public static final NewMethodInfo GET_EVENT_DISPLAY = (new NewMethodInfo("getEventPresenceDisplay", 1, String.class, new Class[] { String.class, AbstractEvent.class, int.class, int.class, int.class })).setDefaultBody("{return " + CharacterRichPresenceProxy.class.getName() + ".GetEventRichPresenceDisplay($0, $1, $2, $3, $4, $5);}");
    
    public static final NewMethodInfo GET_UPGRADE_DISPLAY = (new NewMethodInfo("getPresenceDisplayOnUpgrade", 1, String.class, new Class[] { AbstractCard.class, int.class, int.class, int.class })).setDefaultBody("{return " + CharacterRichPresenceProxy.class.getName() + ".GetUpgradeRichPresenceDisplay($0, $1, $2, $3, $4);}");
  }
}
