package com.example.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AddressDao
import com.example.data.local.dao.CartDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.RecentProductDao
import com.example.data.local.dao.SearchHistoryDao
import com.example.data.local.dao.WishlistDao
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.OrderRecordEntity
import com.example.data.local.entity.RecentProductEntity
import com.example.data.local.entity.SavedAddressEntity
import com.example.data.local.entity.SearchHistoryEntity
import com.example.data.local.entity.WishlistItemEntity

@Database(
    entities = [
        CartItemEntity::class,
        WishlistItemEntity::class,
        OrderRecordEntity::class,
        RecentProductEntity::class,
        SearchHistoryEntity::class,
        SavedAddressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class XcartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun recentProductDao(): RecentProductDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun addressDao(): AddressDao

    companion object {
        @Volatile
        private var INSTANCE: XcartDatabase? = null

        fun getDatabase(context: Context): XcartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    XcartDatabase::class.java,
                    "xcart_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
