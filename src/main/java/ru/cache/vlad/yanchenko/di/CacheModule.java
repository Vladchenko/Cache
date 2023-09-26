package ru.cache.vlad.yanchenko.di;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.cache.vlad.yanchenko.TwoLayerCache;
import ru.cache.vlad.yanchenko.arguments.CacheArgumentsParser;
import ru.cache.vlad.yanchenko.arguments.CacheArgumentsParserImpl;
import ru.cache.vlad.yanchenko.arguments.CacheArgumentsProcessor;
import ru.cache.vlad.yanchenko.arguments.CacheArgumentsValidatorImpl;
import ru.cache.vlad.yanchenko.caches.CacheType;
import ru.cache.vlad.yanchenko.caches.CachesFactory;
import ru.cache.vlad.yanchenko.caches.ICache;
import ru.cache.vlad.yanchenko.logging.CacheLoggingUtils;
import ru.cache.vlad.yanchenko.operating.CacheFeeder;
import ru.cache.vlad.yanchenko.operating.CacheProcessor;
import ru.cache.vlad.yanchenko.test.Testing;
import ru.cache.vlad.yanchenko.utils.CachePopulationUtils;

import static ru.cache.vlad.yanchenko.arguments.ArgumentsConstants.CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY;

@Module
public class CacheModule {

    private final String[] args;

    /**
     * Public constructor for dagger module of caches.
     *
     * @param args  command line arguments
     */
    public CacheModule(String[] args) {
        this.args = args;
    }

    @Provides
    @Singleton
    CacheArgumentsParser provideCacheArgumentsParser() {
        return new CacheArgumentsParserImpl();
    }

    @Provides
    @Singleton
    CachePopulationUtils provideCachePopulationUtils() {
        return new CachePopulationUtils<>();
    }

    @Provides
    @Singleton
    CacheLoggingUtils provideCacheLoggingUtils() {
        return new CacheLoggingUtils<>();
    }

    @Provides
    @Singleton
    CachesFactory provideCachesFactory() {
        return new CachesFactory<>();
    }

    @Provides
    @Singleton
    Logger provideLogger() {
        return LogManager.getLogger(TwoLayerCache.class);
    }

    @Provides
    @Singleton
    Map<String, String> provideCommandLineArguments(CacheArgumentsProcessor cacheArgumentsProcessor) {
        return cacheArgumentsProcessor.processArguments(args);
    }

    @Provides
    @Singleton
    CacheFeeder provideCacheFeeder(Map<String, String> commandLineArguments) {
        return new CacheFeeder<>(
                Integer.parseInt(commandLineArguments.get(CACHE_PIPELINE_RUN_TIMES_ARGUMENT_KEY))
        );
    }

    @Provides
    @Singleton
    CacheArgumentsValidatorImpl provideCacheArgumentsValidatorImpl(Logger logger) {
        return new CacheArgumentsValidatorImpl(logger);
    }

    @Provides
    @Singleton
    CacheArgumentsProcessor provideCacheArgumentsProcessor(CacheArgumentsParser argumentsParser,
                                                           CacheArgumentsValidatorImpl argumentsValidator) {
        return new CacheArgumentsProcessor(argumentsParser, argumentsValidator);
    }

    @Provides
    @Singleton
    TwoLayerCache provideTwoLayerCache(Logger logger,
                                       CacheFeeder cacheFeeder,
                                       CacheProcessor cacheProcessor,
                                       @DiskCache ICache diskCache,
                                       Map<String, String> commandLineArguments,
                                       @MemoryCache ICache memoryCache,
                                       CachePopulationUtils cachePopulationUtils) {
        return new TwoLayerCache<>(logger, diskCache, memoryCache, cacheFeeder, cacheProcessor,
                commandLineArguments, cachePopulationUtils);
    }

    @Provides
    @Singleton
    @MemoryCache
    ICache provideMemoryCache(CachesFactory cachesFactory,
                              Map<String, String> commandLineArguments) {
        return cachesFactory.createCache(CacheType.MEMORY, commandLineArguments);
    }

    @Provides
    @Singleton
    @DiskCache
    ICache provideDiskCache(CachesFactory cachesFactory,
                            Map<String, String> commandLineArguments) {
        return cachesFactory.createCache(CacheType.DISK, commandLineArguments);
    }

    @Provides
    @Singleton
    CacheProcessor provideCacheProcessor(
            Logger logger,
            CacheFeeder cacheFeeder,
            CacheLoggingUtils cacheLoggingUtils,
            @DiskCache ICache diskCache,
            Map<String, String> commandLineArguments,
            @MemoryCache ICache memoryCache) {
        return new CacheProcessor<>(
                logger,
                memoryCache,
                diskCache,
                cacheFeeder,
                commandLineArguments,
                cacheLoggingUtils);
    }

    @Provides
    @Singleton
    Testing provideTesting(
            Logger logger,
            CacheFeeder cacheFeeder,
            CacheProcessor cacheProcessor,
            CacheLoggingUtils cacheLoggingUtils,
            Map<String, String> commandLineArguments) {
        return new Testing(logger,
                cacheFeeder,
                commandLineArguments,
                cacheProcessor,
                cacheLoggingUtils);
    }
}
