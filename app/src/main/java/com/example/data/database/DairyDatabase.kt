package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.DairyDao
import com.example.data.model.Customer
import com.example.data.model.Dispatch
import com.example.data.model.MilkCollection
import com.example.data.model.PaymentRecord

@Database(
    entities = [
        Customer::class,
        MilkCollection::class,
        Dispatch::class,
        PaymentRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DairyDatabase : RoomDatabase() {
    abstract fun dairyDao(): DairyDao

    companion object {
        @Volatile
        private var INSTANCE: DairyDatabase? = null

        fun getDatabase(context: Context): DairyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DairyDatabase::class.java,
                    "dairy_khata_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
