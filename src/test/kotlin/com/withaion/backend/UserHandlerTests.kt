package com.withaion.backend

import org.junit.FixMethodOrder
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureWebTestClient
class UserHandlerTests {
//
//    @Autowired
//    private lateinit var webClient: WebTestClient
//
//    @WithMockUser(username = "tester")
//    @Test
//    fun tc_0_getUsers() {
//        webClient.get().uri("/users")
//                .exchange()
//                .expectStatus().isOk
//                .expectBodyList(User::class.java)
//                .contains(TEST_USER)
//    }
//
//    @WithMockUser(username = "tester")
//    @Test
//    fun tc_1_getUser() {
//        webClient.get().uri("/users/{id}", TEST_USER.id)
//                .exchange()
//                .expectStatus().isOk
//                .expectBody(User::class.java)
//    }
//
//    @WithMockUser(username = "tester")
//    @Test
//    fun tc_2_createUser() {
//        webClient.post().uri("/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromObject(USER))
//                .exchange()
//                .expectStatus().isCreated
//                .expectBody(String::class.java)
//    }
//
//    @WithMockUser(username = "tester")
//    @Test
//    fun tc_1_updateUser() {
//        webClient.put().uri("/users/{id}", TEST_USER.id)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromObject(UPDATE_USER))
//                .exchange()
//                .expectStatus().isCreated
//                .expectBody(String::class.java)
//    }

    companion object {
//        private val TEST_USER = User(
//                id = "7e505470-a55f-4ded-aa71-ee14c48ad293",
//                username = "testuser",
//                firstName = "TestUserFirstName",
//                lastName = "TestUserLastName",
//                email = "test@test.com",
//                password = null,
//                roles = listOf("User")
//        )
//        private val USER = User(
//                username = "testuser",
//                firstName = "TestUserFirstName",
//                lastName = "TestUserLastName",
//                email = "test@test.com",
//                password = "testpass",
//                id = null,
//                roles = null
//        )
//        private val UPDATE_USER = TEST_USER.copy(
//                username = null,
//                firstName = "TestUserFirstName",
//                email = null,
//                password = null
//        )
    }
}
