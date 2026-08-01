package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.DairyDatabase
import com.example.data.model.Customer
import com.example.data.model.Dispatch
import com.example.data.model.MilkCollection
import com.example.data.model.PaymentRecord
import com.example.data.model.RateChartConfig
import com.example.data.repository.DairyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class InventoryStockState(
    val cowStock: Double = 0.0,
    val buffaloStock: Double = 0.0,
    val totalStock: Double = 0.0,
    val totalInflowLiters: Double = 0.0,
    val totalOutflowLiters: Double = 0.0
)

data class TodayMetricsState(
    val totalMilkLt: Double = 0.0,
    val avgFat: Double = 0.0,
    val avgSnf: Double = 0.0,
    val totalAmount: Double = 0.0,
    val collectionCount: Int = 0
)

data class CustomerBillSummary(
    val customer: Customer,
    val startDate: String,
    val endDate: String,
    val collections: List<MilkCollection>,
    val payments: List<PaymentRecord>,
    val totalQuantityLt: Double,
    val avgFat: Double,
    val avgSnf: Double,
    val totalGrossBill: Double,
    val totalPayments: Double,
    val netBalanceDue: Double
)

class DairyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DairyRepository
    val rateConfig = RateChartConfig()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val selectedDate = MutableStateFlow(sdf.format(Calendar.getInstance().time))
    val selectedShift = MutableStateFlow("Morning") // "Morning", "Evening"
    val selectedMilkTypeFilter = MutableStateFlow("All") // "All", "Cow", "Buffalo"

    // Dialog & UI Sheet States
    val showAddCollectionDialog = MutableStateFlow(false)
    val showAddCustomerDialog = MutableStateFlow(false)
    val showAddDispatchDialog = MutableStateFlow(false)
    val showAddPaymentDialog = MutableStateFlow(false)
    val showRateCalculatorDialog = MutableStateFlow(false)

    val selectedCustomerForBill = MutableStateFlow<Customer?>(null)
    val billingStartDate = MutableStateFlow("")
    val billingEndDate = MutableStateFlow(sdf.format(Calendar.getInstance().time))

    val customers: StateFlow<List<Customer>>
    val allCollections: StateFlow<List<MilkCollection>>
    val allDispatches: StateFlow<List<Dispatch>>

    val todayMetrics: StateFlow<TodayMetricsState>
    val inventoryStock: StateFlow<InventoryStockState>
    val activeBillSummary = MutableStateFlow<CustomerBillSummary?>(null)

    init {
        val dao = DairyDatabase.getDatabase(application).dairyDao()
        repository = DairyRepository(dao)

        customers = repository.allCustomers.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        allCollections = repository.allCollections.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        allDispatches = repository.allDispatches.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        // Calculate metrics for selected date reactively
        todayMetrics = combine(allCollections, selectedDate) { collections, date ->
            val dateEntries = collections.filter { it.date == date }
            if (dateEntries.isEmpty()) {
                TodayMetricsState()
            } else {
                val totalLt = dateEntries.sumOf { it.quantityLiters }
                val totalAmt = dateEntries.sumOf { it.totalAmount }
                val avgFat = dateEntries.map { it.fatPercentage }.average().let { if (it.isNaN()) 0.0 else it }
                val avgSnf = dateEntries.map { it.snfPercentage }.average().let { if (it.isNaN()) 0.0 else it }
                TodayMetricsState(
                    totalMilkLt = totalLt,
                    avgFat = avgFat,
                    avgSnf = avgSnf,
                    totalAmount = totalAmt,
                    collectionCount = dateEntries.size
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodayMetricsState())

        // Calculate real-time stock levels reactively
        inventoryStock = combine(allCollections, allDispatches) { collections, dispatches ->
            val cowInflow = collections.filter { it.milkType == "Cow" }.sumOf { it.quantityLiters }
            val cowOutflow = dispatches.filter { it.milkType == "Cow" }.sumOf { it.quantityLiters }

            val buffaloInflow = collections.filter { it.milkType == "Buffalo" }.sumOf { it.quantityLiters }
            val buffaloOutflow = dispatches.filter { it.milkType == "Buffalo" }.sumOf { it.quantityLiters }

            val cowStock = kotlin.math.max(0.0, cowInflow - cowOutflow)
            val buffStock = kotlin.math.max(0.0, buffaloInflow - buffaloOutflow)

            InventoryStockState(
                cowStock = cowStock,
                buffaloStock = buffStock,
                totalStock = cowStock + buffStock,
                totalInflowLiters = cowInflow + buffaloInflow,
                totalOutflowLiters = cowOutflow + buffaloOutflow
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InventoryStockState())

        // Seed initial demo records if database is fresh
        viewModelScope.launch {
            if (repository.allCustomers.first().isEmpty()) {
                repository.seedInitialDataIfEmpty()
            }
        }

        // Set default billing start date to 1st of current month
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        billingStartDate.value = sdf.format(cal.time)
    }

    // --- ACTIONS ---

    fun setDate(date: String) {
        selectedDate.value = date
    }

    fun setShift(shift: String) {
        selectedShift.value = shift
    }

    fun addCustomer(code: String, name: String, phone: String, address: String, defaultMilkType: String, fixedRate: Double) {
        viewModelScope.launch {
            val customer = Customer(
                code = code.ifBlank { "C${System.currentTimeMillis() % 1000}" },
                name = name.trim(),
                phone = phone.trim(),
                address = address.trim(),
                defaultMilkType = defaultMilkType,
                fixedRate = fixedRate
            )
            repository.addCustomer(customer)
            showAddCustomerDialog.value = false
        }
    }

    fun addCollection(
        customer: Customer,
        date: String,
        shift: String,
        milkType: String,
        quantity: Double,
        fat: Double,
        snf: Double,
        rateOverride: Double = 0.0,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val rate = if (rateOverride > 0.0) rateOverride else rateConfig.calculateRate(milkType, fat, snf, customer.fixedRate)
            val total = quantity * rate
            val collection = MilkCollection(
                customerId = customer.id,
                customerName = customer.name,
                date = date,
                shift = shift,
                milkType = milkType,
                quantityLiters = quantity,
                fatPercentage = fat,
                snfPercentage = snf,
                ratePerLiter = rate,
                totalAmount = total,
                notes = notes
            )
            repository.addCollection(collection)
            showAddCollectionDialog.value = false
        }
    }

    fun deleteCollection(collection: MilkCollection) {
        viewModelScope.launch {
            repository.deleteCollection(collection)
        }
    }

    fun addDispatch(date: String, shift: String, milkType: String, quantity: Double, rate: Double, buyerName: String) {
        viewModelScope.launch {
            val dispatch = Dispatch(
                date = date,
                shift = shift,
                milkType = milkType,
                quantityLiters = quantity,
                ratePerLiter = rate,
                buyerName = buyerName.ifBlank { "Chilling Plant" },
                totalAmount = quantity * rate
            )
            repository.addDispatch(dispatch)
            showAddDispatchDialog.value = false
        }
    }

    fun deleteDispatch(dispatch: Dispatch) {
        viewModelScope.launch {
            repository.deleteDispatch(dispatch)
        }
    }

    fun addPayment(customerId: Int, date: String, amount: Double, mode: String, notes: String) {
        viewModelScope.launch {
            val payment = PaymentRecord(
                customerId = customerId,
                date = date,
                amount = amount,
                paymentMode = mode,
                notes = notes
            )
            repository.addPayment(payment)
            showAddPaymentDialog.value = false
            // Refresh billing summary if this customer is currently being viewed
            selectedCustomerForBill.value?.let { customer ->
                if (customer.id == customerId) {
                    generateBillForCustomer(customer, billingStartDate.value, billingEndDate.value)
                }
            }
        }
    }

    fun generateBillForCustomer(customer: Customer, startDate: String, endDate: String) {
        viewModelScope.launch {
            val collections = repository.getCollectionsForCustomerInRange(customer.id, startDate, endDate).first()
            val payments = repository.getPaymentsForCustomerInRange(customer.id, startDate, endDate).first()

            val totalQty = collections.sumOf { it.quantityLiters }
            val avgFat = if (collections.isNotEmpty()) collections.map { it.fatPercentage }.average() else 0.0
            val avgSnf = if (collections.isNotEmpty()) collections.map { it.snfPercentage }.average() else 0.0
            val grossBill = collections.sumOf { it.totalAmount }
            val totalPaid = payments.sumOf { it.amount }
            val netDue = grossBill - totalPaid + customer.initialBalance

            activeBillSummary.value = CustomerBillSummary(
                customer = customer,
                startDate = startDate,
                endDate = endDate,
                collections = collections,
                payments = payments,
                totalQuantityLt = totalQty,
                avgFat = avgFat,
                avgSnf = avgSnf,
                totalGrossBill = grossBill,
                totalPayments = totalPaid,
                netBalanceDue = netDue
            )
        }
    }
}
