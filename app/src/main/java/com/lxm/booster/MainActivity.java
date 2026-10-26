package com.lxm.booster;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.net.InetAddress;

public class MainActivity extends Activity {
    final int bg = Color.rgb(10, 9, 18), panel = Color.rgb(24, 20, 38), purple = Color.rgb(157, 92, 255);
    TextView status, ping, net;
    Button test;
    int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + 0.5f); }

    GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }
    TextView label(String s, int size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        return t;
    }
    void pad(View v, int l, int t, int r, int b) { v.setPadding(dp(l),dp(t),dp(r),dp(b)); }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(bg); getWindow().setNavigationBarColor(bg);
        LinearLayout root = new LinearLayout(this); root.setOrientation(1); root.setBackgroundColor(bg);
        pad(root, 22, 22, 22, 16);
        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this); content.setOrientation(1);
        TextView brand = label("LXM  BOOSTER", 25, Color.WHITE); brand.setTypeface(null, Typeface.BOLD);
        content.addView(brand);
        TextView sub = label("GAME NETWORK • LOCAL TOOLS", 11, purple); pad(sub,0,5,0,22); content.addView(sub);

        LinearLayout hero = new LinearLayout(this); hero.setOrientation(1); hero.setGravity(Gravity.CENTER);
        hero.setBackground(shape(panel,22)); pad(hero,18,22,18,22);
        TextView icon = label("◉", 46, purple); hero.addView(icon);
        TextView game = label("PUBG MOBILE", 20, Color.WHITE); game.setTypeface(null,Typeface.BOLD); pad(game,0,8,0,4); hero.addView(game);
        TextView mode = label("NETWORK DIAGNOSTICS", 11, Color.LTGRAY); hero.addView(mode);
        content.addView(hero, new LinearLayout.LayoutParams(-1, dp(170)));

        LinearLayout cards = new LinearLayout(this); cards.setOrientation(LinearLayout.HORIZONTAL); cards.setPadding(0,dp(16),0,dp(16));
        LinearLayout pCard = card("PING CHECK", "— ms");
        ping = (TextView)pCard.getChildAt(1);
        LinearLayout nCard = card("NETWORK", "Not checked");
        net = (TextView)nCard.getChildAt(1);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(0,-2,1); cp.setMargins(0,0,dp(8),0);
        cards.addView(pCard,cp); LinearLayout.LayoutParams cp2 = new LinearLayout.LayoutParams(0,-2,1); cp2.setMargins(dp(8),0,0,0); cards.addView(nCard,cp2);
        content.addView(cards);

        test = new Button(this); test.setText("RUN NETWORK TEST"); test.setTextColor(Color.WHITE); test.setAllCaps(false);
        test.setBackground(shape(purple,15)); content.addView(test, new LinearLayout.LayoutParams(-1,dp(52)));
        status = label("Ready when you are.", 14, Color.LTGRAY); pad(status,4,16,4,10); content.addView(status);
        TextView tipTitle = label("STABILITY TIPS",16,Color.WHITE); tipTitle.setTypeface(null,Typeface.BOLD); pad(tipTitle,0,18,0,8); content.addView(tipTitle);
        TextView tips = label("• Use a stable Wi‑Fi or mobile signal\n• Pause downloads and streaming during matches\n• Keep the phone cool and close background apps\n• Choose the lowest graphics setting that stays smooth",14,Color.LTGRAY);
        tips.setLineSpacing(dp(5),1f); tips.setBackground(shape(panel,16)); pad(tips,16,16,16,16); content.addView(tips);
        TextView disclaimer = label("LXM checks your connection; it cannot guarantee lower game ping or higher FPS. No paid routing servers are included.",11,Color.GRAY);
        pad(disclaimer,4,16,4,10); content.addView(disclaimer);

        scroll.addView(content); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root);
        test.setOnClickListener(v -> runTest());
    }

    LinearLayout card(String title, String value) {
        LinearLayout box = new LinearLayout(this); box.setOrientation(1); box.setBackground(shape(panel,16)); pad(box,14,16,10,16);
        TextView a = label(title,10,Color.LTGRAY); box.addView(a);
        TextView b = label(value,20,Color.WHITE); b.setTypeface(null,Typeface.BOLD); pad(b,0,8,0,0); box.addView(b);
        return box;
    }

    void runTest() {
        test.setEnabled(false); test.setText("TESTING…"); status.setText("Checking internet reachability…");
        new Thread(() -> {
            long start = System.nanoTime(); String result; String ms = "—";
            try {
                boolean reachable = InetAddress.getByName("1.1.1.1").isReachable(2500);
                long elapsed = (System.nanoTime()-start)/1_000_000;
                ms = elapsed + " ms";
                result = reachable ? "Internet host reachable" : "Host did not reply (may be blocked)";
            } catch(Exception e) { result = "Could not complete test"; }
            String finalMs=ms, finalResult=result;
            runOnUiThread(() -> {
                ping.setText(finalMs);
                ConnectivityManager cm=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo ni=cm==null?null:cm.getActiveNetworkInfo();
                net.setText(ni!=null && ni.isConnected() ? "Connected" : "Offline");
                status.setText(finalResult + ". This is a basic reachability check, not the PUBG server ping.");
                test.setEnabled(true); test.setText("RUN NETWORK TEST");
            });
        }).start();
    }
}
