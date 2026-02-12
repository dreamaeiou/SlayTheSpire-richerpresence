package rs.richerpresence.patches.megacrit.characters;

import basemod.AutoAdd;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import org.clapper.util.classutil.AndClassFilter;
import org.clapper.util.classutil.ClassFilter;
import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassInfo;
import rs.richerpresence.character.CharacterRichPresenceProxy;
import rs.richerpresence.character.RichPresenceCharacter;
import rs.richerpresence.utils.RPUtils;
import rs.richerpresence.utils.classutils.NewMethodInfo;
import rs.richerpresence.utils.classutils.SuperClassFilter;

public class VanillaCharacterEditor {
  @SpirePatch2(clz = CardCrawlGame.class, method = "render")
  public static class AddMethodPatch {
    @SpireRawPatch
    public static void Raw(CtBehavior ctBehavior) throws Exception {
      ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();
      ClassFinder finder = new ClassFinder();
      boolean stsjar = finder.add(new File(Loader.STS_JAR));
      if (!stsjar)
        RPUtils.Log("Unable to load Sts jar in: " + Loader.STS_JAR); 
      SuperClassFilter superClassFilter = new SuperClassFilter(AbstractPlayer.class, pool);
      AndClassFilter andClassFilter = new AndClassFilter(new ClassFilter[] { (ClassFilter)new AutoAdd.PackageFilter(AbstractPlayer.class), (ClassFilter)superClassFilter });
      List<ClassInfo> classList = new ArrayList<>();
      int findings = finder.findClasses(classList, (ClassFilter)andClassFilter);
      if (!classList.isEmpty())
        for (ClassInfo info : classList) {
          CtClass clz = pool.get(info.getClassName());
          VanillaCharacterEditor.addMethod(clz, RichPresenceCharacter.Methods.GET_DISPLAY_NAME, pool, "{return " + CharacterRichPresenceProxy.class
              .getName() + ".GetDisplayName($0);}");
          VanillaCharacterEditor.addMethod(clz, RichPresenceCharacter.Methods.GET_OVERVIEW_DISPLAY, pool, null);
          VanillaCharacterEditor.addMethod(clz, RichPresenceCharacter.Methods.GET_BATTLE_DISPLAY, pool, null);
        }  
    }
  }
  
  protected static void addMethod(CtClass declaring, NewMethodInfo info, ClassPool pool, String body) throws Exception {
    CtMethod displayName = CtNewMethod.make(info.modifiers, pool.get(info.returnType.getName()), info.methodName, 
        getCts(pool, info.getParamTypes()), getCts(pool, info.getExceptions()), (body != null) ? body : info.defaultBody, declaring);
    declaring.addMethod(displayName);
  }
  
  private static CtClass[] getCts(ClassPool pool, String[] classes) throws Exception {
    return RPUtils.GetCtsFromString(pool, classes);
  }
}
