package com.withaion.backend.utils

import biweekly.component.VEvent
import biweekly.property.ExceptionDates
import biweekly.util.Frequency
import biweekly.util.ICalDate
import biweekly.util.Recurrence
import com.withaion.backend.models.Event
import com.withaion.backend.models.RepeatType
import com.withaion.backend.models.RescheduleType
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

object EventUtil {

    fun expandEvents(events: List<Event>, until: LocalDateTime? = null): List<Event> {
        // TODO - Convert to schedule
        return listOf()
//        return events.flatMap { event ->
//            return@flatMap when (event.repeat) {
//                RepeatType.NONE -> listOf(event)
//                RepeatType.DAILY -> {
//                    val eventList = mutableListOf<Event>()
//
//                    val permReschedules = event.reschedules.filter { it.type == RescheduleType.PERM && !(until != null && it.oldDateTime < until) }
//                            .sortedBy { it.oldDateTime }
//
//                    var startDateTime = event.startDateTime.toDate()
//                    permReschedules.forEach { reschedule ->
//                        eventList.addAll(genEvents(startDateTime, reschedule.oldDateTime.minusDays(1).toDate(), event, Frequency.DAILY))
//                        startDateTime = reschedule.newDateTime.toDate()
//                    }
//                    eventList.addAll(genEvents(startDateTime, until?.toDate()
//                            ?: startDateTime.toLocalDateTime().plusMonths(1).toDate(), event, Frequency.DAILY))
//                    event.reschedules.filter { it.type == RescheduleType.TEMP }.forEach {
//                        eventList.addAll(genEvents(it.newDateTime.toDate(), it.newDateTime.plusHours(6).toDate(), event, Frequency.DAILY))
//                    }
//                    eventList.forEach { println(it) }
//                    eventList
//                }
//                RepeatType.WEEKLY -> {
//                    val eventList = mutableListOf<Event>()
//
//                    val permReschedules = event.reschedules.filter { it.type == RescheduleType.PERM && !(until != null && it.oldDateTime < until) }
//                            .sortedBy { it.oldDateTime }
//
//                    var startDateTime = event.startDateTime.toDate()
//                    permReschedules.forEach { reschedule ->
//                        eventList.addAll(genEvents(startDateTime, reschedule.oldDateTime.minusDays(1).toDate(), event, Frequency.WEEKLY))
//                        startDateTime = reschedule.newDateTime.toDate()
//                    }
//                    eventList.addAll(genEvents(startDateTime, until?.toDate()
//                            ?: startDateTime.toLocalDateTime().plusMonths(1).toDate(), event, Frequency.WEEKLY))
//                    event.reschedules.filter { it.type == RescheduleType.TEMP }.forEach {
//                        eventList.addAll(genEvents(it.newDateTime.toDate(), it.newDateTime.plusDays(1).toDate(), event, Frequency.WEEKLY))
//                    }
//                    eventList.forEach { println(it) }
//                    eventList
//                }
//                else -> listOf(event)
//            }
//        }
    }

    // Private extension functions
    private fun LocalDateTime.toDate(): Date {
        return Date.from(this.toInstant(OffsetDateTime.now().offset))
    }

    private fun Date.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())
    }

    private fun LocalDateTime.getEndWith(event: Event): LocalDateTime {
        // TODO - Convert to schedule
        return LocalDateTime.now()
//        return this.plusMinutes(ChronoUnit.MINUTES.between(event.startDateTime, event.endDateTime))
    }

    // Private functions
    private fun genEvents(startDateTime: Date, endDateTime: Date, event: Event, freq: Frequency): List<Event> {
        val eventList = mutableListOf<Event>()

        val vEvent = VEvent()
        vEvent.setDateStart(startDateTime, true)

        val recurrence = Recurrence.Builder(freq).until(endDateTime)
        vEvent.setRecurrenceRule(recurrence.build())

        event.reschedules.filter { it.type == RescheduleType.TEMP }.forEach {
            if (it.oldDateTime.isEqual(startDateTime.toLocalDateTime()) || (it.oldDateTime.isAfter(startDateTime.toLocalDateTime()) && it.oldDateTime.isBefore(endDateTime.toLocalDateTime()))) {
                val exDates = ExceptionDates()
                exDates.values.add(ICalDate(it.oldDateTime.toDate(), true))
                vEvent.addExceptionDates(exDates)
            }
        }

        val iterator = vEvent.getDateIterator(TimeZone.getDefault())
        while (iterator.hasNext()) {
            val nextInstanceStart = iterator.next().toLocalDateTime()
            val nextInstanceEnd = nextInstanceStart.getEndWith(event)

            // TODO - Convert to schedule
//            eventList.add(event.copy(startDateTime = nextInstanceStart, endDateTime = nextInstanceEnd))
        }
        return eventList
    }
}