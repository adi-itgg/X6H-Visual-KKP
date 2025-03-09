package io.aitech.pv;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import io.aitech.pv.config.AppConfiguration;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.TimeZone;

public class MainVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            args = new String[]{MainVerticle.class.getName(), "-vt"};
        }
        VertxApplication vertxApplication = new VertxApplication(args);
        vertxApplication.launch();
    }

    @Override
    public void start() {
        // set default locale & time zone
        Locale.setDefault(Locale.forLanguageTag("in-ID"));
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));


        // setup jackson
        DatabindCodec.mapper().findAndRegisterModules().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);


        // load configuration
        final ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setOptional(true)
                .setConfig(new JsonObject().put("path", "config.json"));
        final ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .setIncludeDefaultStores(false)
                .setScanPeriod(-1L)
                .addStore(fileStore);
        final ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        final AppConfiguration config = retriever.getConfig().await().mapTo(AppConfiguration.class);
        retriever.close();


        // setup database
        log.info("Connecting to database... host: {}, port: {}, database: {}, user: {}", config.postgres().host(), config.postgres().port(), config.postgres().database(), config.postgres().user());
        final PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(config.postgres().port())
                .setHost(config.postgres().host())
                .setDatabase(config.postgres().database())
                .setUser(config.postgres().user())
                .setPassword(config.postgres().password())
                .setPipeliningLimit(config.postgres().pipeliningLimit());
        final PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(config.postgres().poolSize());
        final Pool pool = PgBuilder
                .pool()
                .connectingTo(connectOptions)
                .with(poolOptions)
                .using(vertx)
                .build();


        // setup look and feel
        vertx.executeBlocking(() -> {
            FlatRobotoFont.install();
            FlatLaf.registerCustomDefaultsSource("themes");
            FlatMacDarkLaf.setup();
            UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
            return null;
        }).await();
        // show main form
        EventQueue.invokeLater(() -> new MainFrame(vertx, context, pool, config));
    }

}
