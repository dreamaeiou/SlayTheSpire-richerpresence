package rs.richerpresence.patches.megacrit.rooms;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.rooms.EventRoom;
import rs.richerpresence.core.RichPresenceDistributor;

public class EventRoomPatch {
  @SpirePatch2(clz = EventRoom.class, method = "onPlayerEntry")
  public static class OnPlayerEntryPatch {
    @SpirePostfixPatch
    public static void Postfix() {
      RichPresenceDistributor.OnPonderingEvent();
    }
  }
}
