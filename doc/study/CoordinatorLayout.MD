```
<android.support.design.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/id_coordinatorlayout_collapsing_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <android.support.design.widget.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="200dp"
        app:popupTheme="@style/ThemeOverlay.AppCompat.Light"
        app:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar">

        <android.support.design.widget.CollapsingToolbarLayout
            android:id="@+id/id_collapselayout"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:collapsedTitleTextAppearance="@style/MyToolbarTextStyle"
            app:contentScrim="?attr/colorPrimaryDark"
            app:expandedTitleMarginStart="48dp"
            app:expandedTitleTextAppearance="@style/MyToolbarTextStyle"
            app:layout_scrollFlags="scroll|exitUntilCollapsed">

            <ImageView
                android:id="@+id/id_img_collapse"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:scaleType="centerCrop"
                app:layout_collapseMode="parallax"
                app:layout_collapseParallaxMultiplier="0.6"/>

            <android.support.v7.widget.Toolbar
                android:id="@+id/id_toolbar_collapse"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                android:minHeight="?attr/actionBarSize"
                app:layout_collapseMode="pin"/>
        </android.support.design.widget.CollapsingToolbarLayout>
    </android.support.design.widget.AppBarLayout>

    <android.support.v7.widget.RecyclerView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/id_recyclerview_collapse"
        app:layout_behavior="@string/appbar_scrolling_view_behavior"/>
</android.support.design.widget.CoordinatorLayout>
```

