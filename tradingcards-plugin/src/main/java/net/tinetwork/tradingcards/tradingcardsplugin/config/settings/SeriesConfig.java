package net.tinetwork.tradingcards.tradingcardsplugin.config.settings;

import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.config.settings.SeriesConfigurate;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.schedule.DateSchedule;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.api.model.schedule.Schedule;
import net.tinetwork.tradingcards.api.model.schedule.ScheduleType;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sarhatabaot
 */
public class SeriesConfig extends SeriesConfigurate {
    private Map<String,Series> seriesMap;
    private Map<String,ColorSeries> seriesColors;
    private final ColorSeries defaultColors = new ColorSeries("&a", "&b","&e","&c","&6");

    public SeriesConfig(final TradingCards plugin) throws ConfigurateException {
        super(plugin, "settings"+ File.separator, "series.yml", "settings");
    }

    @Override
    protected void initValues() throws ConfigurateException {
        this.seriesMap = new HashMap<>();
        loadSeries();
        loadSeriesColors();
    }
    private void loadSeriesColors() {
        this.seriesColors = new HashMap<>();
        for(String seriesId: this.seriesMap.keySet()) {
            final ConfigurationNode seriesNode = rootNode.node(seriesId);
            try {
                ColorSeries colorSeries = seriesNode.node("colors").get(ColorSeries.class,defaultColors);
                seriesColors.put(seriesId,colorSeries);
                plugin.debug(SeriesConfig.class,"Added "+colorSeries.toString());
            } catch (SerializationException|NullPointerException e){
                plugin.getLogger().info("Couldn't add="+seriesId);
                Util.logSevereException(e);
            }
        }
        for(Map.Entry<String,ColorSeries> entry: seriesColors.entrySet()) {
            plugin.debug(SeriesConfig.class,entry.getKey()+":"+entry.getValue().toString());
        }
    }
    private void loadSeries() {
        for(Map.Entry<Object, ? extends ConfigurationNode> nodeEntry: rootNode.childrenMap().entrySet()) {
            final String seriesKey = nodeEntry.getValue().key().toString();
            try {
                final Series series = getSeries(seriesKey);
                seriesMap.put(seriesKey,series);
                plugin.debug(SeriesConfig.class,"Added "+series.toString());
            } catch (SerializationException e){
                Util.logSevereException(e);
                plugin.debug(SeriesConfig.class,"Couldn't add="+seriesKey);
            }
        }
        plugin.debug(SeriesConfig.class,"Total Series="+ seriesMap.size());

    }

    private Series getSeries(final String key) throws SerializationException {
        return rootNode.node(key).get(Series.class);
    }

    public Map<String,Series> series() {
        return this.seriesMap;
    }

    public ColorSeries getColorSeries(final String key) {
        plugin.debug(SeriesConfig.class,"SeriesKey="+key);
        return this.seriesColors.get(key);
    }

    @Override
    protected void preLoaderBuild() {
        loaderBuilder.defaultOptions(opts -> opts.serializers(builder ->
                builder.registerExact(DateScheduleSerializer.TYPE, DateScheduleSerializer.INSTANCE)
                        .registerExact(SeriesSerializer.TYPE, SeriesSerializer.INSTANCE)
                        .registerExact(ColorSeriesSerializer.TYPE,ColorSeriesSerializer.INSTANCE)));
    }


    public static final class DateScheduleSerializer implements TypeSerializer<DateSchedule> {
        public static final DateScheduleSerializer INSTANCE = new DateScheduleSerializer();
        public static final Class<DateSchedule> TYPE = DateSchedule.class;

        //This should be changed for an abstract configuration, something that is mostly consistent
        //across all types.
        private static final String ACTIVE_FROM = "active-from";
        private static final String ACTIVE_UNTIL = "active-until";
        private  DateScheduleSerializer(){

        }
        @Override
        public DateSchedule deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            final LocalDate activeFromDate = LocalDate.parse(node.node(ACTIVE_FROM).getString());
            final LocalDate activeUntilDate = LocalDate.parse(node.node(ACTIVE_UNTIL).getString());

            return new DateSchedule(activeFromDate,activeUntilDate);
        }

        @Override
        public void serialize(final Type type, @Nullable final DateSchedule obj, final ConfigurationNode node) throws SerializationException {
            //
        }
    }

    public static final class SeriesSerializer implements TypeSerializer<Series> {
        public static final SeriesSerializer INSTANCE = new SeriesSerializer();
        public static final Class<Series> TYPE = Series.class;

        private static final String DISPLAY_NAME = "display-name";
        private static final String MODE = "mode";
        private static final String SCHEDULE = "schedule";

        private static final String SCHEDULE_TYPE = "type";

        private SeriesSerializer() {

        }

        @Override
        public Series deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            final String displayName = node.node(DISPLAY_NAME).getString();
            final String modeString = node.node(MODE).getString();

            final Mode mode = Mode.getMode(modeString);
            Schedule schedule = null;
            ScheduleType scheduleType = null;

            if(mode == Mode.SCHEDULED) {
                scheduleType = ScheduleType.valueOf(node.node(SCHEDULE).node(SCHEDULE_TYPE).getString());
            }


            if(scheduleType == ScheduleType.DATE) {
                schedule = node.node(SCHEDULE).get(Schedule.class);
            }


            return new Series(node.key().toString(),mode,displayName,schedule);
        }

        @Override
        public void serialize(final Type type, @Nullable final Series obj, final ConfigurationNode node) throws SerializationException {
            //
        }
    }


    public static class ColorSeriesSerializer implements TypeSerializer<ColorSeries> {
        public static final ColorSeriesSerializer INSTANCE = new ColorSeriesSerializer();
        public static final Class<ColorSeries> TYPE = ColorSeries.class;
        private static final String SERIES = "series";
        private static final String TYPE_PATH = "type";
        private static final String INFO = "info";
        private static final String ABOUT = "about";
        private static final String RARITY = "rarity";

        @Override
        public ColorSeries deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            final String typeFormat = node.node(TYPE_PATH).getString();
            final String info = node.node(INFO).getString();
            final String about = node.node(ABOUT).getString();
            final String rarity = node.node(RARITY).getString();
            final String series = node.node(SERIES).getString();
            return new ColorSeries(series,typeFormat,info,about,rarity);
        }

        @Override
        public void serialize(final Type type, final ColorSeries obj, final ConfigurationNode node) throws SerializationException {
            //not using this.
        }
    }

}
