package pw.yumc.MiaoLib.kit;

import java.security.MessageDigest;

public class HashKit {

    private static java.security.SecureRandom random = new java.security.SecureRandom();

    /**
     * 生成种子
     * <p>
     * md5 128bit 16bytes
     * <p>
     * sha1 160bit 20bytes
     * <p>
     * sha256 256bit 32bytes
     * <p>
     * sha384 384bit 48bites
     * <p>
     * sha512 512bit 64bites
     * <p>
     *
     * @param numberOfBytes
     *            数字比特
     * @return 种子字串
     */
    public static String generateSalt(int numberOfBytes) {
        byte[] salt = new byte[numberOfBytes];
        random.nextBytes(salt);
        return toHex(salt);
    }

    /**
     * 字符串加密
     *
     * @param algorithm
     *            算法
     * @param srcStr
     *            字符串
     * @return 加密后的字符串
     */
    public static String hash(String algorithm, String srcStr) {
        try {
            StringBuilder result = new StringBuilder();
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return merge(result, md.digest(srcStr.getBytes("utf-8"))).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * MD5加密
     *
     * @param srcStr
     *            字符串
     * @return 加密后的字符串
     */
    public static String md5(String srcStr) {
        return hash("MD5", srcStr);
    }

    /**
     * sha1加密
     *
     * @param srcStr
     *            字符串
     * @return 加密后的字符串
     */
    public static String sha1(String srcStr) {
        return hash("SHA-1", srcStr);
    }

    /**
     * sha256加密
     *
     * @param srcStr
     *            字符串
     * @return 加密后的字符串
     */
    public static String sha256(String srcStr) {
        return hash("SHA-256", srcStr);
    }

    /**
     * sha384加密
     *
     * @param srcStr
     *            字符串
     * @return 加密后的字符串
     */
    public static String sha384(String srcStr) {
        return hash("SHA-384", srcStr);
    }

    /**
     * sha512加密
     *
     * @param srcStr
     *            字符串
     * @return 加密后的字符串
     */
    public static String sha512(String srcStr) {
        return hash("SHA-512", srcStr);
    }

    /**
     * Byte转字符串
     *
     * @param bytes
     *            Byte数组
     * @return 字符串
     */
    private static String toHex(byte[] bytes) {
        return merge(new StringBuilder(), bytes).toString();
    }

    private static StringBuilder merge(StringBuilder result, byte[] bytes) {
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                result.append("0");
            }
            result.append(hex);
        }
        return result;
    }
}
