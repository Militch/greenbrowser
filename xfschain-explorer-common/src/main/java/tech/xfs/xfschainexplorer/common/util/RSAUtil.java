package tech.xfs.xfschainexplorer.common.util;

import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class RSAUtil {
    private static final String KEY_ALGORITHM = "rsa";
    private static final int DEFAULT_KEY_SIZE = 2048;
    public static final String SIGNATURE_ALGORITHM_MD5_WITH_RSA = "MD5withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA256_WITH_RSA = "SHA256WithRSA";
    public static final String SIGNATURE_ALGORITHM_SHA1_WITH_RSA = "SHA1WithRSA";

    public static final String PEM_FILE_PUBLIC_PKCS1_BEGIN = "-----BEGIN PUBLIC KEY-----";
    public static final String PEM_FILE_PUBLIC_PKCS1_END = "-----END PUBLIC KEY-----";
    public static final String PEM_FILE_PRIVATE_PKCS1_BEGIN = "-----BEGIN RSA PRIVATE KEY-----";
    public static final String PEM_FILE_PRIVATE_PKCS1_END = "-----END RSA PRIVATE KEY-----";
    public static final String PEM_FILE_PRIVATE_PKCS8_BEGIN = "-----BEGIN PRIVATE KEY-----";
    public static final String PEM_FILE_PRIVATE_PKCS8_END = "-----END PRIVATE KEY-----";

    public static String resolvePublicKey(String content) throws Exception {
        return resolveContentBody(content,PEM_FILE_PUBLIC_PKCS1_BEGIN,PEM_FILE_PUBLIC_PKCS1_END);
    }
    private static List<String> resolveContentLines(String content){
        String[] lines = content.split("(\r\n|\n)");
        List<String> lineList = Arrays.asList(lines);
        return lineList.stream().filter(item-> !StringUtils.isEmpty(item)).collect(Collectors.toList());
    }
    private static String resolveContentBody(String content, String beginLine, String endLine) throws Exception {
        List<String> contentLine = resolveContentLines(content);
        int beginLineIndex = ListUtil.findIndex(contentLine,beginLine);
        int endLineIndex = ListUtil.findIndex(contentLine,endLine);
        boolean not = beginLineIndex == -1 || endLineIndex == -1;
        if (not || beginLineIndex >= endLineIndex)
            throw new Exception("??????????????????");
        StringBuilder sb = new StringBuilder();
        for (int i=beginLineIndex+1; i < endLineIndex; i++){
            sb.append(contentLine.get(i));
        }
        return sb.toString();
    }
    public static String resolvePrivateKey(String content) throws Exception {
        return resolveContentBody(content,PEM_FILE_PRIVATE_PKCS8_BEGIN,PEM_FILE_PRIVATE_PKCS8_END);
    }

    public static String formatKeyPem(String beginLine, String key,String endLine){
        if (key == null || key.length() == 0) return null;
        byte[] bs = key.getBytes();
        StringBuilder sb = new StringBuilder();
        sb.append(beginLine).append("\r\n");
        for (int i=0; i<bs.length;i++){
            char c = (char) bs[i];
            sb.append(i%64 == 0 && i > 0?"\r\n":"").append(c);
        }
        sb.append("\r\n").append(endLine);
        return sb.toString();
    }
    /**
     * ?????? 1024 ??????????????????
     * @return ?????????
     */
    public static KeyPair generateKeyPair(){
        return generateKeyPair(DEFAULT_KEY_SIZE);
    }


    public static String getPrivateKey(KeyPair keyPair){
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return (Base64.getEncoder().encodeToString(bytes));
    }

    public static String getPublicKey(KeyPair keyPair){
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return (Base64.getEncoder().encodeToString(bytes));
    }
    /**
     * ??????????????????????????????
     * @param keySize ??????
     * @return ?????????
     */
    public static KeyPair generateKeyPair(int keySize){
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(keySize);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyPair;
    }

    /**
     * ????????????
     * @param data ????????????
     * @param privateKey ??????
     * @param algorithm ????????????
     * @return ???????????????
     */
    public static String sign(byte[] data, String privateKey, String algorithm){
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        // ?????? PKCS#8 ????????????
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            // ?????? RSA ??????????????????
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            // ??????????????????
            PrivateKey privateK = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            // ???????????????????????????????????????
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateK);
            signature.update(data);
            byte[] bytes = signature.sign();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * ????????????-SHA256WithRSA
     * @param data ????????????
     * @param privateKey ??????
     * @return ???????????????
     */
    public static String sign(byte[] data, String privateKey){
        return sign(data,privateKey,SIGNATURE_ALGORITHM_SHA256_WITH_RSA);
    }
    /**
     * ????????????-??????
     * @param data ???????????????
     * @param publicKey ??????
     * @param sign ??????
     * @param algorithm ????????????
     * @return ??????
     */
    public static boolean verify(byte[] data, String publicKey, String sign, String algorithm){
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        // ?????? X509 ????????????
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            // ?????? RSA ??????????????????
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            // ??????????????????
            PublicKey publicK = keyFactory.generatePublic(x509EncodedKeySpec);
            // ????????????????????????????????????
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(publicK);
            signature.update(data);
            // ?????????????????????????????? BASE64 ?????????????????????????????????
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
//            e.printStackTrace();
            return false;
        }
//        return false;
    }

    /**
     * ????????????-??????-SHA256WithRSA
     * @param data ???????????????
     * @param publicKey ??????
     * @param sign ??????
     * @return ??????
     */
    public static boolean verify(byte[] data, String publicKey, String sign){
        return verify(data,publicKey,sign,SIGNATURE_ALGORITHM_SHA256_WITH_RSA);
    }
    /**
     * ????????????-??????
     * @param data ???????????????
     * @param cer ??????
     * @param sign ??????
     * @return ??????
     */
    public static boolean verifyWithCertificate(byte[] data, String cer, String sign,String algorithm){
        String publicKey = parsePublicKeyWithCer(cer);
        return verify(data,publicKey,sign,algorithm);
    }
    /**
     * ????????????-??????-SHA256WithRSA
     * @param data ???????????????
     * @param cer ??????
     * @param sign ??????
     * @return ??????
     */
    public static boolean verifyWithCertificate(byte[] data, String cer, String sign){
        String publicKey = parsePublicKeyWithCer(cer);
        return verify(data,publicKey,sign,SIGNATURE_ALGORITHM_SHA256_WITH_RSA);
    }
    /**
     * ?????????????????????????????????
     * @param cer ???????????????
     * @return ??????
     */
    private static String parsePublicKeyWithCer(String cer){
        byte[] cerBytes = Base64.getDecoder().decode(cer);
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateFactory.generateCertificate(new ByteArrayInputStream(cerBytes));
            PublicKey publicKey = certificate.getPublicKey();
            return Base64.getEncoder().encodeToString(publicKey.getEncoded());
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }
}
