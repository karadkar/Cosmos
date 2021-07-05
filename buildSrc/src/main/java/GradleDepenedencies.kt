/**
 * Kotlin gradle dependecy management
 * check https://handstandsam.com/2018/02/11/kotlin-buildsrc-for-better-gradle-dependency-management/
 */
object Versions {
    const val okHttpLogger = "4.3.0"
    const val gradlePlugin = "4.2.1"
    const val archExtensions = "2.1.0"
    const val roomDb = "2.2.3"
    const val realmDbGradlePlugin = "6.1.0"
    const val kotlin = "1.4.30"
    const val koinVersion = "2.2.0"
    const val daggerHilt = "2.35"
}

object Libraries {
    const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.1.0" // live-data view-model
    const val lifecycleReactiveStreamExtension = "android.arch.lifecycle:reactivestreams:1.1.1" // rx to live-data

    const val roomDbRuntime = "androidx.room:room-runtime:${Versions.roomDb}"
    const val roomDbKaptCompiler = "androidx.room:room-compiler:${Versions.roomDb}"
    const val roomDbRxjava = "androidx.room:room-rxjava2:${Versions.roomDb}"
    const val roomDbKtx = "androidx.room:room-ktx:${Versions.roomDb}"

    const val kotlinStdlibJdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val appcompat = "androidx.appcompat:appcompat:1.1.0"
    const val coreKtx = "androidx.core:core-ktx:1.1.0"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:1.3.3"

    const val recyclerView = "androidx.recyclerview:recyclerview:1.0.0"
    const val materialComponents = "com.google.android.material:material:1.1.0"
    const val swipeRefreashLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.0.0"
    const val picasso = "com.squareup.picasso:picasso:2.71828"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
    const val touchImageView = "com.github.MikeOrtiz:TouchImageView:3.0.5"
    const val viewPager2 = "androidx.viewpager2:viewpager2:1.0.0"

    const val rxJava2 = "io.reactivex.rxjava2:rxjava:2.2.10"
    const val rxAndroid2 = "io.reactivex.rxjava2:rxandroid:2.1.1"

    const val retrofit = "com.squareup.retrofit2:retrofit:2.6.0"
    const val retrofitJacksonConverter = "com.squareup.retrofit2:converter-jackson:2.6.0"
    const val retrofitRxJava2Adapter = "com.squareup.retrofit2:adapter-rxjava2:2.5.0"
    const val okHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttpLogger}"

    const val kaptRealmFieldHelper = "dk.ilios:realmfieldnameshelper:1.1.1"
    const val kaptDatabindingCompiler = "com.android.databinding:compiler:${Versions.gradlePlugin}"

    const val koinViewModel = "org.koin:koin-android-viewmodel:${Versions.koinVersion}"

    const val hiltCompiler = "com.google.dagger:hilt-compiler:${Versions.daggerHilt}"
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.daggerHilt}"
    const val hiltAndroidTesting = "com.google.dagger:hilt-android-testing:${Versions.daggerHilt}"
    const val hiltAndroidTestCompiler = "com.google.dagger:hilt-android-compiler:${Versions.daggerHilt}"

    //region Testing dependencies
    const val junit = "junit:junit:4.12"
    const val testRunner = "androidx.test:runner:1.2.0"
    const val testExt = "androidx.test.ext:junit:1.1.0"
    const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
    const val truthAssersions = "com.google.truth:truth:0.42" // assertions
    const val mockk = "io.mockk:mockk:1.9.3"
    const val mockkAndroid = "io.mockk:mockk-android:1.9.3"
    const val mockWebServer = "com.squareup.okhttp3:mockwebserver:4.9.0"
    const val koinTest = "org.koin:koin-test:${Versions.koinVersion}"
    const val barista = "com.schibsted.spain:barista:3.9.0"
    const val okHttp3IdlingResource = "com.jakewharton.espresso:okhttp3-idling-resource:1.0.0"
    const val rxJava2IdlingResource = "com.squareup.rx.idler:rx2-idler:0.9.1"

    // use same version as lifecycle to  avoid issue https://stackoverflow.com/q/55336613/2804351
    const val coreTesting = "androidx.arch.core:core-testing:${Versions.archExtensions}"
    //endregion
}