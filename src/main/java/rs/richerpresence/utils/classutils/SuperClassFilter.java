package rs.richerpresence.utils.classutils;

import javassist.ClassPool;
import javassist.CtClass;
import org.clapper.util.classutil.ClassFilter;
import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassInfo;
import org.jetbrains.annotations.NotNull;

public class SuperClassFilter implements ClassFilter {
  private final Class<?> superclass;
  
  private final String superClassName;
  
  private final ClassPool pool;
  
  public SuperClassFilter(@NotNull Class<?> superclass, ClassPool pool) {
    this.superclass = superclass;
    this.superClassName = superclass.getName();
    this.pool = pool;
  }
  
  public boolean accept(ClassInfo classInfo, ClassFinder classFinder) {
    if (Object.class.getName().equals(this.superClassName))
      return true; 
    if (classInfo != null) {
      String className = classInfo.getClassName();
      if (!this.superClassName.equals(className) && !Object.class.getName().equals(className))
        try {
          CtClass clz = this.pool.get(className);
          CtClass baseSuperClz = this.pool.get(this.superClassName);
          return (!clz.getName().equals(baseSuperClz.getName()) && clz.subclassOf(baseSuperClz));
        } catch (Exception e) {
          e.printStackTrace();
        }  
    } 
    return false;
  }
}
