# parallaxdecoration
[![](https://www.jitpack.io/v/seagazer/parallaxdecoration.svg)](https://www.jitpack.io/#seagazer/parallaxdecoration)
* A special item decoration for recyclerView, it can display any bitmaps by looper as background of recyclerView. The bitmaps of background can opt to be scrolled within this recyclerView in a parallax fashion. 

* How to use:
```kotlin
// step1. add config in build.gradle of the root project
allprojects {
    repositories {
        maven { url 'https://www.jitpack.io' }
        google()
        jcenter()
    }
}

// step2. add the library in your app module
implementation 'com.github.seagazer:parallaxdecoration:1.0.0'

// step3. create a parallaxdecoration for your recyclerView
private val bgs = intArrayOf(
    R.drawable.rd_gua_seed_1, R.drawable.rd_gua_seed_2, R.drawable.rd_gua_seed_3
)

val parallaxItemDecoration = ParallaxDecoration(context).apply {
        setupResource(bgs.asList())// init the bitmaps of background
        parallax = 0.3f// set the parallax fashion(the range is [0,1])
        autoFill = true// set the bitmap auto scale to fill the size of recyclerView
    }
recycler_view.addItemDecoration(parallaxItemDecoration)
```

* Example demo:
<table>
<tr>
<td><center><img src="https://upload-images.jianshu.io/upload_images/4420407-238522d3e607c728.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/240"/></center></td>
<td><center><img src="https://upload-images.jianshu.io/upload_images/4420407-e1b1cf44ed0f93ed.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/240"/></center></td>
<td><center><img src="https://upload-images.jianshu.io/upload_images/4420407-b462f8b3c11d9430.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/240"/></center></td>
</tr>
</table>