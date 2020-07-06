package com.cdcdata.testEx;

public class TextExpection {

    public static void main(String[] args) {
        String aaa = "aaa";
        try {
            aaa = "aaaaaaaa";
            int i = 1/0;
            String bbb = "bbb";

        }catch (Exception e){
            e.printStackTrace();
        }

        String ccc = "ccccc";

    }


}
