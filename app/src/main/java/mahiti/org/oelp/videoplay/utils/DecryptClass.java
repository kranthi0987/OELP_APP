package mahiti.org.oelp.videoplay.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class DecryptClass {

    private static final String TAG = " Decrypt ";
    private String sbAl = "";
    public String fileName;
    public String inputFolderName;
    private File fileToDecrypt;
    private final char M3 = 'E';
    int finalAsciiVal = 0;
    int finalVal = 0;
    private static final String AN_STR1 = "ABCDEFGHIJKLMNOPQRST";
    private static final String AN_STR3 = "lkjihgfedcbaZYXWVUTS";
    private static final String AN_STR5 = "cdefghijklmnopqrstuv";
    private static final String AN_STR7 = "3210";
    private static OutputStream outputStream = null;
    private String fname;
    private StringBuilder sbkey;
    private File mFileTemp;
    private String outPutPath;
    private File decryptedFile;
    private String encSym = "_E";
    Context context;
    private int chArr = 124;
    private StringBuilder builder;
    private static final char METHOD1 = 'A';

    public DecryptClass(Context mainActivity, String srcPath, String srcFolder) {
        this.context = mainActivity;
        this.fileName = srcPath;
        this.inputFolderName = srcFolder;
    }


    public void getAscii() {

        try {
            sbAl = String.valueOf(METHOD1);
            finalVal = 0;
            finalAsciiVal = 0;
            System.out.println(" Input FilePath : " + fileName);
            fileToDecrypt = new File(fileName);

            char[] fileNameArr = (getFileNameWOExtension(fileToDecrypt.getName())).toCharArray();
            int len = fileNameArr.length;
            int[] storeAscii = new int[len];
            for (int i = 0; i < len; i++) {
                storeAscii[i] = (int) fileNameArr[i];

            }

            for (int i = 0; i < len; i++) {
                finalAsciiVal = (finalAsciiVal + (int) fileNameArr[i]);
            }


            char[] finVal = String.valueOf(finalAsciiVal).toCharArray();

            for (int j = 0; j < finVal.length; j++) {
                finalVal = finalVal + Integer.parseInt(String.valueOf(finVal[j]));
            }


            getAlphaNumeric();
            buildFinal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFileNameWOExtension(String fName) { // gets filename without extension
        int dot = fName.lastIndexOf(".");

        //int sep = fullPath.lastIndexOf(pathSeparator);
        if (dot != -1) {
            Log.i(TAG, " getFileName name with _E:" + fName);
            fName = fName.substring(0, dot);
            if (fName.endsWith(encSym))
                fName = fName.substring(0, dot - 2);
            //fName.replace(encSym,"");
            //Log.i(TAG, " getFileName name withOUT _E:" + fName);
            return fName;
        } else {
            return fName;
        }
    }

    private void getAlphaNumeric() {

        String AN_STR2 = "UVWXYZzyxwvutsrqponm";
        String AN_STR4 = "RQPONMLKJIHGFEDCBAab";
        String AN_STR6 = "wxyz0123456789987654";

        builder = new StringBuilder();
        builder.append(AN_STR7);
        builder.append(AN_STR6);
        builder.append(AN_STR5);
        builder.append(AN_STR4);
        builder.append(AN_STR3);
        builder.append(AN_STR2);
        builder.append(AN_STR1);


        //System.out.println("rand string : " + builder);
    }

    private void buildFinal() {


        int count = 0;
        sbAl = sbAl + M3;
        //System.out.println( "final SBAL : "+sbAl);
        sbkey = new StringBuilder();
        sbkey.append(builder.charAt(finalVal));
        count++;
        int noOfOddSkips = finalVal % 10;
        int noOfEvenSkips = finalVal / 10;


        int dskip = finalVal;

        for (int i = 0; i < 15; i++) {
            if (count % 2 != 0) {
                if ((dskip + noOfOddSkips) >= chArr) {
                    dskip = noOfOddSkips - (124 - dskip);
                } else {
                    dskip = dskip + noOfOddSkips;
                }
                sbkey.append(builder.charAt(dskip));
            } else {
                if ((dskip + noOfEvenSkips) >= chArr) {
                    dskip = noOfEvenSkips - (124 - dskip);
                } else {
                    dskip = dskip + noOfEvenSkips;
                }
                sbkey.append(builder.charAt(dskip));

            }
            count++;
        }
        //Toast.makeText(context, " Decrypt buildFinal() Toast 5  after updateDB ",Toast.LENGTH_SHORT).show();
    }


    public String getFile() {

        try {
            String path = fileName;

            if (path == null || path.isEmpty())
                return "";


            mFileTemp = new File(fileName);
            // add_e(mFileTemp.getName());
            fname = mFileTemp.getName();
            fname = remove_e(fname);
            //createDir(getOpPath(mFileTemp)+"/"+folderName);
            //outPutPath = mFileTemp.getParent()+File.separator+fname; ///Encrpyted/

            outPutPath = getCache() + File.separator + fname; ///Encrpyted
            System.out.println(" Output file path :  " + outPutPath);
            System.out.println("Please wait...");
            decryptedFile = new File(outPutPath);

            if (!decryptedFile.exists()) {
                decryptedFile.createNewFile();
                //Toast.makeText(context,"created decrypt file",Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(context,"created decrypt file",Toast.LENGTH_LONG).show();
            }


            decryptFile(sbkey.toString(), mFileTemp, decryptedFile);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        //Toast.makeText(context, " Decrypt getFIle() Toast 5  after updateDB oPath : "+outPutPath,Toast.LENGTH_SHORT).show();
        return outPutPath;

    }

    public static File getCache() {
        //if(Environment.)
        File localFile = new File(Environment.getExternalStorageDirectory().getPath() + "/.system/cache/lfdn31jkn23k/jk3rb31/kfjff1/fwjnwfkj1/wfjknwr1/wwr23232/sgkjfg233r4");
        if (!localFile.exists())
            localFile.mkdirs();
        return localFile;
    }

    private String remove_e(String sb) {

        int dot = sb.lastIndexOf(".");
        String ext = sb.substring(dot, sb.length());

        //int sep = fullPath.lastIndexOf(pathSeparator);
        //Log.i(TAG, " getFileName name with _E:" +sb);
        sb = sb.substring(0, dot);
        if (sb.endsWith(encSym))
            sb = sb.substring(0, dot - 2);
        sb = sb + ext;
        //fName.replace(encSym,"");
        //Log.i(TAG, " getFileName name withOUT _E:" +sb);
        return sb;

    }


    private void decryptFile(String eKsk, File inputFile, File outputFile) {
        sbAl = sbAl + lastInd();

        try {
            // Toast.makeText(context, " Decrypt DecryptFile() Toast 5  after updateDB ",Toast.LENGTH_SHORT).show();
            //doCrypto(Cipher.DECRYPT_MODE, eKsk, inputFile, outputFile);
            decrypt(eKsk, inputFile, outputFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private char lastInd() {
        char method2 = 'S';
        return method2;

    }


    public static void decrypt(String eKsk, File inputFile, File outputFile) throws Exception {

        FileInputStream inputStream = null;
        long startTime = Calendar.getInstance().getTimeInMillis();
        //In practice you should specify your SecureRandom implementation.
        SecureRandom rnd = new SecureRandom();
        Key sEk = new SecretKeySpec(eKsk.getBytes(), "AES");
        try {
            inputStream = new FileInputStream(inputFile); //url.openConnection().getInputStream();

            //byte[] inputBytes = new byte[(int) inputFile.length()];
            // inputStream.read(inputBytes);

            //byte[] outputBytes = cipher.doFinal(inputBytes);
            //Generate random IV of 128-bit (AES block size)
            byte[] IV = new byte[128 / 8];
            //rnd.nextBytes(IV);
            IvParameterSpec IVSpec = new IvParameterSpec(IV);

            // Logger.logD("Decrypt "," Inside decrypt Function ");
            //Create the cipher object to perform AES operations.
            //Specify Advanced Encryption Standard - Cipher Feedback Mode - No Padding
            Cipher AESCipher = Cipher.getInstance("AES/CTR/NoPadding");

            //Initialize the Cipher with the key and initialization vector.
            AESCipher.init(Cipher.DECRYPT_MODE, sEk, IVSpec);

            //Encrypts the plaintext data
            // byte[] ciphertext = AESCipher.doFinal(inputBytes);

            /*
             * The IV must now be transferred with the ciphertext somehow. The easiest
             * way to accomplish this would be to prepend the IV to the ciphertext
             * message.
             */

            //Allocate new array to hold ciphertext + IV
            //byte[] output = new byte[ciphertext.length + (128 / 8)];

            //Copy the respective parts into the array.
      /*  System.arraycopy(IV, 0, output, 0, IV.length);
        System.arraycopy(ciphertext, 0, output, IV.length, ciphertext.length);*/
            boolean res;
            if (!outputFile.exists()) {
                res = outputFile.createNewFile();
            }

            final byte[] buf = new byte[8192];
            outputStream = new CipherOutputStream(new FileOutputStream(outputFile), AESCipher);
            while (true) {
                int n = inputStream.read(buf);
                if (n == -1) break;
                outputStream.write(buf, 0, n);
            }


            //FileOutputStream outputStream = new FileOutputStream(outputFile);
            //outputStream.write(output);

            inputStream.close();
            outputStream.close();
            /*inputFile.renameTo(new File(inputFile.getPath()+(inputFile.getName().replace("s"," "))));*/
            long endTime = Calendar.getInstance().getTimeInMillis();
            long decryptTime = (endTime - startTime);

            //ger.logD(TAG,"Decrypt --->  decryptTime : "+decryptTime);
        } catch (Exception ex) {
            Logger.logE(TAG, ex.getMessage(), ex);
        } finally {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                inputStream.close();
        }

    }


   /* private void doCrypto(int cipherMode, String eKsk, File inputFile,
                          File outputFile)  {

        String tr=sbAl;

        try {
            Key sEk = new SecretKeySpec(eKsk.getBytes(), sbAl);
            Cipher cipher = Cipher.getInstance(tr);
            cipher.init(cipherMode, sEk);

            FileInputStream inputStream = new FileInputStream(inputFile); //url.openConnection().getInputStream();
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            inputStream.close();
            outputStream.close();
            Toast.makeText(context, " Decrypt docrypto() FileDecrypted ",Toast.LENGTH_SHORT).show();
            System.out.println( "File Decrypted \n\n");
            //playVideo(outputFile.toString());
            //mFileTemp.delete();
        } catch (Exception ex) {
            System.out.println( ex.getMessage());
            Toast.makeText(context,"doCrypto exception : "+ex,Toast.LENGTH_LONG).show();
        }
    }*/


}

