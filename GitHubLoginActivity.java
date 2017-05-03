package com.company.myapp.lib.github;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.company.myapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.company.myapp.lib.github.GitHubAuth;
import android.net.Uri;

public class GitHubLoginActivity extends AppCompatActivity {
  public static final String ACCESS_TOKEN_EXTRA = "GitHubAccessToken";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.github_login_webview_activity);

    Toolbar mToolbar = (Toolbar) findViewById(R.id.github_login_toolbar);

    setSupportActionBar(mToolbar);

    setTitle("GitHub Login");

    setupWebview();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      default:
        finish();
        return true;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.github_login_menu, menu);

    return true;
  }

  private void setupWebview() {
    WebView mWebView = (WebView) findViewById(R.id.github_login_webview);

    mWebView.setWebViewClient(
        new WebViewClient() {
          @Override
          public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
          }

          @Override
          public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
          }

          @Override
          public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(GitHubAuth.REDIRECT_URL) && url.contains("code=")) {
              String code = Uri.parse(url).getQueryParameter("code");
              GitHubAuth.requestAccessToken(
                  code,
                  new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                      JsonObject object =
                          new Gson().fromJson(response.body(), JsonElement.class).getAsJsonObject();
                      String token = object.get("access_token").getAsString();
                      Intent intent = new Intent();
                      intent.putExtra(ACCESS_TOKEN_EXTRA, token);
                      setResult(AppCompatActivity.RESULT_OK, intent);
                      finish();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                      setResult(AppCompatActivity.RESULT_CANCELED);
                      finish();
                    }
                  });
              return true;
            }
            return false;
          }
        });

    mWebView.getSettings().setJavaScriptEnabled(true);

    mWebView.getSettings().setLoadWithOverviewMode(true);

    mWebView.getSettings().setUseWideViewPort(false);

    mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

    mWebView.setScrollbarFadingEnabled(false);

    String url = "https://github.com/login/oauth/authorize" + "?client_id=" + GitHubAuth.CLIENT_ID;

    mWebView.loadUrl(url);
  }
}