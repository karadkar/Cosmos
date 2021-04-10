package io.github.karadkar.sample.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.github.karadkar.sample.utils.TestDataProvider
import io.realm.Realm
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NasaImageDaoTest {

    private lateinit var dao: NasaImageDao
    private lateinit var realm: Realm
    private val testData = TestDataProvider.nasaImageResponseList

    @Before
    fun setUp() {
        realm = Realm.getDefaultInstance()
        dao = NasaImageDao(realm)
    }

    @After
    fun tearDown() {
        realm.executeTransaction { it.deleteAll() }
        dao.close()
    }

    @Test
    fun saveImages() {
        dao.saveImages(testData)

        testData.forEach { nasaImageResponse ->
            val realmValue = realm.where(NasaImageResponse::class.java)
                .equalTo(NasaImageResponseFields.ID, nasaImageResponse.id)
                .findFirst()
            assertThat(realmValue).isNotNull()
            assertThat(realmValue).isEqualTo(nasaImageResponse)
        }
    }
}