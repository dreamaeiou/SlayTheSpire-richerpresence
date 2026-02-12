package rs.richerpresence.patches.megacrit.core;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import javassist.CtBehavior;
import rs.richerpresence.core.Presenter;

public class CardCrawlGameCreatePatch {
  @SpirePatch2(clz = CardCrawlGame.class, method = "create")
  public static class SetUpPresenterDistributorPatch {
    @SpireInsertPatch(locator = Locator.class)
    public static void Insert() {
      Presenter.SetDistributor(CardCrawlGame.publisherIntegration.getType());
    }
    
    private static class Locator extends SpireInsertLocator {
      public int[] Locate(CtBehavior ctBehavior) throws Exception {
        Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(CardCrawlGame.class, "saveMigration");
        return LineFinder.findInOrder(ctBehavior, (Matcher)matcher);
      }
    }
  }
}
