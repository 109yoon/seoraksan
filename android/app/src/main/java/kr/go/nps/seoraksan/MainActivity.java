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
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                // 카카오 OAuth 리다이렉트 가로채기: GitHub Pages에 ?code= 붙어 오는 경우
                if (uri != null
                        && "109yoon.github.io".equals(uri.getHost())
                        && uri.getQueryParameter("code") != null) {
                    String code = uri.getQueryParameter("code");
                    if (code != null && !code.isEmpty()) {
                        // XSS 방지: 코드는 영숫자+일부 특수문자만이어야 하므로 따옴표 이스케이프
                        final String safe = code.replace("\\", "\\\\").replace("'", "\\'");
                        view.post(() -> view.evaluateJavascript(
                            "if(typeof _handleKakaoCode==='function'){" +
                                "_handleKakaoCode('" + safe + "'," +
                                "'https://109yoon.github.io/seoraksan/');" +
                            "}",
                            null
                        ));
                        return true; // GitHub Pages로 실제 이동 차단
                    }
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        };
    }
}
