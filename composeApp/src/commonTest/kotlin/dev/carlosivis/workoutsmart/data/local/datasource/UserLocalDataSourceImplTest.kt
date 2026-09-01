package dev.carlosivis.workoutsmart.data.local.datasource

import com.russhwolf.settings.MapSettings
import dev.carlosivis.workoutsmart.models.UserResponse
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserLocalDataSourceImplTest {

    private lateinit var settings: MapSettings
    private lateinit var dataSource: UserLocalDataSourceImpl

    private val testUser = UserResponse(
        id = 1,
        firebaseUid = "firebase123",
        email = "test@example.com",
        displayName = "Test User",
        points = 1000L,
        photoUrl = "https://example.com/photo.jpg"
    )

    @BeforeTest
    fun setup() {
        settings = MapSettings()
        dataSource = UserLocalDataSourceImpl(settings)
    }

    @AfterTest
    fun tearDown() {
        settings.clear()
    }

    @Test
    fun `when saveUserToken then should store token`() {
        dataSource.saveUserToken("test_token_123")

        assertEquals("test_token_123", dataSource.getUserToken())
    }

    @Test
    fun `when getUserToken without saving then should return null`() {
        assertNull(dataSource.getUserToken())
    }

    @Test
    fun `when saveUserToken multiple times then should override previous`() {
        dataSource.saveUserToken("token_1")
        assertEquals("token_1", dataSource.getUserToken())

        dataSource.saveUserToken("token_2")
        assertEquals("token_2", dataSource.getUserToken())
    }

    @Test
    fun `when saveUserToken with empty string then should store empty token`() {
        dataSource.saveUserToken("")

        assertEquals("", dataSource.getUserToken())
    }

    @Test
    fun `when saveUser then should serialize and store all fields`() {
        dataSource.saveUser(testUser)

        val result = dataSource.getUser()
        assertNotNull(result)
        assertEquals(testUser.id, result.id)
        assertEquals(testUser.firebaseUid, result.firebaseUid)
        assertEquals(testUser.email, result.email)
        assertEquals(testUser.displayName, result.displayName)
        assertEquals(testUser.points, result.points)
        assertEquals(testUser.photoUrl, result.photoUrl)
    }

    @Test
    fun `when getUser without saving then should return null`() {
        assertNull(dataSource.getUser())
    }

    @Test
    fun `when saveUser multiple times then should override with latest`() {
        dataSource.saveUser(testUser)

        val updated = testUser.copy(
            id = 2,
            email = "another@example.com",
            displayName = "Another User",
            points = 500L
        )
        dataSource.saveUser(updated)

        val result = dataSource.getUser()
        assertNotNull(result)
        assertEquals(2, result.id)
        assertEquals("another@example.com", result.email)
        assertEquals(500L, result.points)
    }

    @Test
    fun `when saveUser with empty photoUrl then should store correctly`() {
        dataSource.saveUser(testUser.copy(photoUrl = ""))

        assertEquals("", dataSource.getUser()?.photoUrl)
    }

    @Test
    fun `when saveUser with special characters then should handle encoding`() {
        val userWithSpecialChars = testUser.copy(
            displayName = "User @!#\$%",
            email = "test+alias@example.com"
        )
        dataSource.saveUser(userWithSpecialChars)

        val result = dataSource.getUser()
        assertNotNull(result)
        assertEquals("User @!#\$%", result.displayName)
        assertEquals("test+alias@example.com", result.email)
    }

    @Test
    fun `when clearUserData then should remove token and user`() {
        dataSource.saveUserToken("token123")
        dataSource.saveUser(testUser)

        dataSource.clearUserData()

        assertNull(dataSource.getUserToken())
        assertNull(dataSource.getUser())
    }

    @Test
    fun `when clearUserData without saving then should not throw`() {
        dataSource.clearUserData()

        assertNull(dataSource.getUserToken())
        assertNull(dataSource.getUser())
    }

    @Test
    fun `when saveUser multiple times then should preserve token separately`() {
        dataSource.saveUserToken("persistent_token")
        dataSource.saveUser(testUser)
        dataSource.saveUser(testUser.copy(id = 99))

        assertEquals("persistent_token", dataSource.getUserToken())
        assertEquals(99, dataSource.getUser()?.id)
    }

    @Test
    fun `when saveUserToken then should not affect stored user`() {
        dataSource.saveUser(testUser)
        dataSource.saveUserToken("new_token")

        assertNotNull(dataSource.getUser())
        assertEquals(testUser.email, dataSource.getUser()?.email)
    }
}