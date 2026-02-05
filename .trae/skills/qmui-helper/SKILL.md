---
name: "qmui-helper"
description: "Provides guidance on using QMUI Android library, specifically for layouts and shadows. Invoke when user asks about QMUI usage, shadow configuration, or QMUILayout attributes."
---

# QMUI Usage Guide

This skill provides references and examples for using the QMUI Android library, with a focus on Layouts and Shadows.

shetj-qmui = { module = "com.github.SheTieJun:SimQUMI", version = "0.0.1" }

## 🔗 Official Resources
- [QMUI Website](https://qmuiteam.com/android/get-started/)

## 🎨 Shadow Layout (QMUILayout)

QMUI provides powerful layout components (like `QMUILinearLayout`, `QMUIFrameLayout`, `QMUIRelativeLayout`, `QMUIConstraintLayout`) that support shadows, borders, and dividers without needing extra drawables.

### Example: QMUILinearLayout with Shadow

```xml
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
    app:qmui_showBorderOnlyBeforeL="true" />
```

### Key Attributes (`QMUILayout`)

| Attribute | Format | Description |
| :--- | :--- | :--- |
| **Shadow** | | |
| `qmui_shadowElevation` | dimension | The elevation of the shadow. |
| `qmui_shadowColor` | color/ref | The color of the shadow. |
| `qmui_shadowAlpha` | float | The alpha (transparency) of the shadow (0.0 - 1.0). |
| `qmui_useThemeGeneralShadowElevation` | boolean | Whether to use the global theme shadow elevation. |
| **Border & Radius** | | |
| `qmui_radius` | dimension | Corner radius of the layout. |
| `qmui_hideRadiusSide` | enum | Side to hide radius: `none`, `top`, `right`, `bottom`, `left`. |
| `qmui_borderColor` | color/ref | Color of the border. |
| `qmui_borderWidth` | dimension | Width of the border. |
| `qmui_showBorderOnlyBeforeL` | boolean | Show border only on versions before Lollipop (API 21). |
| **Dividers** | | |
| `qmui_topDividerHeight` | dimension | Height of the top divider. |
| `qmui_topDividerColor` | color/ref | Color of the top divider. |
| `qmui_topDividerInsetLeft` | dimension | Left inset for top divider. |
| `qmui_topDividerInsetRight` | dimension | Right inset for top divider. |
| `qmui_bottomDividerHeight` | dimension | Height of the bottom divider. |
| `qmui_bottomDividerColor` | color/ref | Color of the bottom divider. |
| `qmui_bottomDividerInsetLeft` | dimension | Left inset for bottom divider. |
| `qmui_bottomDividerInsetRight` | dimension | Right inset for bottom divider. |
| `qmui_leftDividerWidth` | dimension | Width of the left divider. |
| `qmui_leftDividerColor` | color/ref | Color of the left divider. |
| `qmui_rightDividerWidth` | dimension | Width of the right divider. |
| `qmui_rightDividerColor` | color/ref | Color of the right divider. |
| **Size Constraints** | | |
| `android:maxWidth` | dimension | Maximum width of the layout. |
| `android:maxHeight` | dimension | Maximum height of the layout. |
| `android:minWidth` | dimension | Minimum width of the layout. |
| `android:minHeight` | dimension | Minimum height of the layout. |

### Outline Configuration

- `qmui_outlineInsetTop`, `qmui_outlineInsetBottom`, `qmui_outlineInsetLeft`, `qmui_outlineInsetRight`: Adjust the shadow outline insets.
- `qmui_outlineExcludePadding`: Whether the outline should exclude padding.
