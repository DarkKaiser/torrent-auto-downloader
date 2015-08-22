# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Program Files (x86)\Android\android-studio\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Cauly 사용을 위한 설정
-keep public class com.fsn.cauly.** {
	public protected *;
}
-keep public class com.trid.tridad.** {
	public protected *;
}
-dontwarn android.webkit.**
-keep class test.adlib.project.ads.SubAdlibAdViewCauly {
  *;
}
