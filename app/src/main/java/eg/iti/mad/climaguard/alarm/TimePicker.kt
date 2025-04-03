package eg.iti.mad.climaguard.alarm

import android.app.TimePickerDialog
import android.os.Build
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerDialog(initialTime: LocalTime, onTimeSelected: (LocalTime) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            onTimeSelected(LocalTime.of(hour, minute))
        },
        initialTime.hour,
        initialTime.minute,
        false
    )

    LaunchedEffect(Unit) {
        timePickerDialog.show()
    }
}
