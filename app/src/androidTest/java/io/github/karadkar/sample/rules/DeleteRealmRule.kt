package io.github.karadkar.sample.rules

import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import io.github.karadkar.sample.utils.logInfo
import io.realm.Realm
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DeleteRealmRule : TestRule {
    override fun apply(base: Statement, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                deleteAllRealm()
                base.evaluate()
                deleteAllRealm()
            }
        }
    }

    private fun deleteAllRealm() {
        UiThreadStatement.runOnUiThread {
            logInfo("deleting realm")
            Realm.getDefaultInstance().executeTransaction {
                it.deleteAll()
            }
        }
    }
}