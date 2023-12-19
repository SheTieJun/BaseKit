#### QMUI-阴影布局部分

[QMUI 官网](https://qmuiteam.com/android/get-started/)

```XML

<me.shetj.qmui.layout.QMUILinearLayout 
    android:id="@+id/layout_for_test"
    android:layout_width="260dp" 
    android:layout_height="300dp" 
    android:background="#ffffff"
    android:gravity="center" 
    android:layout_gravity="center_horizontal"
    android:layout_marginTop="40dp" 
    android:orientation="vertical" 
    app:qmui_shadowColor="#FF0000"
    app:qmui_radius="8dp" 
    app:qmui_shadowAlpha="1" 
    app:qmui_shadowElevation="8dp"
    app:qmui_showBorderOnlyBeforeL="true"></me.shetj.qmui.layout.QMUILinearLayout>
```

```XML

<declare-styleable name="QMUILayout">
    <attr name="android:maxWidth" />
    <attr name="android:maxHeight" />
    <attr name="android:minWidth" />
    <attr name="android:minHeight" />
    <attr name="qmui_bottomDividerHeight" format="dimension" />
    <attr name="qmui_bottomDividerColor" format="color|reference" />
    <attr name="qmui_bottomDividerInsetLeft" format="dimension" />
    <attr name="qmui_bottomDividerInsetRight" format="dimension" />
    <attr name="qmui_topDividerHeight" format="dimension" />
    <attr name="qmui_topDividerColor" format="color|reference" />
    <attr name="qmui_shadowColor" format="color|reference" />
    <attr name="qmui_topDividerInsetLeft" format="dimension" />
    <attr name="qmui_topDividerInsetRight" format="dimension" />
    <attr name="qmui_leftDividerWidth" format="dimension" />
    <attr name="qmui_leftDividerColor" format="color|reference" />
    <attr name="qmui_leftDividerInsetTop" format="dimension" />
    <attr name="qmui_leftDividerInsetBottom" format="dimension" />
    <attr name="qmui_rightDividerWidth" format="dimension" />
    <attr name="qmui_rightDividerColor" format="color|reference" />
    <attr name="qmui_rightDividerInsetTop" format="dimension" />
    <attr name="qmui_rightDividerInsetBottom" format="dimension" />
    <attr name="qmui_radius" />
    <attr name="qmui_borderColor" />
    <attr name="qmui_borderWidth" />
    <attr name="qmui_outerNormalColor" format="color|reference" />
    <attr name="qmui_hideRadiusSide" format="enum">
        <enum name="none" value="0" />
        <enum name="top" value="1" />
        <enum name="right" value="2" />
        <enum name="bottom" value="3" />
        <enum name="left" value="4" />
    </attr>
    <attr name="qmui_showBorderOnlyBeforeL" format="boolean" />
    <attr name="qmui_shadowElevation" format="dimension" />
    <attr name="qmui_useThemeGeneralShadowElevation" format="boolean" />
    <attr name="qmui_shadowAlpha" format="float" />
    <attr name="qmui_outlineInsetTop" format="dimension" />
    <attr name="qmui_outlineInsetLeft" format="dimension" />
    <attr name="qmui_outlineInsetRight" format="dimension" />
    <attr name="qmui_outlineInsetBottom" format="dimension" />
    <attr name="qmui_outlineExcludePadding" format="boolean" />
</declare-styleable>
```