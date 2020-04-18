package com.github.zzt93.syncer.config.code;

import com.github.zzt93.syncer.data.SyncData;
import com.github.zzt93.syncer.data.util.MethodFilter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zzt
 */
public class CheckMongoType implements MethodFilter {

  @Override
  public void filter(List<SyncData> list) {
    SyncData sync = list.get(0);
    for (Map simple : ((List<Map>) sync.getField("simples"))) {
      Long id = (Long) simple.get("id");
      Integer tinyint = (Integer) simple.get("tinyint");
      Long bigint = (Long) simple.get("bigint");
      byte[] bytes = (byte[]) simple.get("bytes");
      String varchar = (String) simple.get("varchar");
      BigDecimal decimal = (BigDecimal) simple.get("decimal");
      Double aDouble = (Double) simple.get("aDouble");
      org.bson.BsonTimestamp timestamp = (org.bson.BsonTimestamp) simple.get("timestamp");
    }
    Map nestedIn = (Map) sync.getField("nestedIn");
    Long id = (Long) nestedIn.get("id");
    Date time = (Date) nestedIn.get("time");
    String currency = (String) nestedIn.get("currency");
    String total = (String) nestedIn.get("total");
    Integer quantity = (Integer) nestedIn.get("quantity");
    Integer type = (Integer) nestedIn.get("type");
    String name = (String) nestedIn.get("name");
    String unit = (String) nestedIn.get("unit");
  }

}