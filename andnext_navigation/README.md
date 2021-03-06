# 一行代码实现Android右滑返回

## 一、使用方式Usage

- 实现效果

![右滑返回效果](./README/navigation_back.gif)  

[演示工程项目](https://github.com/jicanghai37927/WhatsAndroid)

[点击下载演示APK](https://raw.githubusercontent.com/jicanghai37927/WhatsAndroid/master/release/andnext_app_whatsandroid-release.apk)

- 导入and next_navigation模块

下载地址：https://github.com/jicanghai37927/WhatsAndroid/tree/master/andnext_navigation



- 在Application的onCreate()方法中调用  

```Java
NavigationHelper.onCreate()
```

所有的Activity支持右滑返回。



- 在Activity的onCreate()方法中调用

```Java
NavigationHelper.onCreate(); // 必须在调用super.onCreate()方法之前
super.onCreate(); 
```

之后所有的Activity都会支持右滑返回。



- 关闭Activity的右滑返回

```Java
NavigationHelper.exclude(this); // 必须在调用super.onCreate()方法之前
super.onCreate(); 
```

调用`NavigationHelper.exclude()`之后，该Activity即不再支持右滑返回。



## 二、代码解析

* 代码结构

```Java
package club.andnext.navigation;
	NavigationHelper.java // 管理NavigationLayout
	NavigationLayout.java // 负责用户交互，实现右滑效果
```

- `NavigationLayout.java`

NavigationLayout负责实现右滑效果，关键代码是使用ViewDragHelper实现拖拽效果。

- `NavigationHelper.java`

NavigationHelper实现了`Application.ActivityLifecycleCallbacks`接口，监听Activity事件。  

在onCreate()事件中，创建并添加NavigationLayout到DecorView中。  

在onStop()事件中，为NavigationLayout设置上一个Activity的DecorView截图，从而实现右滑显示上一个Activity内容。



## 三、设计思路

- 首先，如何处理用户的触摸事件来判断用户滑动？    

2个地方可以处理触摸事件，`Activity`和`View`的`dispatchTouchEvent()`方法。    

如果采用`Activity`的方式，则定义一个基类NavigationActivity来处理用户触摸事件，并且要求所有支持右滑返回的Activity均继承自NavigationActivity。    

如果采用View的方式，则定义控件NavigationLayout，并将NavigationLayout加入到Activity的控件结构中。  



- 其次，如何将View插入到Activity的控件结构中？    

1、通过`setContentView()`将NavigationLayout加入到控件结构中；    

2、通过`Activity`的`getWindow().getDecorView()`获取根控件，并添加NavigationLayout；  



- 第三，如何显示上一个Activity的内容？  

2种方法可以显示上一个Activity的内容。    

1、将当前Activity的主题设置为透明，即`<item name="android:windowIsTranslucent">false</item>`    

2、获取当前Activity的控件结构，即`this.getWindow().getDecorView()`    

如果采用的是第2种方式，我们还需要获取到上一个Activity实例。  



- 第四，如何获取到上一个Activity实例？  

`Application`的`ActivityLifecycleCallbacks`可以帮助我们实现这个功能。  

处理完这3个问题，我们可以展示当前及上一个Activity的内容，并且也可以处理触摸事件来实现滑动功能。如何将这些功能串联到一起？



## 四、方案过滤

- 第一、采用`View`的`dispatchTouchEvent()`方式来处理触摸事件。  

`Activity`方式要求所有支持右滑返回的`Activity`都必须继承自基类NavigationActivity，侵入性太强，并且可能遇到无法继承的情况。



- 第二、采用`this.getWindow().getDecorView()`方式来显示上一个Activity内容。  

透明主题方式要求每个支持右滑返回的Activity的主题都必须是透明的，侵入性太强，不适合应用于应用内的所有Activity，局部使用可以考虑。

## 五、实现过程

第一、实现NavigationLayout处理触摸事件并展示Activity内容。

第二、将NavigationLayout添加到Activity的控件结构中。

第三、为NavigationLayout提供上一个Activity的内容。

第四、NavigationLayout右滑结束，关闭当前Activity。

#### 1、实现NavigationLayout



#### 2、添加NavigationLayout

比较`Activity`、`FragmentActivity`、`AppCompatActivity`三种Activity的控件结构

`onCreate()`在`setContentView()`后的控件结构

```Java
com.android.internal.policy.DecorView[-1][子控件数 = 1]
android.widget.LinearLayout[-1][子控件数 = 2]
android.widget.FrameLayout[16908290][子控件数 = 1]

设置的ContentView = android.widget.LinearLayout
```

```Java
com.android.internal.policy.DecorView[-1][子控件数 = 1]
android.widget.LinearLayout[-1][子控件数 = 2]
android.widget.FrameLayout[16908290][子控件数 = 1]

设置的ContentView = android.widget.LinearLayout
```

```Java
com.android.internal.policy.DecorView[-1][子控件数 = 1]
android.widget.LinearLayout[-1][子控件数 = 2]
android.widget.FrameLayout[-1][子控件数 = 1]
androidx.appcompat.widget.ActionBarOverlayLayout[2131230791][子控件数 = 2]
androidx.appcompat.widget.ContentFrameLayout[16908290][子控件数 = 1]

设置的ContentView = android.widget.LinearLayout
```

`onStop()`之后的控件结构

```Java
android.view.ViewRootImpl
com.android.internal.policy.DecorView[-1][子控件数 = 3]
android.widget.LinearLayout[-1][子控件数 = 2]
android.widget.FrameLayout[16908290][子控件数 = 1]

设置的ContentView = android.widget.LinearLayout
```

```Java
android.view.ViewRootImpl
com.android.internal.policy.DecorView[-1][子控件数 = 3]
android.widget.LinearLayout[-1][子控件数 = 2]
android.widget.FrameLayout[16908290][子控件数 = 1]

设置的ContentView = android.widget.LinearLayout
```

```Java
android.view.ViewRootImpl
com.android.internal.policy.DecorView[-1][子控件数 = 3]
android.widget.LinearLayout[-1][子控件数 = 2]
android.widget.FrameLayout[-1][子控件数 = 1]
androidx.appcompat.widget.ActionBarOverlayLayout[2131230791][子控件数 = 2]
androidx.appcompat.widget.ContentFrameLayout[16908290][子控件数 = 1]

设置的ContentView = android.widget.LinearLayout
```

结论：将NavigationLayout添加到DecorView，并且将DecorView的子控件移动到Navigation中。

#### 3、获取上一个Activity



## 六、参考项目

- SwipeBackHelper：https://github.com/Jude95/SwipeBackHelper

- SwipeBackLayout：https://github.com/ikew0ng/SwipeBackLayout

- and_swipeback：https://github.com/XBeats/and_swipeback