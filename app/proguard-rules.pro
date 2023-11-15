# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#---------------------------------1.实体类---------------------------------
-keep class com.wbb.xiaodongjia.bean.**{*;}
-keep class com.wbb.xiaodongjia.event.**{*;}
#--数据库实体类不被混淆
-keep class com.wbb.xiaodongjia.roomdao.**{*;}
#-------------------------------------------------------------------------

#---------------------------------2.第三方包-------------------------------
#ARouter
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# 极验 SDK 已做混淆处理，集成时请带上混淆规则，勿再次混淆 SDK
-dontwarn com.geetest.sdk.**
-keep class com.geetest.sdk.**{*;}

#JPush
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

#JPush华为
-ignorewarnings
-keepattributes Annotation
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.{*;}
-keep class com.huawei.updatesdk.{*;}
-keep class com.huawei.hms.{*;}
#JPush oppo
-dontwarn com.coloros.mcsdk.**
-keep class com.coloros.mcsdk.** { *; }

-dontwarn com.heytap.**
-keep class com.heytap.** { *; }

-dontwarn com.mcs.**
-keep class com.mcs.** { *; }
#JPush MI
-dontwarn com.xiaomi.push.**
-keep class com.xiaomi.push.** { *; }
#友盟
-keep class com.umeng.** {*;}
#您如果使用了稳定性模块可以加入该混淆
-keep class com.uc.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


# OSS
-keep class com.alibaba.sdk.android.oss.** { *; }
-dontwarn okio.**
-dontwarn org.apache.commons.codec.binary.**

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

#微信支付
-keep class com.tencent.mm.opensdk.** {
    *;
}
-keep class com.tencent.wxop.** {
    *;
}
-keep class com.tencent.mm.sdk.** {
    *;
}

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# banner 的混淆代码
-keep class com.youth.banner.** {
    *;
 }

#BaseQuickAdapter
 -keep class com.chad.library.adapter.** {
 *;
 }
 -keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
 -keep public class * extends com.chad.library.adapter.base.BaseViewHolder
 -keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
      <init>(...);
 }

#----------- rxjava rxandroid----------------
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent

#eventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

 # VoIP
 -keep class io.agora.rtc.** {*;}

 # Location
 -keep class com.amap.api.**{*;}
 -keep class com.amap.api.services.**{*;}

 # 红包
 -keep class com.google.gson.** { *; }
 -keep class com.uuhelper.Application.** {*;}
 -keep class net.sourceforge.zbar.** { *; }
 -keep class com.google.android.gms.** { *; }
 -keep class com.alipay.** {*;}
 -keep class com.jrmf360.rylib.** {*;}

 -ignorewarnings
 #Ucrop
 -dontwarn com.yalantis.ucrop**
 -keep class com.yalantis.ucrop** { *; }
 -keep interface com.yalantis.ucrop** { *; }

#GSYVideoPlayer
-keep class com.shuyu.gsyvideoplayer.video.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.**
-keep class com.shuyu.gsyvideoplayer.video.base.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.video.base.**
-keep class com.shuyu.gsyvideoplayer.utils.** { *; }
-dontwarn com.shuyu.gsyvideoplayer.utils.**
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#Jpush
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

#Gson
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
-keep class com.google.protobuf.** {*;}

#---------------------------------自定义view-----------------------
-keep class com.wbb.xiaodongjia.jpush.**{*;}
-keep class com.wbb.xiaodongjia.view.**{*;}

#---------------------------------反射相关的类和方法-----------------------
 -keepclassmembers class androidx.viewpager.widget.ViewPager.** {
   public *;
   protected *;
   private *;
}
-keep class androidx.viewpager.widget.ViewPager.**{*;}
#不混淆某个类的内部类
-keep class com.google.android.material.tabs.TabLayout$* {
        *;
 }
 #-------------------------------------------基本不用动区域--------------------------------------------
 #---------------------------------基本指令区----------------------------------
 #指定压缩级别
 -optimizationpasses 5
 # 是否使用大小写混合
 -dontusemixedcaseclassnames
 # 指定不去忽略非公共库的类
 -dontskipnonpubliclibraryclasses
 #不跳过非公共的库的类成员
 -dontskipnonpubliclibraryclassmembers
 # 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度
 -dontpreverify
 # 这句话能够使我们的项目混淆后产生映射文件
 # 包含有类名->混淆后类名的映射关系
 -verbose
 #混淆时采用的算法
 -optimizations !code/simplification/cast,!field/*,!class/merging/*
 # 保留Annotation不混淆
 -keepattributes *Annotation*,InnerClasses
 #保持泛型
 -keepattributes Signature
 #保留行号
 -keepattributes SourceFile,LineNumberTable

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

#表示不混淆枚举中的values()和valueOf()方法，枚举我用的非常少，这个就不评论了
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#表示不混淆任何包含native方法的类的类名以及native方法名，这个和我们刚才验证的结果是一致
-keepclasseswithmembernames class * {
    native <methods>;
}

#这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
#表示不混淆Activity中参数是View的方法，因为有这样一种用法，在XML中配置android:onClick=”buttonClick”属性，
#当用户点击该按钮时就会调用Activity中的buttonClick(View view)方法，如果这个方法被混淆的话就找不到了
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

#表示不混淆任何一个View中的setXxx()和getXxx()方法，
#因为属性动画需要有相应的setter和getter的方法实现，混淆了就无法工作了。
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#表示不混淆Parcelable实现类中的CREATOR字段，
#毫无疑问，CREATOR字段是绝对不能改变的，包括大小写都不能变，不然整个Parcelable工作机制都会失败。
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
#需要序列化和反序列化的类不能被混淆（注：Java反射用到的类也不能被混淆）
-keepnames class * implements java.io.Serializable
#保护实现接口Serializable的类中，指定规则的类成员不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#保持R文件不被混淆，否则，你的反射是获取不到资源id的
-keep class **.R$* { *; }

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# ----------------------------- 其他的 -----------------------------
#
# 删除代码中Log相关的代码
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}


-keep class kotlin.jvm.** {*;}
-keep class kotlin.reflect.jvm.** {*;}
#------------------------------------------------------------------------------#
-keep class com.wbb.swap.bean.** { *; }
-keep class com.wbb.base.bean.** { *; }
#极光一键登录
 -dontoptimize
        -dontpreverify

        -dontwarn cn.jpush.**
        -keep class cn.jpush.** { *; }
        -dontwarn cn.jiguang.**
        -keep class cn.jiguang.** { *; }

        -dontwarn cn.com.chinatelecom.**
        -keep class cn.com.chinatelecom.** { *; }
        -dontwarn com.ct.**
        -keep class com.ct.** { *; }
        -dontwarn a.a.**
        -keep class a.a.** { *; }
        -dontwarn com.cmic.**
        -keep class com.cmic.** { *; }
        -dontwarn com.unicom.**
        -keep class com.unicom.** { *; }
        -dontwarn com.sdk.**
        -keep class com.sdk.** { *; }

        -dontwarn com.sdk.**
        -keep class com.sdk.** { *; }


# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

#腾讯bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#//不混淆某个类
#-keep public class name.huihui.example.Test { *; }
#//不混淆某个类的子类
#-keep public class * extends name.huihui.example.Test { *; }
#//不混淆所有类名中包含了“model”的类及其成员
#-keep public class **.*model*.** {*;}
#//不混淆某个接口的实现
#-keep class * implements name.huihui.example.TestInterface { *; }
#//不混淆某个类的构造方法
#-keepclassmembers class name.huihui.example.Test {
#    public <init>();
#}
#//不混淆某个类的特定的方法
#-keepclassmembers class name.huihui.example.Test {
#    public void test(java.lang.String);
#}
#//不混淆某个类的内部类
#-keep class name.huihui.example.Test$* {
#        *;
# }
#//两个常用的混淆命令，注意：
#//一颗星表示只是保持该包下的类名，而子包下的类名还是会被混淆；
#//两颗星表示把本包和所含子包下的类名都保持；
#-keep class com.suchengkeji.android.ui.**
#-keep class com.suchengkeji.android.ui.*
#//用以上方法保持类后，你会发现类名虽然未混淆，但里面的具体方法和变量命名还是变了，
#//如果既想保持类名，又想保持里面的内容不被混淆，我们就需要以下方法了
#
#//不混淆某个包所有的类
#-keep class com.suchengkeji.android.bean.** { *; }