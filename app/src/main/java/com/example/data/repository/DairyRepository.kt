package com.example.data.repository

import com.example.data.dao.DairyDao
import com.example.data.model.Customer
import com.example.data.model.Dispatch
import com.example.data.model.MilkCollection
import com.example.data.model.PaymentRecord
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DairyRepository(private val dao: DairyDao) {

    val allCustomers: Flow<List<Customer>> = dao.getAllCustomers()
    val allCollections: Flow<List<MilkCollection>> = dao.getAllCollections()
    val allDispatches: Flow<List<Dispatch>> = dao.getAllDispatches()

    fun getCollectionsByDate(date: String): Flow<List<MilkCollection>> = dao.getCollectionsByDate(date)

    fun getCollectionsByDateAndShift(date: String, shift: String): Flow<List<MilkCollection>> =
        dao.getCollectionsByDateAndShift(date, shift)

    fun getCollectionsForCustomer(customerId: Int): Flow<List<MilkCollection>> =
        dao.getCollectionsForCustomer(customerId)

    fun getCollectionsForCustomerInRange(customerId: Int, startDate: String, endDate: String): Flow<List<MilkCollection>> =
        dao.getCollectionsForCustomerInRange(customerId, startDate, endDate)

    fun getPaymentsForCustomer(customerId: Int): Flow<List<PaymentRecord>> =
        dao.getPaymentsForCustomer(customerId)

    fun getPaymentsForCustomerInRange(customerId: Int, startDate: String, endDate: String): Flow<List<PaymentRecord>> =
        dao.getPaymentsForCustomerInRange(customerId, startDate, endDate)

    suspend fun getCustomerById(customerId: Int): Customer? = dao.getCustomerById(customerId)

    suspend fun addCustomer(customer: Customer): Long = dao.insertCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = dao.updateCustomer(customer)
    suspend fun deleteCustomer(customer: Customer) = dao.deleteCustomer(customer)

    suspend fun addCollection(collection: MilkCollection): Long = dao.insertCollection(collection)
    suspend fun deleteCollection(collection: MilkCollection) = dao.deleteCollection(collection)

    suspend fun addDispatch(dispatch: Dispatch): Long = dao.insertDispatch(dispatch)
    suspend fun deleteDispatch(dispatch: Dispatch) = dao.deleteDispatch(dispatch)

    suspend fun addPayment(payment: PaymentRecord): Long = dao.insertPayment(payment)
    suspend fun deletePayment(payment: PaymentRecord) = dao.deletePayment(payment)

    suspend fun seedInitialDataIfEmpty() {
        // Pre-populate realistic milk collections matching image demo if DB empty
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val todayStr = sdf.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = sdf.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, -1)
        val prevDayStr = sdf.format(cal.time)

        val customerList = listOf(
            Customer(code = "F101", name = "Bahubali", phone = "9876543210", address = "Village Sector 1", defaultMilkType = "Cow"),
            Customer(code = "F102", name = "Katappa", phone = "9876543211", address = "Farm House A", defaultMilkType = "Buffalo"),
            Customer(code = "F103", name = "Shivgami", phone = "9876543212", address = "Royal Agro", defaultMilkType = "Cow"),
            Customer(code = "F104", name = "Devsena", phone = "9876543213", address = "Green Valley", defaultMilkType = "Cow")
        )

        for (cust in customerList) {
            val id = dao.insertCustomer(cust).toInt()

            // Insert sample entries for today and yesterday
            val (type, qty, fat, snf, rate) = when (cust.name) {
                "Bahubali" -> Quintuple("Cow", 200.0, 4.5, 8.8, 43.0)
                "Katappa" -> Quintuple("Buffalo", 500.0, 6.2, 9.1, 56.5)
                "Shivgami" -> Quintuple("Cow", 20.0, 4.2, 8.6, 41.5)
                else -> Quintuple("Cow", 20.0, 4.0, 8.5, 40.0)
            }

            // Today Morning
            dao.insertCollection(
                MilkCollection(
                    customerId = id,
                    customerName = cust.name,
                    date = todayStr,
                    shift = "Morning",
                    milkType = type,
                    quantityLiters = qty,
                    fatPercentage = fat,
                    snfPercentage = snf,
                    ratePerLiter = rate,
                    totalAmount = qty * rate
                )
            )

            // Yesterday Evening
            dao.insertCollection(
                MilkCollection(
                    customerId = id,
                    customerName = cust.name,
                    date = yesterdayStr,
                    shift = "Evening",
                    milkType = type,
                    quantityLiters = qty * 0.9,
                    fatPercentage = fat,
                    snfPercentage = snf,
                    ratePerLiter = rate,
                    totalAmount = (qty * 0.9) * rate
                )
            )

            // Day before Yesterday Morning
            dao.insertCollection(
                MilkCollection(
                    customerId = id,
                    customerName = cust.name,
                    date = prevDayStr,
                    shift = "Morning",
                    milkType = type,
                    quantityLiters = qty * 0.95,
                    fatPercentage = fat,
                    snfPercentage = snf,
                    ratePerLiter = rate,
                    totalAmount = (qty * 0.95) * rate
                )
            )
        }

        // Insert sample dispatch outflow
        dao.insertDispatch(
            Dispatch(
                date = todayStr,
                shift = "Morning",
                milkType = "Cow",
                quantityLiters = 180.0,
                ratePerLiter = 45.0,
                buyerName = "City Dairy Chilling Center",
                totalAmount = 180.0 * 45.0
            )
        )
    }

    private data class Quintuple<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)
}
