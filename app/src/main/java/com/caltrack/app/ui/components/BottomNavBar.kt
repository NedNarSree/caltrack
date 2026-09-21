package com.caltrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caltrack.app.ui.theme.CalTrackBorderSubtle
import com.caltrack.app.ui.theme.CalTrackGreen
import com.caltrack.app.ui.theme.CalTrackGreenBg
import com.caltrack.app.ui.theme.CalTrackSurface
import com.caltrack.app.ui.theme.CalTrackTextSecondary
import com.caltrack.app.ui.theme.CalTrackTextTertiary

enum class NavScreen(val title: String, val icon: ImageVector) {
    TODAY("Today", Icons.Default.Dashboard),
    SNAP_LOG("Snap & Log", Icons.Default.CameraAlt),
    HISTORY("History", Icons.Default.CalendarMonth),
    PROFILE("Profile", Icons.Default.Person)
}

@Composable
fun BottomNavBar(
    currentScreen: NavScreen,
    onNavigate: (NavScreen) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CalTrackSurface)
    ) {
        HorizontalDivider(color = CalTrackBorderSubtle, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavScreen.values().forEach { screen ->
                val isSelected = currentScreen == screen

                if (screen == NavScreen.SNAP_LOG) {
                    // Center prominent Snap & Log Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onNavigate(screen) }
                            .padding(horizontal = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) CalTrackGreen else CalTrackGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = if (isSelected) CalTrackSurface else CalTrackGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = screen.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) CalTrackGreen else CalTrackTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onNavigate(screen) }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = if (isSelected) CalTrackGreen else CalTrackTextTertiary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = screen.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) CalTrackGreen else CalTrackTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
