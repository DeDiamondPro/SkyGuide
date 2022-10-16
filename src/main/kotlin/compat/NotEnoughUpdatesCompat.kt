package compat

object NotEnoughUpdatesCompat {
    var neuPresent = false
        private set

    fun initialize() {
        val htmlInfoPane = Class.forName("io.github.moulberry.notenoughupdates.infopanes.HTMLInfoPane") ?: return
    }
}