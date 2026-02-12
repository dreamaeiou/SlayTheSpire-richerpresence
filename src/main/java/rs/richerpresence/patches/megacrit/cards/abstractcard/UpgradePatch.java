package rs.richerpresence.patches.megacrit.cards.abstractcard;

import basemod.AutoAdd;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import org.clapper.util.classutil.AndClassFilter;
import org.clapper.util.classutil.ClassFilter;
import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassInfo;
import rs.richerpresence.core.RichPresenceUpdater;
import rs.richerpresence.utils.RPUtils;
import rs.richerpresence.utils.RemarkableThing;
import rs.richerpresence.utils.classutils.OverrideMethodFilter;
import rs.richerpresence.utils.classutils.SuperClassFilter;

public class UpgradePatch {
  @SpirePatch2(clz = CardCrawlGame.class, method = "render")
  public static class SubUpgradePatch {
    @SpireRawPatch
    public static void Raw(CtBehavior ctBehavior) throws Exception {
      long ms = System.currentTimeMillis();
      ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();
      ClassFinder finder = new ClassFinder();
      boolean stsjar = finder.add(new File(Loader.STS_JAR));
      if (!stsjar)
        RPUtils.Log("Unable to load Sts jar in: " + Loader.STS_JAR); 
      SuperClassFilter superClassFilter = new SuperClassFilter(AbstractCard.class, pool);
      OverrideMethodFilter overrideMethodFilter = new OverrideMethodFilter("upgrade", pool, new CtClass[0]);
      AndClassFilter andClassFilter1 = new AndClassFilter(new ClassFilter[] { (ClassFilter)new AutoAdd.PackageFilter(AbstractCard.class), (ClassFilter)superClassFilter, (ClassFilter)overrideMethodFilter });
      List<ClassInfo> cards = new ArrayList<>();
      finder.findClasses(cards, (ClassFilter)andClassFilter1);
      finder.clear();
      int extramods = 0;
      for (ModInfo modinfo : Loader.MODINFOS) {
        if (finder.add(new File(modinfo.jarURL.toURI())))
          extramods++; 
      } 
      AndClassFilter andClassFilter2 = new AndClassFilter(new ClassFilter[] { (ClassFilter)superClassFilter, (ClassFilter)overrideMethodFilter });
      int count = finder.findClasses(cards, (ClassFilter)andClassFilter2);
      if (count > 0) {
        RPUtils.Log("Find " + count + " card subclasses in " + extramods + " mods");
        count = 0;
        for (ClassInfo classInfo : cards) {
          try {
            CtClass cardClz = pool.get(classInfo.getClassName());
            CtMethod upgrade = cardClz.getDeclaredMethod("upgrade");
            upgrade.insertAfter("{" + UpgradePatch.class.getName() + ".CallOnUpgrade($0);}");
            count++;
          } catch (Exception e) {
            RPUtils.Log("Failed to inject method into [" + classInfo.getClassName() + "] upgrade();");
          } 
        } 
        ms = System.currentTimeMillis() - ms;
        RPUtils.Log(count + " cards' upgrade() have been edited: " + ms + "ms");
      } 
    }
  }
  
  public static void CallOnUpgrade(AbstractCard card) {
    boolean inDungeon = RPUtils.RoomAvailable();
    if (inDungeon && card != null)
      RichPresenceUpdater.UpdateRemarkablePresence(RemarkableThing.UPGRADING_CARD.setRemark(card), AbstractDungeon.ascensionLevel, AbstractDungeon.floorNum, AbstractDungeon.actNum); 
  }
}
