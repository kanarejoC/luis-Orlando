package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.AccountType
import com.example.data.model.ContractStatus
import com.example.data.model.InstitutionType
import com.example.data.model.NotificationType
import com.example.data.model.PaymentStatus
import com.example.data.model.PropertyType
import com.example.data.model.RenewalType
import com.example.data.model.SectorType
import com.example.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromPropertyType(value: PropertyType?): String? = value?.name

    @TypeConverter
    fun toPropertyType(value: String?): PropertyType? = value?.let { PropertyType.valueOf(it) }

    @TypeConverter
    fun fromSectorType(value: SectorType?): String? = value?.name

    @TypeConverter
    fun toSectorType(value: String?): SectorType? = value?.let { SectorType.valueOf(it) }

    @TypeConverter
    fun fromInstitutionType(value: InstitutionType?): String? = value?.name

    @TypeConverter
    fun toInstitutionType(value: String?): InstitutionType? = value?.let { InstitutionType.valueOf(it) }

    @TypeConverter
    fun fromAccountType(value: AccountType?): String? = value?.name

    @TypeConverter
    fun toAccountType(value: String?): AccountType? = value?.let { AccountType.valueOf(it) }

    @TypeConverter
    fun fromRenewalType(value: RenewalType?): String? = value?.name

    @TypeConverter
    fun toRenewalType(value: String?): RenewalType? = value?.let { RenewalType.valueOf(it) }

    @TypeConverter
    fun fromContractStatus(value: ContractStatus?): String? = value?.name

    @TypeConverter
    fun toContractStatus(value: String?): ContractStatus? = value?.let { ContractStatus.valueOf(it) }

    @TypeConverter
    fun fromPaymentStatus(value: PaymentStatus?): String? = value?.name

    @TypeConverter
    fun toPaymentStatus(value: String?): PaymentStatus? = value?.let { PaymentStatus.valueOf(it) }

    @TypeConverter
    fun fromNotificationType(value: NotificationType?): String? = value?.name

    @TypeConverter
    fun toNotificationType(value: String?): NotificationType? = value?.let { NotificationType.valueOf(it) }

    @TypeConverter
    fun fromUserRole(value: UserRole?): String? = value?.name

    @TypeConverter
    fun toUserRole(value: String?): UserRole? = value?.let { UserRole.valueOf(it) }
}
