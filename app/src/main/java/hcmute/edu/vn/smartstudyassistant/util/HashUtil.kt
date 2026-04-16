package hcmute.edu.vn.smartstudyassistant.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object HashUtil {

    private const val ALGORITHM = "SHA-256"
    private const val SALT_LENGTH = 32

    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hashPassword(password: String, salt: String): String {
        val saltBytes = Base64.getDecoder().decode(salt)
        val digest = MessageDigest.getInstance(ALGORITHM)
        digest.update(saltBytes)
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hash)
    }

    fun verifyPassword(password: String, salt: String, hash: String): Boolean {
        return hashPassword(password, salt) == hash
    }
}
