package com.example.core.config

import java.text.NumberFormat
import java.util.Locale

object EnvironmentConfig {
    // Backend API base URL - Configurable for remote deployment
    var apiBaseUrl: String = "https://api.xcart-store.com/v1/"
        private set

    fun setCustomApiUrl(url: String) {
        apiBaseUrl = if (url.endsWith("/")) url else "$url/"
    }

    const val APP_NAME = "Xcart"
    const val STORE_EMAIL = "support@xcart.com.bd"
    const val STORE_PHONE = "+880 9612 345678"
    const val DEFAULT_SHIPPING_FEE = 70.0 // Standard Inside Dhaka
    const val OUTSIDE_DHAKA_SHIPPING_FEE = 120.0
    const val FREE_SHIPPING_THRESHOLD = 2000.0

    // Format currency in Bangladesh Taka (৳ BDT)
    fun formatBdt(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "BD"))
        formatter.maximumFractionDigits = 0
        formatter.minimumFractionDigits = 0
        return "৳${formatter.format(amount)}"
    }

    // Bangladesh Divisions & Districts
    val bangladeshDivisions = listOf(
        "Dhaka",
        "Chattogram",
        "Rajshahi",
        "Khulna",
        "Barishal",
        "Sylhet",
        "Rangpur",
        "Mymensingh"
    )

    val divisionDistricts = mapOf(
        "Dhaka" to listOf("Dhaka", "Gazipur", "Narayanganj", "Tangail", "Faridpur", "Manikganj", "Munshiganj", "Narsingdi", "Kishoreganj", "Gopalganj", "Madaripur", "Rajbari", "Shariatpur"),
        "Chattogram" to listOf("Chattogram", "Cox's Bazar", "Cumilla", "Feni", "Brahmanbaria", "Noakhali", "Chandpur", "Lakshmipur", "Khagrachhari", "Rangamati", "Bandarban"),
        "Rajshahi" to listOf("Rajshahi", "Bogura", "Pabna", "Sirajganj", "Naogaon", "Natore", "Chapai Nawabganj", "Joypurhat"),
        "Khulna" to listOf("Khulna", "Jashore", "Kushtia", "Satkhira", "Jhenaidah", "Chuadanga", "Bagerhat", "Magura", "Meherpur", "Narail"),
        "Barishal" to listOf("Barishal", "Patuakhali", "Bhola", "Pirojpur", "Jhalokati", "Barguna"),
        "Sylhet" to listOf("Sylhet", "Moulvibazar", "Habiganj", "Sunamganj"),
        "Rangpur" to listOf("Rangpur", "Dinajpur", "Gaibandha", "Kurigram", "Nilphamari", "Lalmonirhat", "Thakurgaon", "Panchagarh"),
        "Mymensingh" to listOf("Mymensingh", "Jamalpur", "Netrokona", "Sherpur")
    )

    val commonDhakaUpazilas = listOf(
        "Dhanmondi", "Gulshan", "Banani", "Uttara", "Mirpur", "Mohammadpur", 
        "Badda", "Tejgaon", "Motijheel", "Khilgaon", "Savar", "Keraniganj", 
        "Bashundhara R/A", "Lalbagh", "Wari", "Malibagh", "Rampura", "Pallabi"
    )
}
