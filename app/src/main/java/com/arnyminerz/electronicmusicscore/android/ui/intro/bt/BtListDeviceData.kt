package com.arnyminerz.electronicmusicscore.android.ui.intro.bt

data class BtListDeviceData(
    val type: String,
    val name: String,
    val mac: String,
)

class BtListDeviceDataPPP :
    androidx.compose.ui.tooling.preview.PreviewParameterProvider<BtListDeviceData> {
    override val values: Sequence<BtListDeviceData> =
        sequenceOf(
            BtListDeviceData(
                "EMS_Proto",
                "EMS Prototype 1",
                "10:10:10:10:10:10"
            )
        )
}
