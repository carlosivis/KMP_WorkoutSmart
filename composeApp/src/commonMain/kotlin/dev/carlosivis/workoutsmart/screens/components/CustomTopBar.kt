package dev.carlosivis.workoutsmart.screens.components

import androidx.compose.foundation.layout.Box
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
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (iconNavBack != null) {
            IconButton(
                onClick = { onNavBackClick() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = iconNavBack,
                    contentDescription = stringResource(Res.string.action_back)
                )
            }
        }
        if (title != null) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = FontSizes.TitleMedium,
                textAlign = TextAlign.Center
            )
        }

        if (rightIcon != null) {
            IconButton(
                onClick = { onRightIconClick() },
                modifier = Modifier.align(Alignment.CenterEnd)
            ){
                Icon(
                    imageVector = rightIcon,
                    contentDescription = rightIconDescription?.let { stringResource(it) }
                )
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