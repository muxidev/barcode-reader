# Barcode-Reader

[![](https://jitpack.io/v/muxidev/barcode-reader.svg)](https://jitpack.io/#muxidev/barcode-reader)

A simple library that scan a barcode and return respective response from user. This lib uses:
* CameraX to manage camera to SDK's higher or equal 21
* Camera1(Google vision API) to SDK's lower 21
* Google vision API to detect barcode

## Usage

Add on your **root build.gradle** 

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
        
    }
}

```

Add on your **app build.gradle** 

```gradle 
	implementation "com.github.muxidev:barcode-reader:$latest_version"
```

Add `BarCodeReaderFragment` on your fragment tag inside xml file, e.g `activity_main.xml`, like this:
```xml
<fragment
	android:id="@+id/fragment_barcode"
	android:layout_width="match_parent"
	android:layout_height="match_parent"
	android:name="com.muxi.barcodereader.ui.BarCodeReaderFragment"/>

```

Extends your `Fragment` or `Activity` with `BarcodeListener` and set this listener on `BarcodeManager` listener instance:

```kotlin
class MainActivity: AppCompatActivity(), BarCodeListener {
    
    private val barCodeManager = BarCodeManager.getManagerInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        barCodeManager.setListener(this)
    }
    
    override fun onError() {}

    override fun onPermissionDenied() {}

    override fun onPermissionGranted() {}

    override fun onSuccess(codeReaded: BarcodeResponse) {}
}

```
