# GloriousRecyclerView

A full function RecyclerView integration of Header, Footer,EmptyView and Up Swipe To Load More

### usage

Step 1.  Add the dependency
```
dependencies {
    compile 'com.xpc:gloriousrecyclerview:0.2.0'
}
```

Step 2.  Add the in xml

```xml
<com.xpc.gloriousrecyclerview.GloriousRecyclerView
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

or

```xml
<com.xpc.gloriousrecyclerview.GloriousRecyclerView
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:hideNoMoreData="true"
    app:loadMoreTextColor="#0080ff"
    app:loadMoreTextSize="14sp"
    app:loadMoreBackground="#ccc"
    app:loadMoreIndeterminateDrawable="@drawable/loading_icon_drawable"/>
```

> `hideNoMoreData`: Hide the LoadMoreView When no more data, default is `true`
> `loadMoreIndeterminateDrawable`: The ProgressBar IndeterminateDrawable of LoadMoreView

Step 3.  The code in Activity, see [Demo](./app/src/main/java/com/xpc/gloriousrecyclerviewdemo/GloriousActivity.java)

### Effect Picture

![](./GloriousRecyclerView.gif)