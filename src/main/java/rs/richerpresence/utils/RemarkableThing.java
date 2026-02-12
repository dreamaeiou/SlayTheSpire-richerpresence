package rs.richerpresence.utils;

public class RemarkableThing {
  public static final int CARD_UPGRADE = 0;
  
  public static final RemarkableThing UPGRADING_CARD = new RemarkableThing(null, 0);
  
  public Object remark;
  
  public final int desc;
  
  public RemarkableThing(Object remark, int desc) {
    this.remark = remark;
    this.desc = desc;
  }
  
  public RemarkableThing setRemark(Object remark) {
    this.remark = remark;
    return this;
  }
}
