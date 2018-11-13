# Glide探究一 如何计算加载后的图片尺寸？

## 一、疑问

* 调用`RequestBuilder` `into(@NonNull ImageView view)`，加载后的图片尺寸是多少？
* 调用`RequestBuilder` `into(@NonNull Y target)`，加载后的图片尺寸是多少？
* 调用`RequestOptions` `override(int width, int height)`，加载后的图片尺寸是多少？



## 二、请求尺寸

无论使用何种方式加载图片，`Glide`首先计算`request`请求尺寸。请求尺寸可以在2个地方进行设置。

* `Target`
* `RequestOptions`

通过`RequestBuilder` `into(@NonNull ImageView view)`，实质上是通过`Target`实现的，确切地说是`BitmapImageViewTarget`或者`DrawableImageViewTarget`。`Target`接口中提供了一个常量`SIZE_ORIGINAL`用于将请求尺寸设置为图片尺寸。

```java
int SIZE_ORIGINAL = Integer.MIN_VALUE;
```

其次，可以在`RequestOptions`中通过`override(int width, int height)`覆盖`Target`地请求尺寸。



## 三、三种尺寸

* request请求尺寸（在`Target`或者`RequestOptions`中设置的尺寸）
* source源图片尺寸（图片自身尺寸）
* target目标尺寸（加载后的图片尺寸）

上一部分中，我们已经知道如何设置`Glide`的`request`请求尺寸，并且图片的自身的`source`源尺寸是确定的。如何通过`request`和`source`两种尺寸计算出最终的`target`大小？答案是`DownsampleStrategy`。



## 四、DownsampleStrategy

`DownsampleStrategy`定义了2个抽象方法

```java
public abstract float getScaleFactor(
    int sourceWidth, int sourceHeight, 
    int requestedWidth, int requestedHeight);

public abstract SampleSizeRounding getSampleSizeRounding(
    int sourceWidth, int sourceHeight,
    int requestedWidth, int requestedHeight);
```

`getScaleFactor`根据`source`和`request`大小计算出缩放比例来确定`target`尺寸。

`getSampleSizeRounding`用于计算`inSampleSize`的数值，`SampleSizeRounding`有2个枚举值。

 * `MEMORY`内存优先，增加`inSampleSize`值，保证较小的内存使用。
 * `QUALITY`质量优先，降低`inSampleSize`值，保证较高的图片质量。

由`DownsampleStrategy`的定义可以看出，`Glide`加载后的图片保持了原始比例。



## 五、BitmapFactory.Options

通过`DownsmapleStrategy`的`getScaleFactor`方法，计算出`scaleFactor`缩放比例。从而计算出最终的`target`尺寸。

```java
targetWidth = sourceWidth * scaleFactor; 
targetHeight = sourceHeight * scaleFactor; 
```

接下来通过`BitmapFactory`加载图片即可。与加载图片相关的几个重要参数

```java
public boolean inJustDecodeBounds; // 仅解码图片尺寸，并不加载图片

public Bitmap inBitmap; // 复用已有的Bitmap，Glide总是优先使用inBitmap复用图片

public int inSampleSize; // 采样大小，必须是2的指数，<= 1，2，4，8，16，……

public boolean inScaled; // 缩放，结合inTargetDensity和inDensity确定最终图片大小
public int inTargetDensity; // 目标Density
public int inDensity; // 源Density
```

`BitmapFactory.Options`几个参数对加载后`Bitmap`尺寸的影响。

```java
// inScaled = false，inTargetDensity = inDensity = 0时
// 采样值越大，得到的Bitmap尺寸越小
bitmap.width = sourceWidth / inSampleSize; 
bitmap.height = sourceHeight / inSampleSize; 
```

```java
// 同时对图片进行缩放
bitmap.width = sourceWidth / inSampleSize * inTargetDensity / inDensity; 
bitmap.height = sourceHeight / inSampleSize * inTargetDensity / inDensity; 
```



## 六、Downsampler

`Downsampler`完成了图片的加载过程。

```java
public Resource<Bitmap> decode(
    InputStream is, 
    int requestedWidth, int requestedHeight,
    Options options, 
    DecodeCallbacks callbacks) throws IOException;  // 注意request大小
```

```java
private Bitmap decodeFromWrappedStreams(
    InputStream is,
    BitmapFactory.Options options, 
    DownsampleStrategy downsampleStrategy,
    DecodeFormat decodeFormat, boolean isHardwareConfigAllowed, 
    int requestedWidth, int requestedHeight, 
    boolean fixBitmapToRequestedDimensions, 
    DecodeCallbacks callbacks) throws IOException; // 出现了downsampleStrategy
```

```java
private static void calculateScaling(
      ImageType imageType,
      InputStream is,
      DecodeCallbacks decodeCallbacks,
      BitmapPool bitmapPool,
      DownsampleStrategy downsampleStrategy,
      int degreesToRotate,
      int sourceWidth,
      int sourceHeight,
      int targetWidth,
      int targetHeight,
      BitmapFactory.Options options) throws IOException; // target大小已经确定
```



## 七、RequestOptions

`RequestOptions`中设置`request`尺寸及`DownsampleStrategy`

```java
public RequestOptions override(int width, int height); 

public RequestOptions downsample(@NonNull DownsampleStrategy strategy); 
```



## 八、总结

* `BitmapFactory.Options`的4个参数`inSampleSize`，`inScaled`，`inTargetDensity`，`inDensity`共同决定了加载后的`bitmap`大小；
* `DownsampleStrategy`计算`request`及`source`尺寸的缩放比例，进而计算出`BitmapFactory.Options`的最终参数；
* `RequestOptions`中设置`request`大小及`DownsampleStrategy`；
* `Downsampler`完成了所以计算工作，并完成加载图片的工作；