package com.muxi.barcodereader.data

@Suppress("unused")
sealed class BarcodeResponse {
    data class ContactInfo(
        var addresses: List<String> = listOf(),
        var emails: List<String> = listOf(),
        var firstName: String = "",
        var lastName: String = "",
        var title: String = "",
        var urls: List<String> = listOf()
    ): BarcodeResponse()

    data class Email(
        var address: String = "",
        var body: String = "",
        var subject: String = "",
        var type: Int = UNKNOWN
    ): BarcodeResponse() {
        companion object {
            const val UNKNOWN = 0
            const val WORK = 1
            const val HOME = 2
        }
    }

    data class Phone(
        var number: String = "",
        var type: Int = UNKNOWN

     ): BarcodeResponse() {
        companion object {
            const val UNKNOWN = 0
            const val WORK = 1
            const val HOME = 2
            const val FAX = 3
            const val MOBILE = 4
        }
    }

    data class SMS(
        var message: String = "",
        var phoneNumber: String = ""
    ): BarcodeResponse()

    data class SimpleText(
        var message: String = ""
    ): BarcodeResponse()

    data class UrlBookMark(
        var title: String = "",
        var url: String = ""
    ): BarcodeResponse()

    data class Wifi(
        var encryptionType: Int = OPEN,
        var password: String = "",
        var ssid: String = ""
    ): BarcodeResponse() {
        companion object {
            const val OPEN = 1
            const val WPA = 2
            const val WEp = 3

        }
    }

    data class GeoPoint(
        var lat: Double = 0.0,
        var long: Double = 0.0
    ): BarcodeResponse()

    data class DriverLicense(
        var addressCity: String = "",
        var addressState: String = "",
        var addressStreet: String = "",
        var addressZip: String = "",
        var birthDate: String = "",
        var documentType: String = "",
        var expiryDate: String = "",
        var firstName: String = "",
        var gender: String = "",
        var issueDate: String = "",
        var issuingCountry: String = "",
        var lastName: String = "",
        var licenseNumber: String = "",
        var middleName: String = ""
    ): BarcodeResponse()

    data class CalendarEvent(
         var description: String = "",
         var start: String = "",
         var end: String = "",
         var location: String = "",
         var title: String = "",
         var status: String = ""

    ): BarcodeResponse()
}