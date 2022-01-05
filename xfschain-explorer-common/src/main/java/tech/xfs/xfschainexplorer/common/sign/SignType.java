package tech.xfs.xfschainexplorer.common.sign;

public interface SignType {
    SignVerify sign(String credential);
    String sign2(String credential);
}
