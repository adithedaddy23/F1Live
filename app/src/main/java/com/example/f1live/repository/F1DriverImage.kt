package com.example.f1live.repository

import android.graphics.Paint
import com.example.f1live.R

data class F1DriverImage(
    val name: String,
    val imgUrl:String
)

data class F1logo(
    val name: String,
    val logoUrl: String
)

data class Track(
    val gpName: String,
    val circuitName: String,
    val imgUrl: String // Changed from imgUrl: Painter
)

data class Circuit(
    val gpName: String,
    val circuitName: String,
    val imgUrl: String
)

data class F1Car(
    val name: String,
    val imgUrl: String
)

data class DriverDetailsImg(
    val name: String,
    val imgUrl: String
)

object CircuitPhotos {
    fun getCircuitList(): List<Circuit> {
        return listOf(
            Circuit(
                gpName = "Australian Grand Prix",
                circuitName = "Albert Park Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackmelbournedetailed.webp"
            ),
            Circuit(
                gpName = "Chinese Grand Prix",
                circuitName = "Shanghai International Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackshanghaidetailed.webp"
            ),
            Circuit(
                gpName = "Japanese Grand Prix",
                circuitName = "Suzuka Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026tracksuzukadetailed.webp"
            ),
            Circuit(
                gpName = "Bahrain Grand Prix",
                circuitName = "Bahrain International Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026tracksakhirdetailed.webp"
            ),
            Circuit(
                gpName = "Saudi Arabian Grand Prix",
                circuitName = "Jeddah Corniche Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackjeddahdetailed.webp"
            ),
            Circuit(
                gpName = "Miami Grand Prix",
                circuitName = "Miami International Autodrome",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackmiamidetailed.webp"
            ),
            Circuit(
                gpName = "Emilia Romagna Grand Prix",
                circuitName = "Imola Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Circuit%20maps%2016x9/Emilia_Romagna_Circuit.webp"
            ),
            Circuit(
                gpName = "Monaco Grand Prix",
                circuitName = "Circuit de Monaco",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackmontecarlodetailed.webp"
            ),
            Circuit(
                gpName = "Spanish Grand Prix",
                circuitName = "Circuit de Barcelona-Catalunya",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackmadringdetailed.webp"
            ),
            Circuit(
                gpName = "Canadian Grand Prix",
                circuitName = "Circuit Gilles Villeneuve",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackmontrealdetailed.webp"
            ),
            Circuit(
                gpName = "Austrian Grand Prix",
                circuitName = "Red Bull Ring",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackspielbergdetailed.webp"
            ),
            Circuit(
                gpName = "British Grand Prix",
                circuitName = "Silverstone Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026tracksilverstonedetailed.webp"
            ),
            Circuit(
                gpName = "Belgian Grand Prix",
                circuitName = "Circuit de Spa-Francorchamps",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackspafrancorchampsdetailed.webp"
            ),
            Circuit(
                gpName = "Hungarian Grand Prix",
                circuitName = "Hungaroring",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackhungaroringdetailed.webp"
            ),
            Circuit(
                gpName = "Dutch Grand Prix",
                circuitName = "Circuit Zandvoort",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackzandvoortdetailed.webp"
            ),
            Circuit(
                gpName = "Italian Grand Prix",
                circuitName = "Monza Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackmonzadetailed.webp"
            ),
            Circuit(
                gpName = "Azerbaijan Grand Prix",
                circuitName = "Baku City Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackbakudetailed.webp"
            ),
            Circuit(
                gpName = "Singapore Grand Prix",
                circuitName = "Marina Bay Street Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026tracksingaporedetailed.webp"
            ),
            Circuit(
                gpName = "United States Grand Prix",
                circuitName = "Circuit of the Americas",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackaustindetailed.webp"
            ),
            Circuit(
                gpName = "Mexico City Grand Prix",
                circuitName = "Autódromo Hermanos Rodríguez",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackmexicocitydetailed.webp"
            ),
            Circuit(
                gpName = "São Paulo Grand Prix",
                circuitName = "Interlagos Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackinterlagosdetailed.webp"
            ),
            Circuit(
                gpName = "Las Vegas Grand Prix",
                circuitName = "Las Vegas Strip Circuit",
                imgUrl = "hhttps://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026tracklasvegasdetailed.webp"
            ),
            Circuit(
                gpName = "Qatar Grand Prix",
                circuitName = "Lusail International Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026tracklusaildetailed.webp"
            ),
            Circuit(
                gpName = "Abu Dhabi Grand Prix",
                circuitName = "Yas Marina Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackyasmarinacircuitdetailed.webp"
            ),
            Circuit(
                gpName = "Barcelona-Catalunya Grand Prix",
                circuitName = "Circuit de Barcelona-Catalunya",
                imgUrl = "https://media.formula1.com/image/upload/c_fit,h_704/q_auto/v1740000000/common/f1/2026/track/2026trackcatalunyadetailed.webp"
            )
        )
    }
}

object TrackPhotos {
    fun getTrackList(): List<Track> {
        return listOf(
            Track(
                gpName = "Australian Grand Prix",
                circuitName = "Albert Park Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Australia.webp"
            ),
            Track(
                gpName = "Chinese Grand Prix",
                circuitName = "Shanghai International Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/China.webp"
            ),
            Track(
                gpName = "Japanese Grand Prix",
                circuitName = "Suzuka Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Japan.webp"
            ),
            Track(
                gpName = "Bahrain Grand Prix",
                circuitName = "Bahrain International Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Bahrain.webp"
            ),
            Track(
                gpName = "Saudi Arabian Grand Prix",
                circuitName = "Jeddah Corniche Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Saudi_Arabia.webp"
            ),
            Track(
                gpName = "Miami Grand Prix",
                circuitName = "Miami International Autodrome",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Miami.webp"
            ),
            Track(
                gpName = "Emilia Romagna Grand Prix",
                circuitName = "Imola Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Emilia%20Romagna.webp"
            ),
            Track(
                gpName = "Monaco Grand Prix",
                circuitName = "Circuit de Monaco",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Monaco.webp"
            ),
            Track(
                gpName = "Spanish Grand Prix",
                circuitName = "Circuit de Barcelona-Catalunya",
                imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/fom-website/2023/Spain/IFEMA_MADRID_PRENSA_005.webp"
            ),
            Track(
                gpName = "Canadian Grand Prix",
                circuitName = "Circuit Gilles Villeneuve",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Canada.webp"
            ),
            Track(
                gpName = "Austrian Grand Prix",
                circuitName = "Red Bull Ring",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Austria.webp"
            ),
            Track(
                gpName = "British Grand Prix",
                circuitName = "Silverstone Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Great%20Britain.webp"
            ),
            Track(
                gpName = "Belgian Grand Prix",
                circuitName = "Circuit de Spa-Francorchamps",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Belgium.webp"
            ),
            Track(
                gpName = "Hungarian Grand Prix",
                circuitName = "Hungaroring",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Hungary.webp"
            ),
            Track(
                gpName = "Dutch Grand Prix",
                circuitName = "Circuit Zandvoort",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Netherlands.webp"
            ),
            Track(
                gpName = "Italian Grand Prix",
                circuitName = "Monza Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Italy.webp"
            ),
            Track(
                gpName = "Azerbaijan Grand Prix",
                circuitName = "Baku City Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Azerbaijan.webp"
            ),
            Track(
                gpName = "Singapore Grand Prix",
                circuitName = "Marina Bay Street Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Singapore.webp"
            ),
            Track(
                gpName = "United States Grand Prix",
                circuitName = "Circuit of the Americas",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/USA.webp"
            ),
            Track(
                gpName = "Mexico City Grand Prix",
                circuitName = "Autódromo Hermanos Rodríguez",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Mexico.webp"
            ),
            Track(
                gpName = "São Paulo Grand Prix",
                circuitName = "Interlagos Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Brazil.webp"
            ),
            Track(
                gpName = "Las Vegas Grand Prix",
                circuitName = "Las Vegas Strip Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Las%20Vegas.webp"
            ),
            Track(
                gpName = "Qatar Grand Prix",
                circuitName = "Lusail International Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Qatar.webp"
            ),
            Track(
                gpName = "Abu Dhabi Grand Prix",
                circuitName = "Yas Marina Circuit",
                imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/2018-redesign-assets/Racehub%20header%20images%2016x9/Abu%20Dhabi.webp"
            )
        )
    }
}

object DriversImg {
    val drivers = listOf(
        F1DriverImage(
            name = "Oscar Piastri",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/mclaren/oscpia01/2026mclarenoscpia01right.webp",
        ),
        F1DriverImage(
            name = "Lando Norris",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/mclaren/lannor01/2026mclarenlannor01right.webp"
        ),
        F1DriverImage(
            name = "George Russell",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/mercedes/georus01/2026mercedesgeorus01right.webp"
        ),
        F1DriverImage(
            name = "Andrea Kimi Antonelli",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/mercedes/andant01/2026mercedesandant01right.webp"
        ),
        F1DriverImage(
            name = "Charles Leclerc",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/ferrari/chalec01/2026ferrarichalec01right.webp"
        ),
        F1DriverImage(
            name = "Lewis Hamilton",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/ferrari/lewham01/2026ferrarilewham01right.webp"
        ),
        F1DriverImage(
            name = "Max Verstappen",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/redbullracing/maxver01/2026redbullracingmaxver01right.webp"
        ),
        F1DriverImage(
            name = "Yuki Tsunoda",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2025:fallback:driver:2025fallbackdriverright.webp/v1740000000/common/f1/2025/redbullracing/yuktsu01/2025redbullracingyuktsu01right.webp"
        ),
        F1DriverImage(
            name = "Alexander Albon",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/williams/alealb01/2026williamsalealb01right.webp"
        ),
        F1DriverImage(
            name = "Isack Hadjar",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/redbullracing/isahad01/2026redbullracingisahad01right.webp"
        ),
        F1DriverImage(
            name = "Lance Stroll",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/astonmartin/lanstr01/2026astonmartinlanstr01right.webp"
        ),
        F1DriverImage(
            name = "Fernando Alonso",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/astonmartin/feralo01/2026astonmartinferalo01right.webp"
        ),
        F1DriverImage(
            name = "Nico Hülkenberg",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/audi/nichul01/2026audinichul01right.webp"
        ),
        F1DriverImage(
            name = "Gabriel Bortoleto",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/audi/gabbor01/2026audigabbor01right.webp"
        ),
        F1DriverImage(
            name = "Sergio Perez",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/cadillac/serper01/2026cadillacserper01right.webp"
        ),
        F1DriverImage(
            name = "Valtteri Bottas",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/cadillac/valbot01/2026cadillacvalbot01right.webp"
        ),
        F1DriverImage(
            name = "Esteban Ocon",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/haasf1team/estoco01/2026haasf1teamestoco01right.webp"
        ),
        F1DriverImage(
            name = "Oliver Bearman",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/haasf1team/olibea01/2026haasf1teamolibea01right.webp"
        ),
        F1DriverImage(
            name = "Pierre Gasly",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/alpine/piegas01/2026alpinepiegas01right.webp"
        ),
        F1DriverImage(
            name = "Franco Colapinto",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/alpine/fracol01/2026alpinefracol01right.webp"
        ),
        F1DriverImage(
            name = "Liam Lawson",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/racingbulls/lialaw01/2026racingbullslialaw01right.webp"
        ),
        F1DriverImage(
            name = "Arvid Lindblad",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/racingbulls/arvlin01/2026racingbullsarvlin01right.webp"
        ),
        F1DriverImage(
            name = "Carlos Sainz",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/williams/carsai01/2026williamscarsai01right.webp"
        ),
        F1DriverImage(
            name = "Jack Doohan",
            imgUrl = "https://media.formula1.com/image/upload/c_fill,w_720/q_auto/v1740000000/common/f1/2025/alpine/jacdoo01/2025alpinejacdoo01right.webp"
        ),
        F1DriverImage(
            name = "Valtteri Bottas",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_440/q_auto/d_common:f1:2026:fallback:driver:2026fallbackdriverright.webp/v1740000000/common/f1/2026/cadillac/valbot01/2026cadillacvalbot01right.webp"
        )
    )
}

object DriverDImg {
    val drivers = listOf(
        DriverDetailsImg(
            name = "Oscar Piastri",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2025/Formula_1_Testing_in_Bahrain___Day_1/2202068917.webp",
        ),
        DriverDetailsImg(
            name = "Lando Norris",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9South/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2025/Formula_1_Testing_in_Bahrain___Day_2/2202246179.webp"
        ),
        DriverDetailsImg(
            name = "George Russell",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9North/c_lfill,w_3392/q_auto/v1740000000/fom-website/2025/Testing/GettyImages-2202074600.webp"
        ),
        DriverDetailsImg(
            name = "Andrea Kimi Antonelli",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2025/F1_Grand_Prix_Of_Australia___Previews/2204648015.webp"
        ),
        DriverDetailsImg(
            name = "Charles Leclerc",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9South/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2025/Formula_1_Testing_in_Bahrain___Day_3/2202446635.webp"
        ),
        DriverDetailsImg(
            name = "Lewis Hamilton",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9North/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2025/Formula_1_Testing_in_Bahrain___Day_3/2202476122.webp"
        ),
        DriverDetailsImg(
            name = "Max Verstappen",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/sutton/2022/Bahrain/Thursday/1385967943.webp"
        ),
        DriverDetailsImg(
            name = "Yuki Tsunoda",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2025/F1_Grand_Prix_of_Hungary___Qualifying/2228160226.webp"
        ),
        DriverDetailsImg(
            name = "Alexander Albon",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9North/c_lfill,w_3392/q_auto/v1740000000/fom-website/2025/Testing/GettyImages-2202055223.webp"
        ),
        DriverDetailsImg(
            name = "Isack Hadjar",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9South/c_lfill,w_3392/q_auto/v1740000000/fom-website/2024/RB%20(Visa%20Cash%20App)/GettyImages-2189829243.webp"
        ),
        DriverDetailsImg(
            name = "Lance Stroll",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/manual/DriverAndTeamImages/2024/2032079239_16by9North.webp"
        ),
        DriverDetailsImg(
            name = "Fernando Alonso",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2024/F1_75_Live___Press_Conference/2200412845.webp"
        ),
        DriverDetailsImg(
            name = "Nico Hülkenberg",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2024/F1_Grand_Prix_of_Abu_Dhabi___Practice/2188492336.webp"
        ),
        DriverDetailsImg(
            name = "Gabriel Bortoleto",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2024/Formula_1_Testing_in_Abu_Dhabi/2189165820.webp"
        ),
        DriverDetailsImg(
            name = "Esteban Ocon",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9North/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2025/Formula_1_Testing_in_Bahrain___Day_1/2202063861.webp"
        ),
        DriverDetailsImg(
            name = "Oliver Bearman",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9South/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2024/F1_Grand_Prix_of_Azerbaijan___Practice/2171731545.webp"
        ),
        DriverDetailsImg(
            name = "Pierre Gasly",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2025/Formula_1_Testing_in_Bahrain___Day_1/2202108284.webp"
        ),
        DriverDetailsImg(
            name = "Franco Colapinto",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9North/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2025/F1_Grand_Prix_of_Emilia_Romagna___Previews/2215257744.webp"
        ),
        DriverDetailsImg(
            name = "Liam Lawson",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9North/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2025/F1_Grand_Prix_of_Hungary/2228284915.webp"
        ),
        DriverDetailsImg(
            name = "Carlos Sainz",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9South/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2025/Formula_1_Testing_in_Bahrain___Day_1/2202103709.webp"
        ),
        DriverDetailsImg(
            name = "Jack Doohan",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2025/Formula_1_Testing_in_Bahrain___Day_1/2202053221.webp"
        ),
        DriverDetailsImg(
            name = "Valtteri Bottas",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/manual/DriverAndTeamImages/2024/2027466084-16by9Centre.webp"
        ),
        DriverDetailsImg(
            name = "Kevin Magnussen",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/manual/DriverAndTeamImages/2024/2068819024_16by9North.webp"
        ),
        DriverDetailsImg(
            name = "Sergio Pérez",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9North/c_lfill,w_3392/q_auto/v1740000000/fom-website/2025/Cadillac%20(GM)/GettyImages-2232402082.webp"
        ),
        DriverDetailsImg(
            name = "Daniel Ricciardo",
            imgUrl = "https://media.formula1.com/image/upload/ar_16:9,c_fill,g_north/c_lfill,w_3392/q_auto/v1740000000/fom-website/2025/Lifestyle/Fashion%20eras/Ricciardo/GettyImages-1190572875.webp"
        ),
        DriverDetailsImg(
            name = "Logan Sargeant",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/fom-website/2024/Williams/GettyImages-2158382692.webp"
        ),
        DriverDetailsImg(
            name = "Guanyu Zhou",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/manual/DriverAndTeamImages/2024/2045515483_16by9North.webp"
        ),
        DriverDetailsImg(
            name = "Nyck de Vries",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/fom-website/2025/Miscellaneous/de-vries-portrait-1.webp"
        ),
        DriverDetailsImg(
            name = "Nicholas Latifi",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/manual/Misc/2022manual/2022Races/CanadaGP/Post-race/WilliamsF1_68489_HiRes.webp"
        ),
        DriverDetailsImg(
            name = "Mick Schumacher",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2024/F1_Grand_Prix_of_United_States___Previews/2178907945.webp"
        ),
        DriverDetailsImg(
            name = "Sebastian Vettel",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/fom-website/manual/Hall%20of%20Fame%202024/GettyImages-82801122.webp"
        ),
        DriverDetailsImg(
            name = "Antonio Giovinazzi",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/manual/Italy2020FIAPoolImages/Sun/00120017__AV_3674.webp"
        ),
        DriverDetailsImg(
            name = "Robert Kubica",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/fom-website/2025/Miscellaneous/GettyImages-2171843605.webp"
        ),
        DriverDetailsImg(
            name = "Nikita Mazepin",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/sutton/2021/SaudiArabia/Saturday/1357164133.webp"
        ),
        DriverDetailsImg(
            name = "Kimi Räikkönen",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/fom-website/manual/Hall%20of%20Fame%202024/GettyImages-155466366.webp"
        ),
        DriverDetailsImg(
            name = "Jack Aitken",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/sutton/2020/Portugal/Sunday/1282116124.webp"
        ),
        DriverDetailsImg(
            name = "Pietro Fittipaldi",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/trackside-images/2023/F1_Grand_Prix_of_Abu_Dhabi___Previews/1809154093.webp"
        ),
        DriverDetailsImg(
            name = "Romain Grosjean",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/fom-website/2025/Haas/_X4I1752-195.webp"
        ),
        DriverDetailsImg(
            name = "Daniil Kvyat",
            imgUrl = "https://img.redbull.com/images/q_auto,f_auto/redbullcom/2016/04/24/1331790712275_8/daniil-kvyat-china-2016"
        ),
        DriverDetailsImg(
            name = "Marcus Ericsson",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/sutton/2019/Belgium/Thursday/1017690701-SUT-20190829-MS1_0171.webp"
        ),
        DriverDetailsImg(
            name = "Brendon Hartley",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/manual/Misc/Brendon_Hartley_TPT/AP-1UVYFUZQ92111_hires_jpeg_24bit_rgb.webp"
        ),
        DriverDetailsImg(
            name = "Sergey Sirotkin",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/manual/Misc/2018/DriverAnnouncements/Sirotkin/Sergey_Sirotkin.webp"
        ),
        DriverDetailsImg(
            name = "Stoffel Vandoorne",
            imgUrl = "https://mclaren.bloomreach.io/cdn-cgi/image/format=webp,quality=80/delivery/resources/content/gallery/mclaren-racing/legacy/heritage/hero/1016947616-LAT-20181019-_1ST8309.jpg"
        ),
        DriverDetailsImg(
            name = "Jenson Button",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/fom-website/manual/Hall%20of%20Fame%202024/GettyImages-52930549.webp"
        ),
        DriverDetailsImg(
            name = "Paul di Resta",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/sutton/2019/Germany/Friday/1017625991-LAT-20190725-IMG_0694x.webp"
        ),
        DriverDetailsImg(
            name = "Felipe Massa",
            imgUrl = "https://images.ps-aws.com/c?url=https%3A%2F%2Fd3cm515ijfiu6w.cloudfront.net%2Fwp-content%2Fuploads%2F2025%2F10%2F03152215%2Ffelipe-massa-ferrari-f1-2025-brazilian-grand-prix-2008-1320x742.png"
        ),
        DriverDetailsImg(
            name = "Jolyon Palmer",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/sutton/2017/Belgium/Saturday/dcd1726au20.webp"
        ),
        DriverDetailsImg(
            name = "Pascal Wehrlein",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,w_3392/q_auto/v1740000000/content/dam/fom-website/sutton/2016/Monaco/Thursday/dcc1626my852.webp"
        ),
        DriverDetailsImg(
            name = "Nico Rosberg",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9South/c_lfill,w_3392/q_auto/v1740000000/fom-website/manual/Hall%20of%20Fame%202024/GettyImages-97687113.webp"
        ),
        DriverDetailsImg(
            name = "Arvid Lindblad",
            imgUrl = "https://media.formula1.com/image/upload/t_16by9Centre/c_lfill,w_3392/q_auto/v1740000000/fom-website/2025/Formula%202/lindblad-f2-feature-race-win-barcelona-2025.webp"
        )
    )
}

object logo  {
    val teamLogos = listOf(
        F1logo(
            name = "McLaren",
            logoUrl = "https://media.formula1.com/image/upload/c_lfill,w_48/q_auto/v1740000000/common/f1/2025/mclaren/2025mclarenlogowhite.webp"
        ),
        F1logo(
            name = "Mercedes",
            logoUrl = "https://media.formula1.com/image/upload/c_lfill,w_48/q_auto/v1740000000/common/f1/2025/mercedes/2025mercedeslogowhite.webp"
        ),
        F1logo(
            name = "Ferrari",
            logoUrl = "https://media.formula1.com/image/upload/c_lfill,w_48/q_auto/v1740000000/common/f1/2025/ferrari/2025ferrarilogowhite.webp"
        ),
        F1logo(
            name = "Red Bull",
            logoUrl = "https://media.formula1.com/image/upload/c_lfill,w_48/q_auto/v1740000000/common/f1/2025/redbullracing/2025redbullracinglogowhite.webp"
        ),
        F1logo(
            name = "Williams",
            logoUrl = "https://media.formula1.com/image/upload/c_lfill,w_48/q_auto/v1740000000/common/f1/2025/williams/2025williamslogowhite.webp"
        ),
        F1logo(
            name = "RB F1 Team",
            logoUrl = "https://media.formula1.com/image/upload/c_lfill,w_48/q_auto/v1740000000/common/f1/2025/racingbulls/2025racingbullslogowhite.webp"
        ),
        F1logo(
            name = "Aston Martin",
            logoUrl = "https://media.formula1.com/image/upload/c_lfill,w_48/q_auto/v1740000000/common/f1/2025/astonmartin/2025astonmartinlogowhite.webp"
        ),
        F1logo(
            name = "Sauber",
            logoUrl = "https://media.formula1.com/image/upload/c_lfill,w_48/q_auto/v1740000000/common/f1/2025/kicksauber/2025kicksauberlogowhite.webp"
        ),
        F1logo(
            name = "Haas F1 Team",
            logoUrl = "https://media.formula1.com/image/upload/c_lfill,w_48/q_auto/v1740000000/common/f1/2025/haasf1team/2025haasf1teamlogowhite.webp"
        ),
        F1logo(
            name = "Alpine F1 Team",
            logoUrl = "https://media.formula1.com/image/upload/c_lfill,w_48/q_auto/v1740000000/common/f1/2025/alpine/2025alpinelogowhite.webp"
        ),
        F1logo(
            name = "Audi",
            logoUrl = "https://media.formula1.com/image/upload/c_lfill,w_48/q_auto/v1740000000/common/f1/2026/audi/2026audilogowhite.webp"
        ),
        F1logo(
            name = "Cadillac",
            logoUrl = "https://media.formula1.com/image/upload/c_lfill,w_48/q_auto/v1740000000/common/f1/2026/cadillac/2026cadillaclogowhite.webp"
        )
    )
}

object Car {
    val cars = listOf(
        F1Car(
            name = "McLaren",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,h_224/q_auto/d_common:f1:2026:fallback:car:2026fallbackcarright.webp/v1740000000/common/f1/2026/mclaren/2026mclarencarright.webp"
        ),
        F1Car(
            name = "Mercedes",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,h_224/q_auto/d_common:f1:2026:fallback:car:2026fallbackcarright.webp/v1740000000/common/f1/2026/mercedes/2026mercedescarright.webp"
        ),
        F1Car(
            name = "Ferrari",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,h_224/q_auto/d_common:f1:2026:fallback:car:2026fallbackcarright.webp/v1740000000/common/f1/2026/ferrari/2026ferraricarright.webp"
        ),
        F1Car(
            name = "Red Bull",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,h_224/q_auto/d_common:f1:2026:fallback:car:2026fallbackcarright.webp/v1740000000/common/f1/2026/redbullracing/2026redbullracingcarright.webp"
        ),
        F1Car(
            name = "Williams",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,h_224/q_auto/d_common:f1:2026:fallback:car:2026fallbackcarright.webp/v1740000000/common/f1/2026/williams/2026williamscarright.webp"
        ),
        F1Car(
            name = "RB F1 Team",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,h_224/q_auto/d_common:f1:2026:fallback:car:2026fallbackcarright.webp/v1740000000/common/f1/2026/racingbulls/2026racingbullscarright.webp"
        ),
        F1Car(
            name = "Aston Martin",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,h_224/q_auto/d_common:f1:2026:fallback:car:2026fallbackcarright.webp/v1740000000/common/f1/2026/astonmartin/2026astonmartincarright.webp"
        ),
        F1Car(
            name = "Sauber",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,h_224/q_auto/d_common:f1:2025:fallback:car:2025fallbackcarright.webp/v1740000000/common/f1/2025/kicksauber/2025kicksaubercarright.webp"
        ),
        F1Car(
            name = "Haas F1 Team",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,h_224/q_auto/d_common:f1:2026:fallback:car:2026fallbackcarright.webp/v1740000000/common/f1/2026/haasf1team/2026haasf1teamcarright.webp"
        ),
        F1Car(
            name = "Alpine F1 Team",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,h_224/q_auto/d_common:f1:2026:fallback:car:2026fallbackcarright.webp/v1740000000/common/f1/2026/alpine/2026alpinecarright.webp"
        ),
        F1Car(
            name = "Audi F1 Team",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,h_224/q_auto/d_common:f1:2026:fallback:car:2026fallbackcarright.webp/v1740000000/common/f1/2026/audi/2026audicarright.webp"
        ),
        F1Car(
            name = "Cadillac F1 Team",
            imgUrl = "https://media.formula1.com/image/upload/c_lfill,h_224/q_auto/d_common:f1:2026:fallback:car:2026fallbackcarright.webp/v1740000000/common/f1/2026/cadillac/2026cadillaccarright.webp"
        )
    )
}
