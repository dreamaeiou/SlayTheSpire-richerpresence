package rs.richerpresence.patches.megacrit.characters;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import rs.richerpresence.character.RichPresenceCharacter;
import rs.richerpresence.core.RichPresenceUpdater;
import rs.richerpresence.utils.RPUtils;
import rs.richerpresence.utils.classutils.NewMethodInfo;

public class AbstractPlayerPatch {
  @SpirePatch2(clz = AbstractPlayer.class, method = "<ctor>")
  public static class AddMethodPatch {
    @SpireRawPatch
    public static void Raw(CtBehavior ctBehavior) throws Exception {
      ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();
      CtClass playerClz = ctBehavior.getDeclaringClass();
      CtClass dClz = pool.get(RichPresenceUpdater.class.getName());
      NewMethodInfo info = RichPresenceCharacter.Methods.GET_DISPLAY_NAME;
      CtMethod getter = AbstractPlayerPatch.make(info, pool, playerClz);
      playerClz.addMethod(getter);
      info = RichPresenceCharacter.Methods.GET_OVERVIEW_DISPLAY;
      getter = AbstractPlayerPatch.make(info, pool, playerClz);
      playerClz.addMethod(getter);
      CtMethod overview = dClz.getDeclaredMethod("getCharacterRichPresenceOverviewDisplay");
      overview.setBody("{" + AbstractPlayer.class.getName() + " player = " + AbstractDungeon.class.getName() + ".player;return player." + info.methodName + "($$);}");
      info = RichPresenceCharacter.Methods.GET_BATTLE_DISPLAY;
      getter = AbstractPlayerPatch.make(info, pool, playerClz);
      playerClz.addMethod(getter);
      CtMethod battleview = dClz.getDeclaredMethod("getCharacterBattleRichPresenceDisplay");
      battleview.setBody("{" + AbstractPlayer.class.getName() + " player = " + AbstractDungeon.class.getName() + ".player;return player." + info.methodName + "($$);}");
      info = RichPresenceCharacter.Methods.GET_EVENT_DISPLAY;
      getter = AbstractPlayerPatch.make(info, pool, playerClz);
      playerClz.addMethod(getter);
      battleview = dClz.getDeclaredMethod("getCharacterEventRichPresenceDisplay");
      battleview.setBody("{" + AbstractPlayer.class.getName() + " player = " + AbstractDungeon.class.getName() + ".player;return player." + info.methodName + "($$);}");
      info = RichPresenceCharacter.Methods.GET_UPGRADE_DISPLAY;
      getter = AbstractPlayerPatch.make(info, pool, playerClz);
      playerClz.addMethod(getter);
      battleview = dClz.getDeclaredMethod("getCharacterRichPresenceDisplayOnUpgrade");
      battleview.setBody("{" + AbstractPlayer.class.getName() + " player = " + AbstractDungeon.class.getName() + ".player;return player." + info.methodName + "($$);}");
    }
  }
  
  private static CtMethod make(NewMethodInfo info, ClassPool pool, CtClass playerClz) throws Exception {
    return CtNewMethod.make(info.modifiers, pool.get(info.returnType.getName()), info.methodName, 
        getCts(pool, info.getParamTypes()), getCts(pool, info.getExceptions()), info.defaultBody, playerClz);
  }
  
  private static CtClass[] getCts(ClassPool pool, String[] classes) throws Exception {
    return RPUtils.GetCtsFromString(pool, classes);
  }
}
