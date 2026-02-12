package rs.richerpresence.utils.classutils;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.clapper.util.classutil.ClassFilter;
import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassInfo;

public class OverrideMethodFilter implements ClassFilter {
  private final String mname;
  
  private final ClassPool pool;
  
  private final CtClass[] params;
  
  public OverrideMethodFilter(String mname, ClassPool pool, CtClass... params) {
    this.mname = mname;
    this.pool = pool;
    this.params = params;
  }
  
  public boolean accept(ClassInfo classInfo, ClassFinder classFinder) {
    if (classInfo != null) {
      String className = classInfo.getClassName();
      try {
        CtClass clz = this.pool.get(className);
        CtMethod targetM = null;
        try {
          targetM = clz.getDeclaredMethod(this.mname, this.params);
        } catch (Exception exception) {}
        return (targetM != null && (targetM.getModifiers() & 0x400) == 0);
      } catch (Exception e) {
        e.printStackTrace();
      } 
    } 
    return false;
  }
}
