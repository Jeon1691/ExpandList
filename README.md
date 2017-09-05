# ExpandList

```java
@Override
public boolean shouldOverrideUrlLoading(WebView view, String url) {
    Intent intent = parse(url);
    if (isIntentOf(url)) {
        if (isExistInfo(intent) || isExistPackage(intent)) return start(intent);
        else return startMarket(intent);
    } else if (isMarketOf(url)) return start(intent);
    else return url.contains("https://testquickpay");
}
```

```java
private Intent parse(String url) {
    try {
        return Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
    } catch (URISyntaxException e) {
        return null;
    }
}
```

```java
private boolean isIntentOf(@Nullable String url) {
    return url != null && Pattern.matches("^intent:[\\w:]*//\\S+$", url);
}
```

```java
private boolean isMarketOf(@Nullable String url) {
    return url != null && url.startsWith("market://");
}
```

```java
private boolean isExistPackage(Intent intent) {
    return intent != null && getContext().getPackageManager().getLaunchIntentForPackage(intent.getPackage()) != null;
}
```

```java
private boolean isExistInfo(Intent intent) {
    try {
        return intent != null && getContext().getPackageManager().getPackageInfo(intent.getPackage(), PackageManager.GET_ACTIVITIES) != null;
    } catch (PackageManager.NameNotFoundException e) {
        return false;
    }
}
```

```java
private boolean start(Intent intent) {
    getContext().startActivity(intent);
    return true;
}
```
