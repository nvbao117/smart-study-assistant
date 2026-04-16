package hcmute.edu.vn.smartstudyassistant.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HashUtilTest {

    @Test
    fun `generateSalt returns non-empty base64 string`() {
        val salt = HashUtil.generateSalt()
        assertThat(salt).isNotEmpty()
    }

    @Test
    fun `generateSalt returns unique values each time`() {
        val salt1 = HashUtil.generateSalt()
        val salt2 = HashUtil.generateSalt()
        assertThat(salt1).isNotEqualTo(salt2)
    }

    @Test
    fun `hashPassword is deterministic for same input`() {
        val salt = HashUtil.generateSalt()
        val hash1 = HashUtil.hashPassword("MyPassword1!", salt)
        val hash2 = HashUtil.hashPassword("MyPassword1!", salt)
        assertThat(hash1).isEqualTo(hash2)
    }

    @Test
    fun `hashPassword differs for different passwords`() {
        val salt = HashUtil.generateSalt()
        val hash1 = HashUtil.hashPassword("password1", salt)
        val hash2 = HashUtil.hashPassword("password2", salt)
        assertThat(hash1).isNotEqualTo(hash2)
    }

    @Test
    fun `hashPassword differs for different salts`() {
        val hash1 = HashUtil.hashPassword("samePassword", HashUtil.generateSalt())
        val hash2 = HashUtil.hashPassword("samePassword", HashUtil.generateSalt())
        assertThat(hash1).isNotEqualTo(hash2)
    }

    @Test
    fun `verifyPassword returns true for correct password`() {
        val salt = HashUtil.generateSalt()
        val password = "SecurePass@2024"
        val hash = HashUtil.hashPassword(password, salt)
        assertThat(HashUtil.verifyPassword(password, salt, hash)).isTrue()
    }

    @Test
    fun `verifyPassword returns false for wrong password`() {
        val salt = HashUtil.generateSalt()
        val hash = HashUtil.hashPassword("correctPassword", salt)
        assertThat(HashUtil.verifyPassword("wrongPassword", salt, hash)).isFalse()
    }
}
