package io.github.karadkar.sample

import dagger.hilt.android.testing.CustomTestApplication

// https://developer.android.com/training/dependency-injection/hilt-testing#custom-application
@CustomTestApplication(TestApp::class)
interface HiltTestApp {}