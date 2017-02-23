package cn.net.cc.weibo.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by donghj on 16-6-24.
 */
public class MD5Sum {
    // something around 356000 bytes...
    // < 308,032 bytes
    // public static long SCOUR_MD5_BYTE_LIMIT = 10000;
    public static int SCOUR_MD5_BYTE_LIMIT = Integer.MAX_VALUE;
    private static MessageDigest md = null;
    private static final String TAG = "MD5Sum";

    /**
     * Method: md5Sum Purpose: calculate the MD5 in a way compatible with how
     * the scour.net protocol encodes its passwords (incidentally, it also
     * outputs a string identical to the md5sum unix command).
     *
     * @param str the String from which to calculate the sum
     * @return the MD5 checksum
     */
    public static String md5Sum(String str) {
        try {
            return md5Sum(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public static String md5Sum(byte[] input) {
        return md5Sum(input, -1);
    }

    public static String md5Sum(byte[] input, int limit) {
        try {
            if (md == null)
                md = MessageDigest.getInstance("MD5");

            md.reset();
            byte[] digest;

            if (limit == -1) {
                digest = md.digest(input);
            } else {
                md.update(input, 0,
                        limit > input.length ? input.length : limit);
                digest = md.digest();
            }

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < digest.length; i++) {
                hexString.append(hexDigit(digest[i]));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Method: hexDigit Purpose: convert a hex digit to a String, used by
     * md5Sum.
     *
     * @param x the digit to translate
     * @return the hex code for the digit
     */
    static private String hexDigit(byte x) {
        StringBuffer sb = new StringBuffer();
        char c;

        // First nibble
        c = (char) ((x >> 4) & 0xf);
        if (c > 9) {
            c = (char) ((c - 10) + 'a');
        } else {
            c = (char) (c + '0');
        }

        sb.append(c);

        // Second nibble
        c = (char) (x & 0xf);
        if (c > 9) {
            c = (char) ((c - 10) + 'a');
        } else {
            c = (char) (c + '0');
        }

        sb.append(c);
        return sb.toString();
    }

    /**
     * Method: getFileMD5Sum Purpose: get the MD5 sum of a file. Scour exchange
     * only counts the first SCOUR_MD5_BYTE_LIMIT bytes of a file for
     * caclulating checksums (probably for efficiency or better comaprison
     * counts against unfinished downloads).
     *
     * @param f the file to read
     * @return the MD5 sum string
     * @throws IOException on IO error
     */
    public String getFileMD5SumObsoleted(File f)
            throws IOException {
        String sum = null;
        // Log.i(TAG, "f absolutepath: " + f.getAbsolutePath());
        FileInputStream in = new FileInputStream(f.getAbsolutePath());

        byte[] b = new byte[1024];
        int num = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

        while ((num = in.read(b)) != -1) {
            out.write(b, 0, num);

            if (out.size() > SCOUR_MD5_BYTE_LIMIT) {
                sum = md5Sum(out.toByteArray(), SCOUR_MD5_BYTE_LIMIT);
                break;
            }
        }

        if (sum == null)
            sum = md5Sum(out.toByteArray(), SCOUR_MD5_BYTE_LIMIT);

        in.close();
        out.close();

        return sum;
    }

    public String getFileMD5Sum(File f) throws IOException {
        Log.i(TAG, "f absolutepath: " + f.getAbsolutePath());

        FileInputStream in = new FileInputStream(f.getAbsolutePath());
        byte[] b = new byte[32 * 1024];
        int num = 0;
        byte[] digest = new byte[64];
        if (md == null)
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        md.reset();

        while ((num = in.read(b)) != -1) {
            md.update(b, 0, num);
        }
        digest = md.digest();
        StringBuffer hexString = new StringBuffer();

        for (int k = 0; k < digest.length; k++) {
            hexString.append(hexDigit(digest[k]));
        }
        in.close();
        return hexString.toString();

    }

}

