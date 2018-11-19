package gnss.utils

/*
 * Created by aimozg on 19.11.2018.
 * Confidential unless published on GitHub
 */
fun<T> Iterator<T>.nextOrNull():T? = if (hasNext()) next() else null
fun Iterator<String>.nextOrEmpty() = if (hasNext()) next() else ""