package com.example.mathwiz;

import android.app.Activity;
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
import twitter4j.auth.RequestToken;

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
                    final String oauthVerifier = uri.getQueryParameter("oauth_verifier");
                    System.out.println("oauthVerifier: " + oauthVerifier);
                    if(oauthVerifier != null){
                        System.out.println("authenticated!");
                        Background.run(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    AccessToken accessToken = twitter.getOAuthAccessToken(oauthVerifier);
                                    twitter.setOAuthAccessToken(accessToken);

                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("access token", accessToken.getToken());
                                    returnIntent.putExtra("access token secret", accessToken.getTokenSecret());
                                    System.out.println(accessToken.getToken());
                                    System.out.println(accessToken.getTokenSecret());

                                    setResult(Activity.RESULT_OK, returnIntent);
                                    finish();
                                } catch (TwitterException e) {
                                    System.out.println(e.toString());
                                }
                            }
                        });
                    }
                }
                super.onLoadResource(view, url);
            }
        });

        Background.run(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestToken requestToken = twitter.getOAuthRequestToken();
                    final String requestUrl = requestToken.getAuthenticationURL();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl(requestUrl);
                        }
                    });
                } catch (TwitterException e) {
                    System.out.println(e.toString());
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_CANCELED);
        finish();
        super.onBackPressed();
    }
}
