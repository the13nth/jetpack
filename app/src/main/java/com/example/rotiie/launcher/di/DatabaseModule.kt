package com.example.rotiie.launcher.di

import com.example.rotiie.launcher.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LauncherModule {

    @Provides
    @Singleton
    fun provideRoutineRepository(): RoutineRepository {
        return RoutineRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideIntentionRepository(): IntentionRepository {
        return IntentionRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideUserActionRepository(): UserActionRepository {
        return UserActionRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideBehaviorPatternRepository(): BehaviorPatternRepository {
        return BehaviorPatternRepositoryImpl()
    }

    @Provides
    @Singleton
    fun providePredictionRepository(): PredictionRepository {
        return PredictionRepositoryImpl()
    }
}