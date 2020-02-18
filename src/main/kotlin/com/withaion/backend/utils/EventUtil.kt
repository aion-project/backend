package com.withaion.backend.utils

import biweekly.component.VEvent
import biweekly.util.Frequency
import biweekly.util.Recurrence
import com.withaion.backend.dto.ScheduledEvent
import com.withaion.backend.models.Event
import com.withaion.backend.models.RepeatType
import com.withaion.backend.models.Schedule
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

fun LocalDateTime.getEndWith(start: LocalDateTime, end: LocalDateTime): LocalDateTime {
    return this.plusMinutes(ChronoUnit.MINUTES.between(start, end))
}

object EventUtil {

    fun expandEvents(schedule: Schedule): List<ScheduledEvent> {
        val scheduledEvents = mutableListOf<ScheduledEvent>()
        schedule.event?.let {
            scheduledEvents.addAll(schedule.expand(it, schedule))
        }
        return scheduledEvents
    }

    fun expandEvents(schedules: List<Schedule>): List<ScheduledEvent> {
        val scheduledEvents = mutableListOf<ScheduledEvent>()
        schedules.forEach { schedule ->
            schedule.event?.let {
                scheduledEvents.addAll(schedule.expand(it, schedule))
            }
        }
        return scheduledEvents
    }

    // Private extension functions
    private fun LocalDateTime.toDate(): Date {
        return Date.from(this.toInstant(OffsetDateTime.now().offset))
    }

    private fun Date.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())
    }

    private fun Schedule.expand(event: Event, schedule: Schedule): List<ScheduledEvent> {
        return when (this.repeatType) {
            RepeatType.WEEKLY -> {
                val until = this.until ?: this.startDateTime.plusMonths(1)
                genEvents(this.startDateTime, this.endDateTime, until, event, schedule, Frequency.WEEKLY)
            }
            RepeatType.DAILY -> {
                val until = this.until ?: this.startDateTime.plusMonths(1)
                genEvents(this.startDateTime, this.endDateTime, until, event, schedule, Frequency.DAILY)
            }
            RepeatType.NONE -> {
                listOf(ScheduledEvent(
                        eventId = event.id!!,
                        scheduleId = schedule.id!!,
                        name = event.name,
                        startDateTime = this.startDateTime,
                        endDateTime = this.endDateTime,
                        color = event.subject?.color,
                        location = schedule.location!!,
                        users = schedule.users
                ))
            }
        }
    }

    // Private functions
    private fun genEvents(start: LocalDateTime, end: LocalDateTime, until: LocalDateTime, event: Event, schedule: Schedule, freq: Frequency): List<ScheduledEvent> {
        val events = mutableListOf<ScheduledEvent>()

        val vEvent = VEvent()
        vEvent.setDateStart(start.toDate(), true)

        val recurrence = Recurrence.Builder(freq).until(until.toDate())
        vEvent.setRecurrenceRule(recurrence.build())

        val iterator = vEvent.getDateIterator(TimeZone.getDefault())
        while (iterator.hasNext()) {
            val nextInstanceStart = iterator.next().toLocalDateTime()
            val nextInstanceEnd = nextInstanceStart.getEndWith(start, end)

            events.add(ScheduledEvent(
                    eventId = event.id!!,
                    scheduleId = schedule.id!!,
                    name = event.name,
                    startDateTime = nextInstanceStart,
                    endDateTime = nextInstanceEnd,
                    color = event.subject?.color,
                    location = schedule.location!!,
                    users = schedule.users
            ))
        }
        return events
    }
}