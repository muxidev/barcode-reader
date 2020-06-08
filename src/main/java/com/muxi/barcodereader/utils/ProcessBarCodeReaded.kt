package com.muxi.barcodereader.utils

import android.util.SparseArray
import com.google.android.gms.vision.barcode.Barcode
import com.muxi.barcodereader.data.BarcodeResponse

fun SparseArray<Barcode>.processBarCodeReaded(): BarcodeResponse {
    val barcode = this.valueAt(0)
    return when(barcode.valueFormat) {
        Barcode.CONTACT_INFO -> {
            processContactInfo(barcode.contactInfo)
        }
        Barcode.EMAIL -> {
            BarcodeResponse.Email(
                barcode.email.address,
                barcode.email.body,
                barcode.email.subject,
                barcode.email.type
            )
        }
        Barcode.PHONE -> {
            BarcodeResponse.Phone(
                barcode.phone.number,
                barcode.phone.type
            )
        }
        Barcode.SMS -> {
            BarcodeResponse.SMS(
                barcode.sms.message,
                barcode.sms.phoneNumber
            )
        }
        Barcode.TEXT -> {
            BarcodeResponse.SimpleText(
                barcode.displayValue
            )
        }
        Barcode.URL -> {
            BarcodeResponse.UrlBookMark(
                barcode.url.title,
                barcode.url.url
            )
        }
        Barcode.WIFI -> {
            BarcodeResponse.Wifi(
                barcode.wifi.encryptionType,
                barcode.wifi.password,
                barcode.wifi.ssid
            )
        }
        Barcode.GEO -> {
            BarcodeResponse.GeoPoint(
                barcode.geoPoint.lat,
                barcode.geoPoint.lng
            )
        }
        Barcode.CALENDAR_EVENT -> {
            BarcodeResponse.CalendarEvent(
                barcode.calendarEvent.description,
                barcode.calendarEvent.start.rawValue,
                barcode.calendarEvent.end.rawValue,
                barcode.calendarEvent.location,
                barcode.calendarEvent.summary,
                barcode.calendarEvent.status
            )
        }
        Barcode.DRIVER_LICENSE -> {
            BarcodeResponse.DriverLicense(
                barcode.driverLicense.addressCity,
                barcode.driverLicense.addressState,
                barcode.driverLicense.addressStreet,
                barcode.driverLicense.addressZip,
                barcode.driverLicense.birthDate,
                barcode.driverLicense.documentType,
                barcode.driverLicense.expiryDate,
                barcode.driverLicense.firstName,
                barcode.driverLicense.gender,
                barcode.driverLicense.issueDate,
                barcode.driverLicense.issuingCountry,
                barcode.driverLicense.lastName,
                barcode.driverLicense.licenseNumber,
                barcode.driverLicense.middleName
            )
        }
        else -> BarcodeResponse.SimpleText(barcode.displayValue)
    }
}

fun processContactInfo(contactInfo: Barcode.ContactInfo?): BarcodeResponse.ContactInfo {
    val contactAddresses = mutableListOf<String>()
    val contactEmails = mutableListOf<String>()

    for(address in contactInfo!!.addresses) {
        contactAddresses.add(address.addressLines[0])
    }

    for(emails in contactInfo.emails) {
        contactEmails.add(emails.address)
    }

    return BarcodeResponse.ContactInfo(
        contactAddresses,
        contactEmails,
        contactInfo.name.first,
        contactInfo.name.last,
        contactInfo.title,
        contactInfo.urls.toList()
    )
}

