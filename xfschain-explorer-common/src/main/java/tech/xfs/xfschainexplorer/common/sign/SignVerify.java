package tech.xfs.xfschainexplorer.common.sign;

public interface SignVerify {
    boolean verify(String target);
    String getSign();
}
