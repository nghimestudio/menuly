# Create Menuly upload keystore (one time)

```bash
cd /Users/thanhnguyen/Desktop/from3090newnew/menuscanner/android

keytool -genkeypair -v \
  -keystore menuly-upload.jks \
  -alias menuly \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -dname "CN=Menuly, OU=Mobile, O=Menuly, L=Sydney, ST=NSW, C=AU"
```

Copy example props:

```bash
cp keystore.properties.example keystore.properties
# edit passwords in keystore.properties
```

**Never commit** `menuly-upload.jks` or `keystore.properties`.
Back them up offline — losing the key blocks updates to `com.menuly.app` (unless Play App Signing enrollment covers you).
