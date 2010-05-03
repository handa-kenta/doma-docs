/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.internal.jdbc.query;

import junit.framework.TestCase;

import org.seasar.doma.internal.jdbc.mock.MockConfig;
import org.seasar.doma.jdbc.SqlFileNotFoundException;
import org.seasar.doma.jdbc.dialect.Mssql2008Dialect;

/**
 * @author taedium
 * 
 */
public class SqlFileScriptQueryTest extends TestCase {

    private final MockConfig config = new MockConfig();

    public void testPrepare() throws Exception {
        SqlFileScriptQuery query = new SqlFileScriptQuery();
        query.setConfig(config);
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query
                .setSqlFilePath("META-INF/org/seasar/doma/internal/jdbc/query/SqlFileScriptQueryTest/testPrepare.sql");
        query.setBlockDelimiter("");
        query.prepare();

        assertEquals(config, query.getConfig());
        assertEquals("aaa", query.getClassName());
        assertEquals("bbb", query.getMethodName());
        assertEquals(
                "META-INF/org/seasar/doma/internal/jdbc/query/SqlFileScriptQueryTest/testPrepare.sql",
                query.getSqlFilePath());
        assertNotNull(query.getSqlFileUrl());
        assertNull(query.getBlockDelimiter());
    }

    public void testPrepare_dbmsSpecific() throws Exception {
        config.dialect = new Mssql2008Dialect();
        SqlFileScriptQuery query = new SqlFileScriptQuery();
        query.setConfig(config);
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query
                .setSqlFilePath("META-INF/org/seasar/doma/internal/jdbc/query/SqlFileScriptQueryTest/testPrepare_dbmsSpecific.sql");
        query.setBlockDelimiter("");
        query.prepare();

        assertEquals(config, query.getConfig());
        assertEquals("aaa", query.getClassName());
        assertEquals("bbb", query.getMethodName());
        assertEquals(
                "META-INF/org/seasar/doma/internal/jdbc/query/SqlFileScriptQueryTest/testPrepare_dbmsSpecific-mssql2008.sql",
                query.getSqlFilePath());
        assertNotNull(query.getSqlFileUrl());
        assertEquals("GO", query.getBlockDelimiter());
    }

    public void testPrepare_SqlFileNotFoundException() throws Exception {
        SqlFileScriptQuery query = new SqlFileScriptQuery();
        query.setConfig(config);
        query.setCallerClassName("aaa");
        query.setCallerMethodName("bbb");
        query.setSqlFilePath("META-INF/ccc.sql");
        query.setBlockDelimiter("ddd");
        try {
            query.prepare();
            fail();
        } catch (SqlFileNotFoundException expected) {
            System.out.println(expected.getMessage());
        }
    }
}
