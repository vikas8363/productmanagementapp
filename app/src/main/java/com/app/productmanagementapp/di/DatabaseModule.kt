package com.app.productmanagementapp.di


import android.content.Context
import androidx.room.Room
import com.app.productmanagementapp.data.dao.ProductDao
import com.app.productmanagementapp.data.database.ProductDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideProductDatabase(
        @ApplicationContext context: Context
    ): ProductDatabase {

        return Room.databaseBuilder(
            context,
            ProductDatabase::class.java,
            "product_management_database"
        )
            .build()
    }


    @Provides
    @Singleton
    fun provideProductDao(
        database: ProductDatabase
    ): ProductDao {

        return database.productDao()
    }
}
