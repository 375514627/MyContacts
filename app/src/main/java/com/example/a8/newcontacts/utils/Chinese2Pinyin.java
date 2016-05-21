package com.example.a8.newcontacts.utils;

/**
 * Created by A8 on 2016/5/14.
 */
public class Chinese2Pinyin {

    private static String output;
    private static byte[] bytes;

    public static String getPingYing(String input) {
        int heightByte;
        int lowByte;
        int china_int;
        String PYSX = "";
        char first = input.charAt(0);
        if ((first >= 'a' && first <= 'z') || (first >= 'A' && first <= 'Z')) {
            return (first + "").toUpperCase();
        }

        for (int i = 0; i < input.length(); i++) {
            bytes = input.substring(i, i + 1).getBytes();
            if (bytes.length == 2) {
                heightByte = ((short) (bytes[0] & (byte) 127)) + (short) 128;
                lowByte = ((short) (bytes[1] & (byte) 127)) + (short) 128;
                china_int = heightByte * 256 + lowByte;
                //System.out.println("china_int="+china_int);
                if (china_int >= 45217 && china_int <= 45252) {
                    PYSX += "a";
                } else if (china_int >= 45253 && china_int <= 45760) {
                    PYSX += "b";
                } else if (china_int >= 45761 && china_int <= 46317) {
                    PYSX += "c";
                } else if (china_int >= 46318 && china_int <= 46825) {
                    PYSX += "d";
                } else if (china_int >= 46826 && china_int <= 47009) {
                    PYSX += "e";
                } else if (china_int >= 47010 && china_int <= 47296) {
                    PYSX += "f";
                } else if (china_int >= 47297 && china_int <= 47613) {
                    PYSX += "g";
                } else if (china_int >= 47614 && china_int <= 48118) {
                    PYSX += "h";
                } else if (china_int >= 48119 && china_int <= 49061) {
                    PYSX += "j";
                } else if (china_int >= 49062 && china_int <= 49323) {
                    PYSX += "k";
                } else if (china_int >= 49324 && china_int <= 49895) {
                    PYSX += "l";
                } else if (china_int >= 49896 && china_int <= 50370) {
                    PYSX += "m";
                } else if (china_int >= 50371 && china_int <= 50613) {
                    PYSX += "n";
                } else if (china_int >= 50614 && china_int <= 50621) {
                    PYSX += "o";
                } else if (china_int >= 50622 && china_int <= 50905) {
                    PYSX += "p";
                } else if (china_int >= 50906 && china_int <= 51386) {
                    PYSX += "q";
                } else if (china_int >= 51387 && china_int <= 51445) {
                    PYSX += "r";
                } else if (china_int >= 51446 && china_int <= 52217) {
                    PYSX += "s";
                } else if (china_int >= 52218 && china_int <= 52697) {
                    PYSX += "t";
                } else if (china_int >= 52698 && china_int <= 52979) {
                    PYSX += "w";
                } else if (china_int >= 52980 && china_int <= 53640) {
                    PYSX += "x";
                } else if (china_int >= 53689 && china_int <= 54480) {
                    PYSX += "y";
                } else if (china_int >= 54481 && china_int <= 55289) {
                    PYSX += "z";
                }
            } else {
                PYSX += new String(bytes);
            }
        }
        return PYSX.toUpperCase();
    }

}
