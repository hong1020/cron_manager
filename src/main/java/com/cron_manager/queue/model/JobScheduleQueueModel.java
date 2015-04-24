package com.cron_manager.queue.model;

/**
 * Created by honcheng on 2015/4/23.
 */
public class JobScheduleQueueModel {
    public static final String KEY_SCHEDULE = "schedule";
    public static final String KEY_EXECUTE = "execute";
    public static final String KEY_SCHEDULE_GROUP = "schedule_group";
    public static final String KEY_SCHEDULE_VALUE = "schedule_value";
    public static final String KEY_SCHEDULE_EVENT_VALUE = "schedule_event_value";
    public static final String KEY_SCHEDULE_EVENT_LOCK = "schedule_event_lock";
    public static final String KEY_SCHEDULE_EXECUTE_LOCK = "schedule_execute_lock";
    public static final String KEY_SCHEDULE_EXECUTE_STATE = "schedule_execute_state";

    public static String getScheduleGroupKey(String group) {
        return KEY_SCHEDULE + ":" + group;
    }

    public static String getExecuteGroupKey(String group) {
        return KEY_EXECUTE + ":" + group;
    }

    public static String getScheduleGroupSetKey() {return KEY_SCHEDULE_GROUP;}

    public static String getScheduleValueKey(long id) {return KEY_SCHEDULE_VALUE + ":" + id;}

    public static String getScheduleEventKey(ScheduleEvent event) {
        return KEY_SCHEDULE_EVENT_VALUE + ":" + event.getJobScheduleId() + ":" + event.getEventType();
    }

    public static String getScheduleEventLockKey(ScheduleEvent event) {
        return KEY_SCHEDULE_EVENT_LOCK + ":" + event.getJobScheduleId() + ":" + event.getEventType();
    }

    public static String getJobScheduleExecuteLockKey(String key) {
        return KEY_SCHEDULE_EXECUTE_LOCK + ":" + key;
    }

    public static String getJobScheduleExecuteStateKey(long id) {
        return KEY_SCHEDULE_EXECUTE_STATE + ":" + id;
    }

    public static long getCheckInterval(ScheduleEvent event) {
        return 30000;
    }
}
