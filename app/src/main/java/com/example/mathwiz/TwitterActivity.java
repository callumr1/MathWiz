package com.example.mathwiz;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterActivity extends AppCompatActivity {

    WebView webView;
    Twitter twitter = TwitterFactory.getSingleton();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onLoadResource(WebView view, String url){
                System.out.println(url);
                if(url.startsWith("http://mathwiz.com")){
                    Uri uri = Uri.parse(url);
                    final String oauthVerifier = uri.getQueryParameter("oath_verifier");
                    if(oauthVerifier != null){
                        System.out.println("authenticated!");
                        Background.run(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    AccessToken accessToken = twitter.getOAuthAccessToken(oauthVerifier);
                                    twitter.setOAuthAccessToken(accessToken);

                                    Intent intent = new Intent();
                                    intent.putExtra("access token", accessToken.getToken());
                                    intent.putExtra("access token secret", accessToken.getTokenSecret());

                                    setResult(RESULT_OK, intent);
                                    finish();
                                } catch (TwitterException e) {
                                    System.out.println(e.toString());
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
