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
package org.seasar.doma.jdbc.query;

import static org.seasar.doma.internal.util.AssertionUtil.assertEquals;
import static org.seasar.doma.internal.util.AssertionUtil.assertNotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.seasar.doma.internal.jdbc.entity.AbstractPostUpdateContext;
import org.seasar.doma.internal.jdbc.entity.AbstractPreUpdateContext;
import org.seasar.doma.internal.jdbc.sql.PreparedSql;
import org.seasar.doma.internal.jdbc.sql.PreparedSqlBuilder;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.SqlKind;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.entity.EntityPropertyType;
import org.seasar.doma.jdbc.entity.EntityType;
import org.seasar.doma.jdbc.entity.Property;

/**
 * @author taedium
 * 
 */
public class AutoBatchUpdateQuery<E> extends AutoBatchModifyQuery<E> implements
        BatchUpdateQuery {

    protected boolean versionIgnored;

    protected boolean optimisticLockExceptionSuppressed;

    public AutoBatchUpdateQuery(EntityType<E> entityType) {
        super(entityType);
    }

    @Override
    public void prepare() {
        assertNotNull(method, config, callerClassName, callerMethodName,
                entities, sqls);
        int size = entities.size();
        if (size == 0) {
            return;
        }
        executable = true;
        executionSkipCause = null;
        currentEntity = entities.get(0);
        preUpdate();
        prepareIdAndVersionPropertyTypes();
        validateIdExistent();
        prepareOptions();
        prepareOptimisticLock();
        prepareTargetPropertyTypes();
        prepareSql();
        entities.set(0, currentEntity);
        for (int i = 1; i < size; i++) {
            currentEntity = entities.get(i);
            preUpdate();
            prepareSql();
            entities.set(i, currentEntity);
        }
        assertEquals(entities.size(), sqls.size());
    }

    protected void preUpdate() {
        AutoBatchPreUpdateContext<E> context = new AutoBatchPreUpdateContext<E>(
                entityType, method, config);
        entityType.preUpdate(currentEntity, context);
        if (context.getNewEntity() != null) {
            currentEntity = context.getNewEntity();
        }
    }

    protected void prepareOptimisticLock() {
        if (versionPropertyType != null && !versionIgnored) {
            if (!optimisticLockExceptionSuppressed) {
                optimisticLockCheckRequired = true;
            }
        }
    }

    protected void prepareTargetPropertyTypes() {
        targetPropertyTypes = new ArrayList<EntityPropertyType<E, ?>>(
                entityType.getEntityPropertyTypes().size());
        for (EntityPropertyType<E, ?> p : entityType.getEntityPropertyTypes()) {
            if (!p.isUpdatable()) {
                continue;
            }
            if (p.isId()) {
                continue;
            }
            if (p.isVersion()) {
                targetPropertyTypes.add(p);
                continue;
            }
            if (!isTargetPropertyName(p.getName())) {
                continue;
            }
            targetPropertyTypes.add(p);
        }
    }

    protected void prepareSql() {
        Dialect dialect = config.getDialect();
        PreparedSqlBuilder builder = new PreparedSqlBuilder(config,
                SqlKind.BATCH_UPDATE);
        builder.appendSql("update ");
        builder.appendSql(entityType.getQualifiedTableName(dialect::applyQuote));
        builder.appendSql(" set ");
        for (EntityPropertyType<E, ?> propertyType : targetPropertyTypes) {
            Property<E, ?> property = propertyType.createProperty();
            property.load(currentEntity);
            builder.appendSql(propertyType.getColumnName(dialect::applyQuote));
            builder.appendSql(" = ");
            builder.appendParameter(property);
            if (propertyType.isVersion() && !versionIgnored) {
                builder.appendSql(" + 1");
            }
            builder.appendSql(", ");
        }
        builder.cutBackSql(2);
        if (idPropertyTypes.size() > 0) {
            builder.appendSql(" where ");
            for (EntityPropertyType<E, ?> propertyType : idPropertyTypes) {
                Property<E, ?> property = propertyType.createProperty();
                property.load(currentEntity);
                builder.appendSql(propertyType
                        .getColumnName(dialect::applyQuote));
                builder.appendSql(" = ");
                builder.appendParameter(property);
                builder.appendSql(" and ");
            }
            builder.cutBackSql(5);
        }
        if (versionPropertyType != null && !versionIgnored) {
            if (idPropertyTypes.size() == 0) {
                builder.appendSql(" where ");
            } else {
                builder.appendSql(" and ");
            }
            Property<E, ?> property = versionPropertyType.createProperty();
            property.load(currentEntity);
            builder.appendSql(versionPropertyType
                    .getColumnName(dialect::applyQuote));
            builder.appendSql(" = ");
            builder.appendParameter(property);
        }
        PreparedSql sql = builder.build();
        sqls.add(sql);
    }

    @Override
    public void incrementVersions() {
        if (versionPropertyType != null && !versionIgnored) {
            for (int i = 0, size = entities.size(); i < size; i++) {
                E entity = entities.get(i);
                E newEntity = versionPropertyType.increment(entityType, entity);
                entities.set(i, newEntity);
            }
        }
    }

    @Override
    public void complete() {
        for (int i = 0, len = entities.size(); i < len; i++) {
            currentEntity = entities.get(i);
            postUpdate();
            entities.set(i, currentEntity);
        }
    }

    protected void postUpdate() {
        AutoBatchPostUpdateContext<E> context = new AutoBatchPostUpdateContext<E>(
                entityType, method, config);
        entityType.postUpdate(currentEntity, context);
        if (context.getNewEntity() != null) {
            currentEntity = context.getNewEntity();
        }
    }

    public void setVersionIgnored(boolean versionIgnored) {
        this.versionIgnored |= versionIgnored;
    }

    public void setOptimisticLockExceptionSuppressed(
            boolean optimisticLockExceptionSuppressed) {
        this.optimisticLockExceptionSuppressed = optimisticLockExceptionSuppressed;
    }

    protected static class AutoBatchPreUpdateContext<E> extends
            AbstractPreUpdateContext<E> {

        public AutoBatchPreUpdateContext(EntityType<E> entityType,
                Method method, Config config) {
            super(entityType, method, config);
        }

        @Override
        public boolean isEntityChanged() {
            return true;
        }

        @Override
        public boolean isPropertyChanged(String propertyName) {
            validatePropertyDefined(propertyName);
            return true;
        }
    }

    protected static class AutoBatchPostUpdateContext<E> extends
            AbstractPostUpdateContext<E> {

        public AutoBatchPostUpdateContext(EntityType<E> entityType,
                Method method, Config config) {
            super(entityType, method, config);
        }

        @Override
        public boolean isPropertyChanged(String propertyName) {
            validatePropertyDefined(propertyName);
            return true;
        }
    }
}