## Currency Converter
Currency conversion tool using live exchange rates.

### Installation
<a href="https://play.google.com/store/apps/details?id=com.nicoqueijo.android.currencyconverter" target="_blank">
<img src="graphics/google_play_images/google_play.png" a_blank href="https://play.google.com/store/apps/details?id=com.nicoqueijo.cityskylinequiz">
</a>

### Demo
<p float="left">
  <img src="graphics/demos/demo1.gif" hspace="10" width="250" />
  <img src="graphics/demos/demo2.gif" hspace="10" width="250" /> 
</p>

### Screenshots
https://github.com/nicoqueijo/Android-Currency-Converter/tree/master/graphics/screenshots

### API used
https://openexchangerates.org/

### Architecture
This app implements the MVVM architectural pattern using a single activity with fragment-swapping and data-caching using a repository to fetch local or remote data. 

<img src="graphics/flowcharts/single_activity_diagram.jpg" width="550">
<img src="graphics/flowcharts/data_flow_diagram.png" width="550">

### Built with
* <a href="https://developer.android.com/topic/libraries/architecture/viewmodel" target="_blank">ViewModel</a> - A class designed to store and manage UI-related data in a lifecycle conscious way.
* <a href="https://developer.android.com/topic/libraries/architecture/livedata" target="_blank">LiveData</a> - An observable data holder class.
* <a href="https://developer.android.com/guide/navigation" target="_blank">Navigation</a> - A library that can manage complex navigation, transition animation, deep linking, and compile-time checked argument passing between the screens in your app.
* <a href="https://kotlinlang.org/docs/reference/coroutines-overview.html" target="_blank">Coroutines</a> - A great new feature of Kotlin which allow you to write asynchronous code in a sequential fashion.
* <a href="https://developer.android.com/topic/libraries/data-binding" target="_blank">Data Binding</a> - A support library that allows you to bind UI components in your layouts to data sources in your app using a declarative format rather than programmatically.
* <a href="https://square.github.io/retrofit" target="_blank">Retrofit</a> - Type-safe HTTP client for Android and Java by Square, Inc.
* <a href="https://github.com/square/moshi" target="_blank">Moshi</a> - A modern JSON library for Android and Java.
* <a href="https://developer.android.com/topic/libraries/architecture/room" target="_blank">Room</a> - An abstraction layer over SQLite to allow fluent database access while harnessing the full power of SQLite.
* <a href="https://developers.google.com/admob/android/quick-start" target="_blank">Google AdMob</a> - A mobile advertising platform that you can use to generate revenue from your app.
* <a href="https://github.com/bosphere/Android-FadingEdgeLayout" target="_blank">FadingEdgeLayout</a> - A versatile layout that fades its edges regardless of child view type.
* <a href="https://github.com/turing-tech/MaterialScrollBar" target="_blank">MaterialScrollBar</a> - An Android library that brings the Material Design 5.1 scrollbar to pre-5.1 devices.
* <a href="https://github.com/justasm/DragLinearLayout" target="_blank">DragLinearLayout</a> - An Android LinearLayout that supports draggable and swappable child Views.



### Acknowledgments
* App icon made by Freepik from www.flaticon.com

### License
```
 Copyright 2020 Nicolas Queijo

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
