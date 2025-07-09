package ba.out.bring.hodooMobile

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform