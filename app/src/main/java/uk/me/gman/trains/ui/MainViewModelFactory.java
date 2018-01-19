package uk.me.gman.trains.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import uk.me.gman.trains.data.TrainsRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final TrainsRepository repository;

    @Inject
    public MainViewModelFactory(TrainsRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            //noinspection unchecked
            return (T) new MainViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
