package com.ft.exchlab.base.connectors.exmo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.InterruptedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class QuotesDumper {

    public static final int DUMP_INTERVAL_SEC = 5;

    static final Logger log = LoggerFactory.getLogger(QuotesDumper.class);

    public static void main(String[] args) {


        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1,
                new ThreadFactoryBuilder()
                        .setNameFormat("timer thread")
                        .build()
        );

        final Path path = Paths.get("t_"+System.currentTimeMillis() + "_ticker.txt");
        log.info("Write to file {}", path.toAbsolutePath());

        try(BufferedWriter writer = Files.newBufferedWriter(
                path,
                Charset.defaultCharset(),
                StandardOpenOption.CREATE_NEW
        )){

            ScheduledFuture<?> task = executor.scheduleAtFixedRate(() -> dumpQuotes(writer), 0, DUMP_INTERVAL_SEC, TimeUnit.SECONDS);

            task.get();
        }
        catch (Exception ex){
            log.error("Error ",ex);
        }
        finally {
            executor.shutdownNow();
        }
    }

    static void dumpQuotes(BufferedWriter writer){

        try {
          
            ExmoConnector connector = new ExmoConnector("A","A");

            String result = connector.Request("ticker", new HashMap<>());
            //System.out.println(result);

            writer.append(System.currentTimeMillis()+"").append("=").append(result).append(System.lineSeparator());
            writer.flush();

        }
        catch (Exception ex){
            log.error("", ex);
        }
    }

}
