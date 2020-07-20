package com.smsapp.mylokohack;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class FloatingWindow extends Service {

    private static final String[] generalwords = {"to", "of", "What", "what", "is",
            "the", "MRP", "Complete", "you", "You", "have", "this", "buy", "a", "?", "will", "need","According"};
    FrameLayout mLayout;
    Button getAnswer;
    ArrayList<String> options = new ArrayList<>();
    String question;
    TextView optionA, optionB, optionC, probableAnswer;
    TextView type;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64)";

    String gameType;

    String subtype="";
    private WindowManager wm;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            gameType = intent.getStringExtra("gameType");
            type = mLayout.findViewById(R.id.type);

            subtype = intent.getStringExtra("subType");

            type.setText(gameType+subtype);

        }
        return START_STICKY;
    }


    @Override
    public void onCreate() {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        mLayout = new FrameLayout(this);
        LinearLayout.LayoutParams layoutParameteres = new LinearLayout.LayoutParams(
                800, 400);
        mLayout.setLayoutParams(layoutParameteres);

        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(
                500, 200, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        parameters.gravity = Gravity.LEFT | Gravity.TOP;
        parameters.x = 0;
        parameters.y = 0;


        ViewGroup.LayoutParams btnParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LayoutInflater inflater = LayoutInflater.from(this);
        final View mview = inflater.inflate(R.layout.floatingwindow, mLayout);

        wm.addView(mLayout, parameters);
        Button stop = mLayout.findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(mLayout);
                stopSelf();
                System.exit(0);
            }
        });

        getAnswer = mLayout.findViewById(R.id.getanswer);
        optionA = mLayout.findViewById(R.id.optionA);
        optionB = mLayout.findViewById(R.id.optionB);
        optionC = mLayout.findViewById(R.id.optionC);


        getAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "onClick: get answered clicked");
                mLayout.setVisibility(View.INVISIBLE);
                final String name = RandomStringUtils.randomAlphanumeric(5);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        testscreenshot(name);
                        findQuestionandAnswer(name);
                        mLayout.setVisibility(View.VISIBLE);
                    }
                }, 0000);


            }
        });

    }

    void testscreenshot(String name) {
        Process sh = null;
        try {

            sh = Runtime.getRuntime().exec("su", null, null);
            OutputStream os = sh.getOutputStream();
            os.write(("/system/bin/screencap -p " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + gameType + "/" + name + ".jpg").getBytes("ASCII"));
            os.flush();
            os.close();
            sh.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    private void findQuestionandAnswer(String name) {
        options.clear();

        String img_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + gameType + "/" + name + ".jpg";
        File image = new File(img_path);
        inspect(image);

        findAnswer();

    }


    private void findAnswer() {

        String A = "option A", B = "option B", C = "option c";

        optionA.setTextColor(Color.BLACK);
        optionB.setTextColor(Color.BLACK);
        optionC.setTextColor(Color.BLACK);

        if (null != subtype && subtype.equals("price") && gameType.equals("loco")) {
            searchprice();
        }
        else {

        if (options.size() > 2) {
            A = (options.get(0));
            B = (options.get(1));
            C = (options.get(2));
        }


            Answer ans = new Answer(question, A, B, C);
            FindAnswers obj = new FindAnswers();
            Answer ret = obj.googleSearch(ans);


            A = ret.getCountofA().toString();
            B = ret.getCountofB().toString();
            C = ret.getCountofC().toString();

            optionA.setText(A);
            optionB.setText(B);
            optionC.setText(C);

            String max = ret.getMax();

            if (max.equals("optionA"))
                optionA.setTextColor(Color.RED);

            else if (max.equals("optionB"))
                optionB.setTextColor(Color.RED);
            else
                optionC.setTextColor(Color.RED);
        }


    }

    public void searchprice() {
        Thread t1 = null;
        Thread t2 = null;
       final StringBuffer sb=new StringBuffer();
        final List<String> words=new ArrayList<>();
        Log.i(TAG, "question." +question);
        for (String word : question.split("\\s+")) {
            if(!Arrays.asList(generalwords).contains(word))
                words.add(word);
        }
        Log.i(TAG, "the words"+words);
        t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "Starting T1 Pricee.");
                sb.append(pricee(words));
                Log.i(TAG, "Finishing T1.");
            }
        });

        t1.start();
        try {
            t1.join();
            optionA.setText(sb.toString());
        }
        catch(InterruptedException ex)
        {
            Log.i(TAG, "some error in updation");
        }



        t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "Starting T2 Google.");

                Log.i(TAG, "Finishing T2.");
            }
        });
    }

    String pricee(List<String> keywords)
    {
        StringBuffer url=new StringBuffer("https://pricee.com/api/v1/search.php?q=");
        url.append(keywords);
        url.append("&size=").append(2).append("&lang=en&vuid=0&prev_id=3993568");
        try {
            String json = Jsoup.connect(url.toString()).ignoreContentType(true).execute().body();
            JSONObject jSONObject = new JSONObject(json);
            JSONArray data = jSONObject.getJSONArray("data");
            int prevcount=-1;
            int index=0;
            for (int i = 0; i < data.length(); i++) {
                int count=0;
                JSONObject item = data.getJSONObject(i);
                Log.i(TAG, item.toString());
                String name=item.get("title").toString();

                for (String word : name.split("\\s+")) {
                    if(keywords.contains(word))
                        count++;

                }
                if(prevcount==-1)
                    prevcount=count;
                else if(prevcount<count)
                {
                    prevcount=count;
                    index=i;
                }



            }
            int discountedPriced=0;
            int discounted=0;
            JSONObject finalItem = data.getJSONObject(index);

            String discountedPrice=finalItem.get("source_price").toString().replaceAll(",","");
            String discount=finalItem.get("discount").toString().replaceAll(",","");

            if(!discountedPrice.equals(""))
             discountedPriced =Integer.valueOf(discountedPrice);
            if(!discount.equals(""))
                discounted =Integer.valueOf(discount);

            int finalprice=discountedPriced+discountedPriced*discounted/100;

            return "pricee -"+finalItem.get("title").toString()+" "+ finalprice;




        }
        catch (IOException|JSONException ex)
        {
            Log.i(TAG, "error in Pricee.");
        }

        return "sorry";


    }
    private void inspect(File file) {

        InputStream is = null;
        Bitmap bitmap = null;
        try {

            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            inspectFromBitmap(convertColorIntoBlackAndWhiteImage(bitmap));
        } catch (Exception e) {
            Log.w(TAG, "Failed to find the file: " + e);
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.w(TAG, "Failed to close InputStream", e);
                }
            }
        }

    }

    private void inspectFromBitmap(Bitmap bitmap) {
        options.clear();
        question = "";
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        try {
            if (!textRecognizer.isOperational()) {
                new AlertDialog.
                        Builder(this).
                        setMessage("Text recognizer could not be set up on your device").show();
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
            }
            Collections.sort(textBlocks, new Comparator<TextBlock>() {
                @Override
                public int compare(TextBlock o1, TextBlock o2) {
                    int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                    int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                    if (diffOfTops != 0) {
                        return diffOfTops;
                    }
                    return diffOfLefts;
                }
            });

            StringBuilder detectedText = new StringBuilder();


            if (gameType.equals("bb")) {
                String block="";
                int j = textBlocks.size() - 1;

                block=textBlocks.get(j).getValue();
                if(block.contains("\n"))
                {
                    String splitblock[]=block.split("\n");
                    for(String option:splitblock)
                        options.add(option);
                    block=textBlocks.get(j - 1).getValue();
                    question= block.replace("\n", " ").replace("\"", "").toString() +" ";
                }
                    else{
                    options.add(textBlocks.get(j).getValue());
                    options.add(textBlocks.get(j - 1).getValue());
                    options.add(textBlocks.get(j - 2).getValue());
                    Collections.reverse(options);


                    for(int k=0;k< textBlocks.size()-3;k++) {
                        block=textBlocks.get(k).getValue();
                        if(block.length()>11) {
                            question += block.replace("\n", " ").replace("\"", "").toString() +" ";

                        }
                    }
                }




                if(question.contains("Which of these") || question.contains("Which of the following"))
                    question+=" among "+options.get(0)+","+options.get(1)+","+options.get(2);

            } else if (gameType.equals("loco")) {
                String block="";
                boolean iterate=false;
                int j = textBlocks.size() - 1;

                options.add(textBlocks.get(j).getValue());
                options.add(textBlocks.get(j-1).getValue());
                options.add(textBlocks.get(j-2).getValue());
                Collections.reverse(options);


                for(int k=0;k< textBlocks.size()-3;k++) {
                     block=textBlocks.get(k).getValue();
                     if(block.equals("ELIMINATED") || (block.contains("Win") && block.length()<=10)) {
                         iterate = true;
                         continue;
                     }
                    if(block.contains("Q") || iterate) {
                        iterate=true;
                        question += block.replace("\n", " ").replace("\"", "").toString() +" ";


                    }
                }

                int index = question.indexOf(".");
                question = question.substring(index + 1, question.length());

                if(question.contains("Which of these") || question.contains("Which of the following"))
                    question+=" among "+options.get(0)+","+options.get(1)+","+options.get(2);
                }
            else if (gameType.equals("hq")) {
                {
                    int i = 0;
                    for (int j = textBlocks.size() - 2; j > 0; j--) {
                        i++;
                        if (i <= 4) {
                            if (i <= 3) {
                                options.add(textBlocks.get(j).getValue());
                            } else {
                                question = textBlocks.get(j).getValue().replace("\n", " ").toString();


                            }
                        } else
                            break;
                    }
                }


            }


        } finally {
            textRecognizer.release();
        }

    }

    public void setQuestionandAnsForBB(String questionandAnsForBB) {
        try {
            String[] splitText = questionandAnsForBB.split("\\?");

            question = splitText[0].substring(splitText[0].indexOf("K") + 1);

            question = question.replaceFirst(Pattern.quote("ELIMINATED"), " ");

            question = question.replaceAll("\\n", " ");

            String[] optionText = splitText[1].split("\\r?\\n");

            for (String option : optionText) {
                if (!option.isEmpty())
                    options.add(option);
            }
            Collections.reverse(options);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    public void takeScreenshot(String name) {
//        Window window = getWindow();
//        View rootView = window.getDecorView(); //Get Root View
//        rootView.setDrawingCacheEnabled(true);
//         saveBitmap(rootView.getDrawingCache(),name);
//    }

    public void saveBitmap(Bitmap bitmap, String name) {
        File imagePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + name + ".jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wm.removeView(mLayout);
        stopSelf();
        System.exit(0);
        stopSelf();
    }

    private Bitmap convertColorIntoBlackAndWhiteImage(Bitmap orginalBitmap) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);

        Bitmap blackAndWhiteBitmap = orginalBitmap.copy(
                Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setColorFilter(colorMatrixFilter);

        Canvas canvas = new Canvas(blackAndWhiteBitmap);
        canvas.drawBitmap(blackAndWhiteBitmap, 0, 0, paint);

        return blackAndWhiteBitmap;
    }
}
