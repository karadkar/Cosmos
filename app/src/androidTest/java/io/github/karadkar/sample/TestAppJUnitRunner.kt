package io.github.karadkar.sample

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * https://developer.android.com/training/dependency-injection/hilt-testing#custom-application
 *
 * Replaces application class [NasaPicturesApp] with [HiltTestApp] for android tests
 *
 * But, because of some BUG! hilt cannot create instance of our custom [HiltTestApp]
 * so workaround is to provide generate class
 * https://github.com/google/dagger/issues/2033#issuecomment-682407930
 */
class TestAppJUnitRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApp_Application::class.java.name, context)
    }
}