-repackageclasses
-overloadaggressively
-allowaccessmodification
-mergeinterfacesaggressively
# 不进行预校验,Android不需要,可加快混淆速度。
#-dontpreverify
-assumenosideeffects class android.util.Log {
public static *** isLoggable(java.lang.String, int);
public static *** v(...);
public static *** i(...);
public static *** w(...);
public static *** d(...);
public static *** e(...);
}