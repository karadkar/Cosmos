# Nasa Pictures App
![Tests and apk build](https://github.com/karadkar/NasaPictures/workflows/Android%20Tests%20and%20apk%20build/badge.svg?branch=master)
### Used Libraries
- RxJava2
- Koin for dependency injection
- Barista for espresso tests

### MVVM architecture with Unidirectional data flow
- [Evolving Android architecture by Fragmented Podcast](https://fragmentedpodcast.com/episodes/148/)
- [Sample repo by Kaushik Gopal](https://github.com/kaushikgopal/movies-usf-android)
- [Managing state with rx-java by Jake Wharton](https://jakewharton.com/the-state-of-managing-state-with-rxjava/)

### Important Notes
- JSON data is hosted `https://api.jsonbin.io/b/6069381f1c2ec27de8b09d47`
- Disable animations before running Android Tests
- Do Gradle sync if kotlin buildScr code completion/navigation is not working in Gradle files

### General Highlights
- Grid view to display images
- Detail page to scroll through list of images
- save picture to device storage
- ViewPager2 used with recyclerview adapter

### Highlights in Testing
- Replacing HttpUrl dependency with MockWebServer url to mock the api data (NasaPicturesActivityTest.kt)[app/src/androidTest/java/io/github/karadkar/sample/NasaPicturesActivityTest.kt] 
- Verifying Bottom sheet data based on its state (BottomSheetIdlingResource.kt)[app/src/androidTest/java/io/github/karadkar/sample/rules/BottomSheetIdlingResource.kt]
- Rule to delete all realm data before tests
- Testing Realm dao with instrumentation tests.
  Replacing Async operation with Sync operation during tests.
  See `RealmExtensions.kt` in `main` and `test` dir
- Reading raw `json` files from `resoureces` dir. See (ResourceFileReader.kt)[app/src/main/java/io/github/karadkar/sample/utils/ResourceFileReader.kt] 