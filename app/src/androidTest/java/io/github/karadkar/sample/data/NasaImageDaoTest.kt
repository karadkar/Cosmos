package io.github.karadkar.sample.data

import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.github.karadkar.sample.utils.TestDataProvider
import io.realm.Realm
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class NasaImageDaoTest : KoinTest {
//
//    private val okHttpClient by inject<OkHttpClient>()
//
//    @get:Rule
//    val idlingResourceRule = IdlingResourceRule(okHttpClient)

    private lateinit var dao: NasaImageDao
    private lateinit var realm: Realm
    private val testData = TestDataProvider.nasaImageResponseList

    @UiThreadTest
    @Before
    fun setUp() {
        realm = Realm.getDefaultInstance()
        dao = NasaImageDao(realm)
    }

    @UiThreadTest
    @After
    fun tearDown() {
        realm.executeTransaction { it.deleteAll() }
        dao.close()
    }

    @UiThreadTest
    @Test
    fun saveImages() {
        dao.saveImages(testData).test()
            .assertComplete()

        // saving same data again should be possible
        dao.saveImages(testData).test()
            .assertNoErrors().assertComplete()

        testData.forEach { nasaImageResponse ->
            val realmValue = realm.where(NasaImageResponse::class.java)
                .equalTo(NasaImageResponseFields.ID, nasaImageResponse.id)
                .findFirst()
            assertThat(realmValue).isNotNull()
            assertThat(realmValue).isEqualTo(nasaImageResponse)
        }
    }

    @UiThreadTest
    @Test
    fun getImage() {

        val resultTester = dao.getImagesFlowable().test()
        resultTester.apply {
            assertNotComplete()

            // no images saved yet
            assertThat(values()[0]).isEmpty()

            // save 2 images
            dao.saveImages(testData.take(2)).test().assertComplete()
            // verify we get only 2 images in second result
            assertThat(values()[1].size).isEqualTo(2)

            // now save 5 images
            dao.saveImages(testData.take(5)).test().assertComplete()
            // verify we get 5 images as third result
            assertThat(values()[2].size).isEqualTo(5)
        }
    }
}