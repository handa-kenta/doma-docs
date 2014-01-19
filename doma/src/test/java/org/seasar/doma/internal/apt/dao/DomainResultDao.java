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
package org.seasar.doma.internal.apt.dao;

import java.util.List;
import java.util.Optional;

import org.seasar.doma.Dao;
import org.seasar.doma.LoadType;
import org.seasar.doma.Select;
import org.seasar.doma.jdbc.IterationCallback;

import example.domain.PhoneNumber;

/**
 * @author nakamura-to
 * 
 */
@Dao(config = MyConfig.class)
public interface DomainResultDao {

    @Select
    PhoneNumber selectSingleResult();

    @Select
    Optional<PhoneNumber> selectOptionalSingleResult();

    @Select
    List<PhoneNumber> selectResultList();

    @Select
    List<Optional<PhoneNumber>> selectOptionalResultList();

    @Select(load = LoadType.ITERATION)
    <R> R iterate(IterationCallback<PhoneNumber, R> callback);

    @Select(load = LoadType.ITERATION)
    <R> R iterateOptional(IterationCallback<PhoneNumber, R> callback);
}
