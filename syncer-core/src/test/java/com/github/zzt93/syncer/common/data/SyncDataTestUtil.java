package com.github.zzt93.syncer.common.data;

import com.github.zzt93.syncer.data.SimpleEventType;
import com.github.zzt93.syncer.producer.dispatch.mysql.event.NamedFullRow;
import com.google.common.collect.Maps;

/**
 * @author zzt
 */
public class SyncDataTestUtil {
  public static SyncData update() {
    SyncData syncData = new SyncData(new BinlogDataId("mysql-bin.00001", 4, 10), SimpleEventType.UPDATE, "test", "test", "id", 1234L, new NamedFullRow(Maps.newHashMap()).setBeforeFull(Maps.newHashMap()));
    syncData.setContext(EvaluationFactory.context());
    return syncData;
  }

  public static SyncData update(String repo, String entity) {
    SyncData update = update();
    update.setRepo(repo).setEntity(entity);
    return update;
  }

  public static SyncData write() {
    SyncData update = update();
    update.toWrite();
    return update;
  }

  public static SyncData write(String repo, String entity) {
    SyncData write = write();
    write.setRepo(repo).setEntity(entity);
    return write;
  }

  public static SyncData delete() {
    SyncData update = update();
    update.toDelete();
    return update;
  }

  public static SyncData delete(String repo, String entity) {
    SyncData delete = delete();
    delete.setRepo(repo).setEntity(entity);
    return delete;
  }

}
