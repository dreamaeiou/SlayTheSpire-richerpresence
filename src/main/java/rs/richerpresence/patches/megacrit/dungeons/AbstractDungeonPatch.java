package rs.richerpresence.patches.megacrit.dungeons;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import rs.richerpresence.core.Presenter;
import rs.richerpresence.core.RichPresenceDistributor;

public class AbstractDungeonPatch {
  @SpirePatch2(clz = AbstractDungeon.class, method = "nextRoomTransition", paramtypez = {SaveFile.class})
  public static class NextRoomTransitionPatch {
    @SpireRawPatch
    public static void Raw(CtBehavior ctBehavior) throws Exception {
      ctBehavior.instrument(new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
              if ("setRichPresenceDisplayPlaying".equals(m.getMethodName()))
                m.replace("if(" + Presenter.class.getName() + ".UsingDefaultDistributor){$_=$proceed($$);}"); 
            }
          });
      ctBehavior.instrument(new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
              if ("onPlayerEntry".equals(m.getMethodName()))
                m.replace("{if(!" + Presenter.class.getName() + ".UsingDefaultDistributor){" + AbstractDungeonPatch.NextRoomTransitionPatch.class
                    .getName() + ".CallCustomDistributor();}$_=$proceed($$);}"); 
            }
          });
    }
    
    public static void CallCustomDistributor() {
      RichPresenceDistributor.OnRoomTransition();
    }
  }
}
