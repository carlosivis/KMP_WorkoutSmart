package dev.carlosivis.workoutsmart.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import dev.carlosivis.workoutsmart.composeResources.Res
import dev.carlosivis.workoutsmart.composeResources.action_back
import dev.carlosivis.workoutsmart.composeResources.home_screen_title
import dev.carlosivis.workoutsmart.repository.ThemeMode
import dev.carlosivis.workoutsmart.utils.FontSizes
import dev.carlosivis.workoutsmart.utils.WorkoutsSmartTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CustomTopBar(
    onNavBackClick: () -> Unit = {},
    iconNavBack: ImageVector? = Icons.AutoMirrored.Filled.ArrowBack,
    title: String? = null,
    rightIcon: ImageVector? = null,
    rightIconDescription: StringResource? = null,
    onRightIconClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            if (iconNavBack != null) {
                IconButton(onClick = onNavBackClick) {
                    Icon(
                        imageVector = iconNavBack,
                        contentDescription = stringResource(Res.string.action_back)
                    )
                }
            }
        }

        Box(
            modifier = Modifier.weight(3f),
            contentAlignment = Alignment.Center
        ) {
            if (title != null) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = FontSizes.TitleMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (rightIcon != null) {
                IconButton(onClick = onRightIconClick) {
                    Icon(
                        imageVector = rightIcon,
                        contentDescription = rightIconDescription?.let { stringResource(it) }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun CustomTopBarPreview() {
    WorkoutsSmartTheme(ThemeMode.DARK) {
        Scaffold {
            CustomTopBar(
                onNavBackClick = {},
                title = stringResource(Res.string.home_screen_title)
            )
        }
    }

}