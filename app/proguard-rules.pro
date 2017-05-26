# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\studio\sdk/tools/proguard/proguard-android.txt
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

#---------------高德地图 start-------------------------------------------------------------
    #3D 地图
    -keep   class com.amap.api.maps.**{*;}
    -keep   class com.autonavi.amap.mapcore.*{*;}
    -keep   class com.amap.api.trace.**{*;}

    #定位
    -keep class com.amap.api.location.**{*;}
    -keep class com.amap.api.fence.**{*;}
    -keep class com.autonavi.aps.amapapi.model.**{*;}

    #搜索
    -keep   class com.amap.api.services.**{*;}

    #2D地图
    -keep class com.amap.api.maps2d.**{*;}
    -keep class com.amap.api.mapcore2d.**{*;}

    #导航
    -keep class com.amap.api.navi.**{*;}
    -keep class com.autonavi.**{*;}

#--------------高德地图 end --------------------------------------------------------




#---------------极光推送-start---------------------------------------------------------------
   -dontoptimize
   -dontpreverify

   -dontwarn cn.jpush.**
   -keep class cn.jpush.** { *; }

   -dontwarn cn.jiguang.**
   -keep class cn.jiguang.** { *; }
    #2.0.5 ~ v2.1.7 版本有引入 gson 和 protobuf ，增加排除混淆的配置。(2.1.8版本不需配置)
   -dontwarn com.google.**
   -keep class com.google.gson.** {*;}
   -keep class com.google.protobuf.** {*;}

#---------------极光推送-end-----------------------------------------------------------------





#----------------腾讯bugly start------------------------------------------------------------------

   -dontwarn com.tencent.bugly.**
   -keep public class com.tencent.bugly.**{*;}

#----------------腾讯bugly end------------------------------------------------------------------
#-----------------友盟 start----------------------------------------------

#-----------------友盟 end------------------------------------------------

#------------------环信 start---------------------------------------------
    -keep class com.hyphenate.** {*;}
    -dontwarn  com.hyphenate.**
#------------------环信 end-----------------------------------------------

#------------------支付宝 start------------------------------------------

    -keep class com.alipay.android.app.IAlixPay{*;}
    -keep class com.alipay.android.app.IAlixPay$Stub{*;}
    -keep class com.alipay.android.app.IRemoteServiceCallback{*;}
    -keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
    -keep class com.alipay.sdk.app.PayTask{ public *;}
    -keep class com.alipay.sdk.app.AuthTask{ public *;}

#------------------支付宝 end------------------------------------------

#---------------------图库 start --------------------------------------
    -keep class com.jph.takephoto.** { *; }
    -dontwarn com.jph.takephoto.**

    -keep class com.darsh.multipleimageselect.** { *; }
    -dontwarn com.darsh.multipleimageselect.**

    -keep class com.soundcloud.android.crop.** { *; }
    -dontwarn com.soundcloud.android.crop.**

    -dontwarn android.support.**
    -keep class android.support.** { *; }


    -keepattributes InnerClasses
    -dontoptimize

    -keep class com.jph.takephoto.** { *; }
    -dontwarn com.jph.takephoto.**

    -keep class com.darsh.multipleimageselect.** { *; }
    -dontwarn com.darsh.multipleimageselect.**

    -keep class com.soundcloud.android.crop.** { *; }
    -dontwarn com.soundcloud.android.crop.**
#---------------------图库 end --------------------------------------

#--------------------百度地图 start-----------------------------------
    -keep class com.baidu.** {*;}
    -keep class vi.com.** {*;}
    -dontwarn com.baidu.**
#--------------------百度地图 end-------------------------------------

 # 使用Gson时需要配置Gson的解析对象及变量都不混淆。不然Gson会找不到变量。
     -keep classcom.anyline.model.ui.main.bean.** { *; }
     -keep classcom.anyline.model.ui.other.bean.** { *; }
     -keep classcom.anyline.model.widget.entry.**{*;}
     -keep classcom.anyline.model.ui.my_center.bean.**{*;}
     -keep classcom.anyline.model.wxapi.**{*;}
     -keep classcom.anyline.model.ui.charge.bean.**{*;}
     -keep classcom.anyline.model.ui.main.model.**{*;}
