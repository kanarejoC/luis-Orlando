package com.example

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.PropertyEntity
import com.example.data.model.PropertyType
import com.example.data.model.SectorType
import com.example.ui.components.PropertyCard
import com.example.ui.theme.ArriendoTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun propertyCard_screenshot() {
        val sampleProp = PropertyEntity(
            id = 1,
            title = "Moderno Departamento en La Carolina",
            description = "Excelente ubicación en sector financiero",
            propertyType = PropertyType.DEPARTAMENTO,
            sectorType = SectorType.URBANO,
            city = "Quito",
            neighborhood = "La Carolina",
            address = "Av. República del Salvador",
            monthlyRent = 450.0,
            guaranteeAmount = 900.0,
            bedrooms = 2,
            bathrooms = 2,
            areaSqMeters = 85.0,
            amenities = "Ascensor, Balcón, Seguridad 24/7",
            imageDrawableRes = "img_apartment_modern",
            landlordName = "Lic. Carlos Mendoza",
            landlordPhone = "0991234567",
            landlordEmail = "carlos.mendoza@inmuebles.ec",
            landlordIdCard = "1712345678"
        )

        composeTestRule.setContent {
            ArriendoTheme {
                PropertyCard(
                    property = sampleProp,
                    onClick = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/property_card.png")
    }
}
