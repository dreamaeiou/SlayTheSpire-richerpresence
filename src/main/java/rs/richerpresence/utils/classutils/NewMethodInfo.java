package rs.richerpresence.utils.classutils;

public class NewMethodInfo {
  public final String methodName;
  
  public final int modifiers;
  
  public final Class[] paramTypes;
  
  public final Class<?> returnType;
  
  public final Class[] exceptions;
  
  public String defaultBody;
  
  public NewMethodInfo(String name, int modifiers, Class<?> returnType, Class[] exceptions, Class... paramTypes) {
    this.methodName = name;
    this.modifiers = modifiers;
    this.returnType = returnType;
    this.paramTypes = paramTypes;
    this.exceptions = exceptions;
  }
  
  public NewMethodInfo(String name, int modifiers, Class<?> returnType, Class... paramTypes) {
    this(name, modifiers, returnType, null, paramTypes);
  }
  
  public NewMethodInfo setDefaultBody(String defaultBody) {
    this.defaultBody = defaultBody;
    return this;
  }
  
  public String getReturnType() {
    return this.returnType.getName();
  }
  
  public String[] getParamTypes() {
    if (this.paramTypes == null)
      return new String[0]; 
    String[] types = new String[this.paramTypes.length];
    for (int i = 0; i < this.paramTypes.length; i++)
      types[i] = this.paramTypes[i].getName(); 
    return types;
  }
  
  public String[] getExceptions() {
    if (this.exceptions == null)
      return new String[0]; 
    String[] types = new String[this.exceptions.length];
    for (int i = 0; i < this.exceptions.length; i++)
      types[i] = this.exceptions[i].getName(); 
    return types;
  }
}
