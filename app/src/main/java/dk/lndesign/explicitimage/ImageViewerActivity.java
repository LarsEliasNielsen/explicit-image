package dk.lndesign.explicitimage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class ImageViewerActivity extends AppCompatActivity {

    public static final String IMAGE_URL = "image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        if (getIntent().getExtras() != null) {
            String imageUrl = getIntent().getStringExtra(IMAGE_URL);
            if (imageUrl != null) {
                webView.loadUrl(imageUrl);
            }
        }
    }
}
