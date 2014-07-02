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
package org.seasar.doma.jdbc.tx;

import java.io.Serializable;

import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.message.Message;

/**
 * トランザクションがまだ開始されていない場合にスローされる例外です。
 * 
 * @author nakamura-to
 * @since 2.0.0
 */
public class TransactionNotYetBegunException extends JdbcException
        implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * インスタンスを構築します。
     * 
     * @param message
     *            メッセージ
     * @param args
     *            メッセージの引数
     */
    public TransactionNotYetBegunException(Message message, Object... args) {
        super(message, args);
    }

}