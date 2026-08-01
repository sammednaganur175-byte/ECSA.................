package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Customer
import com.example.data.model.Dispatch
import com.example.data.model.MilkCollection
import com.example.data.model.PaymentRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DairyDao {

    // --- CUSTOMERS ---
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :customerId LIMIT 1")
    suspend fun getCustomerById(customerId: Int): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)


    // --- MILK COLLECTIONS ---
    @Query("SELECT * FROM milk_collections ORDER BY date DESC, id DESC")
    fun getAllCollections(): Flow<List<MilkCollection>>

    @Query("SELECT * FROM milk_collections WHERE date = :date ORDER BY id DESC")
    fun getCollectionsByDate(date: String): Flow<List<MilkCollection>>

    @Query("SELECT * FROM milk_collections WHERE date = :date AND shift = :shift ORDER BY id DESC")
    fun getCollectionsByDateAndShift(date: String, shift: String): Flow<List<MilkCollection>>

    @Query("SELECT * FROM milk_collections WHERE customerId = :customerId ORDER BY date DESC, id DESC")
    fun getCollectionsForCustomer(customerId: Int): Flow<List<MilkCollection>>

    @Query("SELECT * FROM milk_collections WHERE customerId = :customerId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC, id ASC")
    fun getCollectionsForCustomerInRange(customerId: Int, startDate: String, endDate: String): Flow<List<MilkCollection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: MilkCollection): Long

    @Delete
    suspend fun deleteCollection(collection: MilkCollection)


    // --- DISPATCHES (OUTFLOWS) ---
    @Query("SELECT * FROM dispatches ORDER BY date DESC, id DESC")
    fun getAllDispatches(): Flow<List<Dispatch>>

    @Query("SELECT * FROM dispatches WHERE date = :date ORDER BY id DESC")
    fun getDispatchesByDate(date: String): Flow<List<Dispatch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispatch(dispatch: Dispatch): Long

    @Delete
    suspend fun deleteDispatch(dispatch: Dispatch)


    // --- PAYMENTS ---
    @Query("SELECT * FROM payments WHERE customerId = :customerId ORDER BY date DESC")
    fun getPaymentsForCustomer(customerId: Int): Flow<List<PaymentRecord>>

    @Query("SELECT * FROM payments WHERE customerId = :customerId AND date BETWEEN :startDate AND :endDate")
    fun getPaymentsForCustomerInRange(customerId: Int, startDate: String, endDate: String): Flow<List<PaymentRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentRecord): Long

    @Delete
    suspend fun deletePayment(payment: PaymentRecord)
}
