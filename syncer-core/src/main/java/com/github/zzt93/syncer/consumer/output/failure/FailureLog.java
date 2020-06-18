package com.github.zzt93.syncer.consumer.output.failure;

import com.github.zzt93.syncer.common.util.FileUtil;
import com.github.zzt93.syncer.common.util.NamedThreadFactory;
import com.github.zzt93.syncer.config.consumer.output.FailureLogConfig;
import com.github.zzt93.syncer.consumer.output.Resource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zzt
 */
public class FailureLog<T> implements Resource {

  private static final Gson gson = new GsonBuilder()
      .disableHtmlEscaping()
      .create();
  private final Logger logger = LoggerFactory.getLogger(FailureLog.class);
  private final Type type;
  private final AtomicInteger itemCount = new AtomicInteger(0);
  private final BufferedWriter writer;
  private final int countLimit;
  private final ScheduledExecutorService service;

  private FailureLog(Path path, FailureLogConfig limit, TypeToken token)
      throws FileNotFoundException {
    countLimit = limit.getCountLimit();
    service = Executors.newScheduledThreadPool(1, new NamedThreadFactory("syncer-failure-log-timer"));
    service
        .scheduleWithFixedDelay(() -> itemCount.set(0), limit.getTimeLimit(), limit.getTimeLimit(),
            limit.getUnit());

    type = token.getType();
    assert token.getRawType() == FailureEntry.class;
    FileUtil.createFile(path, (e) -> logger.error("Fail to create failure log file [{}]", path, e));
    writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(path.toFile(), true)));
  }

  private List<T> recover(Path path) {
    List<T> res = new LinkedList<>();
    // TODO 18/2/1 @see SyncDeserializer
    return res;
  }

  public boolean log(T data, String errorMsg) {
    itemCount.incrementAndGet();
    try {
      FailureEntry<T> failureEntry = new FailureEntry<>(data, LocalDateTime.now(), errorMsg);
      writer.write(gson.toJson(failureEntry, type));
      writer.newLine();
      writer.flush();
    } catch (IOException e) {
      logger.error("Fail to convert to json {}", data, e);
    } catch (StackOverflowError e) {
      logger.error("Fail to convert to json {}", data);
      throw e;
    }
    int count = itemCount.get();
    if (count > countLimit) {
      logger.error("Too many failure: {} > {}", count, countLimit);
//      throw new FailureException("Too many failed items, abort and need human influence");
    }
    return true;
  }

  @Override
  public void cleanup() throws IOException {
    service.shutdown();
    writer.close();
  }

  public static <T> FailureLog<T> getLogger(Path path, FailureLogConfig limit, TypeToken token) {
    try {
      return new FailureLog<>(path, limit, token);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("Impossible state", e);
    }
  }
}
