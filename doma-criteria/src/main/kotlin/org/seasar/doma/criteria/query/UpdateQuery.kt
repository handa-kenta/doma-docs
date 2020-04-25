package org.seasar.doma.criteria.query

import java.lang.reflect.Method
import org.seasar.doma.jdbc.Config
import org.seasar.doma.jdbc.PreparedSql
import org.seasar.doma.jdbc.SqlExecutionSkipCause
import org.seasar.doma.jdbc.SqlLogType

class UpdateQuery(private val config: Config, private val sql: PreparedSql) : org.seasar.doma.jdbc.query.UpdateQuery {
    override fun getConfig(): Config {
        return config
    }

    override fun getSql(): PreparedSql {
        return sql
    }

    override fun getClassName(): String {
        // TODO
        return javaClass.name
    }

    override fun getMethodName(): String {
        // TODO
        return "delete"
    }

    override fun comment(sql: String?): String {
        // TODO
        return sql!!
    }

    override fun getSqlLogType(): SqlLogType {
        return sql.sqlLogType
    }

    override fun getQueryTimeout(): Int {
        return config.queryTimeout
    }

    override fun incrementVersion() {
    }

    override fun getMethod(): Method {
        throw UnsupportedOperationException()
    }

    override fun prepare() {
        throw UnsupportedOperationException()
    }

    override fun complete() {
        throw UnsupportedOperationException()
    }

    override fun getSqlExecutionSkipCause(): SqlExecutionSkipCause {
        throw UnsupportedOperationException()
    }

    override fun isOptimisticLockCheckRequired(): Boolean {
        return false
    }

    override fun isAutoGeneratedKeysSupported(): Boolean {
        return false
    }

    override fun isExecutable(): Boolean {
        return true
    }
}
