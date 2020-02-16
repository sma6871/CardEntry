
# CardEntry / OtpEntry



A custom library to show and edit card/otp numbers in your app.

[![](https://jitpack.io/v/sma6871/CardEntry.svg)](https://jitpack.io/#sma6871/CardEntry)

<img src="https://github.com/sma6871/CardEntry/blob/master/screenshots/sample.jpg?raw=true" />

## Setup
**Step1.** Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:
````gradle
allprojects {
  repositories {
    ...
    maven { url "https://jitpack.io" }
  }
}
````   
**Step2.** Add the dependency
```gradle
dependencies {
   implementation 'com.github.sma6871:CardEntry:<latest-version>'
}
```

## Usage

It is really easy, just add the following to your **xml** file:
**Card entry sample:**
````xml
    ...
    <com.sma6871.cardentry.CardEntry  
      android:layout_width="wrap_content"  
      android:layout_height="wrap_content"  
      android:maxLength="16"  
      android:selectAllOnFocus="false"  
      android:text="6104337982412311"  
      android:textSize="16sp"  
      app:ce_number_count="16"  
      app:ce_part_count="4"  
      app:ce_parts_space="32dp"  />      
      ...
````
**OTP Sample:**
````xml
...
<com.sma6871.cardentry.CardEntry  
  android:layout_width="wrap_content"  
  android:layout_height="wrap_content"  
  android:cursorVisible="false"  
  android:maxLength="4"  
  android:selectAllOnFocus="false"  
  android:text="123"  
  android:textIsSelectable="false"  
  android:textSize="16sp"  
  app:ce_digit_width="30dp"  
  app:ce_number_count="4"  
  app:ce_part_count="4"  
  app:ce_parts_space="32dp" />
  ...
````

## Callback listener
As this class extended from **EditText**, you can use **TextWatcher**s to perform actions before/after/on text change events; however, there is a callback method which tells you is input completed (based on max length) and the length of input numbers. Here is an example:
````kotlin
input.onPinChange { isComplete, length ->  
  Toast.makeText(this,"is Completed=$isComplete, length=$length",Toast.LENGTH_SHORT).show()  
}
````
## CardEntry Attributes
| Attribute | Use |
|--|--|
| ce_number_count | length of input (used for check whether input completed or not) |  
| ce_part_count | count of number parts (e.g. 4 for credit number) |  
| ce_selection_color | selection text color |  
| ce_line_color | color of lines which shows under parts |  
| ce_filled_line_color | after each part completes, line color will change to this color |  
| ce_has_animation | indicates whether new entered number will show with animation or not |  
| ce_show_lines | show lines under parts or not |  
| ce_parts_space | space between parts |  
| ce_digit_width | width of space which each character takes (default is text_size) |  
| ce_digit_line_spacing | numbers line spacing |

Apart from these you can use any property that applies to an `EditText`.
