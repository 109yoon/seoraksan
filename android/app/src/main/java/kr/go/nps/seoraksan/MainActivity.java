package kr.go.nps.seoraksan;

import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.BridgeWebViewClient;

public class MainActivity extends BridgeActivity {

    @Override
    protected WebViewClient getWebViewClient() {
        return new BridgeWebViewClient(bridge) {
            private String pendingKakaoCode = null;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                // 카카오 OAuth 리다이렉트 가로채기: GitHub Pages에 ?code= 붙어 오는 경우
                if (uri != null
                        && "109yoon.github.io".equals(uri.getHost())
                        && uri.getQueryParameter("code") != null) {
                    String code = uri.getQueryParameter("code");
                    if (code != null && !code.isEmpty()) {
                        // 코드를 저장하고 앱 메인 페이지로 복귀 → onPageFinished에서 주입
                        pendingKakaoCode = code.replace("\\", "\\\\").replace("'", "\\'");
                        view.post(() -> view.loadUrl("http://localhost"));
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (pendingKakaoCode != null
                        && (url.startsWith("http://localhost") || url.startsWith("capacitor://localhost"))) {
                    final String safe = pendingKakaoCode;
                    pendingKakaoCode = null;
                    // 앱 JS가 초기화된 후 코드 전달
                    view.postDelayed(() -> view.evaluateJavascript(
                        "if(typeof _handleKakaoCode==='function'){" +
                            "_handleKakaoCode('" + safe + "'," +
                            "'https://109yoon.github.io/seoraksan/');" +
                        "}",
                        null
                    ), 400);
                }
            }
        };
    }
}
