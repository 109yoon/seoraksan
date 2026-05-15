#!/bin/bash
# APK 서명용 키스토어 생성 스크립트
# 한 번만 실행하면 됩니다

keytool -genkey -v \
  -keystore seoraksan.keystore \
  -alias seoraksan \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass seoraksan2025 \
  -keypass seoraksan2025 \
  -dname "CN=Seoraksan NPS, OU=Field Management, O=NPS, L=Sokcho, S=Gangwon, C=KR"

echo ""
echo "=== GitHub Secrets에 등록할 값들 ==="
echo ""
echo "SIGNING_KEY (base64):"
base64 -w 0 seoraksan.keystore
echo ""
echo ""
echo "KEY_ALIAS: seoraksan"
echo "KEY_STORE_PASSWORD: seoraksan2025"
echo "KEY_PASSWORD: seoraksan2025"
echo ""
echo "위 값들을 GitHub → Settings → Secrets → Actions 에 등록하세요"
