package rs.richerpresence.core;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.convert.Transformer;
import rs.richerpresence.character.RichPresenceCharacter;
import rs.richerpresence.utils.RPUtils;
import rs.richerpresence.utils.classutils.NewMethodInfo;

public class UpdaterRewriter {
  @SpirePatch2(clz = CardCrawlGame.class, method = "render")
  public static class OverwriteMethods {
    @SpireRawPatch
    public static void Raw(CtBehavior ctBehavior) throws Exception {
      ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();
      CtClass dClz = pool.get(RichPresenceUpdater.class.getName());
      CtMethod ctMethod = dClz.getDeclaredMethod("getCharacterDisplayName");
      rewriteGetDisplayName(ctMethod, pool);
    }
    
    private static void rewriteGetDisplayName(CtMethod ctMethod, final ClassPool pool) throws CannotCompileException {
      ctMethod.instrument(new CodeConverter() {
          
          });
    }
  }
  
  private static void addInvokeVirtual(Bytecode bc, CtClass clz, NewMethodInfo info, ClassPool pool) throws Exception {
    bc.addInvokevirtual(clz, info.methodName, Descriptor.ofMethod(pool.get(info.getReturnType()), 
          RPUtils.GetCtsFromString(pool, info.getParamTypes())));
  }
}
