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