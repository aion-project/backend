package com.withaion.backend

import com.withaion.backend.models.*
import com.withaion.backend.utils.EventUtil
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.time.LocalDateTime

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration
class EventUtilTests {

    // TODO - Write tests for event expansion
//    @Test
//    fun testWeeklyExpanding() {
//        println("---- Test Start ----")
//        EventUtil.expandEvents(listOf(weeklyRepeatingEvent))
//        println("---- Test End ----")
//        assert(true)
//    }
//
//    @Test
//    fun testDailyExpanding() {
//        println("---- Test Start ----")
//        EventUtil.expandEvents(listOf(dailyRepeatingEvent))
//        println("---- Test End ----")
//        assert(true)
//    }
//
//    companion object {
//        val weeklyRepeatingEvent = Event(
//                id = "testWeekly",
//                name = "Test Weekly",
//                description = "Test Weekly Description",
//                startDateTime = LocalDateTime.of(2019, 4, 24, 8, 0),
//                endDateTime = LocalDateTime.of(2019, 4, 24, 10, 0),
//                repeat = RepeatType.WEEKLY,
//                reschedules = listOf(
//                        Reschedule(
//                                id = "test",
//                                oldDateTime = LocalDateTime.of(2019, 5, 1, 8, 0),
//                                newDateTime = LocalDateTime.of(2019, 5, 2, 8, 0),
//                                type = RescheduleType.PERM,
//                                status = RescheduleStatus.PENDING
//                        ),
//                        Reschedule(
//                                id = "test",
//                                oldDateTime = LocalDateTime.of(2019, 5, 2, 8, 0),
//                                newDateTime = LocalDateTime.of(2019, 5, 2, 6, 0),
//                                type = RescheduleType.TEMP,
//                                status = RescheduleStatus.PENDING
//                        )
//                )
//        )
//        val dailyRepeatingEvent = Event(
//                id = "testDaily",
//                name = "Test Daily",
//                description = "Test Daily Description",
//                startDateTime = LocalDateTime.of(2019, 4, 24, 8, 0),
//                endDateTime = LocalDateTime.of(2019, 4, 24, 10, 0),
//                repeat = RepeatType.DAILY,
//                reschedules = listOf(
//                        Reschedule(
//                                id = "test",
//                                oldDateTime = LocalDateTime.of(2019, 5, 1, 8, 0),
//                                newDateTime = LocalDateTime.of(2019, 5, 2, 8, 0),
//                                type = RescheduleType.PERM,
//                                status = RescheduleStatus.PENDING
//                        ),
//                        Reschedule(
//                                id = "test",
//                                oldDateTime = LocalDateTime.of(2019, 5, 2, 8, 0),
//                                newDateTime = LocalDateTime.of(2019, 5, 2, 6, 0),
//                                type = RescheduleType.TEMP,
//                                status = RescheduleStatus.PENDING
//                        )
//                )
//        )
//    }

}