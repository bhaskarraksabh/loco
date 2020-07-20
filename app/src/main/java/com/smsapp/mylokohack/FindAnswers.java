package com.smsapp.mylokohack;

import android.os.StrictMode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FindAnswers {
    private static final String GOOGLE_URL = "https://www.google.com/search?q=";
    private  static final  String BING_URL="https://www.bing.com/search?q=";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64)";
    Answer returnans = new Answer();
    String currentQuestion="";

    private static final String[] generalwords={"the","a","is","with","all","and","or","of","what","which","when","whose","these","Which"
    ,"no","Where","either","Why","often","in","for"};


        final static ArrayList skipkeywords = new ArrayList();
        static {
            skipkeywords.add("the");
            skipkeywords.add("The");
            skipkeywords.add("is");
            skipkeywords.add("What");
            skipkeywords.add("of");
        }



     private int count(String subString, String string) {
        int lastIndex = 0;
        int cnt = 0;

        if(skipkeywords.contains(subString))
            return cnt;
        if(subString.length()<=2)
            return cnt;

        while (lastIndex != -1) {
            lastIndex = string.indexOf(subString, lastIndex);

            if (lastIndex != -1) {
                cnt++;
                lastIndex += subString.length();
            }
        }
        return cnt;
    }

    private int count2(String subString, String string) {
        int cnt = 0;
        Pattern p = Pattern.compile(Pattern.quote(subString));
        Matcher m = p.matcher(string);
        while (m.find())
            cnt++;
        return cnt;
    }


    public Answer googleSearch(Answer ans) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        boolean negetiveQuestion=false;

        String question=ans.getQuestion();
        for (String word : question.split("\\s+")) {
         if(word.equalsIgnoreCase("no") ||word.equalsIgnoreCase("not") || word.equalsIgnoreCase("never") )
        {
            negetiveQuestion=true;
        }
        }

       /* if(question.contains("these") || question.contains("These"))
        {
            return theseQuestionSoluton(ans);
            //question+= ": "+ans.getOptionA()+"or "+ans.getOptionB()+"or "+ans.getOptionC();
        }*/

        Document doc;
        int max = 0;
        String a = "";
        String b = "";
        String c = "";

        int countofA=0,totalCountofA=0;
        int countofB=0,totalCountofB=0;
        int countofC=0,totalCountofC=0;
        try {
            currentQuestion=ans.getQuestion();
            doc = Jsoup.connect(GOOGLE_URL + URLEncoder.encode(ans.getQuestion(), "UTF-8") + "&num=10").userAgent(USER_AGENT).get();
            String text = doc.body().text().toLowerCase();


            String optionA[] = ans.getOptionA().split(" ");
            for (String option : optionA) {
                countofA=count(option.toLowerCase().trim(), text);
                a += option + " (" +  countofA + ") ";
                totalCountofA+=countofA;
            }
            a+="= "+totalCountofA;
            String optionB[] = ans.getOptionB().split(" ");
            for (String option : optionB) {
                countofB=count(option.toLowerCase().trim(), text);
                b += option + " (" +  countofB + ") ";
                totalCountofB+=countofB;
            }

            b+="= "+totalCountofB;
            String optionC[] = ans.getOptionC().split(" ");
            for (String option : optionC) {
                countofC=count(option.toLowerCase().trim(), text);
                c += option + " (" +  countofC + ") ";
                totalCountofC+=countofC;
            }
            c+="= "+totalCountofC;

        } catch (IOException ioe) {
            ioe.printStackTrace();


        }

        if(negetiveQuestion)
            max= Math.min(Math.min
            (totalCountofA,totalCountofB),totalCountofC);
        else
        max =  Math.max(Math.max(totalCountofA,totalCountofB),totalCountofC);


        if(totalCountofA==max)
            returnans.setMax("optionA");
       else if(totalCountofB==max)
            returnans.setMax("optionB");
       else
            returnans.setMax("optionC");

        returnans.setCountofA(a);

        returnans.setCountofB(b);

        returnans.setCountofC(c);

        return returnans;


    }

    Answer theseQuestionSoluton(Answer ans)
 {
     boolean negetiveQuestion=false;
     List<String> options=new ArrayList<>();
     options.add(ans.getOptionA());
     options.add(ans.getOptionB());
     options.add(ans.getOptionC());
     int i=-1;
     int counta=0;
     int countb=0;
     int countc=0;

     String question=ans.getQuestion();
     List<String> impwords=new ArrayList<>();
     for (String word : question.split("\\s+"))

    {
        if (word.equalsIgnoreCase("no") || word.equalsIgnoreCase("not") || word.equalsIgnoreCase("never")) {
            negetiveQuestion = true;
        }
        if (!Arrays.asList(generalwords).contains(word.trim())) {
            impwords.add(word);
        }


    }

     try {
         for (String option : options) {
             int count=0;

             i++;
             Document doc = Jsoup.connect(BING_URL + URLEncoder.encode(option, "UTF-8") + "&num=1").userAgent(USER_AGENT).get();
             String text = doc.body().text().toLowerCase();

             for (String word : impwords) {

                       count+=count(word, text);
             }

             if(i==0) {
                 returnans.setCountofA(option + " (" + count + ") ");
                 counta=count;
             }
             if(i==1) {
                 returnans.setCountofB(option + " (" + count + ") ");
                 countb=count;
             }
             if(i==2) {
                 returnans.setCountofC(option + " (" + count + ") ");
                 countc=count;
             }
         }

         int max=0;
         if(negetiveQuestion)
             max= Math.min(Math.min
                     (counta,countb),countc);
         else
             max =  Math.max(Math.max(counta,countb),countc);


         if(counta==max)
             returnans.setMax("optionA");
         else if(countb==max)
             returnans.setMax("optionB");
         else
             returnans.setMax("optionC");

     }



     catch (IOException ex)
     {

     }
     return returnans;
 }

}
