package org.d3if3104.wof.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.d3if3104.wof.R

// Set of Material typography styles to start with
val Montserrat = FontFamily(
    Font(R.font.montserrat_regular),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    displayMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    displayLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    displaySmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    )
)