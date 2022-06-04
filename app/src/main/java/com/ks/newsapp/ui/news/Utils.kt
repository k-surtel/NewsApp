package com.ks.newsapp.ui.news

object Utils {

    fun mapCountry(country: String): String? {
        return when(country) {
            "United Arab Emirates" -> "ae"
            "Argentina" -> "ar"
            "Austria" -> "at"
            "Australia" -> "au"
            "Belgium" -> "be"
            "Bulgaria" -> "bg"
            "Brazil" -> "br"
            "Canada" -> "ca"
            "Switzerland" -> "ch"
            "China" -> "cn"
            "Colombia" -> "co"
            "Cuba" -> "cu"
            "Czechia" -> "cz"
            "Germany" -> "de"
            "Egypt" -> "eg"
            "France" -> "fr"
            "United Kingdom" -> "gb"
            "Greece" -> "gr"
            "Hong Kong" -> "hk"
            "Hungary" -> "hu"
            "Indonesia" -> "id"
            "Ireland" -> "ie"
            "Israel" -> "il"
            "India" -> "in"
            "Italy" -> "it"
            "Japan" -> "jp"
            "South Korea" -> "kr"
            "Lithuania" -> "lt"
            "Latvia" -> "lv"
            "Morocco" -> "ma"
            "Mexico" -> "mx"
            "Malaysia" -> "my"
            "Nigeria" -> "ng"
            "The Netherlands" -> "nl"
            "Norway" -> "no"
            "New Zealand" -> "nz"
            "The Philippines" -> "ph"
            "Poland" -> "pl"
            "Portugal" -> "pt"
            "Romania" -> "ro"
            "Serbia" -> "rs"
            "Russia" -> "ru"
            "Saudi Arabia" -> "sa"
            "Sweden" -> "se"
            "Singapore" -> "sg"
            "Slovenia" -> "si"
            "Slovakia" -> "sk"
            "Thailand" -> "th"
            "Turkey" -> "tr"
            "Taiwan" -> "tw"
            "Ukraine" -> "ua"
            "United States" -> "us"
            "Venezuela" -> "ve"
            "South Africa" -> "za"
            else -> null
        }
    }

    fun mapCategory(category: String): String? {
        return when(category) {
            "All" -> null
            else -> category
        }
    }

    fun mapLanguage(language: String): String? {
        return when(language) {
            "Arabic" -> "ar"
            "German" -> "de"
            "English" -> "en"
            "Spanish" -> "es"
            "French" -> "fr"
            "Hebrew" -> "he"
            "Italian" -> "it"
            "Dutch" -> "nl"
            "Norwegian" -> "no"
            "Portuguese" -> "pt"
            "Russian" -> "ru"
            "Swedish" -> "sv"
            "Chinese" -> "zh"
            else -> null
        }
    }
}