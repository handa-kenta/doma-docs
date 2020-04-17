package org.seasar.doma.internal.apt.processor.dao;

import java.math.BigDecimal;
import org.seasar.doma.Dao;

@Dao(config = MyConfig.class)
public interface VirtualDefaultMethodDao {

  BigDecimal execute(String aaa, Integer bbb);

  class DefaultImpls {
    public static BigDecimal execute(VirtualDefaultMethodDao $this, String aaa, Integer bbb) {
      return BigDecimal.ONE;
    }
  }
}