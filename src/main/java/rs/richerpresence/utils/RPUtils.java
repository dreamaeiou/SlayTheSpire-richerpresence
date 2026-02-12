package rs.richerpresence.utils;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.ClassPool;
import javassist.CtClass;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.utils.LMGameGeneralUtils;
import rs.richerpresence.core.Presenter;

public interface RPUtils extends LMGameGeneralUtils {
  static CtClass[] GetCtsFromString(ClassPool pool, String... classes) throws Exception {
    if (classes != null && classes.length > 0) {
      CtClass[] cts = new CtClass[classes.length];
      for (int i = 0; i < classes.length; i++)
        cts[i] = pool.get(classes[i]); 
      return cts;
    } 
    return null;
  }
  
  @NotNull
  static String SupLang() {
    switch (Settings.language) {
      case ZHS:
        return "zhs";
      case ZHT:
        return "zht";
    } 
    return "eng";
  }
  
  @NotNull
  static String GetPrefix() {
    return "richerpre:";
  }
  
  @NotNull
  static String MakeID(String id) {
    return GetPrefix() + id;
  }
  
  static void Log(String what) {
    Presenter.Log(what);
  }
  
  static UIStrings UIStrings(@NotNull String id) {
    if (!id.startsWith(GetPrefix()))
      id = MakeID(id); 
    return CardCrawlGame.languagePack.getUIString(id);
  }
  
  static boolean RoomChecker(Class<? extends AbstractRoom> clz, AbstractRoom.RoomPhase phase) {
    return (RoomAvailable() && clz.isInstance(AbstractDungeon.getCurrRoom()) && (AbstractDungeon.getCurrRoom()).phase == phase);
  }
  
  static boolean RoomChecker(Class<? extends AbstractRoom> clz) {
    return (RoomAvailable() && clz.isInstance(AbstractDungeon.getCurrRoom()));
  }
  
  static boolean RoomChecker(AbstractRoom.RoomPhase phase) {
    return (RoomAvailable() && (AbstractDungeon.getCurrRoom()).phase == phase);
  }
  
  static boolean RoomAvailable() {
    return (AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() != null);
  }
}
