package com.example.data.repository

import com.example.core.model.BannerItem
import com.example.core.model.Category
import com.example.core.model.NotificationItem
import com.example.core.model.NotificationType
import com.example.core.model.Product
import com.example.core.model.ProductVariant
import com.example.core.model.Review

object ProductCatalogData {

    val categories = listOf(
        Category("cat_electronics", "Electronics", "ic_devices", 48, listOf("Smartphones", "Audio & Sound", "Tablets", "Wearables")),
        Category("cat_accessories", "Mobile Accessories", "ic_cable", 65, listOf("Fast Chargers", "Power Banks", "Cases & Covers", "Cables")),
        Category("cat_fashion", "Fashion", "ic_checkroom", 84, listOf("Men's Clothing", "Women's Ethnic", "Footwear", "Watches")),
        Category("cat_gadgets", "Smart Gadgets", "ic_watch", 32, listOf("Smart Watches", "Fitness Trackers", "Smart Rings", "Drones")),
        Category("cat_home", "Home & Living", "ic_home", 55, listOf("Kitchen Appliances", "Decor", "Lighting", "Bedding")),
        Category("cat_beauty", "Beauty & Care", "ic_spa", 40, listOf("Skincare", "Haircare", "Grooming", "Fragrances")),
        Category("cat_lifestyle", "Lifestyle & Bags", "ic_backpack", 29, listOf("Travel Backpacks", "Wallets", "Eyewear", "Luggage")),
        Category("cat_sports", "Sports & Fitness", "ic_fitness", 22, listOf("Gym Equipment", "Yoga Mats", "Sportswear", "Water Bottles"))
    )

    val banners = listOf(
        BannerItem(
            id = "banner_1",
            title = "Mega Shopping Festival",
            subtitle = "Up to 60% OFF on Top Tech & Gadgets",
            tag = "HOT DEAL",
            drawableResName = "banner_mega_sale_1791119940020",
            categoryTarget = "cat_electronics"
        ),
        BannerItem(
            id = "banner_2",
            title = "Super Lifestyle Deals",
            subtitle = "Exclusive Flash Sale across Bangladesh",
            tag = "LIMITED OFFER",
            drawableResName = "banner_super_deals_1791119961944",
            categoryTarget = "cat_gadgets"
        )
    )

    val products = listOf(
        Product(
            id = "prod_earbuds_pro",
            title = "AcousticPro ANC Active Noise Cancelling Wireless Earbuds",
            description = "High-fidelity spatial audio with 42dB Hybrid Active Noise Cancellation, Bluetooth 5.3, ultra-low latency gaming mode, and 36-hour total battery life with fast Qi wireless charging case. IPX5 water resistance for sweat and rain protection.",
            currentPrice = 2490.0,
            originalPrice = 4200.0,
            rating = 4.8f,
            reviewCount = 342,
            stockCount = 28,
            categoryId = "cat_electronics",
            categoryName = "Electronics",
            brand = "AcousticPro",
            images = listOf(
                "banner_mega_sale_1791119940020",
                "banner_super_deals_1791119961944"
            ),
            variants = listOf(
                ProductVariant("var_color", "Color", listOf("Midnight Black", "Arctic White", "Ocean Navy"))
            ),
            specifications = mapOf(
                "Driver Size" to "12mm Dynamic Drivers",
                "Noise Cancellation" to "42dB Hybrid ANC",
                "Battery Playback" to "Up to 36 Hours",
                "Connectivity" to "Bluetooth 5.3",
                "Water Rating" to "IPX5 Sweatproof",
                "Warranty" to "1 Year Official Warranty"
            ),
            isFlashDeal = true,
            isFeatured = true,
            deliveryDays = "1-3 Days",
            freeShipping = true
        ),
        Product(
            id = "prod_smartwatch_ultra",
            title = "Apex Ultra 2.04\" AMOLED Smartwatch with Bluetooth Calling",
            description = "Stunning 2.04-inch Always-On AMOLED curved display with 1000 nits peak brightness. Features Bluetooth calling with noise-canceling mic, 24/7 heart rate, SpO2 blood oxygen tracking, 120+ sports modes, and durable titanium alloy frame.",
            currentPrice = 3850.0,
            originalPrice = 5900.0,
            rating = 4.9f,
            reviewCount = 512,
            stockCount = 15,
            categoryId = "cat_gadgets",
            categoryName = "Smart Gadgets",
            brand = "Apex Tech",
            images = listOf(
                "banner_super_deals_1791119961944",
                "banner_mega_sale_1791119940020"
            ),
            variants = listOf(
                ProductVariant("var_strap", "Strap Color", listOf("Graphite Black", "Ocean Orange", "Silver Alpine"))
            ),
            specifications = mapOf(
                "Display" to "2.04\" Curved AMOLED, 368x448",
                "Battery" to "420mAh (Up to 10 days)",
                "Sensors" to "HR, SpO2, Sleep, Pedometer",
                "Frame Material" to "Aero-grade Titanium Alloy",
                "Water Resistance" to "IP68 Certified",
                "Compatibility" to "Android & iOS"
            ),
            isFlashDeal = true,
            isFeatured = true,
            deliveryDays = "2-4 Days",
            freeShipping = true
        ),
        Product(
            id = "prod_gan_charger_65w",
            title = "VoltMax 65W GaN Fast Charger 3-Port (2x USB-C + USB-A)",
            description = "Next-generation GaN III semiconductor technology providing blazing fast 65W Power Delivery. Charges laptops, tablets, and smartphones simultaneously. Features comprehensive multi-protect temperature control and fold-out prongs.",
            currentPrice = 1650.0,
            originalPrice = 2500.0,
            rating = 4.7f,
            reviewCount = 189,
            stockCount = 42,
            categoryId = "cat_accessories",
            categoryName = "Mobile Accessories",
            brand = "VoltMax",
            images = listOf(
                "banner_mega_sale_1791119940020"
            ),
            variants = listOf(
                ProductVariant("var_color", "Color", listOf("Classic Black", "Pure White"))
            ),
            specifications = mapOf(
                "Total Output" to "65W Max",
                "Ports" to "2x Type-C + 1x USB-A",
                "Technology" to "GaN III Fast Charging",
                "Fast Charging Protocols" to "PD 3.0, QC 4.0, PPS",
                "Safety" to "Over-voltage, Over-temp Protection"
            ),
            isFlashDeal = false,
            isFeatured = true,
            isNewArrival = true,
            deliveryDays = "1-2 Days",
            freeShipping = false
        ),
        Product(
            id = "prod_powerbank_20000",
            title = "NovaCharge 20000mAh 22.5W Super Charge Metal Power Bank",
            description = "High capacity 20,000mAh lithium-polymer battery encased in premium anodized aluminum shell. Features intelligent digital LED battery indicator, two-way fast charging, and dual USB-A + Type-C PD ports.",
            currentPrice = 1890.0,
            originalPrice = 2800.0,
            rating = 4.6f,
            reviewCount = 274,
            stockCount = 33,
            categoryId = "cat_accessories",
            categoryName = "Mobile Accessories",
            brand = "NovaCharge",
            images = listOf(
                "banner_super_deals_1791119961944"
            ),
            variants = listOf(
                ProductVariant("var_color", "Color", listOf("Space Gray", "Midnight Blue"))
            ),
            specifications = mapOf(
                "Capacity" to "20,000mAh / 74Wh",
                "Max Output" to "22.5W Huawei SuperCharge / 20W PD",
                "Input" to "Type-C 18W Fast Recharge",
                "Material" to "Anodized Aluminum Shell",
                "Display" to "Precision Digital Percentage Display"
            ),
            isFlashDeal = true,
            isFeatured = false,
            deliveryDays = "2-4 Days",
            freeShipping = false
        ),
        Product(
            id = "prod_men_oxford_shirt",
            title = "UrbanFit Premium 100% Cotton Formal Oxford Long Sleeve Shirt",
            description = "Crafted from 100% combed long-staple cotton for unmatched breathability and comfort in Bangladesh's weather. Wrinkle-resistant finish, tailored slim-fit silhouette, and reinforced mother-of-pearl buttons.",
            currentPrice = 1250.0,
            originalPrice = 1950.0,
            rating = 4.5f,
            reviewCount = 145,
            stockCount = 50,
            categoryId = "cat_fashion",
            categoryName = "Fashion",
            brand = "UrbanFit BD",
            images = listOf(
                "banner_mega_sale_1791119940020"
            ),
            variants = listOf(
                ProductVariant("var_size", "Size", listOf("M (38)", "L (40)", "XL (42)", "XXL (44)")),
                ProductVariant("var_color", "Color", listOf("Sky Blue", "Crisp White", "Navy Blue", "Olive Green"))
            ),
            specifications = mapOf(
                "Fabric" to "100% Combed Oxford Cotton",
                "Fit" to "Modern Tailored Fit",
                "Collar" to "Button-Down Collar",
                "Care" to "Machine Wash Cold, Gentle Cycle",
                "Origin" to "Made in Bangladesh"
            ),
            isFlashDeal = false,
            isFeatured = true,
            isNewArrival = true,
            deliveryDays = "2-3 Days",
            freeShipping = false
        ),
        Product(
            id = "prod_leather_wallet",
            title = "Heritage Handcrafted Genuine Cowhide RFID Bifold Wallet",
            description = "Handmade from 100% genuine full-grain cowhide leather that ages beautifully with a rich patina. Equipped with advanced RFID blocking lining to protect your cards against electronic theft.",
            currentPrice = 890.0,
            originalPrice = 1500.0,
            rating = 4.8f,
            reviewCount = 98,
            stockCount = 60,
            categoryId = "cat_lifestyle",
            categoryName = "Lifestyle & Bags",
            brand = "Heritage Leather",
            images = listOf(
                "banner_super_deals_1791119961944"
            ),
            variants = listOf(
                ProductVariant("var_color", "Leather Tone", listOf("Antique Brown", "Jet Black", "Cognac Tan"))
            ),
            specifications = mapOf(
                "Material" to "100% Genuine Full-Grain Leather",
                "Slots" to "8 Card Slots, 2 Cash Compartments",
                "Security" to "Certified RFID Protection",
                "Dimensions" to "11.5cm x 9.5cm",
                "Packaging" to "Gift Box Included"
            ),
            isFlashDeal = false,
            isFeatured = false,
            deliveryDays = "2-4 Days",
            freeShipping = false
        ),
        Product(
            id = "prod_travel_backpack",
            title = "Ventura 35L Water-Resistant Anti-Theft Laptop Travel Backpack",
            description = "Engineered for daily commute and international travel. Features dedicated padded compartment for up to 16.5\" laptops, hidden anti-theft back pocket, integrated USB charging pass-through, and breathable ergonomic honeycomb back panel.",
            currentPrice = 2150.0,
            originalPrice = 3400.0,
            rating = 4.9f,
            reviewCount = 310,
            stockCount = 20,
            categoryId = "cat_lifestyle",
            categoryName = "Lifestyle & Bags",
            brand = "Ventura Gear",
            images = listOf(
                "banner_mega_sale_1791119940020"
            ),
            variants = listOf(
                ProductVariant("var_color", "Color", listOf("Charcoal Gray", "Stealth Black", "Navy Blue"))
            ),
            specifications = mapOf(
                "Capacity" to "35 Liters",
                "Laptop Sleeve" to "Fits up to 16.5 Inch Laptop",
                "Material" to "High-Density 900D Oxford Fabric",
                "Water Protection" to "Water-Repellent Coating",
                "Zippers" to "Heavy-Duty YKK Dual Zippers"
            ),
            isFlashDeal = true,
            isFeatured = true,
            deliveryDays = "2-3 Days",
            freeShipping = true
        ),
        Product(
            id = "prod_ring_light",
            title = "StudioPro 12\" LED Ring Light with 7ft Heavy Duty Tripod",
            description = "Professional creator lighting setup with 3 color modes (Warm, Natural, Cool White) and 10 adjustable brightness levels. Includes 360-degree rotating phone holder, Bluetooth wireless shutter remote, and sturdy 2.1m aluminum tripod stand.",
            currentPrice = 1450.0,
            originalPrice = 2200.0,
            rating = 4.6f,
            reviewCount = 175,
            stockCount = 25,
            categoryId = "cat_gadgets",
            categoryName = "Smart Gadgets",
            brand = "StudioPro",
            images = listOf(
                "banner_super_deals_1791119961944"
            ),
            variants = listOf(
                ProductVariant("var_kit", "Package", listOf("Standard Stand (2.1m)", "Desk Clamp Edition"))
            ),
            specifications = mapOf(
                "Diameter" to "12 Inches (30cm)",
                "Power" to "USB 5V Powered",
                "Color Temperature" to "3000K - 6500K",
                "Tripod Height" to "Adjustable from 65cm to 210cm",
                "Remote" to "Bluetooth Wireless Camera Shutter"
            ),
            isFlashDeal = false,
            isFeatured = false,
            deliveryDays = "2-4 Days",
            freeShipping = false
        )
    )

    val sampleReviews = listOf(
        Review("rev_1", "Tanvir Ahmed", 5.0f, "Yesterday", "Extremely impressed with the build quality! Sound is super crisp, ANC works like a charm. Fast delivery inside Dhaka within 24 hours.", true),
        Review("rev_2", "Farhana Islam", 4.5f, "3 days ago", "Original authentic product. Battery lasts almost 4 days for my normal usage. Loved the packaging.", true),
        Review("rev_3", "Shahadat Hossain", 5.0f, "1 week ago", "Best value for money in Bangladesh right now. Cash on delivery was seamless, delivery rider was very polite.", true)
    )

    val initialNotifications = listOf(
        NotificationItem(
            id = "notif_1",
            title = "Order Confirmed!",
            message = "Your order #XC-84920 has been verified and is being packed for shipment.",
            type = NotificationType.ORDER,
            date = "Today, 11:30 AM",
            isRead = false
        ),
        NotificationItem(
            id = "notif_2",
            title = "Mega Shopping Festival is LIVE!",
            message = "Get up to 60% discount on headphones, smart watches and electronics today.",
            type = NotificationType.PROMO,
            date = "Yesterday",
            isRead = false
        ),
        NotificationItem(
            id = "notif_3",
            title = "Price Drop Alert",
            message = "An item in your wishlist (Apex Ultra 2.04\" AMOLED Smartwatch) dropped by ৳350!",
            type = NotificationType.PRICE_DROP,
            date = "2 days ago",
            isRead = true
        )
    )
}
