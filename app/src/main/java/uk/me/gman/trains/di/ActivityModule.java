package uk.me.gman.trains.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import uk.me.gman.trains.ui.MainActivity;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();
}
