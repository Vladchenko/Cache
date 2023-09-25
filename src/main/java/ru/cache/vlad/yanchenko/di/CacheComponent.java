package ru.cache.vlad.yanchenko.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.cache.vlad.yanchenko.TwoLayerCache;
import ru.cache.vlad.yanchenko.test.Testing;

/** Dagger component */
@Singleton
@Component(modules = {CacheModule.class})
public interface CacheComponent {
    Testing getTesting();
    TwoLayerCache getTwoLayerCache();
}
